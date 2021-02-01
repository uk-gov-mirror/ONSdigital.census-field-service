package uk.gov.ons.ctp.integration.censusfieldsvc.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.CTPException.Fault;
import uk.gov.ons.ctp.integration.caseapiclient.caseservice.CaseServiceClientServiceImpl;
import uk.gov.ons.ctp.integration.caseapiclient.caseservice.model.CaseContainerDTO;
import uk.gov.ons.ctp.integration.caseapiclient.caseservice.model.QuestionnaireIdDTO;
import uk.gov.ons.ctp.integration.censusfieldsvc.config.AppConfig;
import uk.gov.ons.ctp.integration.censusfieldsvc.config.EqConfig;
import uk.gov.ons.ctp.integration.eqlaunch.crypto.KeyStore;
import uk.gov.ons.ctp.integration.eqlaunch.service.EqLaunchService;

@RunWith(MockitoJUnitRunner.class)
public class LauncherServiceImplTest {
  private static final String A_CASE_ID_STR = "3305e937-6fb1-4ce1-9d4c-077f147789ab";
  private static final UUID A_CASE_ID = UUID.fromString(A_CASE_ID_STR);
  private static final String A_USER_ID = "freda";
  private static final String A_QID = "123";
  private static final String A_HOST = "a_host";
  private static final String SALT = "CENSUS";
  private static final String A_DUMMY_ENCRYPTED_PAYLOAD = "xasdsada";
  private static final String A_URL_RESULT =
      "https://" + A_HOST + "?token=" + A_DUMMY_ENCRYPTED_PAYLOAD;

  @Mock private KeyStore keyStoreEncryption;

  @Mock private AppConfig appConfig;

  @Mock private CaseServiceClientServiceImpl caseServiceClient;

  @Spy private EqLaunchService eqLaunchService;

  @InjectMocks private LauncherServiceImpl service;

  @Before
  public void setup() {
    EqConfig eq = new EqConfig();
    eq.setHost(A_HOST);
    eq.setResponseIdSalt(SALT);
    when(appConfig.getEq()).thenReturn(eq);
  }

  @Test
  public void shouldGetEqUrl() throws Exception {
    QuestionnaireIdDTO qdto = makeQuestionnaireDto();
    CaseContainerDTO ccdto = makeCaseDetails();

    when(eqLaunchService.getEqLaunchJwe(any())).thenReturn(A_DUMMY_ENCRYPTED_PAYLOAD);
    when(caseServiceClient.getCaseById(eq(A_CASE_ID), eq(false))).thenReturn(ccdto);
    when(caseServiceClient.getReusableQuestionnaireId(eq(A_CASE_ID))).thenReturn(qdto);

    LaunchDetails details = service.getEqUrl(A_USER_ID, A_CASE_ID);

    assertEquals(A_URL_RESULT, details.getEqUrl());
    assertEquals(A_CASE_ID, details.getCaseId());
    assertEquals(A_QID, details.getQuestionnaireId());
  }

  @Test
  public void shouldRejectFailureToGetJwe() throws Exception {
    QuestionnaireIdDTO qdto = makeQuestionnaireDto();
    CaseContainerDTO ccdto = makeCaseDetails();

    when(eqLaunchService.getEqLaunchJwe(any())).thenThrow(new CTPException(Fault.BAD_REQUEST));
    when(caseServiceClient.getCaseById(eq(A_CASE_ID), eq(false))).thenReturn(ccdto);
    when(caseServiceClient.getReusableQuestionnaireId(eq(A_CASE_ID))).thenReturn(qdto);

    FieldServiceException e =
        assertThrows(FieldServiceException.class, () -> service.getEqUrl(A_USER_ID, A_CASE_ID));

    assertEquals(FieldServiceException.Fault.SYSTEM_ERROR, e.getFault());
  }

  @Test
  public void shouldRejectInactiveQuestionnaire() throws Exception {
    QuestionnaireIdDTO qdto = makeQuestionnaireDto();
    qdto.setActive(false);
    CaseContainerDTO ccdto = makeCaseDetails();

    when(caseServiceClient.getCaseById(eq(A_CASE_ID), eq(false))).thenReturn(ccdto);
    when(caseServiceClient.getReusableQuestionnaireId(eq(A_CASE_ID))).thenReturn(qdto);

    FieldServiceException e =
        assertThrows(FieldServiceException.class, () -> service.getEqUrl(A_USER_ID, A_CASE_ID));

    assertEquals(FieldServiceException.Fault.QUESTIONNAIRE_INACTIVE, e.getFault());
  }

  @Test
  public void shouldRejectCaseServiceClientNotFound() throws Exception {
    verifyCaseServiceClientError(
        HttpStatus.NOT_FOUND, FieldServiceException.Fault.RESOURCE_NOT_FOUND);
  }

  @Test
  public void shouldRejectCaseServiceClientBadRequest() throws Exception {
    verifyCaseServiceClientError(HttpStatus.BAD_REQUEST, FieldServiceException.Fault.BAD_REQUEST);
  }

  @Test
  public void shouldRejectCaseServiceClientInternalError() throws Exception {
    verifyCaseServiceClientError(
        HttpStatus.INTERNAL_SERVER_ERROR, FieldServiceException.Fault.SYSTEM_ERROR);
  }

  @Test
  public void shouldRejectCaseServiceClientOtherError() throws Exception {
    verifyCaseServiceClientError(HttpStatus.BAD_GATEWAY, FieldServiceException.Fault.SYSTEM_ERROR);
  }

  private void verifyCaseServiceClientError(
      HttpStatus httpStatus, FieldServiceException.Fault expectedFault) throws Exception {
    when(caseServiceClient.getCaseById(eq(A_CASE_ID), eq(false)))
        .thenThrow(new ResponseStatusException(httpStatus));

    FieldServiceException e =
        assertThrows(FieldServiceException.class, () -> service.getEqUrl(A_USER_ID, A_CASE_ID));

    assertEquals(expectedFault, e.getFault());
  }

  private QuestionnaireIdDTO makeQuestionnaireDto() {
    QuestionnaireIdDTO dto = new QuestionnaireIdDTO();
    dto.setActive(true);
    dto.setFormType("X");
    dto.setQuestionnaireId(A_QID);
    return dto;
  }

  private CaseContainerDTO makeCaseDetails() {
    CaseContainerDTO dto = new CaseContainerDTO();
    dto.setId(A_CASE_ID);
    return dto;
  }
}

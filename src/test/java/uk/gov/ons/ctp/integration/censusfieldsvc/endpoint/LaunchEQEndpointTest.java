package uk.gov.ons.ctp.integration.censusfieldsvc.endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.ulisesbocchio.spring.boot.security.saml.user.SAMLUserDetails;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import uk.gov.ons.ctp.integration.censusfieldsvc.service.LauncherService;
import uk.gov.ons.ctp.integration.censusfieldsvc.service.SurveyLaunchedService;
import uk.gov.ons.ctp.integration.censusfieldsvc.service.impl.FieldServiceException;
import uk.gov.ons.ctp.integration.censusfieldsvc.service.impl.FieldServiceException.Fault;
import uk.gov.ons.ctp.integration.censusfieldsvc.service.impl.LaunchDetails;

@RunWith(MockitoJUnitRunner.class)
public class LaunchEQEndpointTest {
  private static final String A_CASE_ID_STR = "3305e937-6fb1-4ce1-9d4c-077f147789ab";
  private static final UUID A_CASE_ID = UUID.fromString(A_CASE_ID_STR);
  private static String AN_EQ_URL = "https://somewhere";
  private static String A_QID = "123";
  private static final LaunchDetails LAUNCH_DETAILS =
      new LaunchDetails(AN_EQ_URL, A_QID, A_CASE_ID);

  @Mock private LauncherService launcherService;

  @Mock private SurveyLaunchedService surveyLaunchedService;

  @Mock private SAMLUserDetails user;
  @Mock private RedirectAttributes redirectAttribs;

  @InjectMocks private LaunchEQEndpoint controller;

  @Test
  public void shouldLaunchEq() throws Exception {
    when(launcherService.getEqUrl(any(), any())).thenReturn(LAUNCH_DETAILS);
    RedirectView view = controller.launchEQ(A_CASE_ID_STR, user, redirectAttribs);
    verify(surveyLaunchedService).surveyLaunched(eq(A_QID), eq(A_CASE_ID), any());
    assertEquals(AN_EQ_URL, view.getUrl());
  }

  @Test
  public void shouldErrorOnIllegalArgument() throws Exception {
    when(launcherService.getEqUrl(any(), any())).thenThrow(new IllegalArgumentException());
    RedirectView view = controller.launchEQ(A_CASE_ID_STR, user, redirectAttribs);
    verify(surveyLaunchedService, never()).surveyLaunched(any(), any(), any());
    verifyError(view, "Bad request - Case ID invalid");
  }

  @Test
  public void shouldFailToSendEvent() throws Exception {
    when(launcherService.getEqUrl(any(), any())).thenReturn(LAUNCH_DETAILS);
    doThrow(new RuntimeException()).when(surveyLaunchedService).surveyLaunched(any(), any(), any());

    RedirectView view = controller.launchEQ(A_CASE_ID_STR, user, redirectAttribs);

    verify(surveyLaunchedService).surveyLaunched(eq(A_QID), eq(A_CASE_ID), any());
    verifyError(view, "System error");
  }

  @Test
  public void shouldHandleQuestionnaireInactive() throws Exception {
    when(launcherService.getEqUrl(any(), any()))
        .thenThrow(new FieldServiceException(Fault.QUESTIONNAIRE_INACTIVE));
    RedirectView view = controller.launchEQ(A_CASE_ID_STR, user, redirectAttribs);
    verify(surveyLaunchedService, never()).surveyLaunched(any(), any(), any());
    assertEquals("/questionnaireInactive", view.getUrl());
  }

  @Test
  public void shouldHandleResourceNotFound() throws Exception {
    verifyErrorFault(Fault.RESOURCE_NOT_FOUND, "Data could not be found");
  }

  @Test
  public void shouldHandleBadRequest() throws Exception {
    verifyErrorFault(Fault.BAD_REQUEST, "Bad request");
  }

  @Test
  public void shouldHandleSystemError() throws Exception {
    verifyErrorFault(Fault.SYSTEM_ERROR, "System error");
  }

  @Test
  public void shouldHandleValidationFailed() throws Exception {
    verifyErrorFault(Fault.VALIDATION_FAILED, "Unknown");
  }

  @Test
  public void shouldHandleAccessDenied() throws Exception {
    verifyErrorFault(Fault.ACCESS_DENIED, "Unknown");
  }

  private void verifyErrorFault(Fault fault, String reason) throws Exception {
    when(launcherService.getEqUrl(any(), any())).thenThrow(new FieldServiceException(fault));
    RedirectView view = controller.launchEQ(A_CASE_ID_STR, user, redirectAttribs);
    verify(surveyLaunchedService, never()).surveyLaunched(any(), any(), any());
    verifyError(view, reason);
  }

  private void verifyError(RedirectView view, String reason) {
    verify(redirectAttribs).addFlashAttribute(eq("incident"), any());
    verify(redirectAttribs).addFlashAttribute(eq("reason"), eq(reason));
    assertEquals("/error", view.getUrl());
  }
}

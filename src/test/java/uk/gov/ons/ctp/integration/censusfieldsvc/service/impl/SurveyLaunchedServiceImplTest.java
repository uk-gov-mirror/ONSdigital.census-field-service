package uk.gov.ons.ctp.integration.censusfieldsvc.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.event.EventPublisher;
import uk.gov.ons.ctp.common.event.EventPublisher.Channel;
import uk.gov.ons.ctp.common.event.EventPublisher.EventType;
import uk.gov.ons.ctp.common.event.EventPublisher.Source;
import uk.gov.ons.ctp.common.event.model.SurveyLaunchedResponse;

@RunWith(MockitoJUnitRunner.class)
public class SurveyLaunchedServiceImplTest {
  private static final String A_CASE_ID_STR = "3305e937-6fb1-4ce1-9d4c-077f147789ab";
  private static final UUID A_CASE_ID = UUID.fromString(A_CASE_ID_STR);
  private static final String A_QID = "123";
  private static final String A_USER_ID = "freda";

  @Mock private EventPublisher eventPublisher;

  @InjectMocks private SurveyLaunchedServiceImpl service;

  @Captor private ArgumentCaptor<SurveyLaunchedResponse> responseCaptor;

  @Test
  public void shouldSendEvent() throws Exception {
    service.surveyLaunched(A_QID, A_CASE_ID, A_USER_ID);

    verify(eventPublisher)
        .sendEvent(
            eq(EventType.SURVEY_LAUNCHED),
            eq(Source.FIELDWORK_GATEWAY),
            eq(Channel.FIELD),
            responseCaptor.capture());

    SurveyLaunchedResponse response = responseCaptor.getValue();

    assertEquals(A_QID, response.getQuestionnaireId());
    assertEquals(A_CASE_ID, response.getCaseId());
    assertEquals(A_USER_ID, response.getAgentId());
  }
}

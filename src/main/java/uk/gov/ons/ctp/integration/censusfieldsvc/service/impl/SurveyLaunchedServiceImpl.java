package uk.gov.ons.ctp.integration.censusfieldsvc.service.impl;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.event.EventPublisher;
import uk.gov.ons.ctp.common.event.EventPublisher.Channel;
import uk.gov.ons.ctp.common.event.EventPublisher.EventType;
import uk.gov.ons.ctp.common.event.EventPublisher.Source;
import uk.gov.ons.ctp.common.event.model.SurveyLaunchedResponse;
import uk.gov.ons.ctp.integration.censusfieldsvc.service.SurveyLaunchedService;

@Service
public class SurveyLaunchedServiceImpl implements SurveyLaunchedService {
  private static final Logger log = LoggerFactory.getLogger(SurveyLaunchedServiceImpl.class);

  @Autowired private EventPublisher eventPublisher;

  @Override
  public void surveyLaunched(String questionnaireId, UUID caseId, String userId) {

    log.with("questionnaireId", questionnaireId)
        .with("caseId", caseId)
        .with("userId", userId)
        .info("Generating SurveyLaunched event");

    SurveyLaunchedResponse response =
        SurveyLaunchedResponse.builder()
            .questionnaireId(questionnaireId)
            .caseId(caseId)
            .agentId(userId)
            .build();

    String transactionId =
        eventPublisher.sendEvent(
            EventType.SURVEY_LAUNCHED, Source.FIELDWORK_GATEWAY, Channel.FIELD, response);

    log.with("caseId", response.getCaseId())
        .with("transactionId", transactionId)
        .debug("SurveyLaunch event published");
  }
}

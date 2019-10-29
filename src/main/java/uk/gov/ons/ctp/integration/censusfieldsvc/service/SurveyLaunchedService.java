package uk.gov.ons.ctp.integration.censusfieldsvc.service;

import java.util.UUID;
import uk.gov.ons.ctp.common.error.CTPException;

public interface SurveyLaunchedService {

  void surveyLaunched(String questionnaireId, UUID caseId, String agentId) throws CTPException;
}

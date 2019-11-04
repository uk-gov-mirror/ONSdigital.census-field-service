package uk.gov.ons.ctp.integration.censusfieldsvc.service;

import java.util.UUID;
import uk.gov.ons.ctp.integration.censusfieldsvc.service.impl.FieldServiceException;
import uk.gov.ons.ctp.integration.censusfieldsvc.service.impl.LaunchDetails;

public interface LauncherService {
  LaunchDetails getEqUrl(String userId, UUID caseId) throws FieldServiceException;
}

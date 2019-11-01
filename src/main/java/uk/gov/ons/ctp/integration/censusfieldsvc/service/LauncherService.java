package uk.gov.ons.ctp.integration.censusfieldsvc.service;

import java.util.UUID;
import uk.gov.ons.ctp.integration.censusfieldsvc.service.impl.FieldServiceException;

public interface LauncherService {
  String getEqUrl(String userId, UUID caseId) throws FieldServiceException;
}

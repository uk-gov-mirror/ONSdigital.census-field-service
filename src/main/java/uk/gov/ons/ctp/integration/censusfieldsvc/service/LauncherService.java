package uk.gov.ons.ctp.integration.censusfieldsvc.service;

import java.util.UUID;

public interface LauncherService {
  String getEqUrl(UUID caseId);
}

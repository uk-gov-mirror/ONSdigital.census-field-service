package uk.gov.ons.ctp.integration.censusfieldsvc.service.impl;

import java.util.UUID;
import lombok.Data;

/**
 * Composite object which is used to supply the user of LaunchService with details about the
 * redirection to EQ.
 */
@Data
public class LaunchDetails {
  private String eqUrl;
  private String questionnaireId;
  private UUID caseId;
}

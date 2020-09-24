package uk.gov.ons.ctp.integration.censusfieldsvc.service.impl;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Composite object which is used to supply the user of LaunchService with details about the
 * redirection to EQ.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LaunchDetails {
  private String eqUrl;
  private String questionnaireId;
  private UUID caseId;
}

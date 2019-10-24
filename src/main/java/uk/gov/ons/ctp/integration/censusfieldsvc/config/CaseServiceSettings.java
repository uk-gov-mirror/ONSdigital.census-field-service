package uk.gov.ons.ctp.integration.censusfieldsvc.config;

import lombok.Data;
import uk.gov.ons.ctp.common.rest.RestClientConfig;

@Data
public class CaseServiceSettings {
  private RestClientConfig restClientConfig;
}

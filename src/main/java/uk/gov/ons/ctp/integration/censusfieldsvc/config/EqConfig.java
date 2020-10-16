package uk.gov.ons.ctp.integration.censusfieldsvc.config;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EqConfig {
  @NotBlank private String host;
  @NotBlank private String responseIdSalt;
}

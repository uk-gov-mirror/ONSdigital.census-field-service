package uk.gov.ons.ctp.integration.censusfieldsvc.config;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.validation.annotation.Validated;
import uk.gov.ons.ctp.integration.eqlaunch.crypto.KeyStore;

/** Application Config bean */
@EnableRetry
@Configuration
@ConfigurationProperties
@Data
@Validated
public class AppConfig {
  @NotBlank private String domain;
  private CaseServiceSettings caseServiceSettings;
  private Logging logging;
  private SsoConfig sso;
  private KeyStore keystore;
  private EqConfig eq;
}

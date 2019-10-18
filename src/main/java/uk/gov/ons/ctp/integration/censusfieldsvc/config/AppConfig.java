package uk.gov.ons.ctp.integration.censusfieldsvc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.validation.annotation.Validated;
import lombok.Data;

/** Application Config bean */
@EnableRetry
@Configuration
@ConfigurationProperties
@Data
@Validated
public class AppConfig {
  private CaseServiceSettings caseServiceSettings;
  private Logging logging;
  private SsoConfig sso;
}

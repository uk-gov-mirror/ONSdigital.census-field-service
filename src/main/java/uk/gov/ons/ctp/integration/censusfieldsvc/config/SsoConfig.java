package uk.gov.ons.ctp.integration.censusfieldsvc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
@Data
public class SsoConfig {
  private boolean useReverseProxy;
  private ReverseProxyConfig reverseProxy;
}

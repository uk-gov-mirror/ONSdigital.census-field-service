package uk.gov.ons.ctp.integration.censusfieldsvc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
@ConfigurationProperties
@Data
public class SsoConfig {
  private boolean useReverseProxy;
  private ReverseProxyConfig reverseProxy;
}

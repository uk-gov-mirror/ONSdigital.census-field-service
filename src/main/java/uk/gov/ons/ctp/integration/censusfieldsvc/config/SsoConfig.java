package uk.gov.ons.ctp.integration.censusfieldsvc.config;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SsoConfig {
  private boolean useReverseProxy;
  @NotBlank private String idpRedirect;
  @NotBlank private String idpPost;
  @NotBlank private String idpEntityId;
  @NotBlank private String metadataCertificate;
  private long springMaxAuthenticationAge;
  private ReverseProxyConfig reverseProxy;
}

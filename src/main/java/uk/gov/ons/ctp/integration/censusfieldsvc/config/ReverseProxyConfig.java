package uk.gov.ons.ctp.integration.censusfieldsvc.config;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

// import net.sourceforge.cobertura.CoverageIgnore;

// @CoverageIgnore
@Data
@ToString
public class ReverseProxyConfig {
  @NotBlank private String scheme;
  @NotBlank private String contextPath;
  @NotBlank private String serverName;
  private int serverPort;
  private boolean includeServerPortInRequestURL;
}

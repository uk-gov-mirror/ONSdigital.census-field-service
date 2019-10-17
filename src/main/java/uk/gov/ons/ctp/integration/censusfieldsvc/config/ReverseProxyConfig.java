package uk.gov.ons.ctp.integration.censusfieldsvc.config;

import lombok.Data;
import lombok.ToString;

// import net.sourceforge.cobertura.CoverageIgnore;

// @CoverageIgnore
@Data
@ToString
public class ReverseProxyConfig {
  private String scheme;
  private String contextPath;
  private String serverName;
  private int serverPort;
  private boolean includeServerPortInRequestURL;
}

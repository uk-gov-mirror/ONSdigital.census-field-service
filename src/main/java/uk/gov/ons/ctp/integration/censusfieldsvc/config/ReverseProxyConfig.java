package uk.gov.ons.ctp.integration.censusfieldsvc.config;

import lombok.Data;

// import net.sourceforge.cobertura.CoverageIgnore;

// @CoverageIgnore
@Data
public class ReverseProxyConfig {
  private String scheme;
  private String contextPath;
  private String serverName;
  private int serverPort;
  private boolean includeServerPortInRequestURL;
}

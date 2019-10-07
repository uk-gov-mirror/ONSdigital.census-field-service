package uk.gov.ons.ctp.integration.censusfieldsvc.service.impl;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.ons.ctp.integration.censusfieldsvc.service.HelloWorldService;

/** Dummy service. */
@Service
@Validated()
public class HelloWorldServiceImpl implements HelloWorldService {
  private static final Logger log = LoggerFactory.getLogger(HelloWorldServiceImpl.class);

  @Override
  public String getHelloText() {
    log.info("Getting hello world text");
    return "Hello world";
  }
}

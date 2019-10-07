package uk.gov.ons.ctp.integration.censusfieldsvc.service.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import uk.gov.ons.ctp.integration.censusfieldsvc.service.HelloWorldService;

public class AddressServiceImplTest {

  @InjectMocks HelloWorldService helloWorldService = new HelloWorldServiceImpl();

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testHello() throws Exception {
    assertEquals("Hello world", helloWorldService.getHelloText());
  }
}

package uk.gov.ons.ctp.integration.censusfieldsvc.integration;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.ons.ctp.integration.censusfieldsvc.endpoint.HelloWorldEndpoint;

/**
 * This class holds integration tests that submit requests to the Census Field service, which in
 * turn will delegating the query to the Address Index service.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Ignore
public final class HelloWorldIT {
  @Autowired private HelloWorldEndpoint helloWorldEndpoint;

  private MockMvc mockMvc;

  @Before
  public void setUp() throws Exception {
    this.mockMvc = MockMvcBuilders.standaloneSetup(helloWorldEndpoint).build();
  }

  /**
   * This test submits a generic address query and validates that some data is returned in the
   * expected format. Without a fixed test data set this is really as much validation as it can do.
   */
  @Test
  public void validateHelloResponse() throws Exception {
    MvcResult result = mockMvc.perform(get("/hello")).andExpect(status().isOk()).andReturn();
    String response = result.getResponse().getContentAsString();

    assertEquals("Hello world", response);
  }
}

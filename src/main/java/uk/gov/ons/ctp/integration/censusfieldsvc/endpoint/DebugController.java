package uk.gov.ons.ctp.integration.censusfieldsvc.endpoint;

import java.util.Enumeration;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DebugController {

  @RequestMapping("/debug")
  public ModelAndView home(
      HttpServletRequest request, @RequestParam(required = false) String caseId) {
    HashMap<String, String> headerMap = new HashMap<>();

    Enumeration<String> names = request.getHeaderNames();
    while (names.hasMoreElements()) {
      String headerName = names.nextElement();
      String headerValue = request.getHeader(headerName);

      headerMap.put(headerName, headerValue);
    }

    ModelAndView homeView = new ModelAndView("debug");
    homeView.addObject("headers", headerMap);
    return homeView;
  }
}

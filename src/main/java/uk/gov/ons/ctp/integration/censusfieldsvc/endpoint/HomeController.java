package uk.gov.ons.ctp.integration.censusfieldsvc.endpoint;

import com.github.ulisesbocchio.spring.boot.security.saml.annotation.SAMLUser;
import com.github.ulisesbocchio.spring.boot.security.saml.user.SAMLUserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

  @RequestMapping("/home")
  public ModelAndView home(
      @SAMLUser SAMLUserDetails user, @RequestParam(required = false) String caseId) {
    ModelAndView homeView = new ModelAndView("home");
    homeView.addObject("userId", user.getUsername());
    homeView.addObject("samlAttributes", user.getAttributes());
    homeView.addObject("caseId", caseId);
    return homeView;
  }
}

package uk.gov.ons.ctp.integration.censusfieldsvc.endpoint;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Ulises Bocchio
 */

import com.github.ulisesbocchio.spring.boot.security.saml.annotation.SAMLUser;
import com.github.ulisesbocchio.spring.boot.security.saml.user.SAMLUserDetails;

@Controller
public class HomeController {

  @RequestMapping("/home")
  public ModelAndView home(@SAMLUser SAMLUserDetails user, 
      @RequestParam(required = false) String pppp) {
    ModelAndView homeView = new ModelAndView("home");
    homeView.addObject("userId", user.getUsername());
    homeView.addObject("samlAttributes", user.getAttributes());
    return homeView;
  }
}

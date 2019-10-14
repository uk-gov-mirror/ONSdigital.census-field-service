package uk.gov.ons.ctp.integration.saml;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.github.ulisesbocchio.spring.boot.security.saml.configurer.ServiceProviderBuilder;
import com.github.ulisesbocchio.spring.boot.security.saml.configurer.ServiceProviderConfigurerAdapter;

//@SpringBootApplication
//@EnableSAMLSSOWhenNotInTest
public class MovedSpringBootSecuritySAMLDemoApplication {

//    public static void main(String[] args) {
//        SpringApplication.run(MovedSpringBootSecuritySAMLDemoApplication.class, args);
//    }
//
//    @Configuration
//    public static class MvcConfig implements WebMvcConfigurer {
//
//        @Override
//        public void addViewControllers(ViewControllerRegistry registry) {
//            registry.addViewController("/").setViewName("index");
//            registry.addViewController("/hello3").setViewName("hello3");
//            registry.addViewController("/anon/hello").setViewName("anon/hello");
//            registry.addViewController("/anon/hello2").setViewName("anon/hello2");
//            registry.addViewController("/protected").setViewName("protected");
//            registry.addViewController("/afterlogout").setViewName("afterlogout");
//        }
//    }

    @Configuration
    public static class MyServiceProviderConfig extends ServiceProviderConfigurerAdapter {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .regexMatchers("/")
                    .permitAll()
                    .antMatchers("/completed")
                    .permitAll()
                    .antMatchers("/info")
                    .permitAll()
                    .antMatchers("/hello3")
                    .permitAll()
                    .regexMatchers("/anon/hello")
                    .permitAll();
        }

        @Override
        public void configure(ServiceProviderBuilder serviceProvider) throws Exception {
            // @formatter:off
            serviceProvider
                .metadataGenerator()
                .entityId("localhost")
            .and()
                .sso()
                //.ssoLoginURL(ssoLoginURL)
                //.defaultSuccessURL("/homeeoueuoeou")
            .and()
                .logout()
                .defaultTargetURL("/afterlogout")
            .and()
                .metadataManager()
                .metadataLocations("classpath:/GoogleIDPMetadata-test.field.census.gov.uk.xml")
                .defaultIDP("https://accounts.google.com/o/saml2?idpid=C00n4re6c")
                .refreshCheckInterval(0)
            .and()
                .extendedMetadata()
                .idpDiscoveryEnabled(false)  // disable IDP selection page
            .and()
                .keyManager()
                .privateKeyDERLocation("classpath:/localhost.key.der")
                .publicKeyPEMLocation("classpath:/localhost.cert")
            .and();
            // @formatter:on

        }
    }
}

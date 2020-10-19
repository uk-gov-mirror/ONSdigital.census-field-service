package uk.gov.ons.ctp.integration.censusfieldsvc;

import com.github.ulisesbocchio.spring.boot.security.saml.bean.override.DSLWebSSOProfileConsumerImpl;
import com.github.ulisesbocchio.spring.boot.security.saml.configurer.ServiceProviderBuilder;
import com.github.ulisesbocchio.spring.boot.security.saml.configurer.ServiceProviderConfigurerAdapter;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.godaddy.logging.LoggingConfigs;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import org.opensaml.saml2.metadata.provider.ResourceBackedMetadataProvider;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.saml.websso.WebSSOProfileConsumer;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.ons.ctp.common.config.CustomCircuitBreakerConfig;
import uk.gov.ons.ctp.common.event.EventPublisher;
import uk.gov.ons.ctp.common.event.EventSender;
import uk.gov.ons.ctp.common.event.SpringRabbitEventSender;
import uk.gov.ons.ctp.common.event.persistence.FirestoreEventPersistence;
import uk.gov.ons.ctp.common.jackson.CustomObjectMapper;
import uk.gov.ons.ctp.common.rest.RestClient;
import uk.gov.ons.ctp.common.rest.RestClientConfig;
import uk.gov.ons.ctp.integration.caseapiclient.caseservice.CaseServiceClientServiceImpl;
import uk.gov.ons.ctp.integration.censusfieldsvc.config.AppConfig;
import uk.gov.ons.ctp.integration.censusfieldsvc.config.ReverseProxyConfig;
import uk.gov.ons.ctp.integration.censusfieldsvc.config.SsoConfig;

/** The 'main' entry point for the CensusField Svc SpringBoot Application. */
@SpringBootApplication
@EnableSAMLSSOWhenNotInTest
@IntegrationComponentScan("uk.gov.ons.ctp.integration")
@ComponentScan(basePackages = {"uk.gov.ons.ctp.integration", "uk.gov.ons.ctp.common"})
@ImportResource("springintegration/main.xml")
@EnableCaching
public class CensusFieldSvcApplication {
  private static final Logger log = LoggerFactory.getLogger(CensusFieldSvcApplication.class);

  private AppConfig appConfig;

  // Table to convert from AddressIndex response status values to values that can be returned to the
  // invoker of this service
  private static final HashMap<HttpStatus, HttpStatus> httpErrorMapping;

  static {
    httpErrorMapping = new HashMap<HttpStatus, HttpStatus>();
    httpErrorMapping.put(HttpStatus.OK, HttpStatus.OK);
    httpErrorMapping.put(HttpStatus.BAD_REQUEST, HttpStatus.INTERNAL_SERVER_ERROR);
    httpErrorMapping.put(HttpStatus.UNAUTHORIZED, HttpStatus.INTERNAL_SERVER_ERROR);
    httpErrorMapping.put(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND);
    httpErrorMapping.put(HttpStatus.SERVICE_UNAVAILABLE, HttpStatus.INTERNAL_SERVER_ERROR);
    httpErrorMapping.put(HttpStatus.GATEWAY_TIMEOUT, HttpStatus.INTERNAL_SERVER_ERROR);
    httpErrorMapping.put(HttpStatus.REQUEST_TIMEOUT, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // This is the http status to be used for error mapping if a status is not in the mapping table
  HttpStatus defaultHttpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

  /**
   * Constructor for CensusFieldSvcApplication
   *
   * @param appConfig contains the configuration for the current deployment.
   */
  @Autowired
  public CensusFieldSvcApplication(final AppConfig appConfig) {
    this.appConfig = appConfig;
    SsoConfig ssoConfig = appConfig.getSso();
    if (ssoConfig.getIdpPost().contains("google.com")) {
      log.info("*** Using Google GSuite IDP provider ***");
    } else {
      log.info("*** Using alternative IDP provider ***");
    }
    log.info("*** POST URL: {}", ssoConfig.getIdpPost());
    log.info("*** Redirect URL: {}", ssoConfig.getIdpRedirect());
    log.info("*** IDP entity ID: {}", ssoConfig.getIdpEntityId());
  }

  /**
   * The main entry point for this application.
   *
   * @param args runtime command line args
   */
  public static void main(final String[] args) {
    SpringApplication.run(CensusFieldSvcApplication.class, args);
  }

  /**
   * The restTemplate bean injected in REST client classes
   *
   * @return the restTemplate used in REST calls
   */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  /**
   * Custom Object Mapper
   *
   * @return a customer object mapper
   */
  @Bean
  @Primary
  public CustomObjectMapper customObjectMapper() {
    return new CustomObjectMapper();
  }

  @Value("#{new Boolean('${logging.useJson}')}")
  private boolean useJsonLogging;

  @PostConstruct
  public void initJsonLogging() {
    if (useJsonLogging) {
      LoggingConfigs.setCurrent(LoggingConfigs.getCurrent().useJson());
    }
  }

  @Bean
  @Qualifier("caseServiceClient")
  public CaseServiceClientServiceImpl caseServiceClient() {
    RestClientConfig clientConfig = appConfig.getCaseServiceSettings().getRestClientConfig();
    RestClient restHelper = new RestClient(clientConfig, httpErrorMapping, defaultHttpStatus);
    CaseServiceClientServiceImpl csClientServiceImpl = new CaseServiceClientServiceImpl(restHelper);
    return csClientServiceImpl;
  }

  /**
   * Bean used to publish asynchronous event messages
   *
   * @param rabbitTemplate RabbitMQ connection settings and strategies
   * @return the event publisher
   */
  @Bean
  public EventPublisher eventPublisher(
      final RabbitTemplate rabbitTemplate,
      final FirestoreEventPersistence eventPersistence,
      final Resilience4JCircuitBreakerFactory circuitBreakerFactory) {
    EventSender sender = new SpringRabbitEventSender(rabbitTemplate);
    CircuitBreaker circuitBreaker = circuitBreakerFactory.create("eventSendCircuitBreaker");
    return EventPublisher.createWithEventPersistence(sender, eventPersistence, circuitBreaker);
  }

  @Bean
  public Customizer<Resilience4JCircuitBreakerFactory> defaultCircuitBreakerCustomiser() {
    CustomCircuitBreakerConfig config = appConfig.getCircuitBreaker();
    log.info("Circuit breaker configuration: {}", config);
    return config.defaultCircuitBreakerCustomiser();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
    final var template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(new Jackson2JsonMessageConverter());
    template.setExchange("events");
    template.setChannelTransacted(true);
    return template;
  }

  // Tell Thymeleaf about the supported HTML pages
  @Configuration
  public static class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
      registry.addViewController("/completed").setViewName("completed");
      registry.addViewController("/error").setViewName("error");
      registry.addViewController("/questionnaireCompleted").setViewName("questionnaireCompleted");
      registry.addViewController("/questionnaireSaved").setViewName("questionnaireSaved");
      registry.addViewController("/questionnaireInactive").setViewName("questionnaireInactive");
    }
  }

  /**
   * Spring-session tries to use the Redis Config command during initialisation. Hosted Redis
   * services disable this command and during startup you get the error:
   * RedisCommandExecutionException: ERR unknown command `CONFIG`. Bean disables automatic
   * configuration of Redis.
   */
  @Bean
  ConfigureRedisAction configureRedisAction() {
    return ConfigureRedisAction.NO_OP;
  }

  /** Override Spring-session setting of SameSite cookie attribute which breaks Spring SAML */
  @Bean
  public CookieSerializer cookieSerializer() {
    DefaultCookieSerializer serializer = new DefaultCookieSerializer();
    serializer.setSameSite("");
    return serializer;
  }

  @Configuration
  public static class MyServiceProviderConfig extends ServiceProviderConfigurerAdapter {
    @Autowired private AppConfig appConfig;
    @Autowired private IdpMetadata idpMetadata;

    /** List the pages which can be accessed without SSO authentication. */
    @Override
    public void configure(HttpSecurity http) throws Exception {
      http.authorizeRequests()
          .antMatchers("/info")
          .permitAll()
          .antMatchers("/error")
          .permitAll()
          .antMatchers("/questionnaireCompleted")
          .permitAll()
          .antMatchers("/questionnaireInactive.html")
          .permitAll()
          .antMatchers("/questionnaireSaved.html")
          .permitAll();
    }

    @Override
    public void configure(ServiceProviderBuilder serviceProvider) throws Exception {
      SsoConfig ssoConfig = appConfig.getSso();

      String metadata = idpMetadata.load();
      ResourceBackedMetadataProvider idpMetadataProvider =
          new ResourceBackedMetadataProvider(null, new StringResource(metadata));

      serviceProvider
          .metadataManager()
          .metadataProvider(idpMetadataProvider)
          .refreshCheckInterval(-1)
          .and()
          .ssoProfileConsumer(customWebSSOProfileConsumer());

      if (ssoConfig.isUseReverseProxy()) {
        ReverseProxyConfig reverseProxyConfig = ssoConfig.getReverseProxy();
        log.info("Using reverseProxy: " + reverseProxyConfig);

        serviceProvider
            .samlContextProviderLb()
            .scheme(reverseProxyConfig.getScheme())
            .contextPath(reverseProxyConfig.getContextPath())
            .serverName(reverseProxyConfig.getServerName())
            .serverPort(reverseProxyConfig.getServerPort())
            .includeServerPortInRequestURL(reverseProxyConfig.isIncludeServerPortInRequestURL());
      }
    }

    // Sets the max authentication age to a really large value.
    // This prevents spring boot from deciding that authentication is too old and throwing an
    // exception. It should mean that we rely on whatever reauthentication period the IDP uses.
    private WebSSOProfileConsumer customWebSSOProfileConsumer() {
      DSLWebSSOProfileConsumerImpl consumer = new DSLWebSSOProfileConsumerImpl();
      consumer.setMaxAuthenticationAge(appConfig.getSso().getSpringMaxAuthenticationAge());
      return consumer;
    }

    @Value("#{new Boolean('${logging.useJson}')}")
    private boolean useJsonLogging;

    @PostConstruct
    public void initJsonLogging() {
      if (useJsonLogging) {
        LoggingConfigs.setCurrent(LoggingConfigs.getCurrent().useJson());
      }
    }
  }
}

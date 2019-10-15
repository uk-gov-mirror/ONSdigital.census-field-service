package uk.gov.ons.ctp.integration.censusfieldsvc;

// @Configuration
// @EnableWebSecurity
// public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
public class WebSecurityConfig {

  //  private static final Logger log = LoggerFactory.getLogger(WebSecurityConfig.class);
  //
  //  @Value("${spring.security.user.name}")
  //  String username;
  //
  //  @Value("${spring.security.user.password}")
  //  String password;
  //
  //  @Override
  //  protected void configure(HttpSecurity http) throws Exception {
  //    http.authorizeRequests()
  //        .antMatchers("/info")
  //        .permitAll()
  //        .anyRequest()
  //        .authenticated()
  //        .and()
  //        .csrf()
  //        .disable()
  //        .httpBasic();
  //  }
  //
  //  // USE OF THE PASSWORD ENCRYPTION COMMENTED OUT WHILE I TRY AND WORK OUT WHY IT IS NOT WORKING
  //  // WHEN
  //  // CONFIGURED THROUGH SECRETS IN GCP
  //  @Override
  //  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
  //    auth.inMemoryAuthentication().withUser(username).password("{noop}" +
  // password).roles("USER");
  //  }
  //
  //  //  @Bean
  //  //  public PasswordEncoder passwordEncoder() {
  //  //    return new BCryptPasswordEncoder();
  //  //  }
}

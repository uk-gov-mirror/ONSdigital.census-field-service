# Using an alternative IDP

The Google IDP (Identity Provider) has proved problematic for automated (cucumber) testing of a SAML 
username/password challenge because of its use of the Captcha technique 
for tests run from the kubernetes environments (we don't normally see the Captcha when run locally).

In order to fix this, there is support for an alternative IDP to be used which allows cucumber to be configured to test out the SAML login.

## Using samltest.id

A free service at [samltest.id](https://samltest.id/) allows us to use their online IDP for testing.

In order to do this the IDP must recognise an SP ("Service Provider" - this is the field-service for us), 
and **samltest.id** has an upload page for doing this, where you can pick one of the metadata XML files
required for running locally, or in DEV or TEST environments from the [metadata](metadata) directory.

A testing X.509 certificate and private key have been prepared for use with the alternate IDP and are
stored in the [samltest resource area](src/main/resources/samltest).

## Running locally against the alternative IDP

The Spring Boot YAML profile file **application-local-with-alt-idp.yml** is configured with alternative details.

You can start up from the commandline as so:
```
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=local-with-alt-idp
```

## Configuring kubernetes secrets for the alternative IDP so that cucumber can run

For DEV and TEST environments which run Cucumber, the field service should be configured to use the
alternative IDP.

#### fs-sso-identity

* **sso-idpid:** this can be anything as it is not used, e.g. "dontcare"
* **sso-metadatacertificate:** the certifcate as seen in **application-dev.yml**  sso.metadataCertificate

#### fs-cucumber-identity

Use any of the suggested username/password combinations suggested by the test IDP. At the time
of writing on **samtest.id** the first valid combination is for username "rick":

* **username:** "rick"
* **password:** "psych"


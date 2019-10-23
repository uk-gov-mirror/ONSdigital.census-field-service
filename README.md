# census-field-service
Census Integration team's Field Proxy Service


## Spring boot security SAML

### Self signed keys

The extension makes use of a self signed private/public key pair.

These can be regenerated with the following commands:

    # KEY AND CERT
    cd census-field-service/src/main/resources
    openssl genrsa -out localhost.key 2048
    openssl req -new -x509 -key localhost.key -out localhost.pem -days 3650 -subj /CN=localhost
    # PEM KEY to DER
    openssl pkcs8 -topk8 -inform PEM -outform DER -in  localhost.key -out  localhost.key.der -nocrypt
    # Finish and cleanup
    mv localhost.pem localhost.cert 
    rm localhost.key

## Links

### Spring security SAML extension

Spring security SAML extension: https://docs.spring.io/spring-security-saml/docs/current/reference/html/index.html

This extension is nowhere near as simple to use as advertised. Here is a page describing how to use Spring security: https://medium.com/@viraj.rajaguru/how-to-use-spring-saml-extension-7ffe0dd38465

### Spring boot security SAML

This project is a library which sits over Spring security SAML, and essentially provides a much simpler
way to configure the Spring security SAML extension.

Project homepage: https://github.com/ulisesbocchio/spring-boot-security-saml 

Samples using this project: https://github.com/ulisesbocchio/spring-boot-security-saml-samples

The projects issue page, which is very useful for troubleshooting: https://github.com/ulisesbocchio/spring-boot-security-saml/issues

Page by the author describing how to use: https://jitpack.io/p/ulisesbocchio/spring-boot-security-saml

Configuration parameters: https://github.com/ulisesbocchio/spring-boot-security-saml/blob/master/docs/properties/config-properties.md

### Google G Suite

How to enable SAML: https://cloud.google.com/identity-platform/docs/how-to-enable-application-for-saml

Description of G Suite SAML error messages: https://support.google.com/a/answer/6301076?hl=en


## Field service in dev environment

LaunchEQ: https://dev-fieldservice.fwmt-gateway.census-gcp.onsdigital.uk/launch/47066415-b59f-4df1-869b-8e2b4e818e82

An unprotected field service page: https://dev-fieldservice.fwmt-gateway.census-gcp.onsdigital.uk/completed

## Field service on localhost

To run the census-field-service you can run CensusFieldSvcApplication with the following VM arguments:

    -Dspring.profiles.active=local
    -Dsso.idpId=C00n4re6c 
    -Dsso.metadataCertificate=MIIDdD....

### Endpoints and pages

LaunchEQ: https://localhost:8443/launch/47066415-b59f-4df1-869b-8e2b4e818e82

An unprotected field service page: https://localhost:8443/completed

### HTTPS & SSL keystore

For testing on localhost you'll need to run the service using https. In Eclipse run the service with the VM argument: '-Dspring.profiles.active=local' 

The keystore has been created by the following commands. Note that the localhost config assumes a password of 'cfsstore-pw':
  
    $ cd ~/source/census-field-service/src/main/resources.
    
    $ keytool -genkey -alias tomcat -storetype PKCS12 -keyalg RSA -keysize 4096 -keystore localhost-ssl-keystore.p12 -validity 3650
    Enter keystore password:  
    Re-enter new password: 
    What is your first and last name?
      [Unknown]:  
    What is the name of your organizational unit?
      [Unknown]:  
    What is the name of your organization?
      [Unknown]:  
    What is the name of your City or Locality?
      [Unknown]:  
    What is the name of your State or Province?
      [Unknown]:  
    What is the two-letter country code for this unit?
      [Unknown]:  
    Is CN=Unknown, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown correct?
      [no]:  yes
    
  
  
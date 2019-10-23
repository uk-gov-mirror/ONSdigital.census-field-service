# census-field-service
Census Integration team's Field Proxy Service

## Running in a development environment

### Starting CFS

To run the census-field-service you can run CensusFieldSvcApplication with the following VM arguments:

    -Dspring.profiles.active=local
    -Dsso.idpId=C00n4re6c 
    -Dsso.metadataCertificate=MIIDdD....

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
    
  
  
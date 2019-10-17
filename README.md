# census-field-service
Census Integration team's Field Proxy Service


## HTTPS

For testing on localhost you'll need to run the service using https. In Eclipse run the service with the VM argument: '-Dspring.profiles.active=local' 

### SSL keystore

The keystore has been created by:
  
    $ cd ~/source/census-field-service
    
    $ keytool -genkey -alias tomcat -storetype PKCS12 -keyalg RSA -keysize 4096 -keystore ssl-keystore.p12 -validity 3650
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
    
  
  
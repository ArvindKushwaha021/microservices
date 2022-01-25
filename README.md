# microservices
spring cloud microservies

#Limits service- Config client
In new version of Spring cloud below property is mandatory if we are using config client(spring-cloud-starter-config)

spring.config.import=optional:configserver:https://localhost:8888

here optinal means if config server is running it will connect to it otherwise ignore.

-->to create the configuration create a class Configuation and annotate it with 
@ConfigurationProperties(limits-service);l,kjmn nb
here argument is the the name of coniguration so define two virable in the class as minumum and maximum and define below properties in application .properties

limits-service.minumum=10-- here minimum will be mapped with minimum of configuration.
limits-service.maximum=901--here maximum will be mapped with maximum of configuration.

#spring-cloud-config server
use @EnableConfigServer to enable the config server
-- define these properties
spring.application.name=spring-cloud-config-server
server.port=8888
spring.cloud.config.server.git.uri=file:////G:/Mission2021_22/Microservices/gitproject/git-localconfig-repo
spring.cloud.config.server.git.default-label=master
spring.profiles.active=dev

-- at above given location initialize the git repository means git-localconfig-repo should be a git repository.
-- create property files in this folder. For ex I have created 5 files

my-client.properties
my-client-dev.properties
limits-service.properties
limits-service-dev.properties
limits-service-qa.properties

-- To access the config server access 
http://localhost:8888/limits-service/default: This will return limits-service.properties file
http://localhost:8888/limits-service/dev: this will return limits-service.properties and limits-service-dev.properties files
http://localhost:8888/limits-service/qa: this will return limits-service.properties and limits-service-qa.properties files
http://localhost:8888/my-client/default : this will return my-client.properties 
http://localhost:8888/limits-service/dev : this will return my-client.properties and my-client-dev.properties
http://localhost:8888/limits-service/qa : this will reurn my-client.properties file as there is not file for qa profile

# microservices
spring cloud microservies

#Limits service
In new version of Spring cloud below property is mandatory if we are using config client(spring-cloud-starter-config)

spring.config.import=optional:configserver:https://localhost:8888

here optinal means if config server is running it will connect to it otherwise ignore.

-->to create the configuration create a class Configuation and annotate it with 
@ConfigurationProperties("limits-service")
here argument is the the name of coniguration so define two virable in the class as minumum and maximum and define below properties in application .properties

limits-service.minumum=10-- here minimum will be mapped with minimum of configuration.
limits-service.maximum=901--here maximum will be mapped with maximum of configuration.
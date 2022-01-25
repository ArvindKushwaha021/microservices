# microservices
spring cloud microservies

#Limits service
In new version of Spring cloud below property is mandatory if we are using config client(spring-cloud-starter-config)

spring.config.import=optional:configserver:https://localhost:8888

here optinal means if config server is running it will connect to it otherwise ignore.



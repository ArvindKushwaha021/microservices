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
spring.cloud.config.server.git.default-label=master -- git has renamed the default branch from master to main So if you local git has master then you need to define this property

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

#Connecting limit service to config server

limits-service property file
spring.application.name=limits-service---this is important because it will searh the property file name with application name
Eg. if we give name as limits-service then it will read file limits-service.properties and if we give name my-client then my-client.properties will be read. It file is not available with application name then it will read properties from the local(limit-service) properties file.

spring.config.import=optional:configserver:http://localhost:8888 ---It will search the application.properties file at give location.if It is there it will pick the profile from config server and give prefernce to it if file or any property is not found in config-server property file then it will read property from aplication.proterties of limits-service. But preference is always given to application.proerties file from config server.

For ex. if config server property file have only maximum then maximun will be taken from there and minimum will be taken from application.proerties file of limits-service.
limits-service.minimum=3
limits-service.maximum=997

# Cofiguring profile with limit service
--To connect with a config server profile below property is defined and based on it respective property file is fetched.

spring.cloud.config.profile=dev

-- if we do not want to use the application name for file fetching then we can define below property applicaiton files with this name will be fetched.
spring.cloud.config.name=limits-service

this property has preference over application name to fetch the properties.

#Currency exchange service

http://localhost:8000/currency-exchange/from/USD/to/INR
Response Structure
{
   "id":10001,
   "from":"USD",
   "to":"INR",
   "conversionMultiple":65.00,
   "environment":"8000 instance-id"
}
spring.application.name=currency-exchange-service
server.port=8000
spring.config.import=optional:configserver:http://localhost:8888


	import org.springframework.core.env.Environment;	
	@Autowired
	Environment environment;-- this give the environment detail of the application
	
	environment.getProperty("local.server.port");-- this tell the port on which app is running.
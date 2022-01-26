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
	
#Fetching currency exchange from DB(h2 database)
spring.jpa.show-sql=true
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enable=true

--create data.sql parallel to application.properties.
from springboot 2.4 version data.sql is executed even before creation of table So It will give error. define below property to defer the insertion

spring.jpa.defer-datasource-initialization=true

#currency-conversion-service
http://localhost:8100/currency-conversion/from/USD/to/INR/quantity/10
{
  "id": 10001,
  "from": "USD",
  "to": "INR",
  "conversionMultiple": 65.00,
  "quantity": 10,
  "totalCalculatedAmount": 650.00,
  "environment": "8000 instance-id"
}

#CurrencyConversion with Feign

<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-openfeign</artifactId>
		</dependency>
		
		
@EnableFeignClients in main class to enable feign
-->create a interface which is called proxy

@FeignClient(name="currency-exchange", url="localhost:8000")
here name should be the application name for which this proxy is create and whose rest api needs to be called from this application.
url should be the "doman(IP):port"
public interface CurrencyExchangeProxy {
	//There is some problem in feign So it is mandatory to pass (value="xyz") in PathVariable annotation
	/*@GetMapping("/currency-exchange/from/{from}/to/{to}")	
	public CurrencyConversion retrieveExchangeValue(@PathVariable String from, @PathVariable String to);*/
	
	
	@GetMapping("/currency-exchange/from/{from}/to/{to}")	
	public CurrencyConversion retrieveExchangeValue(@PathVariable(value="from") String from, 	@PathVariable(value="to") String to);
--This is the same method that is defined in CurrencyExchangeControler in currency-exchange project.
But here we are using CurrencyConversion as return type while It is CurrencyExchange in CurrencyExchangeControler.
But this is not an issue in this case because CurrencyConversion have all parameter of CurrencyExchange So It will be mapped.

Now We can remove below boilerplate code to call rest api just with one line

Existing boilerplance code with resttemplate
Map<String, String> urlVariables=new HashMap<>();
		urlVariables.put("from", from);
		urlVariables.put("to", to);
		ResponseEntity<CurrencyConversion> responseEntity = new RestTemplate().
				getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", 
				CurrencyConversion.class,urlVariables);
		CurrencyConversion currencyConversion = responseEntity.getBody();

--One liner code to execute rest call
CurrencyConversion currencyConversion = proxy.retrieveExchangeValue(from, to);
}







	
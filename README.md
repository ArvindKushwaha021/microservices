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

#Euereka Naming Server:

add Eureka Server dependency
use @EnableEurekaServer to enable server

spring.application.name=naming-server
server.port=8761
eureka.client.register-with-eureka=false --as this is naming server So we have to disable the register for it
eureka.client.fetch-registory=false --as this is naming server So we have to disable the fetch for it

>> Add below dependency in Clients sto that they can register with naming server.
		<dependency>
   			 <groupId>org.springframework.cloud</groupId>
   			  <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>

We have add only the dependency in clients , not other change is required in app. 
The client will register to naming server if it is running on default port on same machine. It it is running
on different port(9010) then we have to specify the machine and port manually by below property

eureka.client.serviceUrl.defaultZone=http://localhost:9010/eureka

# Loadbalancing 
In Earlier version of Spring cloud Netflix's Ribbon was used for load balancing. But now spring cloud comes
with it's own load balancer. It is part of dependency(jar) spring-cloud-starter-netflix-eureka-client which internaly contain spring-cloud-netflix-eureka-client..

If we are using feign and eureka , loadbalacing come as free. It is client side load balancing. Feing using same loadbalancer jar for load balancing.

to achieve the load balancer we only need to do below change in CurrenyExchangeProxy

//@FeignClient(name="currency-exchange", url="localhost:8000")
@FeignClient(name="currency-exchange")-- just remove url.

If we are removing feign check with eureka server if any service is running with name currency-exchange and load balance automatically.

#Api Gateway
In typical microservice architecture there would be 100 of microservices and these microservices will have lot of common feature, authentication , authorization, logging etc where would these be implemented. Typical solution is Api gateway. In earlier version of Spring cloud netfix Zuul was used as api gateway but it is not supported now. So in new version spring cloud gateway is used Api gateway.

To implement api gateway just create a project with eureka discovery client and spring cloud gateway dependency
 property file
spring.application.name=api-gateway
server.port=8765
#eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka
spring.cloud.gateway.discovery.locator.enabled=true --It is mandatory to work the api gate way.
It is running on port 8765 so just use be below urls to access the application

API GATEWAY urls
http://localhost:8765/CURRENCY-EXCHANGE/currency-exchange/from/USD/to/INR --here CURRENCY-EXCHANGE is the application name registred and showing on naming server console
http://localhost:8765/CURRENCY-CONVERSION/currency-conversion-feign/from/USD/to/INR/quantity/10

here upper case name is not looking good to add below property to make is lowercase

spring.cloud.gateway.discovery.locator.lowerCaseServiceId=true

after adding this below url will work
http://localhost:8765/currency-exchange/currency-exchange/from/USD/to/INR

#API gateway features
Simple, yet effective way to route to APIs
Provide cross cutting concerns:
Security
Monitoring/metrics
Built on top of Spring WebFlux (Reactive
Approach)
Features:
Match routes on any request attribute
Define Predicates and Filters
Integrates with Spring Cloud Discovery Client (Load
Balancing)
Path Rewriting

For Croscutting concernn we need to implement the filters

@Component
public class LoggingFilter implements GlobalFilter {

	private Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, 
			GatewayFilterChain chain) {
		logger.info("Path of the request received -> {}", 
				exchange.getRequest().getPath());
		logger.info("Query parameters of the request received -> {}", 
				exchange.getRequest().getQueryParams());
		return chain.filter(exchange);
	}

}

For routing using API Gateway:

@Configuration
public class ApiGatewayConfiguration {
	
	@Bean
	public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p -> p
						.path("/get")
						.filters(f -> f
								.addRequestHeader("MyHeader", "MyURI")
								.addRequestParameter("Param", "MyValue"))
						.uri("http://httpbin.org:80"))
				.route(p -> p.path("/currency-exchange/**")
						.uri("lb://currency-exchange"))
				.route(p -> p.path("/currency-conversion/**")
						.uri("lb://currency-conversion"))
				.route(p -> p.path("/currency-conversion-feign/**")
						.uri("lb://currency-conversion"))
				.route(p -> p.path("/currency-conversion-new/**")
						.filters(f -> f.rewritePath(
								"/currency-conversion-new/(?<segment>.*)", 
								"/currency-conversion-feign/${segment}"))
						.uri("lb://currency-conversion"))
				.build();
	}

}

#Resilience4j
Resilience4j is a lightweight, easy-to-use fault tolerance library inspired by
Netflix Hystrix, but designed for Java 8 and functional programming. Lightweight, because the library only uses Vavr, which does not have any other external library dependencies. Netflix Hystrix, in contrast, has a compile dependency to Archaius which has many more external library dependencies such as Guava and Apache Commons Configuration.

Resilience4j provides higher-order functions (decorators) to enhance any functional interface, lambda expression or method reference with a Circuit Breaker, Rate Limiter, Retry or Bulkhead. You can stack more than one decorator on any functional interface, lambda expression or method reference. The advantage is that you have the choice to select the decorators you need and nothing else.

resilience4j-circuitbreaker: Circuit breaking
resilience4j-ratelimiter: Rate limiting
resilience4j-bulkhead: Bulkheading
resilience4j-retry: Automatic retrying (sync and async)
resilience4j-cache: Result caching
resilience4j-timelimiter: Timeout handling

https://resilience4j.readme.io/docs/

dependencies required for relillience4j


 compile "io.github.resilience4j:resilience4j-spring-cloud2:${resilience4jVersion}"
    compile('org.springframework.boot:spring-boot-starter-actuator')
    compile('org.springframework.boot:spring-boot-starter-aop')
    
1. Retry
create simple api that will fail
	@GetMapping("/sample-api")
	@Retry(name = "default")-- it will use default configurations.
	@Retry(name = "sample-api")--this will retry multiple time before returning the response and use sample-api properties defined in application.properties.
	
	@Retry(name = "sample-api", fallbackMethod = "hardcodedResponse")--this will return mulitiple time then it will call the hardcodedResponse method.
	Note: hardcodedResponse method should accept parameter as Throwable. We can define overloaded method with different Exception and those method will be called if particular exception occurs.
	
 public String sampleApi() {
		logger.info("Sample api call received");
		ResponseEntity<String> forEntity = new 	RestTemplate().getForEntity("http://localhost:8080/some-dummy-url", 
					String.class);
	//return forEntity.getBody();
		return "sample-api";
	}
	
	public String hardcodedResponse(Exception ex) {
		logger.info("returning hardcoded response");
		return "fallback-response";
	}
	
We can define below properties for handling

---here sample.api is the name defined in @Retry annotation
resilience4j.retry.instances.sample-api.maxRetryAttempts=5 -- It will retry 5 times
resilience4j.retry.instances.sample-api.waitDuration=1s ---this is duration between retry
resilience4j.retry.instances.sample-api.enableExponentialBackoff=true-- if It is true It will retry in exponential time like 1,1.5, 2.5, 4.5 etc.


2. CircuitBreaker

https://resilience4j.readme.io/docs/circuitbreaker

CircuitBreaker pattern means work on finite state machine with 3 states
CLOSED, OPEN, HALF-OPEN.
Initiatl It is in CLOSED state if the api start failing after specific number of failiure It will make the state as OPEN and the api will not be called and fallback response will be send and for some request It will send the hard coded response.
@CircuitBreaker(name = "default", fallbackMethod = "hardcodedResponse")


resilience4j.ratelimiter.instances.default.limitForPeriod=2
resilience4j.ratelimiter.instances.default.limitRefreshPeriod=10s
--It means It will allow only two calls in 10s period and other will be failed.After 2 calls It will fail and give "RateLimiter 'default' does not permit further calls" error.

3. Bulkhead- It means it will allow max n number of concurrent calls
	//@Bulkhead(name="sample-api")

#resilience4j.bulkhead.instances.default.maxConcurrentCalls=10
#resilience4j.bulkhead.instances.sample-api.maxConcurrentCalls=10

Note: for @Retry, @Circutibreaker, @Ratelimiter and @Buldhead We can define it on mulitple methods with same name  or different name. If we want defferent behavior for different api we can just define with different name parameter and define the resilience4j properties with same name.

example
resilience4j.bulkhead.instances.default.maxConcurrentCalls=10
resilience4j.bulkhead.instances.sample-api.maxConcurrentCalls=10

here two same properties are defined for different name means for different api's.

#Zipkin and sleuth
docker run -p 9411:9411 openzipkin/zipkin:2.23
spring.zipkin.baseUrl=http://localhost:9411/

Zipkin and sleuth are used for distributed tracing. Slueth add a trace Id in the request which sent with request across each microservice.When zipkin dependency available at classpath spring cloud check that if zipkin is running at default port 9411 It send the request to Zipkin server.

	<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-sleuth</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-sleuth-zipkin</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.amqp</groupId>
			<artifactId>spring-rabbit</artifactId>
		</dependency>
		
spring.sleuth.sampler.probability=1.0 ---It means It will send 100% requests to zipkin.

Generall we do not send all the request to zipkin due to performance issue.

spring.zipkin.baseUrl=http://localhost:9400/-- define if zipkin is running on some other port.
spring.zipkin.sender.type=rabbit

#Docker in microservices

Addd below docker docker maven configuration in build tag:
<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
					<image>
						<name>kushwaha021/${project.artifactId}:${project.version}</name>
					</image>
					<pullPolicy>IF_NOT_PRESENT</pullPolicy>
				</configuration>				
			</plugin>
		</plugins>
	</build>
	
and run below maven build command to create image
mvn spring-boot:build-image 

it will create image with name kushwaha021/prjectArtifactname:projectVersion
eg. kushwaha021/currency-exchange-service:0.0.1 SNAPSHOT


in same way create images for all services and create below docker-compose.yaml file



version: '3.7'

services:

  currency-exchange:
    image: in28min/mmv2-currency-exchange-service:0.0.1-SNAPSHOT
    mem_limit: 700m
    ports:
      - "8000:8000"
    networks:
      - currency-network
    depends_on:
      - naming-server
      - rabbitmq
    environment:
      EUREKA.CLIENT.SERVICEURL.DEFAULTZONE: http://naming-server:8761/eureka
      SPRING.ZIPKIN.BASEURL: http://zipkin-server:9411/
      RABBIT_URI: amqp://guest:guest@rabbitmq:5672
      SPRING_RABBITMQ_HOST: rabbitmq
      SPRING_ZIPKIN_SENDER_TYPE: rabbit

  currency-conversion:
    image: in28min/mmv2-currency-conversion-service:0.0.1-SNAPSHOT
    mem_limit: 700m
    ports:
      - "8100:8100"
    networks:
      - currency-network
    depends_on:
      - naming-server
      - rabbitmq
    environment:
      EUREKA.CLIENT.SERVICEURL.DEFAULTZONE: http://naming-server:8761/eureka
      SPRING.ZIPKIN.BASEURL: http://zipkin-server:9411/
      RABBIT_URI: amqp://guest:guest@rabbitmq:5672
      SPRING_RABBITMQ_HOST: rabbitmq
      SPRING_ZIPKIN_SENDER_TYPE: rabbit

  api-gateway:
    image: in28min/mmv2-api-gateway:0.0.1-SNAPSHOT
    mem_limit: 700m
    ports:
      - "8765:8765"
    networks:
      - currency-network
    depends_on:
      - naming-server
      - rabbitmq
    environment:
      EUREKA.CLIENT.SERVICEURL.DEFAULTZONE: http://naming-server:8761/eureka
      SPRING.ZIPKIN.BASEURL: http://zipkin-server:9411/
      RABBIT_URI: amqp://guest:guest@rabbitmq:5672
      SPRING_RABBITMQ_HOST: rabbitmq
      SPRING_ZIPKIN_SENDER_TYPE: rabbit

  naming-server:
    image: in28min/mmv2-naming-server:0.0.1-SNAPSHOT
    mem_limit: 700m
    ports:
      - "8761:8761"
    networks:
      - currency-network

#docker run -p 9411:9411 openzipkin/zipkin:2.23

  zipkin-server:
    image: openzipkin/zipkin:2.23
    mem_limit: 300m
    ports:
      - "9411:9411"
    networks:
      - currency-network
    environment:
      RABBIT_URI: amqp://guest:guest@rabbitmq:5672
    depends_on:
      - rabbitmq
    restart: always #Restart if there is a problem starting up

  rabbitmq:
    image: rabbitmq:3.8.12-management
    mem_limit: 300m
    ports:
      - "5672:5672"
      - "15672:15672"
    networks:
      - currency-network


networks:
  currency-network:
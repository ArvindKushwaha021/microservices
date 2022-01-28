package com.in28minutes.microservices.currencyexchangeservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@RestController
public class CircuitBreakerController {
	
	private Logger logger = 
				LoggerFactory.getLogger(CircuitBreakerController.class);
	
	@GetMapping("/sample-api")
	//@Retry(name = "sample-api")
	//@Retry(name = "sample-api", fallbackMethod = "hardcodedResponse")
	//@CircuitBreaker(name = "default", fallbackMethod = "hardcodedResponse")
	@RateLimiter(name="default") //It will use the properties defined as default in app.properties
	
	//@Bulkhead(name="sample-api")

	public String sampleApi() {
		logger.info("Sample api call received");
//		ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://localhost:8080/some-dummy-url", 
//					String.class);
	//return forEntity.getBody();
		return "sample-api";
	}
	
	public String hardcodedResponse(Exception ex) {
		logger.info("returning hardcoded response");
		return "fallback-response";
	}
}

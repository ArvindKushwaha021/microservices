package com.in28minutes.microservices.currencyconversionservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name="currency-exchange", url="localhost:8000")
@FeignClient(name="currency-exchange")
public interface CurrencyExchangeProxy {
	//There is some probmle in feign So it is neccessary to pass (value="from") in PathVariable annotation
	/*@GetMapping("/currency-exchange/from/{from}/to/{to}")	
	public CurrencyConversion retrieveExchangeValue(@PathVariable String from, @PathVariable String to);*/
	
	
	@GetMapping("/currency-exchange/from/{from}/to/{to}")	
	public CurrencyConversion retrieveExchangeValue(@PathVariable(value="from") String from, @PathVariable(value="to") String to);
}

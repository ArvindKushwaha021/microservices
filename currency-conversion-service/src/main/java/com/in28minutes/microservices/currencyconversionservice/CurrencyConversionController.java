package com.in28minutes.microservices.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrencyConversionController {
	@Autowired
	private CurrencyExchangeProxy proxy;

	@GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversion retriveCurrnecyConversion(
				@PathVariable String from,
				@PathVariable String to,
				@PathVariable BigDecimal quantity) {
		
		Map<String, String> urlVariables=new HashMap<String,String>();
		urlVariables.put("from", from);
		urlVariables.put("to", to);
		ResponseEntity<CurrencyConversion> responseEntity = new RestTemplate().
				getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", 
				CurrencyConversion.class,urlVariables);
		CurrencyConversion currencyConversion = responseEntity.getBody();
		return new CurrencyConversion(1000L,
									  from,
									  to,
									  quantity,
									  currencyConversion.getConversionMultiple(),
									  quantity.multiply(currencyConversion.getConversionMultiple()),
									  currencyConversion.getEnvironment()+" :Rest");
		
	}
	
	
	@GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversion retriveCurrnecyConversionFeign(
				@PathVariable String from,
				@PathVariable String to,
				@PathVariable BigDecimal quantity) {
		
		CurrencyConversion currencyConversion = proxy.retrieveExchangeValue(from, to);
		return new CurrencyConversion(1000L,
									  from,
									  to,
									  quantity,
									  currencyConversion.getConversionMultiple(),
									  quantity.multiply(currencyConversion.getConversionMultiple()),
									  currencyConversion.getEnvironment()+" :Feign");		
	}
}

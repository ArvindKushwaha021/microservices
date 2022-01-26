package com.in28minutes.microservices.currencyexchangeservice.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.in28minutes.microservices.currencyexchangeservice.bean.CurrencyExchange;
import com.in28minutes.microservices.currencyexchangeservice.repository.CurrencyExchangeRepository;

@RestController
public class CurrencyExchangeController {
	
	@Autowired
	Environment environment;
	
	@Autowired
	CurrencyExchangeRepository CERepository;
	
	@GetMapping("/currency-exchange/from/{from}/to/{to}")	
	public CurrencyExchange retrieveExchanceValue(@PathVariable String from, @PathVariable String to) {
		
		String port = environment.getProperty("local.server.port");
		CurrencyExchange currencyExchange = CERepository.findByFromAndTo(from, to);
		if(currencyExchange==null) {
			throw new RuntimeException("Currency Exhange is not available for "+from+" "+"to"+to);
		}
		
		return new CurrencyExchange(1000L, from,to,currencyExchange.getConversionMultiple(),port);
	}
	
	

}

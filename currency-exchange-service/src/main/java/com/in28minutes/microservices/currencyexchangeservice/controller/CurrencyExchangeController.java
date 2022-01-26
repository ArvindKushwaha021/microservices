package com.in28minutes.microservices.currencyexchangeservice.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.in28minutes.microservices.currencyexchangeservice.bean.CurrencyExchange;

@RestController
public class CurrencyExchangeController {
	
	@Autowired
	Environment environment;
	
	@GetMapping("/currency-exchange/{from}/USD/{to}/INR")	
	public CurrencyExchange retrieveExchanceValue(@PathVariable String from, @PathVariable String to) {
		
		String port = environment.getProperty("local.server.port");
		return new CurrencyExchange(1000L, "USD","INR",BigDecimal.valueOf(60),port);
	}
	
	

}

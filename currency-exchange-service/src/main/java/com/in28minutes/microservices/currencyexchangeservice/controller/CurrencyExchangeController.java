package com.in28minutes.microservices.currencyexchangeservice.controller;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.in28minutes.microservices.currencyexchangeservice.bean.CurrencyExchange;

@RestController
public class CurrencyExchangeController {
	
	@GetMapping("/currency-exchange/{from}/USD/{to}/INR")	
	public CurrencyExchange retrieveExchanceValue(@PathVariable String from, @PathVariable String to) {
		
		return new CurrencyExchange(1000L, "USD","INR",BigDecimal.valueOf(60));
	}
	
	

}

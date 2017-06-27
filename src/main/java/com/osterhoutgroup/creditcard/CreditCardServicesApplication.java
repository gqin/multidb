package com.osterhoutgroup.creditcard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class CreditCardServicesApplication
    extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(CreditCardServicesApplication.class, args);
	}
}

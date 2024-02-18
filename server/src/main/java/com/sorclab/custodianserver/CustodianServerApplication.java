package com.sorclab.custodianserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CustodianServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(CustodianServerApplication.class, args);
	}
}

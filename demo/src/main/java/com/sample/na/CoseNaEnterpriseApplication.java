package com.sample.na;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class CoseNaEnterpriseApplication {
	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));
		SpringApplication.run(new Object[] { CoseNaEnterpriseApplication.class }, args);
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
}

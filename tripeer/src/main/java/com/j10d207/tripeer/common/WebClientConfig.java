package com.j10d207.tripeer.common;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Bean
	public WebClient webClient() {
		// return WebClient.create("https://tripeer.co.kr");
		return WebClient.create("http://localhost:3001");
	}
}

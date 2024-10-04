package com.j10d207.tripeer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableCaching
public class TripeerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TripeerApplication.class, args);
	}

}

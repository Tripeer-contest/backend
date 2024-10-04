package com.j10d207.tripeer.common;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

	@Bean
	public CacheManager cacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		CaffeineCache caches =new CaffeineCache("emailCodes",
				Caffeine.newBuilder()
					.expireAfterWrite(1800, TimeUnit.SECONDS)
					.maximumSize(100)
					.build());
		cacheManager.setCaches(List.of(caches));
		return cacheManager;
	}
}

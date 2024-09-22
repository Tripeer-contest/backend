package com.j10d207.tripeer.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {
	@Value("${spring.elasticsearch.uris}")
	private String uris;
	@Value("${spring.elasticsearch.password}")
	private String password;
	@Value("${spring.elasticsearch.username}")
	private String username;
	@Override
	public ClientConfiguration clientConfiguration() {
		return ClientConfiguration.builder()
			.connectedTo(uris)
			.usingSsl()
			.withBasicAuth(username, password)
			.build();
	}
}

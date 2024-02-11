package com.transfer.TransferClient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.reactive.function.client.WebClient;
import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration;

@SpringBootApplication
@Import(SpringDataRestConfiguration.class)
@EnableEurekaClient
public class TransferClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransferClientApplication.class, args);
	}

	@Bean
	@LoadBalanced
	public WebClient.Builder getWebClientBuilder() {
		return  WebClient.builder();
	}
}

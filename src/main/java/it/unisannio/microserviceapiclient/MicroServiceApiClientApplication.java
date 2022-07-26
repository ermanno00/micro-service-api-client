package it.unisannio.microserviceapiclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("it.unisannio.microserviceapiclient")
@EnableDiscoveryClient
public class MicroServiceApiClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroServiceApiClientApplication.class, args);
	}

}

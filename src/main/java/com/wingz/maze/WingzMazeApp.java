package com.wingz.maze;

import com.wingz.maze.client.WingzClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;

@SpringBootApplication
public class WingzMazeApp {

	public static void main(String[] args) throws Exception {
		new SpringApplication(WingzMazeApp.class).run(args);
	}

	@Configuration
	protected static class ClientConfiguration {
		@Value("${wingz.client.key}")
		private String apiKey;
		@Value("${wingz.client.url}")
		private String apiUrl;

		@Bean
		public WingzClient wingzClient() {
			return Feign.builder()
				.requestInterceptor(template -> template.header("X-Api-Key", apiKey))
				.decoder(new GsonDecoder())
				.encoder(new GsonEncoder())
				.target(WingzClient.class, apiUrl);
		}

	}

	@Configuration
	protected static class WebMvcConfig extends WebMvcConfigurerAdapter {
		@Override
		public void addViewControllers(ViewControllerRegistry registry) {
			registry.addViewController("/root").setViewName("/index.html");
		}
	}

}

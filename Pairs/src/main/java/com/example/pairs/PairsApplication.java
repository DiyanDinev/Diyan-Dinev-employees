package com.example.pairs;

import java.util.Properties;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class PairsApplication {

	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application
					 	  .sources(PairsApplication.class)
					 	  .properties(getProperties());
	}

	public static void main(String[] args) {
		SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(PairsApplication.class);
		springApplicationBuilder
								.sources(PairsApplication.class)
								.properties(getProperties())
								.run(args);
	}							

	static Properties getProperties() {
		Properties props = new Properties();
		props.put("spring.config.location", "classpath:PairsApplication/");
		return props;
	}

}

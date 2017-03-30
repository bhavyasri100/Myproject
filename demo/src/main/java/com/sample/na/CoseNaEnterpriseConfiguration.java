package com.sample.na;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.common.base.Predicate;
import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class CoseNaEnterpriseConfiguration {
	// @Bean
	// RestTemplate restTemplate() {
	// return new RestTemplate();
	// }

	@Bean
	public Docket swaggerSpringMvcPluginS() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("TripVehicleAPI").apiInfo(apiInfo()).select()
				.paths(paths()).build();
	}

	@SuppressWarnings("unchecked")
	private Predicate<String> paths() {

		return or(regex("/api/v1/vehicles.*"), regex("/api/v1/trips.*"), regex("/api/v1/message.*"));

	}

	@SuppressWarnings("deprecation")
	private ApiInfo apiInfo() {

//		return new ApiInfoBuilder().title("Bosch Vehicle Link API")
//				.description("API provides basic driver behaviour by VIN")

		return new ApiInfoBuilder().title("Bosch Vehicle LInk API")
				.description("API provides basic driver behaviour by VIN").contact("Bosch Connected Service")

				.license("Apache License Version 2.0")
				.licenseUrl("https://github.com/springfox/springfox/blob/master/LICENSE").version("1.0").build();
	}

	@Bean
	UiConfiguration uiConfig() {
		return new UiConfiguration(null);
	}
}

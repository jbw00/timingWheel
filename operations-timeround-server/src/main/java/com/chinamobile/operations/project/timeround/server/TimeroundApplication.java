package com.chinamobile.operations.project.timeround.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableEurekaClient
@EnableSwagger2
@EnableAsync
@SpringBootApplication
@ComponentScan({"com.chinamobile.operations.project.timeround.server"})
public class TimeroundApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimeroundApplication.class, args);
	}

	@Bean
	public Docket createRestApi() {

		ApiInfo apiInfo = new ApiInfoBuilder()
				.title("时间轮文档")
				.description("")
				.version("1.0")
				.build();

		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo)
				.select()
				//以扫描包的方式
				.apis(RequestHandlerSelectors.basePackage("com.chinamobile.operations.project.timeround.server"))
				.paths(PathSelectors.any())
				.build();
	}

}

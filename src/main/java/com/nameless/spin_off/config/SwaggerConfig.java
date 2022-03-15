package com.nameless.spin_off.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Profile("real")
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private String version;
    private String title;
    private String description;

    @Bean
    public Docket swaggerTest() {

        version = "1.0.0";
        title = "SpinOff API";
        description = "테스트용";

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any()) // 현재 RequestMapping으로 할당된 모든 URL 리스트를 추출
//                .apis(RequestHandlerSelectors.basePackage("com.nameless.spin_off.controller/api"))
                .paths(PathSelectors.ant("/api/**")) // 그중 /api/** 인 URL들만 필터링
                .build()
                .apiInfo(apiInfo(title, version, description));

    }
    private ApiInfo apiInfo(String title, String version, String description) {
        return new ApiInfoBuilder()
                .title(title)
                .version(version)
                .description(description)
                .build();
    }
}

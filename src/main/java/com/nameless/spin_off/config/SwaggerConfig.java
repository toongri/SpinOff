package com.nameless.spin_off.config;

import com.nameless.spin_off.config.member.MemberDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Profile({"real", "local"})
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
                .useDefaultResponseMessages(false)
                .ignoredParameterTypes(MemberDetails.class)
                .ignoredParameterTypes(Pageable.class)
                .select()
                .apis(RequestHandlerSelectors.any()) // 현재 RequestMapping으로 할당된 모든 URL 리스트를 추출
                .paths(PathSelectors.ant("/api/**")) // 그중 /api/** 인 URL들만 필터링
                .build()
                .apiInfo(apiInfo(title, version, description))
                .securityContexts(List.of(securityContext()))
                .securitySchemes(List.of(apiKey()));

    }
    private ApiInfo apiInfo(String title, String version, String description) {
        return new ApiInfoBuilder()
                .title(title)
                .version(version)
                .description(description)
                .build();
    }
    private ApiKey apiKey() {
        return new ApiKey("JWT", "X-AUTH-TOKEN", "header");
    }

    private SecurityContext securityContext() {
        return springfox
                .documentation
                .spi.service
                .contexts
                .SecurityContext
                .builder()
                .securityReferences(defaultAuth()).forPaths(PathSelectors.any()).build();
    }
    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return List.of(new SecurityReference("JWT", authorizationScopes));
    }
}

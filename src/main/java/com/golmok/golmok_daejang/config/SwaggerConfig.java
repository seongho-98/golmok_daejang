package com.golmok.golmok_daejang.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("골목대장 API")
                        .description("골목대장 서비스 REST API 명세서")
                        .version("v1.0.0"));
    }

    // springdoc의 QueryDSL 연동 빈이 Spring Boot 4.x와 충돌하여 제거
    @Bean
    public static BeanDefinitionRegistryPostProcessor suppressQuerydslSpringdocIntegration() {
        return (BeanDefinitionRegistry registry) -> {
            final String beanName = "queryDslQuerydslPredicateOperationCustomizer";
            if (registry.containsBeanDefinition(beanName)) {
                registry.removeBeanDefinition(beanName);
            }
        };
    }
}

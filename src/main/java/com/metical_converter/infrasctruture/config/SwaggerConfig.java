package com.metical_converter.infrasctruture.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class SwaggerConfig {
    @Value("${base.url}")
    private String baseUrl;
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API da Aplicação de Conversão de Moedas")
                        .version("1.0.0")
                        .description("Documentação da API para a aplicação de conversão de moedas.")
                        .contact(new Contact()
                                .name("Suporte")
                                .email("devmathusses@gmail.com")))
                .servers(List.of(
                        new Server().url(baseUrl).description("Ambiente de Testes")
                ));
    }
    @Bean
    public GroupedOpenApi httpApi() {
        return GroupedOpenApi.builder()
                .group("http")
                .pathsToMatch("/**")
                .build();
    }

}
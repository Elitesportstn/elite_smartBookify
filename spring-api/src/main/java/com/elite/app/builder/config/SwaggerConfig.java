package com.elite.app.builder.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Elite App Builder  API")
                        .version("1.0")
                        .description("API for building and managing apps  and apks")
                        .contact(new Contact()
                                .name("admin")
                                .email("elitesport.tn@gmail.com")
                                .url("https://elitesport.com")));
    }



}
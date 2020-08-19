package com.example.mirotest.configurations

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfiguration {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
                .components(Components())
                .info(Info()
                        .version("1.0.0")
                        .title("Widgets API")
                        .description("This service is responsible for manipulating Widgets on a Z plane")
                        .contact(Contact()
                                .email("info@miro.com")
                                .name("Miro")
                        )
                )
    }
}
package ru.utmn.roadmap.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI roadMapOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RoadMap API")
                        .description("Сервис расчёта дорожной карты по анкете мигранта")
                        .version("1.0.0")
                        .license(new License().name("MIT").url("https://opensource.org/licenses/MIT"))
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Учебный проект UTMN / ФМС")
                );
    }
}

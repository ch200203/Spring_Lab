package com.example.restfulwebservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class OpenApiConfig {
    private static final  Contact DEFAULT_CONTACT = new Contact().name("Hayden").url("https://naver.com").email("ch200203@sample.com");

    private static final Info DEFAULT_API_INFO = new Info().title("Demo API").contact(DEFAULT_CONTACT);
    // => 이런식으로도 생성가능하다.

    private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES = new HashSet<>(
            Arrays.asList("application/json", "application.xml")
    );

    @Bean
    public OpenAPI openAPI(){
        String appVersion = "1.0";
        Info info = new Info().title("Demo API").version(appVersion)
                .description("Spring Boot Web Application 예시.")
                .license(new License().name("아파치 라이센스 버전 2.0").url("http://http://www.apache.org/licenses/LICENSE-2.0"));

        return new OpenAPI()
                .components(new Components())
                .info(info);
    }

//    public OpenAPI openAPI2(){
//        return new OpenAPI().components(new Components()).info(DEFAULT_API_INFO); // 이런식으로도 사용 가능
//    }
}

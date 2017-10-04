package helloworld;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;

@Configuration
public class AudioRecoConfig {

    @Bean(name = "contentBotRestTemplate")
    RestTemplate contentBotRestTemplate() {
        final RestTemplate restTemplate = new RestTemplate();
        final DefaultUriTemplateHandler defaultUriTemplateHandler = new DefaultUriTemplateHandler();
        defaultUriTemplateHandler.setBaseUrl("http://localhost:8081");
        restTemplate.setUriTemplateHandler(defaultUriTemplateHandler);
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Content-Type", MediaType.TEXT_PLAIN_VALUE);
            request.getHeaders().add("Accept", MediaType.TEXT_PLAIN_VALUE);
            return execution.execute(request, body);
        });
        return restTemplate;
    }

}

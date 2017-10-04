package helloworld;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class ContentBotRepo {

    private RestTemplate restTemplate;

    ContentBotRepo(@Qualifier("contentBotRestTemplate") final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String ask(String question) {
        return restTemplate.postForObject("/question",question,String.class);
    }

}

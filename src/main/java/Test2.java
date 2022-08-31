import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class Test2 {
    public static void main(String[] args) throws JsonProcessingException {
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String string = "{\"text\": \"Hallo wie gewählt?\"}";
        HttpEntity<String> request =
                new HttpEntity<>(string, headers);

        ResponseEntity<List> res = template.postForEntity("http://localhost:8000/text", request, List.class);
        List<List<String>> body = res.getBody();
        for (List<String> s : body) {
            System.out.println(s);
        }
    }
}

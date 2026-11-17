package Sprint1.demo.service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

// Turns a prompt into a cover letter draft by asking the Groq AI service
@Service
public class CoverLetterService {

    private static final String AI_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String AI_MODEL = "openai/gpt-oss-20b";

    private final RestClient restClient;

    public CoverLetterService() {
        // Give up if the service takes too long, so the page never waits forever
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(20));
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    public String generateDraft(String prompt) {
        // The key is read from an environment variable, so it is never saved in the code
        String apiKey = System.getenv("GROQ_API_KEY");

        // No key set: return a fixed letter so the page can still be tested
        if (apiKey == null || apiKey.isBlank()) {
            return "Dear Hiring Manager,\n\n"
                 + "[Placeholder draft. Set GROQ_API_KEY to get a real AI letter.]\n\n"
                 + "Sincerely,\n"
                 + "Your name";
        }

        // The message we send: which model, and our prompt
        Map<String, Object> body = Map.of(
                "model", AI_MODEL,
                "messages", List.of(Map.of("role", "user", "content", prompt)));

        Map<?, ?> response = callAi(apiKey, body);

        // The letter text is in response -> choices -> first -> message -> content
        List<?> choices = (List<?>) response.get("choices");
        Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
        Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
        return (String) message.get("content");
    }

    // Sends the request. If the service is busy (5xx or too many requests), wait and try again, up to 3 times
    private Map<?, ?> callAi(String apiKey, Map<String, Object> body) {
        int maxAttempts = 3;
        for (int attempt = 1; ; attempt++) {
            try {
                return restClient.post()
                        .uri(AI_URL)
                        .header("Authorization", "Bearer " + apiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)
                        .retrieve()
                        .body(Map.class);
            } catch (HttpServerErrorException | HttpClientErrorException.TooManyRequests exception) {
                if (attempt >= maxAttempts) {
                    throw exception;
                }
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    throw exception;
                }
            }
        }
    }
}
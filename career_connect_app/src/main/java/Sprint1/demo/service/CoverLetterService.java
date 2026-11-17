package Sprint1.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpServerErrorException;

// Turns a prompt into a cover letter draft by asking the Gemini AI service
@Service
public class CoverLetterService {

    private static final String AI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent";

    private final RestClient restClient = RestClient.create();

    public String generateDraft(String prompt) {
        // The key is read from an environment variable, so it is never saved in the code
        String apiKey = System.getenv("GEMINI_API_KEY");

        // No key set: return a fixed letter so the page can still be tested
        if (apiKey == null || apiKey.isBlank()) {
            return "Dear Hiring Manager,\n\n"
                 + "[Placeholder draft. Set GEMINI_API_KEY to get a real AI letter.]\n\n"
                 + "Sincerely,\n"
                 + "Your name";
        }

        // The message we send: our prompt, inside the shape Gemini expects
        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt)))));

        // Send it to Gemini and get the answer back
         Map<?, ?> response = callGemini(apiKey, body);
        // The letter text is in response -> candidates -> first -> content -> parts -> first -> text
        List<?> candidates = (List<?>) response.get("candidates");
        Map<?, ?> firstCandidate = (Map<?, ?>) candidates.get(0);
        Map<?, ?> content = (Map<?, ?>) firstCandidate.get("content");
        List<?> parts = (List<?>) content.get("parts");
        Map<?, ?> firstPart = (Map<?, ?>) parts.get(0);
        return (String) firstPart.get("text");
}
    // Sends the request to Gemini. If Gemini is busy (a 5xx error), wait a moment and try again, up to 3 times
    private Map<?, ?> callGemini(String apiKey, Map<String, Object> body) {
        int maxAttempts = 3;
        for (int attempt = 1; ; attempt++) {
            try {
                return restClient.post()
                        .uri(AI_URL)
                        .header("x-goog-api-key", apiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)
                        .retrieve()
                        .body(Map.class);
            } catch (HttpServerErrorException exception) {
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
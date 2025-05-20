package book.dto.response;

public class GeminiResponse {
    private String answer;

    public GeminiResponse() {}

    public GeminiResponse(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}

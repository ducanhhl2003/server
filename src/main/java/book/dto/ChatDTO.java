//package book.dto;
//
//import java.util.List;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//class Message {
//    private String role; // "user", "assistant", "system"
//    private String content;
//}
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//class ChatGPTRequest {
//    private String model = "gpt-3.5-turbo";
//    private List<Message> messages;
//}
//
//@Data
//@NoArgsConstructor
//class ChatGPTResponse {
//    private List<Choice> choices;
//
//    @Data
//    public static class Choice {
//        private Message message;
//    }
//}

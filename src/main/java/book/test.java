package book;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

//import java.security.Key;
//import java.util.Base64;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//
//public class test {
//    public static void main(String[] args) {
//        String oldToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjgyMWYzYmM2NmYwNzUxZjc4NDA2MDY3OTliMWFkZjllOWZiNjBkZmIiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI1MDg2OTQ5NTQ0ODgtaTd1ajk4MDlkMDQzMXM4NWJzZDE0cnM3cG5vMW1nOHYuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI1MDg2OTQ5NTQ0ODgtaTd1ajk4MDlkMDQzMXM4NWJzZDE0cnM3cG5vMW1nOHYuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTM3MTg3NTk2NzI1NzgxMTgyNDgiLCJlbWFpbCI6ImR1b25nZHVjYW5oejIwQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhdF9oYXNoIjoiNjRyZlI3YzhmYmxpeHYxM1BxVGxUUSIsIm5hbWUiOiJBbmggRMawxqFuZyDEkOG7qWMiLCJwaWN0dXJlIjoiaHR0cHM6Ly9saDMuZ29vZ2xldXNlcmNvbnRlbnQuY29tL2EvQUNnOG9jTEIwb3BDQ3BTaUY5eG9uNkhGLUtMdUFpNkwta1VKVGk5c1A4SWZJWXVwZ0xaYzJoRT1zOTYtYyIsImdpdmVuX25hbWUiOiJBbmgiLCJmYW1pbHlfbmFtZSI6IkTGsMahbmcgxJDhu6ljIiwiaWF0IjoxNzQzMDU5NTc0LCJleHAiOjE3NDMwNjMxNzR9.jjdvLny68rozVu11NxroObZ3UXYgy477YpIJxZSLcIaIpELrypDwtbZkBt7KtJS_216ry1FhmjrsY_XenAyG1auaA_KkHOMntKc8M3RXsaBET1aOTCXQ70ycZLQe_4ioOI8_n5iEHBPdRkm9TGogjwlVkYUlMJzEH0dz3g7DRPHtwRueHw8Dvd8nKRyTZ64w9Qrge1bPn7rb4EcOYUX5ioq_In9rCMU45cpVp-HO2cs-wffE_m-KwJCkPSC6jHE9FZFJNivKUx35ZKisuKophB7PE1gGH87bTffj9sVnM4mxsuSqGY2GINkHm3BlQg-EVGJe_RUH9wOy2eFt99SlvA"; // Token RS256
//        String secretKey = "1TjXchw5FloESb63Kc+DFhTARvpWL4jUGCwfGWxuG5SIf/1y/LgJxHnMqaF6A/ij"; // Key mới
//        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArxLSY1w1gu+IzjVkBEqZXWcA1adZ15VmGpPYKpt8N/MXbgwICCy//iPVvuvSqetTvshwxEEK8ZcbmEyG/rcPiIBBoHYdtVb/cTlNR7JfT2ZOFKZUW1y3FBnZ2TTBHCgCJ9N7d+r6doQ+NI0GXOWzZh5Q9CPc9NDZoe8RfH+RE4m1RNGAukKThomofesSyw5OY92WxK9sfwTshmlK+J+wFB2OlN7xuwF3Rns/CJLdnajhf5XVMdNqEeSk3Fyoi72qWRQbDhfEhT5qcpkMX42BgWbmlom0ZPwPPhyyd9jrfFNN0BNgvF2kPD2eJ8qsaaUAZn4DBvcTpC5RhiwSY/AB8wIDAQAB";
//
//        // Bước 1: Giải mã payload (Bỏ qua chữ ký)
//        String[] parts = oldToken.split("\\.");
//        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
//
//        // Bước 2: Giải mã JWT có chữ ký bằng khóa công khai (Public Key)
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(publicKey)  // Thay bằng khóa công khai của bạn
//                .build()
//                .parseClaimsJws(oldToken)
//                .getBody();
//
//        // Bước 3: Tạo token mới với HS256
//        Key hmacKey = Keys.hmacShaKeyFor(secretKey.getBytes());
//
//        String newToken = Jwts.builder()
//                .setClaims(claims) // Giữ nguyên payload
//                .signWith(hmacKey, SignatureAlgorithm.HS256)
//                .compact();
//
//        System.out.println("New HS256 Token: " + newToken);
//    }
//}
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class test {
    public static void main(String[] args) throws Exception {
        // 🛑 Token gốc (RS256)
        String oldToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjgyMWYzYmM2NmYwNzUxZjc4NDA2MDY3OTliMWFkZjllOWZiNjBkZmIiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI1MDg2OTQ5NTQ0ODgtaTd1ajk4MDlkMDQzMXM4NWJzZDE0cnM3cG5vMW1nOHYuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI1MDg2OTQ5NTQ0ODgtaTd1ajk4MDlkMDQzMXM4NWJzZDE0cnM3cG5vMW1nOHYuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTM3MTg3NTk2NzI1NzgxMTgyNDgiLCJlbWFpbCI6ImR1b25nZHVjYW5oejIwQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhdF9oYXNoIjoiNjRyZlI3YzhmYmxpeHYxM1BxVGxUUSIsIm5hbWUiOiJBbmggRMawxqFuZyDEkOG7qWMiLCJwaWN0dXJlIjoiaHR0cHM6Ly9saDMuZ29vZ2xldXNlcmNvbnRlbnQuY29tL2EvQUNnOG9jTEIwb3BDQ3BTaUY5eG9uNkhGLUtMdUFpNkwta1VKVGk5c1A4SWZJWXVwZ0xaYzJoRT1zOTYtYyIsImdpdmVuX25hbWUiOiJBbmgiLCJmYW1pbHlfbmFtZSI6IkTGsMahbmcgxJDhu6ljIiwiaWF0IjoxNzQzMDU5NTc0LCJleHAiOjE3NDMwNjMxNzR9.jjdvLny68rozVu11NxroObZ3UXYgy477YpIJxZSLcIaIpELrypDwtbZkBt7KtJS_216ry1FhmjrsY_XenAyG1auaA_KkHOMntKc8M3RXsaBET1aOTCXQ70ycZLQe_4ioOI8_n5iEHBPdRkm9TGogjwlVkYUlMJzEH0dz3g7DRPHtwRueHw8Dvd8nKRyTZ64w9Qrge1bPn7rb4EcOYUX5ioq_In9rCMU45cpVp-HO2cs-wffE_m-KwJCkPSC6jHE9FZFJNivKUx35ZKisuKophB7PE1gGH87bTffj9sVnM4mxsuSqGY2GINkHm3BlQg-EVGJe_RUH9wOy2eFt99SlvA";  // Thay token RS256 thật của bạn vào đây
        
        // 🛑 Public Key dạng Base64 (để xác minh token)
        String publicKeyBase64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArxLSY1w1gu+IzjVkBEqZXWcA1adZ15VmGpPYKpt8N/MXbgwICCy//iPVvuvSqetTvshwxEEK8ZcbmEyG/rcPiIBBoHYdtVb/cTlNR7JfT2ZOFKZUW1y3FBnZ2TTBHCgCJ9N7d+r6doQ+NI0GXOWzZh5Q9CPc9NDZoe8RfH+RE4m1RNGAukKThomofesSyw5OY92WxK9sfwTshmlK+J+wFB2OlN7xuwF3Rns/CJLdnajhf5XVMdNqEeSk3Fyoi72qWRQbDhfEhT5qcpkMX42BgWbmlom0ZPwPPhyyd9jrfFNN0BNgvF2kPD2eJ8qsaaUAZn4DBvcTpC5RhiwSY/AB8wIDAQAB"; // Thay bằng Public Key thật

        // 🛑 Secret Key mới để ký HS256
        String secretKeyString = "1TjXchw5FloESb63Kc+DFhTARvpWL4jUGCwfGWxuG5SIf/1y/LgJxHnMqaF6A/ij"; // Thay bằng secret key đủ mạnh

        // 🔹 1. Chuyển đổi publicKey từ Base64 sang PublicKey object
        PublicKey publicKey = convertStringToPublicKey(publicKeyBase64);

        // 🔹 2. Giải mã token RS256
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(oldToken)
                .getBody();

        System.out.println("Decoded Claims: " + claims);

        // 🔹 3. Tạo JWT mới với HS256
        String newToken = Jwts.builder()
                .setClaims(claims) // Giữ nguyên payload
                .signWith(Keys.hmacShaKeyFor(secretKeyString.getBytes()), SignatureAlgorithm.HS256) // Dùng HS256
                .compact();

        System.out.println("\n🔑 New HS256 Token: " + newToken);
    }

    // Hàm chuyển đổi PublicKey từ chuỗi Base64
    private static PublicKey convertStringToPublicKey(String publicKeyBase64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }
}


//import java.math.BigInteger;
//import java.security.*;
//import java.security.interfaces.RSAPublicKey;
//import java.security.spec.*;
//import java.util.Base64;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//public class test {
//    public static PublicKey convertJwkToPublicKey(String jwkJson) throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode jwkNode = objectMapper.readTree(jwkJson);
//
//        BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(jwkNode.get("n").asText()));
//        BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(jwkNode.get("e").asText()));
//
//        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, exponent);
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//
//        return keyFactory.generatePublic(publicKeySpec);
//    }
//
//    public static void main(String[] args) throws Exception {
//        String jwkJson = "{"
//                + "\"kty\": \"RSA\","
//                + "\"n\": \"rxLSY1w1gu-IzjVkBEqZXWcA1adZ15VmGpPYKpt8N_MXbgwICCy__iPVvuvSqetTvshwxEEK8ZcbmEyG_rcPiIBBoHYdtVb_cTlNR7JfT2ZOFKZUW1y3FBnZ2TTBHCgCJ9N7d-r6doQ-NI0GXOWzZh5Q9CPc9NDZoe8RfH-RE4m1RNGAukKThomofesSyw5OY92WxK9sfwTshmlK-J-wFB2OlN7xuwF3Rns_CJLdnajhf5XVMdNqEeSk3Fyoi72qWRQbDhfEhT5qcpkMX42BgWbmlom0ZPwPPhyyd9jrfFNN0BNgvF2kPD2eJ8qsaaUAZn4DBvcTpC5RhiwSY_AB8w\","
//                + "\"e\": \"AQAB\","
//                + "\"alg\": \"RS256\""
//                + "}";
//
//        PublicKey publicKey = convertJwkToPublicKey(jwkJson);
//        System.out.println("Public Key: " + Base64.getEncoder().encodeToString(publicKey.getEncoded()));
//    }
//}

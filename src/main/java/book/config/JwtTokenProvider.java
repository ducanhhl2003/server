//package book.config;
//
//import java.util.Base64;
//import java.util.Date;
//import java.util.List;
//import java.util.logging.Logger;
//
//import javax.crypto.SecretKey;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Component;
//
//import book.CustomUserDetails;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//
//@Component
//public class JwtTokenProvider {
//    private static final Logger logger = Logger.getLogger(JwtTokenProvider.class.getName());
//    private final SecretKey secretKey;
//
//    @Value("${jwt.expiration}")
//    private long jwtExpiration;
//
//    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
//        try {
//            System.out.println("Original JWT Secret: " + secret);
//            byte[] decodedKey = Base64.getDecoder().decode(secret);
//            this.secretKey = Keys.hmacShaKeyFor(decodedKey);
//            System.out.println("Decoded Key Length: " + decodedKey.length);
//        } catch (Exception e) {
//            throw new IllegalArgumentException("JWT secret key is invalid or not Base64-encoded: " + e.getMessage());
//        }
//    }
//
//    public String generateToken(Authentication authentication) {
//        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
//            return null;
//        }
//
//        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + jwtExpiration);
//
//        // ✅ Thêm tiền tố "ROLE_" để Spring Security hiểu đúng
//        List<String> roles = user.getRoles().stream()
//                .map(role -> "ROLE_" + role)
//                .toList();
//
//        List<String> permissions = user.getPermissions();
//
//        return Jwts.builder()
//                .setSubject(user.getUsername())
//                .claim("name", user.getFullName())
//                .claim("authorities", roles)  // ✅ Gộp roles vào authorities
//                .claim("permissions", permissions)
//                .setIssuedAt(now)
//                .setExpiration(expiryDate)
//                .signWith(secretKey, SignatureAlgorithm.HS512)
//                .compact();
//    }
//
//    public String getUsernameFromToken(String token) {
//        return getClaimsFromToken(token).getSubject();
//    }
//
//    public List<String> getAuthoritiesFromToken(String token) {
//        Claims claims = getClaimsFromToken(token);
//        return claims.get("authorities", List.class);
//    }
//
//    private Claims getClaimsFromToken(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(secretKey)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
//            return true;
//        } catch (Exception e) {
//            logger.warning("Invalid JWT token: " + e.getMessage());
//            return false;
//        }
//    }
//}

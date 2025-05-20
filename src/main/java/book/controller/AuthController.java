package book.controller;

import java.text.ParseException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.nimbusds.jose.JOSEException;

import book.config.GoogleTokenValidator;
import book.config.JwtUtil;
import book.dto.UserDTO;
import book.dto.request.ApiResponse;
import book.dto.request.AuthenticationRequest;
import book.dto.request.IntrospectRequest;
import book.dto.request.RefreshRequest;
import book.dto.response.AuthenticationResponse;
import book.dto.response.IntrospectResponse;
import book.entity.UserEntity;
import book.exception.AppException;
import book.exception.ErrorCode;
import book.repository.UserRepository;
import book.service.impl.AuthenticationService;
import book.utils.LocalizationUtils;
import book.utils.MessageKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@CrossOrigin
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
	AuthenticationService authenticationService;

	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private GoogleTokenValidator googleTokenValidator;

	@Autowired
	private UserRepository userRepository;

	private final LocalizationUtils localizationUtils;
	private final OAuth2AuthorizedClientService authorizedClientService;

//    @GetMapping("/google")
//    public ResponseEntity<?> loginWithGoogle(@RequestParam String token) {
//        RestTemplate restTemplate = new RestTemplate();
//        String userInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + token;
//        ResponseEntity<Map> response = restTemplate.getForEntity(userInfoEndpoint, Map.class);
//
//        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
//            String email = (String) response.getBody().get("email");
//            String jwt = jwtService.generateToken(email);
//            return ResponseEntity.ok(Map.of("jwtToken", jwt, "userInfo", response.getBody()));
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google Token");
//    }

	 @PostMapping("/token")
	    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
	        var result = authenticationService.authenticate(request);
	        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
	    }
//	@PostMapping("/token")
//	ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
//		try {
//			var result = authenticationService.authenticate(request);
//			return ApiResponse.<AuthenticationResponse>builder()
//					.message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY)).result(result)
//					.build();
//		} catch (AppException e) {
//			// Truyền tham số lỗi vào `{0}`
//			String errorMessage = localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED,
//					e.getErrorCode().getMessage());
//
//			return ApiResponse.<AuthenticationResponse>builder().message(errorMessage).code(e.getErrorCode().getCode())
//					.build();
//		} catch (Exception e) {
//			// Xử lý lỗi hệ thống chung
//			String errorMessage = localizationUtils.getLocalizedMessage(MessageKeys.INTERNAL_SERVER_ERROR);
//
//			return ApiResponse.<AuthenticationResponse>builder().message(errorMessage)
//					.code(ErrorCode.INTERNAL_SERVER_ERROR.getCode()).build();
//		}
//	}

	@PostMapping("/introspect")
	ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
			throws ParseException, JOSEException {
		var result = authenticationService.introspect(request);
		return ApiResponse.<IntrospectResponse>builder().result(result).build();
	}

	@PostMapping("/refresh")
	ApiResponse<AuthenticationResponse> authenticate(@RequestBody RefreshRequest request)
			throws ParseException, JOSEException {
		var result = authenticationService.refreshToken(request);
		return ApiResponse.<AuthenticationResponse>builder().result(result).build();
	}

	@PostMapping("/details")
	public ResponseEntity<UserDTO> getUserDetails(@RequestHeader("Authorization") String token) {
		try {
			String extractedToken = token.replaceFirst("Bearer ", "");

			UserDTO user = authenticationService.getUserDetailsFromToken(extractedToken);

			return ResponseEntity.ok(user);
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

//    @GetMapping("/google")
//    public ResponseEntity<Map<String, Object>> googleLogin(@AuthenticationPrincipal OAuth2User oAuth2User) {
//        String email = oAuth2User.getAttribute("email");
//        String token = jwtUtil.generateTokenGoogle(email); // Gọi method từ instance
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("token", token);
//        response.put("email", email);
//        return ResponseEntity.ok(response);
//    }

//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null) {
//            new SecurityContextLogoutHandler().logout(request, response, authentication);
//        }
//        return ResponseEntity.ok().body(Map.of("message", "Logout successful"));
//    }
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    @PostMapping("/google-login")
//    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request) {
//        try {
//            String idToken = request.get("idToken");
//            GoogleIdToken.Payload payload = googleTokenValidator.verifyToken(idToken);
//            
//            // Trả về thông tin user
//            Map<String, Object> userInfo = Map.of(
//                    "email", payload.getEmail(),
//                    "name", payload.get("name"),
//                    "picture", payload.get("picture"),
//                    "email_verified", payload.getEmailVerified()
//            );
//            
//            return ResponseEntity.ok(userInfo);
//        } catch (Exception e) {
//            return ResponseEntity.status(401).body("Invalid Google ID Token");
//        }
//    }
	@PostMapping("/google-login")
	public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request) {
	    try {
	        String idToken = request.get("idToken");
	        GoogleIdToken.Payload payload = googleTokenValidator.verifyToken(idToken);

	        if (payload == null) {
	            return ResponseEntity.status(401).body("Invalid Google ID Token");
	        }

	        String email = payload.getEmail();
	        String googleUserId = payload.getSubject(); // Google ID dạng chuỗi

	        // Chuyển googleUserId thành số nguyên (bằng cách băm hashCode)
	        int numericId = Math.abs(googleUserId.hashCode()); 

	        // Tìm user theo email, nếu chưa có thì tạo mới với ID từ Google
	        UserEntity user = userRepository.findById(numericId).orElseGet(() -> {
	            UserEntity newUser = new UserEntity();
	            newUser.setId(numericId);  // Dùng Google ID đã băm
	            newUser.setUserName(email);
	            newUser.setEmail(email);
	            newUser.setFullName((String) payload.get("name"));
	            return userRepository.save(newUser);
	        });

	        String jwtToken = jwtUtil.generateTokenGoogle(email);

	        // Trả về thông tin user cho frontend
	        Map<String, Object> userInfo = Map.of(
	            "id", user.getId(),
	            "email", email,
	            "name", payload.get("name"),
	            "picture", payload.get("picture"),
	            "email_verified", payload.getEmailVerified(),
	            "token", jwtToken
	        );

	        return ResponseEntity.ok(userInfo);
	    } catch (Exception e) {
	        return ResponseEntity.status(401).body("Invalid Google ID Token");
	    }
	}



//	@PostMapping("/google-login")
//	public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request) {
//		try {
//			String idToken = request.get("idToken");
//			GoogleIdToken.Payload payload = googleTokenValidator.verifyToken(idToken);
//			if (payload == null) {
//				return ResponseEntity.status(401).body("Invalid Google ID Token");
//			}
//
//			String email = payload.getEmail();
//
//			UserEntity user = userRepository.findByEmail(email).orElseGet(() -> {
//				UserEntity newUser = new UserEntity();
//				newUser.setUserName(email);
//				newUser.setEmail(email);
//				newUser.setFullName((String) payload.get("name"));
//				return userRepository.save(newUser);
//			});
//
//			String jwtToken = jwtUtil.generateTokenGoogle(email);
//
//			Map<String, Object> userInfo = Map.of("email", email, "name", payload.get("name"), "picture",
//					payload.get("picture"), "email_verified", payload.getEmailVerified(), "token", jwtToken);
//
//			return ResponseEntity.ok(userInfo);
//		} catch (Exception e) {
//			return ResponseEntity.status(401).body("Invalid Google ID Token");
//		}
//	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			new SecurityContextLogoutHandler().logout(request, response, authentication);
		}
		return ResponseEntity.ok().body(Map.of("message", "Logout successful"));
	}
}

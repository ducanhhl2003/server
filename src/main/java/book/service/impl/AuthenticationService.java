package book.service.impl;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import book.dto.UserDTO;
import book.dto.request.AuthenticationRequest;
import book.dto.request.IntrospectRequest;
import book.dto.request.RefreshRequest;
import book.dto.response.AuthenticationResponse;
import book.dto.response.IntrospectResponse;
import book.entity.InvalidatedToken;
import book.entity.UserEntity;
import book.exception.AppException;
import book.exception.DataNotFoundException;
import book.exception.ErrorCode;
import book.repository.InvalidatedTokenRepository;
import book.repository.UserRepository;
import book.utils.MessageKeys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

	@Autowired
	InvalidatedTokenRepository invalidatedTokenRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	private ModelMapper modelMapper;
	@NonFinal
	@Value("${jwt.signerKey}")
	protected String SIGNER_KEY;

	@NonFinal
	@Value("${jwt.valid-duration}")
	protected long VALID_DURATION;

	@NonFinal
	@Value("${jwt.refreshable-duration}")
	protected long REFRESHABLE_DURATION;

	public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
		var token = request.getToken();
		boolean isValid = true;

		try {
			verifyToken(token, false);
		} catch (AppException e) {
			isValid = false;
		}

		return IntrospectResponse.builder().valid(isValid).build();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		var user = userRepository.findByUserName(request.getUsername());

		// Kiểm tra user có tồn tại không
		if (user == null) {
			throw new DataNotFoundException(MessageKeys.USER_NOT_FOUND);
		}

		// Kiểm tra password có hợp lệ không
		if (user.getPassWord() == null || !passwordEncoder.matches(request.getPassword(), user.getPassWord())) {
			throw new AppException(ErrorCode.UNAUTHENTICATED, "Invalid username or password.");
		}
		boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassWord());

		if (!authenticated)
			throw new AppException(ErrorCode.UNAUTHENTICATED);

		var token = generateToken(user);

		return AuthenticationResponse.builder().token(token).authenticated(true).build();
	}

	public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
		var signedJWT = verifyToken(request.getToken(), true);

		var jit = signedJWT.getJWTClaimsSet().getJWTID();
		var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

		InvalidatedToken invalidatedToken = InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

		invalidatedTokenRepository.save(invalidatedToken);

		var username = signedJWT.getJWTClaimsSet().getSubject();

		var user = userRepository.findByUserName(username);

		var token = generateToken(user);

		return AuthenticationResponse.builder().token(token).authenticated(true).build();
	}

	private String generateToken(UserEntity user) {
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

		JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder().subject(user.getUserName()).issuer("devteria.com")
				.issueTime(new Date())
				.expirationTime(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
				.jwtID(UUID.randomUUID().toString()).claim("scope", buildScope(user)).build();

		Payload payload = new Payload(jwtClaimsSet.toJSONObject());

		JWSObject jwsObject = new JWSObject(header, payload);

		try {
			jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
			return jwsObject.serialize();
		} catch (JOSEException e) {
			log.error("Cannot create token", e);
			throw new RuntimeException(e);
		}
	}

	private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
		JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

		SignedJWT signedJWT = SignedJWT.parse(token);

		Date expiryTime = (isRefresh)
				? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant()
						.plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
				: signedJWT.getJWTClaimsSet().getExpirationTime();

		var verified = signedJWT.verify(verifier);

		if (!(verified && expiryTime.after(new Date())))
			throw new AppException(ErrorCode.UNAUTHENTICATED);

		if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
			throw new AppException(ErrorCode.UNAUTHENTICATED);

		return signedJWT;
	}

	private String buildScope(UserEntity user) {
		StringJoiner stringJoiner = new StringJoiner(" ");

		if (!CollectionUtils.isEmpty(user.getRoles()))
			user.getRoles().forEach(role -> {
				stringJoiner.add("ROLE_" + role.getName());
				if (!CollectionUtils.isEmpty(role.getPermissions()))
					role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
			});

		return stringJoiner.toString();
	}

	public UserDTO getUserDetailsFromToken(String token) {
		try {
			SignedJWT signedJWT = this.verifyToken(token, false); // Gọi verifyToken chính xác
			String username = signedJWT.getJWTClaimsSet().getSubject();

			UserEntity user = userRepository.findByUserName(username);

			UserDTO resultDTO = modelMapper.map(user, UserDTO.class);
			if (!user.getRoles().isEmpty()) {
				resultDTO.setRoleName(user.getRoles().iterator().next().getName());
			} // Nếu chỉ lấy chuỗi tên roles

			return resultDTO;
		} catch (Exception e) {
			throw new AppException(ErrorCode.UNAUTHENTICATED, "Token không hợp lệ!");
		}
	}

}

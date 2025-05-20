package book.service.impl;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import book.dto.OAuth2UserInfo;
import book.entity.UserEntity;
import book.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
	private final UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

		String provider = userRequest.getClientRegistration().getRegistrationId();
		OAuth2UserInfo userInfo = new OAuth2UserInfo(oAuth2User, provider);

		UserEntity user = userRepository.findByEmail(userInfo.getEmail()).orElseGet(() -> {

			UserEntity newUser = new UserEntity();
			newUser.setUserName(userInfo.getEmail());
			newUser.setEmail(userInfo.getEmail());
			newUser.setFullName(userInfo.getName());
			return userRepository.save(newUser);
		});

		return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("USER")),
				oAuth2User.getAttributes(), "email");
	}
}

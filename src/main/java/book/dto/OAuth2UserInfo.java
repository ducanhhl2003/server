package book.dto;

import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuth2UserInfo {
	private String email;
	private String name;

	public OAuth2UserInfo(OAuth2User oAuth2User, String provider) {
		this.email = oAuth2User.getAttribute("email");
		this.name = oAuth2User.getAttribute("name");

	}
}

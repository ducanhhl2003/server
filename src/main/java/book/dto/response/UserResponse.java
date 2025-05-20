package book.dto.response;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import book.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserResponse {
	private Integer id;

	@JsonProperty(value = "user_name")
	private String username;

	@JsonProperty(value = "email")
	private String email;

	@JsonProperty(value = "phone_number")
	private String phoneNumber;

	@JsonProperty(value = "full_name")
	private String fullName;

	@JsonProperty(value = "address")
	@NotBlank(message = "Address is required")
	private String address;

	@JsonProperty(value = "role")
	private List<RoleResponse> roleGroupResponses;

	public static UserResponse fromUser(final UserEntity user) {
		List<RoleResponse> roleGroupResponses = new ArrayList<>();
		if (user.getRoles() != null) {
			roleGroupResponses = user.getRoles().stream().map(RoleResponse::fromRoleGroup).toList();
		}
		return UserResponse.builder().id(user.getId()).username(user.getUserName()).email(user.getEmail())
				.fullName(user.getFullName()).address(user.getAddress()).roleGroupResponses(roleGroupResponses).build();
	}

}

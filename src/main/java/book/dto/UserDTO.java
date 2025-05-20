package book.dto;

import java.util.Set;
import java.util.stream.Collectors;

import book.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO extends AbstractDTO<UserDTO> {
	private Integer id;

	@NotBlank(message = "Username không được để trống")
	@Size(min = 5, max = 20, message = "Username phải từ 5 - 20 ký tự")
	private String userName;
	private String fullName;
	private String address;

//	@Email(message = "Email không hợp lệ")
//    @NotBlank(message = "Email không được để trống")
	private String email;

	@NotBlank(message = "Mật khẩu không được để trống")
	@Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
	private String passWord;
	@NotBlank(message = "Mật khẩu không được để trống")
	@Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
	private String retypePassword;

	private Boolean isAccepted;

	@Pattern(regexp = "^\\d{10,11}$", message = "Số điện thoại không hợp lệ")
	private String phone;
	private Set<String> roles;

	public UserDTO(UserEntity user) {
		this.id = user.getId();
		this.userName = user.getUserName();
		this.fullName = user.getFullName();
		this.address = user.getAddress();
		this.email = user.getEmail();
		this.phone = user.getPhone();
		this.passWord = user.getPassWord();
		this.retypePassword = user.getRetypePassword();
		this.isAccepted = user.getIsAccepted();

		if (user.getRoles() != null) {
			this.roles = user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet());
		}
	}

	public String getRoleName() {
		return (roles != null && !roles.isEmpty()) ? roles.iterator().next() : null;
	}

	public void setRoleName(String roleName) {
		this.roles = Set.of(roleName);
	}
}

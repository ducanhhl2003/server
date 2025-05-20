package book.dto;

import java.util.Set;
import java.util.stream.Collectors;

import book.entity.PermissionEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO extends AbstractDTO<PermissionDTO> {
	private Integer id;
	@NotBlank(message = "Mã quyền không được để trống")
	@Size(max = 50, message = "Mã quyền không được vượt quá 50 ký tự")
	private String code;
	@NotBlank(message = "Tên quyền không được để trống")
	@Size(max = 100, message = "Tên quyền không được vượt quá 100 ký tự")
	private String name;
	private Set<String> roles;

	public PermissionDTO(PermissionEntity permission) {
		this.id = permission.getId();
		this.code = permission.getCode();
		this.name = permission.getName();
		if (permission.getRoles() != null) {
			this.roles = permission.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet());
		}
	}

	public String getRoleName() {
		return (roles != null && !roles.isEmpty()) ? roles.iterator().next() : null;
	}

	public void setRoleName(String roleName) {
		this.roles = Set.of(roleName);
	}
}

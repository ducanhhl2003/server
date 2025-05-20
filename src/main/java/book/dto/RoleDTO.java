package book.dto;

import java.util.Set;
import java.util.stream.Collectors;

import book.entity.PermissionEntity;
import book.entity.RoleEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO extends AbstractDTO<RoleDTO> {
	private Integer id;

	@NotBlank(message = "Tên vai trò không được để trống")
	@Size(min = 3, max = 50, message = "Tên vai trò phải có từ 3 đến 50 ký tự")
	private String name;
	private Set<String> users;
	private Set<String> permissions;

	@NotNull(message = "Danh sách quyền không được null")
	public RoleDTO(RoleEntity role) {
		this.id = role.getId();
		this.name = role.getName();

		if (role.getUsers() != null) {
			this.users = role.getUsers().stream().map(user -> user.getUserName()).collect(Collectors.toSet());
		}

		this.permissions = role.getPermissions().stream().map(PermissionEntity::getName).collect(Collectors.toSet());
	}

	public String getUserName() {
		return (users != null && !users.isEmpty()) ? users.iterator().next() : null;
	}

	public void setUserName(String userName) {
		this.users = Set.of(userName);
	}

	public String getPermissionName() {
		return (permissions != null && !permissions.isEmpty()) ? permissions.iterator().next() : null;
	}

	public void setPermissionName(String name) {
		this.permissions = Set.of(name);
	}
}

package book.dto.response;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import book.entity.PermissionEntity;
import book.entity.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionResponse {
	private Integer id;

	@JsonProperty(value = "permission_name")
	private String permissionName;

	@JsonProperty(value = "description")
	private String description;

	@JsonProperty(value = "role_names")
	private List<String> roleNames;

	public static PermissionResponse fromFunction(PermissionEntity permission) {
		List<String> roleName = new ArrayList<>();
		if (permission.getRoles() != null) {
			roleName = permission.getRoles().stream().map(RoleEntity::getName).toList();
		}
		return PermissionResponse.builder().id(permission.getId()).permissionName(permission.getName())
				.roleNames(roleName).build();
	}
}

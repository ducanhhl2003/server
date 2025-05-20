package book.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import book.entity.PermissionEntity;
import book.entity.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RoleResponse {
	private Integer id;

	@JsonProperty(value = "role_name")
	private String roleName;

	private String description;

	@JsonProperty(value = "permission_names")
	private List<String> permissionNames;

	public static RoleResponse fromRoleGroup(final RoleEntity role) {
		List<String> permissionNames = role.getPermissions().stream().map(PermissionEntity::getName).toList();

		return RoleResponse.builder().id(role.getId()).roleName(role.getName()).permissionNames(permissionNames)
				.build();
	}
}

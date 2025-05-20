package book;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import book.entity.PermissionEntity;
import book.entity.RoleEntity;
import book.entity.UserEntity;

public class CustomUserDetails implements UserDetails {
	private final UserEntity user;

	public CustomUserDetails(UserEntity user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> roleAuthorities = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())).collect(Collectors.toList());
		List<GrantedAuthority> permissionAuthorities = user.getRoles().stream()
				.flatMap(role -> role.getPermissions().stream())
				.map(permission -> new SimpleGrantedAuthority(permission.getCode())).collect(Collectors.toList());

		return Stream.concat(roleAuthorities.stream(), permissionAuthorities.stream()).collect(Collectors.toList());
	}

	public UserEntity getUserEntity() {
		return user;
	}

	@Override
	public String getPassword() {
		return user.getPassWord();
	}

	@Override
	public String getUsername() {
		return user.getUserName();
	}

	public String getFullName() {
		return user.getFullName();
	}

	public List<String> getRoles() {
		return user.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toList());
	}

	public List<String> getPermissions() {
		return user.getRoles().stream().flatMap(role -> role.getPermissions().stream()).map(PermissionEntity::getCode)
				.distinct().collect(Collectors.toList());
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}

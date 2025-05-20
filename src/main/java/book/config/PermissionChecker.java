package book.config;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import book.CustomUserDetails;

@Service("permissionChecker")
public class PermissionChecker {

    public boolean hasPermission(Authentication authentication, String permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails customUser) {
            return customUser.getPermissions().contains(permission);
        }

        return false;
    }
}

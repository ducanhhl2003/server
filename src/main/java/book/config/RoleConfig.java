package book.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import book.entity.PermissionEntity;
import book.repository.RoleRepository;
import book.utils.UrlFormatUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RoleConfig {
    private final Map<String, String> roles = new HashMap<>();
    private final RoleRepository roleRepository;

    public RoleConfig(@Value("classpath:role.properties") Resource resource, RoleRepository roleRepository) throws IOException {
        this.roleRepository = roleRepository;
        Properties properties = new Properties();
        properties.load(resource.getInputStream());

        for (String key : properties.stringPropertyNames()) {
            String function = properties.getProperty(key).trim();
            roles.put(key.trim(), function);
            log.info("🔹 Loaded role mapping: {} -> {}", key, function);
        }
    }

    /**
     * Lấy quyền (function) tương ứng với URI API từ role.properties
     */
    public String getFunctionByUri(String uri) {
        String formattedUri = UrlFormatUtil.formatUrl(uri);
        log.info("🔹 Original URI: {}", uri);
        log.info("🔹 Formatted URI: {}", formattedUri);

        // Tìm theo URI gốc
        String function = roles.get(formattedUri);

        // Nếu không có, thử thay "/" bằng "."
        String dottedUri = formattedUri.replace("/", ".");
        if (function == null) {
            function = roles.get(dottedUri);
            log.info("🔹 Trying dotted URI: {}", dottedUri);
        }

        // Nếu vẫn không có, bỏ dấu "." ở đầu rồi thử lại
        if (function == null && dottedUri.startsWith(".")) {
            String noLeadingDotUri = dottedUri.substring(1);
            function = roles.get(noLeadingDotUri);
            log.info("🔹 Trying no-leading-dot URI: {}", noLeadingDotUri);
        }

        log.info("🔹 Function for URI [{}]: {}", formattedUri, function);
        return function != null ? function : "";
    }





    /**
     * Kiểm tra role có quyền tương ứng hay không
     */
    public boolean roleGroupHasFunction(String roleName, String permissionName) {
        if (!roleName.startsWith("ROLE_")) {
            log.warn("⚠️ Invalid role format: {}", roleName);
            return false;
        }

        String actualRole = roleName.replace("ROLE_", "").trim();
        List<String> permissions = roleRepository.findPermissionsByName(actualRole)
                .stream()
                .map(PermissionEntity::getName)
                .toList();

        boolean hasPermission = permissions.contains(permissionName.trim());

        log.info("🔹 Role: {}", roleName);
        log.info("🔹 Permissions from DB: {}", permissions);
        log.info("🔹 Required Permission: {}", permissionName);
        log.info("🔹 Has Permission: {}", hasPermission);

        return hasPermission;
    }
}

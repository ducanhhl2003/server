package book.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @GetMapping
    public String debugRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "User Roles: " + authentication.getAuthorities()
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }
}


package pt.isep.desofs.vendnet.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> dashboard() {
        return ResponseEntity.ok(Map.of(
                "message", "Welcome to the admin dashboard",
                "auth", "hasRole('ADMIN') — single role"
        ));
    }

    @GetMapping("/users")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Map<String, String>> listUsers() {
        return ResponseEntity.ok(Map.of(
                "message", "Admin-only user list endpoint",
                "auth", "@Secured(\"ROLE_ADMIN\") — single role (secured)"
        ));
    }

    @GetMapping("/reports")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Map<String, String>> reports() {
        return ResponseEntity.ok(Map.of(
                "message", "Reports accessible by admin and user",
                "auth", "hasAnyRole('ADMIN', 'USER') — role collection"
        ));
    }
}

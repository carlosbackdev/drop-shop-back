package com.motogear.dropshopback.users.web;

import com.motogear.dropshopback.users.dtd.UserMeResponse;
import com.motogear.dropshopback.users.domain.User;
import com.motogear.dropshopback.users.service.UserService;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para operaciones relacionadas con usuarios
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Endpoints para gestión de usuarios")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(
            summary = "Obtener usuario actual",
            description = "Retorna la información del usuario autenticado mediante JWT",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<UserMeResponse> getCurrentUser() {
        User user = userService.getCurrentUser();

        UserMeResponse response = UserMeResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .photoUrl(user.getPhotoUrl())
                .authProvider(user.getAuthProvider())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/name/{userId}")
    @Operation(
            summary = "Obtener nombre del usuario actual",
            description = "Retorna el nombre completo del usuario para reseñas"
    )
    public ResponseEntity<Object> getCurrentUserName(@PathVariable Long userId) {
        String fullName = userService.getName(userId);
        if (fullName == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Json.pretty(fullName));
    }
    @PostMapping("/chage/password")
    @Operation(
            summary = "Cambiar contraseña del usuario",
            description = "Permite al usuario autenticado cambiar su contraseña",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Object> changePassword(@RequestParam String email, @RequestParam String newPassword) {
         if(!userService.existsByEmail(email)) {
              return ResponseEntity.ok(Json.pretty("Usuario no encontrado"));
         }
         userService.chagePassword(newPassword);
            return ResponseEntity.ok(Json.pretty("Contraseña cambiada exitosamente"));
    }
}


package com.motogear.dropshopback.users.dtd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para autenticación (login/registro)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de autenticación con token JWT")
public class AuthResponse {

    @Schema(description = "Token JWT para autenticación", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "ID del usuario", example = "1")
    private Long userId;

    @Schema(description = "Email del usuario", example = "usuario@example.com")
    private String email;

    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String fullName;

    @Schema(description = "Rol del usuario", example = "USER")
    private String role;

    @Schema(description = "URL de la foto de perfil", example = "https://lh3.googleusercontent.com/a/...")
    private String photoUrl;
}


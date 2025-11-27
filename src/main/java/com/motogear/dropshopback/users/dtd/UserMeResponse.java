package com.motogear.dropshopback.users.dtd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para información del usuario actual
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información del usuario actual")
public class UserMeResponse {

    @Schema(description = "ID del usuario", example = "1")
    private Long id;

    @Schema(description = "Email del usuario", example = "usuario@example.com")
    private String email;

    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String fullName;

    @Schema(description = "Rol del usuario", example = "USER")
    private String role;

    @Schema(description = "URL de la foto de perfil", example = "https://lh3.googleusercontent.com/a/...")
    private String photoUrl;

    @Schema(description = "Proveedor de autenticación", example = "GOOGLE")
    private String authProvider;
}


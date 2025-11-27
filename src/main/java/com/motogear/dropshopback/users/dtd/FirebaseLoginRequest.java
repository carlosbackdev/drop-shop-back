package com.motogear.dropshopback.users.dtd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de login con Firebase/Google
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud de login con Firebase/Google Authentication")
public class FirebaseLoginRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Schema(description = "Email del usuario obtenido de Firebase/Google", example = "usuario@gmail.com")
    private String email;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String fullName;

    @Schema(description = "Token de Firebase para validación (opcional)", example = "eyJhbGciOiJSUzI1NiIsImtpZCI6...")
    private String firebaseToken;

    @Schema(description = "UID de Firebase del usuario (opcional)", example = "firebase-uid-123456")
    private String firebaseUid;

    @Schema(description = "URL de la foto de perfil (opcional)", example = "https://lh3.googleusercontent.com/a/...")
    private String photoUrl;
}


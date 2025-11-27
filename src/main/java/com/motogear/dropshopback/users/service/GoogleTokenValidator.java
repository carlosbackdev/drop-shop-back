package com.motogear.dropshopback.users.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Servicio para validar tokens de Google (OPCIONAL)
 * Para usar este servicio, debes añadir la dependencia google-api-client al pom.xml
 */
@Service
@Slf4j
public class GoogleTokenValidator {

    @Value("${google.client.id}")
    private String googleClientId;

    /**
     * Valida un token de Google ID y extrae la información del usuario
     *
     * @param idTokenString Token de Google obtenido del frontend
     * @return Información del usuario extraída del token
     * @throws RuntimeException si el token es inválido
     */
    public GoogleUserInfo validateToken(String idTokenString) {
        if (googleClientId == null || googleClientId.isEmpty()) {
            log.warn("⚠️ google.client.id no configurado. Validación de token de Google deshabilitada.");
            return null; // Si no está configurado, no validamos
        }

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                // Extraer información del usuario desde el token de Google
                String email = payload.getEmail();
                boolean emailVerified = payload.getEmailVerified();
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");
                String googleId = payload.getSubject();

                if (!emailVerified) {
                    throw new RuntimeException("El email de Google no está verificado");
                }

                log.info("✅ Token de Google validado para: {}", email);

                return GoogleUserInfo.builder()
                        .email(email)
                        .name(name)
                        .photoUrl(pictureUrl)
                        .googleId(googleId)
                        .emailVerified(emailVerified)
                        .build();
            } else {
                throw new RuntimeException("Token de Google inválido");
            }
        } catch (Exception e) {
            log.error("❌ Error al validar token de Google: {}", e.getMessage());
            throw new RuntimeException("No se pudo validar el token de Google: " + e.getMessage());
        }
    }

    /**
     * Clase interna para encapsular la información del usuario de Google
     */
    @lombok.Data
    @lombok.Builder
    public static class GoogleUserInfo {
        private String email;
        private String name;
        private String photoUrl;
        private String googleId;
        private boolean emailVerified;
    }
}


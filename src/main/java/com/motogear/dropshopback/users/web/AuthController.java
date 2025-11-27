package com.motogear.dropshopback.users.web;

import com.motogear.dropshopback.users.dtd.AuthResponse;
import com.motogear.dropshopback.users.dtd.LoginRequest;
import com.motogear.dropshopback.users.dtd.RegisterRequest;
import com.motogear.dropshopback.users.components.JwtTokenProvider;
import com.motogear.dropshopback.users.domain.User;
import com.motogear.dropshopback.users.dtd.FirebaseLoginRequest;
import com.motogear.dropshopback.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para autenticación (registro y login)
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para registro y login de usuarios")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    /**
     * Registra un nuevo usuario en el sistema
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario", description = "Crea una nueva cuenta de usuario y retorna un token JWT")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // Registrar el usuario
            System.out.println("Registrando usuario: " + request);
            User user = userService.register(request);

            // Cargar UserDetails y generar token
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            String token = jwtTokenProvider.generateToken(userDetails, user.getId(), user.getRole().name());

            // Construir respuesta
            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .userId(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .role(user.getRole().name())
                    .photoUrl(user.getPhotoUrl())
                    .build();

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Inicia sesión con email y contraseña
     */
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y retorna un token JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            // Autenticar credenciales
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // Obtener el usuario
            User user = userService.findByEmail(request.getEmail());

            // Generar token
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            String token = jwtTokenProvider.generateToken(userDetails, user.getId(), user.getRole().name());

            // Construir respuesta
            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .userId(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .role(user.getRole().name())
                    .photoUrl(user.getPhotoUrl())
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
    }

    /**
     * Login o registro con Firebase/Google Authentication
     * Busca o crea el usuario por email y genera un JWT
     */
    @PostMapping("/firebase-login")
    @Operation(
            summary = "Login/Registro con Firebase/Google",
            description = "Autentica o registra un usuario mediante Firebase/Google Authentication y retorna un JWT del backend"
    )
    public ResponseEntity<AuthResponse> firebaseLogin(@Valid @RequestBody FirebaseLoginRequest request) {
        try {
            // TODO: (Opcional) Validar el token de Firebase aquí
            // FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(request.getFirebaseToken());
            // String firebaseEmail = decodedToken.getEmail();
            // if (!firebaseEmail.equals(request.getEmail())) {
            //     throw new BadCredentialsException("Token de Firebase inválido");
            // }

            // Registrar o actualizar usuario con Firebase
            User user = userService.registerOrLoginWithFirebase(
                    request.getEmail(),
                    request.getFullName(),
                    request.getFirebaseUid(),
                    request.getPhotoUrl()
            );

            // Generar token JWT de nuestro backend
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            String token = jwtTokenProvider.generateToken(userDetails, user.getId(), user.getRole().name());

            // Construir respuesta
            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .userId(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .role(user.getRole().name())
                    .photoUrl(user.getPhotoUrl())
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error en autenticación con Firebase: " + e.getMessage());
        }
    }
}


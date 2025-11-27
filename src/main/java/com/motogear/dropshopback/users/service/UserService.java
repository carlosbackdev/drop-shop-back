package com.motogear.dropshopback.users.service;

import com.motogear.dropshopback.users.dtd.RegisterRequest;
import com.motogear.dropshopback.users.domain.User;
import com.motogear.dropshopback.users.domain.UserRole;
import com.motogear.dropshopback.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Servicio para la gestión de usuarios
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con ID: " + id));
    }
    @Transactional
    public User register(RegisterRequest request) {
        // Validar que el email no esté duplicado
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado: " + request.getEmail());
        }

        // Crear el usuario
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(UserRole.USER)
                .build();

        // Guardar y retornar
        return userRepository.save(user);
    }
    @Transactional
    public String getName(Long id) {
       Optional <User> user = userRepository.findById(id);
        return user.map(User::getFullName).orElse(null);
    }

   @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User chagePassword( String newPassword) {
        User user = getCurrentUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getPrincipal().equals("anonymousUser")) {
            throw new UsernameNotFoundException("No hay usuario autenticado");
        }

        String email = authentication.getName();
        return findByEmail(email);
    }

    @Transactional
    public User registerOrLoginWithFirebase(String email, String fullName, String firebaseUid, String photoUrl) {
        // Buscar si el usuario ya existe
        var existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            // Usuario existe: actualizar información de Firebase si es necesario
            User user = existingUser.get();

            boolean needsUpdate = false;

            // Actualizar firebaseUid si no lo tiene
            if (firebaseUid != null && user.getFirebaseUid() == null) {
                user.setFirebaseUid(firebaseUid);
                needsUpdate = true;
            }

            // Actualizar foto si cambió
            if (photoUrl != null && !photoUrl.equals(user.getPhotoUrl())) {
                user.setPhotoUrl(photoUrl);
                needsUpdate = true;
            }

            // Actualizar nombre si cambió
            if (fullName != null && !fullName.equals(user.getFullName())) {
                user.setFullName(fullName);
                needsUpdate = true;
            }

            // Marcar como GOOGLE si no tenía provider
            if (user.getAuthProvider() == null || user.getAuthProvider().equals("LOCAL")) {
                user.setAuthProvider("GOOGLE");
                needsUpdate = true;
            }

            if (needsUpdate) {
                return userRepository.save(user);
            }

            return user;
        } else {
            // Usuario no existe: crear uno nuevo
            User newUser = User.builder()
                    .email(email)
                    .fullName(fullName)
                    .password(passwordEncoder.encode(java.util.UUID.randomUUID().toString())) // Password random (no se usará)
                    .role(UserRole.USER)
                    .firebaseUid(firebaseUid)
                    .photoUrl(photoUrl)
                    .authProvider("GOOGLE")
                    .build();

            return userRepository.save(newUser);
        }
    }
}


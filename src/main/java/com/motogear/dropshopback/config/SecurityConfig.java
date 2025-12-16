package com.motogear.dropshopback.config;

import java.util.List;

import com.motogear.dropshopback.users.components.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Value("${scraping.api.url}")
    private String apiScrapUrl;

    @Value("${front.url}")
    private String frontUrl;

    @Value("${front.admin.security.url}")
    private String frontAdminUrl;
    @Value("${front.url.www}")
    private String frontWwwUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/payments/success",
                                "/api/auth/firebase-login",
                                "/api/payments/stripe/webhook"
                        ).permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/api-docs/**",
                                "/h2-console/**"
                        ).permitAll()

                        // Endpoints de productos, categorías, admin orders, etc.
                        .requestMatchers(
                                "/api/products/**",
                                "/api/categories/**",
                                "/api/track/admin/**",
                                "/api/review/list/**",
                                "/api/users/name/**",
                                "/api/orders/admin/**",
                                "/api/products-images/get-image/**",
                                "/api/best/**",
                                "/api/home-banners/**",
                                "/api/blog/**"
                        ).access((authentication, context) -> {
                            HttpServletRequest request = context.getRequest();
                            String path = request.getRequestURI();

                            // Si es un endpoint de admin, lo restringimos por ORIGIN del front admin
                            if (path.contains("/admin/")) {
                                String origin = request.getHeader("Origin");
                                boolean allowed = frontAdminUrl.equals(origin) || "http://localhost:8080".equals(origin);
                                return new org.springframework.security.authorization.AuthorizationDecision(allowed);
                            }

                            // Resto de estos endpoints: permitidos (ya filtrará más abajo /api/** por origin si aplica)
                            return new org.springframework.security.authorization.AuthorizationDecision(true);
                        })

                        // Endpoints de usuario requieren autenticación (JWT)
                        .requestMatchers("/api/users/**").authenticated()

                        // Regla general para todos los /api/** basada en ORIGIN
                        .requestMatchers("/api/**").access((authentication, context) -> {
                            HttpServletRequest request = context.getRequest();
                            String origin = request.getHeader("Origin");

                            boolean allowed =
                                    apiScrapUrl.equals(origin) ||   // http://aliexpres-scrapping:3001
                                            frontAdminUrl.equals(origin) || // http://localhost:5173
                                            frontUrl.equals(origin) ||        // http://localhost:8081
                                            frontWwwUrl.equals(origin);        // http://localhost:8080

                            return new org.springframework.security.authorization.AuthorizationDecision(allowed);
                        })

                        // Cualquier otra cosa autenticada
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permitimos los 3 orígenes conocidos
        configuration.setAllowedOrigins(List.of(
                apiScrapUrl,
                frontUrl,
                frontAdminUrl,
                frontWwwUrl,
                "http://localhost:8080"
        ));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

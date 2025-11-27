package com.motogear.dropshopback.config;

import java.util.List;

import com.motogear.dropshopback.users.components.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private static final IpAddressMatcher LOCALHOST_IPV4 = new IpAddressMatcher("127.0.0.1");
    private static final IpAddressMatcher LOCALHOST_IPV6 = new IpAddressMatcher("::1");

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Value("${api.scrap.security.url}")
    private String apiScrapUrl;

    @Value("${front.security.url}")
    private String frontUrl;

    @Value("${front.admin.security.url}")
    private String frontAdminUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
            .authorizeHttpRequests(auth -> auth
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
                .requestMatchers(
                    "/api/products/**",
                    "/api/categories/**",
                    "/api/track/admin/**",
                    "/api/review/list/**",
                    "/api/users/name/**",
                    "/api/orders/admin/**",
                    "/api/products-images/get-image/**"
                ).access((authentication, context) -> {
                    HttpServletRequest request = context.getRequest();
                    String path = request.getRequestURI();

                    if (path.contains("/admin/")) {
                        String remoteAddr = request.getRemoteAddr();
                        boolean isLocalhost = "localhost".equals(request.getRemoteHost()) ||
                                              "127.0.0.1".equals(remoteAddr) ||
                                              "0:0:0:0:0:0:0:1".equals(remoteAddr) ||
                                              LOCALHOST_IPV4.matches(remoteAddr) ||
                                              LOCALHOST_IPV6.matches(remoteAddr);
                        return new org.springframework.security.authorization.AuthorizationDecision(isLocalhost);
                    }

                    return new org.springframework.security.authorization.AuthorizationDecision(true);
                })
                .requestMatchers("/api/users/**").authenticated()
                .requestMatchers("/api/**").access((authentication, context) -> {
                    HttpServletRequest request = context.getRequest();
                    String origin = request.getHeader("Origin");
                    return new org.springframework.security.authorization.AuthorizationDecision(apiScrapUrl.equals(origin) || frontAdminUrl.equals(origin) || frontUrl.equals(origin));
                })
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
        configuration.setAllowedOrigins(List.of(apiScrapUrl, frontUrl, frontAdminUrl));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

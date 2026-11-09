package com.hospital.auth.service;

import com.hospital.auth.dto.AuthResponse;
import com.hospital.auth.dto.LoginRequest;
import com.hospital.auth.dto.RegisterRequest;
import com.hospital.auth.dto.TokenValidationResponse;
import com.hospital.auth.entity.ERole;
import com.hospital.auth.entity.Role;
import com.hospital.auth.entity.User;
import com.hospital.auth.exception.UserAlreadyExistsException;
import com.hospital.auth.repository.RoleRepository;
import com.hospital.auth.repository.UserRepository;
import com.hospital.auth.security.JwtUtils;
import com.hospital.auth.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String TOKEN_TYPE = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        return buildResponse(principal);
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("El nombre de usuario ya está en uso: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("El email ya está registrado: " + request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(resolveRoles(request.getRoles()))
                .enabled(true)
                .build();

        User saved = userRepository.save(user);
        return buildResponse(UserDetailsImpl.build(saved));
    }

    @Override
    public TokenValidationResponse validateToken(String token) {
        if (token == null || token.isBlank() || !jwtUtils.validateToken(token)) {
            return TokenValidationResponse.builder()
                    .valid(false)
                    .username(null)
                    .roles(Collections.emptyList())
                    .build();
        }

        return TokenValidationResponse.builder()
                .valid(true)
                .username(jwtUtils.getUsernameFromToken(token))
                .roles(jwtUtils.getRolesFromToken(token))
                .build();
    }

    private AuthResponse buildResponse(UserDetailsImpl principal) {
        String token = jwtUtils.generateToken(principal);
        List<String> roles = principal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList());

        return AuthResponse.builder()
                .token(token)
                .type(TOKEN_TYPE)
                .username(principal.getUsername())
                .email(principal.getEmail())
                .roles(roles)
                .expiresIn(jwtUtils.getExpirationMs())
                .build();
    }

    private Set<Role> resolveRoles(Set<String> requestedRoles) {
        Set<Role> roles = new HashSet<>();

        if (requestedRoles == null || requestedRoles.isEmpty()) {
            roles.add(findRole(ERole.ROLE_PATIENT));
            return roles;
        }

        for (String raw : requestedRoles) {
            roles.add(findRole(parseRole(raw)));
        }
        return roles;
    }

    private ERole parseRole(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("El rol no puede estar vacío");
        }
        String normalized = raw.trim().toUpperCase(Locale.ROOT);
        if (!normalized.startsWith("ROLE_")) {
            normalized = "ROLE_" + normalized;
        }
        try {
            ERole role = ERole.valueOf(normalized);
            if (role == ERole.ROLE_ADMIN) {
                throw new IllegalArgumentException("No se puede registrar un administrador desde el registro público");
            }
            return role;
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Rol no válido: " + raw);
        }
    }

    private Role findRole(ERole name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new IllegalStateException("Rol no configurado en la base de datos: " + name));
    }
}

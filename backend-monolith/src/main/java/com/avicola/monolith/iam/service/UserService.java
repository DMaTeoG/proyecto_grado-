package com.avicola.monolith.iam.service;

import com.avicola.monolith.iam.dto.LoginRequest;
import com.avicola.monolith.iam.dto.LoginResponse;
import com.avicola.monolith.iam.dto.RegisterRequest;
import com.avicola.monolith.iam.model.Usuario;
import com.avicola.monolith.iam.repository.UsuarioRepository;
import com.avicola.monolith.iam.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public UserService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public LoginResponse authenticate(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales invalidas"));

        if (!usuario.isActivo()) {
            throw new IllegalStateException("El usuario se encuentra inactivo");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciales invalidas");
        }

        String token = tokenProvider.generateToken(usuario);
        return new LoginResponse(token, usuario.getId(), usuario.getNombreCompleto(), usuario.getEmail(), usuario.getRol().name());
    }

    @Transactional
    public Usuario register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya se encuentra registrado");
        }

        Usuario usuario = new Usuario(
                request.getNombreCompleto(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getRol()
        );

        return usuarioRepository.save(usuario);
    }
}

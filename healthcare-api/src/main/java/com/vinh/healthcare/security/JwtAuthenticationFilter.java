package com.vinh.healthcare.security;

import com.vinh.healthcare.repository.AdminRepository;
import com.vinh.healthcare.repository.PatientRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final PatientRepository patientRepository;
    private final AdminRepository adminRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            PatientRepository patientRepository,
            AdminRepository adminRepository
    ) {
        this.jwtService = jwtService;
        this.patientRepository = patientRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                if (jwtService.isTokenValid(token)) {
                    String email = jwtService.extractEmail(token);
                    String role = jwtService.extractRole(token);

                    // Tương thích token bệnh nhân đã cấp trước đây
                    if (role == null) {
                        role = "PATIENT";
                    }

                    boolean exists = switch (role) {
                        case "ADMIN" ->
                                adminRepository.findByEmail(email).isPresent();
                        case "PATIENT" ->
                                patientRepository.findByEmail(email).isPresent();
                        default -> false;
                    };

                    if (exists && SecurityContextHolder.getContext()
                            .getAuthentication() == null) {

                        var authentication =
                                new UsernamePasswordAuthenticationToken(
                                        email,
                                        null,
                                        List.of(new SimpleGrantedAuthority(
                                                "ROLE_" + role))
                                );

                        SecurityContextHolder.getContext()
                                .setAuthentication(authentication);
                    }
                }
            } catch (Exception ignored) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}

package com.sebit.movie.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sebit.movie.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.Map;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    public RoleInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    private final Map<String, List<String>> protectedRoutes = Map.of(
            "/api/search-movie", List.of("ROLE_ADMIN", "ROLE_DIRECTOR", "ROLE_USER"),
            "/api/report-download", List.of("ROLE_ADMIN", "ROLE_DIRECTOR"),
            "/api/all", List.of("ROLE_ADMIN", "ROLE_DIRECTOR", "ROLE_USER")
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        if (protectedRoutes.containsKey(path)) {
            String header = request.getHeader("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                Map<String, String> errorBody = Map.of("error", "JWT Token yok.");
                String json = new ObjectMapper().writeValueAsString(errorBody);

                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write(json);
                response.getWriter().flush();

                return false;
            }

            String token = header.substring(7);
            List<String> userRoles;

            try {
                userRoles = jwtUtils.getRolesFromToken(token);
                System.out.println("KULLANICI ROLLERİ: " + userRoles);
            } catch (Exception e) {
                Map<String, String> errorBody = Map.of("error", "Geçersiz JWT Token");
                String json = new ObjectMapper().writeValueAsString(errorBody);

                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write(json);
                response.getWriter().flush();

                return false;
            }

            List<String> requiredRoles = protectedRoutes.get(path);

            boolean hasPermission = userRoles.stream()
                    .anyMatch(requiredRoles::contains);

            if (!hasPermission) {
                Map<String, String> errorBody = Map.of("error", "Bu işlem için yetkin yok.");
                String json = new ObjectMapper().writeValueAsString(errorBody);

                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write(json);
                response.getWriter().flush();

                return false;
            }
        }

        return true;
    }
}

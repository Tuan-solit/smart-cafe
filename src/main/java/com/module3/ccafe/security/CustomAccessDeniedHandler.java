package com.module3.ccafe.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {

        Authentication authentication =
                (Authentication) request.getUserPrincipal();

        if (authentication != null) {

            boolean isAdmin =
                    authentication.getAuthorities()
                            .stream()
                            .anyMatch(authority ->
                                    authority.getAuthority()
                                            .equals("ROLE_ADMIN")
                            );

            boolean isEmployee =
                    authentication.getAuthorities()
                            .stream()
                            .anyMatch(authority ->
                                    authority.getAuthority()
                                            .equals("ROLE_EMPLOYEE")
                            );

            if (isAdmin) {

                response.sendRedirect(
                        request.getContextPath()
                                + "/admin/dashboard?error=accessDenied"
                );

                return;
            }

            if (isEmployee) {

                response.sendRedirect(
                        request.getContextPath()
                                + "/employee/dashboard?error=accessDenied"
                );

                return;
            }
        }

        response.sendRedirect(
                request.getContextPath()
                        + "/login?error=accessDenied"
        );
    }
}
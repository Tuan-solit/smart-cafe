package com.module3.ccafe.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RoleBasedSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch((a ->a.getAuthority().equals("ROLE_ADMIN")));
        boolean isEmployee = authentication.getAuthorities().stream().anyMatch(a ->a.getAuthority().equals("ROLE_EMPLOYEE"));
        if(isAdmin){
            response.sendRedirect(("/admin/dashboard"));
        }
        else if(isEmployee){
            response.sendRedirect(("/employee/dashboard"));
        }else{
            response.sendRedirect("/login?error=norole");
        }

    }
}

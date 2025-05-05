package com.br.pi4.artinlife.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.core.*;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String redirectParam = request.getParameter("redirect");
        String redirectUrl = (redirectParam != null && !redirectParam.isBlank()) ? redirectParam : "/";

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();

            switch (role) {
                case "ROLE_ADMIN":
                    redirectUrl = "/admin/usersadm";
                    break;
                case "ROLE_STOCKER":
                    redirectUrl = "/admin/productsadm";
                    break;
                case "ROLE_CLIENT":
                    if (redirectParam == null) redirectUrl = "/index";
                    break;
            }
        }

        response.sendRedirect(redirectUrl);
    }
}

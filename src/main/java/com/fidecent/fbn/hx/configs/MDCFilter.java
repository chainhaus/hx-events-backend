package com.fidecent.fbn.hx.configs;

import com.microsoft.azure.spring.autoconfigure.aad.UserPrincipal;
import org.slf4j.MDC;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;

@Component
public class MDCFilter implements Filter {

    private static final String USER_KEY = "username";
    private static final String ADMIN_KEY = "admin";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        Principal principal = req.getUserPrincipal();

        if (principal != null) {
            String username = retrieveUsername(principal);
            registerUsername(username, true);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(USER_KEY);
            MDC.remove(ADMIN_KEY);
        }
    }

    private String retrieveUsername(Principal principal) {
        if (principal instanceof PreAuthenticatedAuthenticationToken) {
            Object userPrincipal = ((PreAuthenticatedAuthenticationToken) principal).getPrincipal();
            if (userPrincipal instanceof UserPrincipal) {
                Object email = ((UserPrincipal) userPrincipal).getClaim("email");
                return email != null ? email.toString() : null;
            }
        }
        return null;
    }


    /**
     * Register the user in the MDC under USER_KEY.
     *
     * @param username username to register
     * @return true id the user can be successfully registered
     */
    public boolean registerUsername(String username, boolean admin) {
        if (username != null && username.trim().length() > 0) {
            MDC.put(USER_KEY, username);
            MDC.put(ADMIN_KEY, admin + "");
            return true;
        }
        return false;
    }
}
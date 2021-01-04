package com.fidecent.fbn.hx;

import com.azure.spring.autoconfigure.aad.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class CommonUtils {
    public static Optional<String> getLoggedInUser() {
        return Optional.of(SecurityContextHolder.getContext()).map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .filter(p -> p instanceof UserPrincipal)
                .map(UserPrincipal.class::cast)
                .flatMap(principal -> {
                    String email = (String) principal.getClaim("email");
                    return Optional.ofNullable(email).or(() -> Optional.of(principal.getSubject()));
                });
    }
}

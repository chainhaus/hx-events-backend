package com.rahilhusain.hxevent.configs;

import com.microsoft.azure.spring.autoconfigure.aad.UserPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditProvider {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of(SecurityContextHolder.getContext()).map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .filter(p -> p instanceof UserPrincipal)
                .map(UserPrincipal.class::cast)
                .flatMap(principal -> {
                    String email = (String) principal.getClaim("email");
                    return Optional.ofNullable(email).or(() -> Optional.of(principal.getSubject()));
                });
    }
}

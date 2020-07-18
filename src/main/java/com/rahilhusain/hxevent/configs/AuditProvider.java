package com.rahilhusain.hxevent.configs;

import com.microsoft.azure.spring.autoconfigure.aad.UserPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditProvider {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Object p = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (p instanceof UserPrincipal) {
                UserPrincipal principal = (UserPrincipal) p;
                String email = (String) principal.getClaim("email");
                return Optional.ofNullable(email).or(() -> Optional.of(principal.getSubject()));
            }
            return Optional.empty();
        };
    }
}

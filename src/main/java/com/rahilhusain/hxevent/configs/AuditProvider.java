package com.rahilhusain.hxevent.configs;

import com.rahilhusain.hxevent.mappers.DataMapper;
import com.rahilhusain.hxevent.security.LoggedInUser;
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
    public AuditorAware auditorAware(DataMapper mapper) {
        return () -> {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof LoggedInUser) {
                return Optional.of(mapper.mapToEntity((LoggedInUser) principal));
            }
            return Optional.empty();
        };
    }
}

package com.fidecent.fbn.hx.configs;

import com.fidecent.fbn.hx.CommonUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class AuditProvider {

    @Bean
    public AuditorAware<String> auditorAware() {
        return CommonUtils::getLoggedInUser;
    }
}

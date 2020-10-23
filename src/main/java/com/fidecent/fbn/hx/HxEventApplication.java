package com.fidecent.fbn.hx;

import com.nimbusds.jwt.proc.BadJWTException;
import com.ulisesbocchio.jasyptspringboot.environment.StandardEncryptableEnvironment;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@EnableAsync
@ControllerAdvice
public class HxEventApplication {

    public static void main(String[] args) {
        System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
        System.setProperty("mail.smtp.starttls.enable", "true");
        System.setProperty("mail.smtp.starttls.required", "true");
        System.setProperty("mail.smtp.auth", "true");
        new SpringApplicationBuilder()
                .environment(new StandardEncryptableEnvironment())
                .sources(HxEventApplication.class).run(args);
    }

    @ExceptionHandler(BadJWTException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "JWT token is invalid")
    public void handleBadJWT() {
    }

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }
}

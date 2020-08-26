package com.fidecent.fbn.hx;

import com.nimbusds.jwt.proc.BadJWTException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@EnableAsync
@ControllerAdvice
public class HxEventApplication {

    public static void main(String[] args) {
        SpringApplication.run(HxEventApplication.class, args);
    }

    @ExceptionHandler(BadJWTException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "JWT token is invalid")
    public void handleBadJWT() {
    }

}
package com.fidecent.fbn.hx.security;

import com.microsoft.azure.spring.autoconfigure.aad.AADAppRoleStatelessAuthenticationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AADAppRoleStatelessAuthenticationFilter appRoleAuthFilter;

    public WebSecurityConfig(AADAppRoleStatelessAuthenticationFilter appRoleAuthFilter) {
        this.appRoleAuthFilter = appRoleAuthFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .requiresChannel()
                .anyRequest()
                .requiresSecure()
                .and()
                .csrf().disable()
                .cors().disable()
                .headers()
                .frameOptions().sameOrigin()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .addFilterBefore(appRoleAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/api/events/{eventId}").permitAll()
                .antMatchers("/api/rsvp/{invitationId}/reply/**").permitAll()
                .antMatchers("/api/rsvp/{invitationId}/view").permitAll()
                .antMatchers("/api/rsvp/{invitationId}/copyright.png").permitAll()
                .antMatchers("/api/**").authenticated()
                .antMatchers("/**").permitAll();
    }

}
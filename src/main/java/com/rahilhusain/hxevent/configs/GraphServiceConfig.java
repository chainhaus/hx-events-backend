package com.rahilhusain.hxevent.configs;

import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.UserNamePasswordParameters;
import com.microsoft.graph.authentication.IAuthenticationProvider;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
@Configuration
public class GraphServiceConfig {

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    private String token;

    private final ApplicationContext context;

    private final long DEFAULT_TOKEN_REFRESH_DELAY_ON_ERROR = 10;//10 secs

    private long nextTokenRefreshDelayOnError = DEFAULT_TOKEN_REFRESH_DELAY_ON_ERROR;

    @Value("${hx-events.azure.application-id}")
    private String applicationId;

    @Value("${hx-events.azure.authority}")
    private String authority;

    @Value("${hx-events.azure.scopes}")
    private Set<String> scopes;

    ExecutorService pool = Executors.newFixedThreadPool(1);


    public GraphServiceConfig(ApplicationContext context) {
        this.context = context;
    }

    @Bean
    public IAuthenticationProvider authenticationProvider() {
        return request -> request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }

    @Bean
    @SneakyThrows
    public PublicClientApplication authorizedClientManager() {
        return PublicClientApplication.builder(applicationId)
                .authority(authority)
                .executorService(pool)
                .build();
    }


    @Bean
    public InitializingBean initTokenTask(PublicClientApplication application) {
        return () -> updateGraphToken(application);
    }


    public void updateGraphToken(PublicClientApplication application) {
        // Request a token, passing the requested permission scopes
        UserNamePasswordParameters parameters = UserNamePasswordParameters.builder(scopes, username, password.toCharArray()).build();
        IAuthenticationResult result = application.acquireToken(parameters).exceptionally(ex -> {
            onTokenUpdateError(ex);
            return null;
        }).join();
        if (result != null) {
            this.token = result.accessToken();
            this.nextTokenRefreshDelayOnError = DEFAULT_TOKEN_REFRESH_DELAY_ON_ERROR;//reset on success
            log.info("Graph OAuth2 token updated");
            scheduleTokenRefreshTask(result.expiresOnDate().toInstant());
        }
    }

    public void scheduleTokenRefreshTask(Instant instant) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.initialize();
        taskScheduler.schedule(() -> this.updateGraphToken(context.getBean(PublicClientApplication.class)), instant);
        taskScheduler.setErrorHandler(this::onTokenUpdateError);
    }

    public void onTokenUpdateError(Throwable error) {
        log.error(error.getMessage());
        scheduleTokenRefreshTask(Instant.now().plusSeconds(nextTokenRefreshDelayOnError));
        nextTokenRefreshDelayOnError *= 2;//double on every error;
    }
}

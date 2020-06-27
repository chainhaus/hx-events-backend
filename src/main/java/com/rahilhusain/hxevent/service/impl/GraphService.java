package com.rahilhusain.hxevent.service.impl;

import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.requests.extensions.GraphServiceClient;

public interface GraphService {
    default IGraphServiceClient getGraphClient() {
        return GraphServiceClient
                .builder()
                .authenticationProvider(getAuthenticationProvider())
                .buildClient();
    }

    IAuthenticationProvider getAuthenticationProvider();
}

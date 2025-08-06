package com.programmingmukesh.gateway.config;

import com.programmingmukesh.gateway.filter.AuthenticationFilter;
import com.programmingmukesh.gateway.filter.ResponseTransformationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public AuthenticationFilter authenticationFilter() {
        return new AuthenticationFilter();
    }

    @Bean
    public ResponseTransformationFilter responseTransformationFilter() {
        return new ResponseTransformationFilter();
    }
}
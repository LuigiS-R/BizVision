package com.example.TechWeek25.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean("noEncoding")
    public WebClient passThroughWebClient() { // Named for clarity
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();

        // This is the key: Tell WebClient to do ZERO encoding
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        return WebClient.builder()
                .uriBuilderFactory(factory)
                .build();
    }

    @Bean("encoding")
    public WebClient encodingWebClient() { // Named for clarity
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();

        // This is the key: Tell WebClient to do ZERO encoding
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.TEMPLATE_AND_VALUES);

        return WebClient.builder()
                .uriBuilderFactory(factory)
                .build();
    }
}
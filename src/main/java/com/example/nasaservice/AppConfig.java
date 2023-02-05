package com.example.nasaservice;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableCaching
@EnableScheduling
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean("completableFutureExecutor")
    @Primary
    public Executor createExecutor() {
        return Executors.newFixedThreadPool(150, (r) -> {
            var t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

    }

}

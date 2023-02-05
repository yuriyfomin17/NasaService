package com.example.nasaservice.NasaScheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class NasaScheduler {
    private final CacheManager cacheManager;

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    public void cleanCache() {
        var largestPictureURLCahce = cacheManager.getCache("getLargestPictureURL");
        if (largestPictureURLCahce != null) {
            largestPictureURLCahce.invalidate();
        }
    }


}

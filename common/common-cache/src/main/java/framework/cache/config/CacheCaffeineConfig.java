package framework.cache.config;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

public class CacheCaffeineConfig {
    //初始容量
    @Value("${caffeine.build.initial-capacity:10}")
    private Integer initialCapacity;
    //最大容量（键值对数）
    @Value("${caffeine.build.maximum-size:1024}")
    private Long maximumSize;
    //存活时间
    @Value("${caffeine.build.expire-time:20}")
    private Long expireTimeout;

    @Bean
    public Cache<String, Object> caffeineCache() {
        return Caffeine.newBuilder()
                .initialCapacity(initialCapacity)
                .maximumSize(maximumSize)
                .expireAfterWrite(expireTimeout, TimeUnit.SECONDS)
                .build();
    }
}

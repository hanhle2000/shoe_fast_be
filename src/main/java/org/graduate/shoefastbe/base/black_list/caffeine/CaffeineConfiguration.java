package org.graduate.shoefastbe.base.black_list.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineConfiguration {
    @Bean
    public Cache<Long, Integer> otpConfigure(){
        return Caffeine.newBuilder()
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
    }

    @Bean
    public Cache<Long, OffsetDateTime> updateAccountBlackListConfigure(){
        return Caffeine.newBuilder()
                .expireAfterWrite(100000, TimeUnit.MINUTES)
                .maximumSize(99999)
                .build();
    }

    @Bean
    public Cache<String, Boolean> logOutAccountBlackListConfigure(){
        return Caffeine.newBuilder()
//                .expireAfterWrite(10, TimeUnit.DAYS)
                .maximumSize(99999)
                .build();
    }
}

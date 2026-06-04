package com.foodieapp.user.cache;

import com.foodieapp.user.model.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class UserCacheConfig {

    @Bean("userCache")
    public ConcurrentHashMap<Long, User> userCache() {
        return new ConcurrentHashMap<>();
    }

    @Bean("userEmailCache")
    public ConcurrentHashMap<String, User> userEmailCache() {
        return new ConcurrentHashMap<>();
    }
}

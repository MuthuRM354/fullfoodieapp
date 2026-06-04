package com.foodieapp.user.cache;

import com.foodieapp.user.model.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserCacheService {

    private final ConcurrentHashMap<Long, User> userCache;
    private final ConcurrentHashMap<String, User> userEmailCache;

    public UserCacheService(@Qualifier("userCache") ConcurrentHashMap<Long, User> userCache,
                            @Qualifier("userEmailCache") ConcurrentHashMap<String, User> userEmailCache) {
        this.userCache = userCache;
        this.userEmailCache = userEmailCache;
    }

    public void put(User user) {
        userCache.put(user.getId(), user);
        userEmailCache.put(user.getEmail(), user);
    }

    public Optional<User> getById(Long id) {
        return Optional.ofNullable(userCache.get(id));
    }

    public Optional<User> getByEmail(String email) {
        return Optional.ofNullable(userEmailCache.get(email));
    }

    public void evict(Long id) {
        User user = userCache.remove(id);
        if (user != null) userEmailCache.remove(user.getEmail());
    }

    public void clear() {
        userCache.clear();
        userEmailCache.clear();
    }
}

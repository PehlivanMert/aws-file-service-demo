package org.pehlivan.mert.awsfileservicedemo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final long DEFAULT_EXPIRATION = 24; // hours

    public void set(String key, Object value) {
        set(key, value, DEFAULT_EXPIRATION);
    }

    public void set(String key, Object value, long expirationHours) {
        redisTemplate.opsForValue().set(key, value, expirationHours, TimeUnit.HOURS);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
}
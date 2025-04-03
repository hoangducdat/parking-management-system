package org.project.backend.service.impl;

import java.util.concurrent.TimeUnit;
import org.project.backend.service.RedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    @Override
    public void save(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value.toString(), timeout, unit);
    }
    @Override
    public void save(String key, Object value) {
        redisTemplate.opsForValue().set(key, value.toString());
    }

    @Override
    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void increment(String key, long delta) {
        redisTemplate.opsForValue().increment(key, delta);
    }
    @Override
    public void decrement(String key, long delta) {
        redisTemplate.opsForValue().decrement(key, delta);
    }
    @Override
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }
}

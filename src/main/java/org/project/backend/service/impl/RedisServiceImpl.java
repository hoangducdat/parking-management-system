package org.project.backend.service.impl;

import java.util.concurrent.TimeUnit;
import org.project.backend.constant.Constants;
import org.project.backend.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceImpl implements RedisService {

    private static final Logger log = LoggerFactory.getLogger(RedisServiceImpl.class);
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

    @Override
    public void blacklistToken(String token, long ttl) {
        log.debug("Blacklisting token: {} with TTL: {} seconds", token, ttl);
        redisTemplate.opsForValue().set(Constants.TOKEN_BLACKLIST_PREFIX + token, "blacklisted", ttl, TimeUnit.SECONDS);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        Boolean exists = redisTemplate.hasKey(Constants.TOKEN_BLACKLIST_PREFIX + token);
        return exists != null && exists;
    }
}

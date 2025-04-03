package org.project.backend.service;

import java.util.concurrent.TimeUnit;

public interface RedisService {
    void save(String key, Object value, long timeout, TimeUnit unit);
    void save(String key, Object value);
    String getValue(String key);
    void increment(String key, long delta);
    void decrement(String key, long delta);
    void deleteKey(String key);
}

package org.project.backend.service;

public interface RedisService {
    void setValue(String key, String value, long ttl);
    boolean hasKey(String key);
    void setValue(String key, String value);
    String getValue(String key);
    void deleteKey(String key);
}

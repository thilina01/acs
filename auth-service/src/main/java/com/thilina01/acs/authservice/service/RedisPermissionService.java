package com.thilina01.acs.authservice.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class RedisPermissionService {

    private final StringRedisTemplate redisTemplate;

    public RedisPermissionService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void grantTemporaryPermission(String username, String permission, Duration ttl) {
        String key = "perm:" + username;
        redisTemplate.opsForSet().add(key, permission);
        redisTemplate.expire(key, ttl);
    }

    public List<String> getPermissions(String username) {
        String key = "perm:" + username;
        return redisTemplate.opsForSet().members(key).stream().toList();
    }
}

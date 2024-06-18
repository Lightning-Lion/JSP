package com.hoarp.web.utils;

import redis.clients.jedis.Jedis;

public class RedisTemplate {
    private static RedisTemplate redisTemplate;
    private final Jedis jedis;

    private RedisTemplate() {
        jedis = new Jedis("127.0.0.1", 6379);
    }

    public static RedisTemplate getInstance() {
        if (redisTemplate == null) {
            synchronized (RedisTemplate.class) {
                if (redisTemplate == null) {
                    redisTemplate = new RedisTemplate();
                }
            }
        }
        return redisTemplate;
    }

    public void set(String key, String value) {
        jedis.set(key, value);
    }

    public void set(String key, String value, long timeout) {
        jedis.psetex(key, timeout, value);
    }

    public String get(String key) {
        return jedis.get(key);
    }

    public void delete(String key) {
        jedis.del(key);
    }
}

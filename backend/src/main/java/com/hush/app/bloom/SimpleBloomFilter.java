package com.hush.app.bloom;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimpleBloomFilter {

    private static final long FILTER_SIZE = 1_000_000L;
    private static final String BLOOM_KEY = "bloom:filter:hashes";
    private final StringRedisTemplate redisTemplate;

    private long indexA(String value) {
        return Math.abs((long) value.hashCode()) % FILTER_SIZE;
    }

    private long indexB(String value) {
        long hash = 7;
        for (int i = 0; i < value.length(); i++) {
            hash = hash * 31 + value.charAt(i);
        }
        return Math.abs(hash) % FILTER_SIZE;
    }

    public void add(String value) {
        if (StringUtils.isBlank(value)) return;
        redisTemplate.opsForValue().setBit(BLOOM_KEY, indexA(value), true);
        redisTemplate.opsForValue().setBit(BLOOM_KEY, indexB(value), true);
    }

    public boolean isMaybeTaken(String value) {
        Boolean a = redisTemplate.opsForValue().getBit(BLOOM_KEY, indexA(value));
        Boolean b = redisTemplate.opsForValue().getBit(BLOOM_KEY, indexB(value));
        return Boolean.TRUE.equals(a) && Boolean.TRUE.equals(b);
    }
}
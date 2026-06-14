package com.hush.app.config.redis;

import com.hush.app.model.Item;
import com.hush.app.repository.ItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisExpiryService {

    private final StringRedisTemplate redisTemplate;
    private final ItemRepository itemRepository;

    @PostConstruct
    public void init() {
        List<Item> allHashesToExpire = itemRepository.findAllHashesToExpire();
        if (allHashesToExpire != null && !allHashesToExpire.isEmpty()) {
            allHashesToExpire.forEach((item -> {
                setExpiryAtSpecificTime(item.getHash(), "true", item.getExpiresAt());
            }));
        }
    }

    public void setExpiryAtSpecificTime(String key, String value, LocalDateTime targetDateTime) {
        Instant expiryInstant = targetDateTime.atZone(ZoneId.of("Asia/Kolkata")).toInstant();
        Duration ttl = Duration.between(Instant.now(), expiryInstant);

        if (ttl.isNegative() || ttl.isZero()) {
            log.warn("Skipping Redis expiry for key='{}': expiresAt={} is already in the past (ttl={})",
                    key, targetDateTime, ttl);
            return;
        }
        redisTemplate.opsForValue().set(key, value);
        Boolean result = redisTemplate.expireAt(key, expiryInstant);
        log.info("Redis expiry set: key='{}', expiresAt={} UTC, ttl={}, success={}",
                key, expiryInstant, ttl, result);
    }
}

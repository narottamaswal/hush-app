package com.hush.app.config.redis;

import com.hush.app.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisEventListener implements MessageListener {

    private final ItemRepository itemRepository;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String key = new String(message.getBody());
        log.info("<<< Redis Keyevent Received >>> channel={}, expiredKey={}", channel, key);
        log.info("Handling expired key: {}", key);
        itemRepository.findByHash(key).ifPresentOrElse(item -> {
            item.setIsExpired(true);
            itemRepository.save(item);
            log.info("Soft-expired (isExpired=true) item with hash: {}", key);
        }, () -> log.warn("Expired key '{}' has no matching item in DB — already deleted or never existed", key));
    }
}

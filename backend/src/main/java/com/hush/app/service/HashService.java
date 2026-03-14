package com.hush.app.service;

import com.hush.app.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class HashService {
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int HASH_LENGTH = 8;

    private final ItemRepository itemRepository;
    String generateUniqueHash() {
        SecureRandom random = new SecureRandom();
        String hash;
        do {
            StringBuilder sb = new StringBuilder(HASH_LENGTH);
            for (int i = 0; i < HASH_LENGTH; i++) {
                sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
            }
            hash = sb.toString();
        } while (itemRepository.existsByHash(hash));
        return hash;
    }
    String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(digest);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
}

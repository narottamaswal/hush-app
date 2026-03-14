package com.hush.app.repository;

import com.hush.app.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByHash(String hash);
    boolean existsByHash(String hash);
    List<Item> findByOwnerEmailOrderByUpdatedAtDesc(String ownerEmail);
}

package com.hush.app.repository;

import com.hush.app.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByHash(String hash);
    boolean existsByHash(String hash);
    List<Item> findByOwnerEmailOrderByUpdatedAtDesc(String ownerEmail);

    @Query(value = "SELECT hash FROM items WHERE hash IS NOT NULL ORDER BY created_at DESC", nativeQuery = true)
    List<String> findAllHashes();

    @Query(value = "SELECT * FROM items WHERE hash IS NOT NULL AND expires_at IS NOT NULL AND is_expired=false ORDER BY expires_at DESC", nativeQuery = true)
    List<Item> findAllHashesToExpire();
}

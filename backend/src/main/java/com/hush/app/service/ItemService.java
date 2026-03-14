package com.hush.app.service;

import com.hush.app.model.Item;
import com.hush.app.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final HashService hashService;

    public List<Item> getAllByOwner(String ownerEmail) {
        return itemRepository.findByOwnerEmailOrderByUpdatedAtDesc(ownerEmail);
    }

    public Item create(String ownerEmail, String ownerName, String title, String content, String password) {
        Item item = new Item();
        item.setHash(hashService.generateUniqueHash());
        item.setTitle(title);
        item.setContent(content);
        item.setOwnerEmail(ownerEmail);
        item.setOwnerName(ownerName);
        if (password != null && !password.isBlank()) {
            item.setPasswordHash(hashService.hashPassword(password));
        }
        return itemRepository.save(item);
    }

    public Item getByHash(String hash) {
        return itemRepository
                .findByHash(hash)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
    }

    public boolean checkPassword(String hash, String password) {
        Item item = getByHash(hash);
        if (item.getPasswordHash() == null) return true;
        return item.getPasswordHash().equals(hashService.hashPassword(password));
    }

    public Item update(String hash, String ownerEmail, String title, String content, String password) {
        Item item = getByHash(hash);

        if (!item.getOwnerEmail().equals(ownerEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not the owner");
        }

        item.setTitle(title);
        item.setContent(content);

        if (password != null && !password.isBlank()) {
            item.setPasswordHash(hashService.hashPassword(password));
        } else if (password != null && password.isBlank()) {
            item.setPasswordHash(null);
        }

        return itemRepository.save(item);
    }

    public void delete(String hash, String ownerEmail) {
        Item item = getByHash(hash);
        if (!item.getOwnerEmail().equals(ownerEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not the owner");
        }
        itemRepository.delete(item);
    }


}

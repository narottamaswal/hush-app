package com.hush.app.service;

import com.hush.app.bloom.SimpleBloomFilter;
import com.hush.app.model.Item;
import com.hush.app.model.ResponseDto;
import com.hush.app.repository.ItemRepository;
import com.hush.app.validation.ItemContext;
import com.hush.app.validation.exceptions.PostValidationException;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final HashService hashService;
    private final SimpleBloomFilter simpleBloomFilter;
    @PostConstruct
    public void init() {
        List<String> allExistingUrls = itemRepository.findAllHashes();
        if(allExistingUrls!=null && !allExistingUrls.isEmpty()){
            allExistingUrls.forEach(simpleBloomFilter::add);
        }
    }
    public List<Item> getAllByOwner(String ownerEmail) {
        return itemRepository.findByOwnerEmailOrderByUpdatedAtDesc(ownerEmail);
    }
    public void applyChanges(ItemContext ctx, Item item, String fingerprint){
        if (ctx.isMarkViewed()) {
            item.setViewed(true);
        }
        if (ctx.isUpdateFingerprint()) {
            item.setFingerprint(fingerprint);
        }
        if(ctx.isMarkViewed() || ctx.isUpdateFingerprint()){
            itemRepository.save(item);
        }
    }

    public boolean isAliasAvailable(String alias){
        if (!simpleBloomFilter.isMaybeTaken(alias)) {
            return true;
        }
        return !itemRepository.existsByHash(alias);
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

    public Item create(String ownerEmail, String ownerName, ResponseDto.CreateRequest req) {
        Item item = new Item();
        String hash;
        if(!StringUtils.isBlank(req.getAlias())){
            if(itemRepository.existsByHash(req.getAlias())){
                throw PostValidationException.alisAlreadyUsed();
            }
            hash= req.getAlias();
        }else{
            hash = hashService.generateUniqueHash();
        }
        item.setHash(hash);
        item.setTitle(req.getTitle());
        item.setContent(req.getContent());
        item.setOwnerEmail(ownerEmail);
        item.setOwnerName(ownerName);
        item.setViewOnce(req.isViewOnce());
        item.setViewed(false);
        item.setNoForward(req.isNoForward());
        simpleBloomFilter.add(hash);
        if (!StringUtils.isBlank(req.getPassword())) {
            item.setPasswordHash(hashService.hashPassword(req.getPassword()));
        }
        itemRepository.save(item);
        return item;
    }

    public Item update(String hash, String ownerEmail, ResponseDto.UpdateRequest req) {
        Item item = getByHash(hash);
        if (!item.getOwnerEmail().equals(ownerEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not the owner");
        }
        item.setTitle(req.getTitle());
        item.setContent(req.getContent());
        item.setViewOnce(req.isViewOnce());
        item.setViewed(false);
        item.setNoForward(req.isNoForward());
        String password = req.getPassword();
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
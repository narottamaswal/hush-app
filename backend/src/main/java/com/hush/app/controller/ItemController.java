package com.hush.app.controller;

import com.hush.app.model.Item;
import com.hush.app.model.ResponseDto;
import com.hush.app.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/mine")
    public List<ResponseDto.Response> mine(OAuth2AuthenticationToken auth) {
        String email = getEmail(auth);
        return itemService.getAllByOwner(email)
                .stream()
                .map(item -> ResponseDto.Response.from(item, email))
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto.Response create(@RequestBody ResponseDto.CreateRequest req, OAuth2AuthenticationToken auth) {
        String email = getEmail(auth);
        String name = getName(auth);
        Item item = itemService.create(email, name, req.getTitle(), req.getContent(), req.getPassword());
        return ResponseDto.Response.from(item, email);
    }

    @GetMapping("/{hash}/meta")
    public ResponseDto.Response meta(@PathVariable String hash) {
        Item item = itemService.getByHash(hash);
        return ResponseDto.Response.meta(item);
    }

    @PostMapping("/{hash}/unlock")
    public ResponseEntity<ResponseDto.Response> unlock(
            @PathVariable String hash,
            @RequestBody ResponseDto.PasswordRequest req,
            OAuth2AuthenticationToken auth) {

        boolean validPassword = itemService.checkPassword(hash, req.getPassword());
        if (!validPassword) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        Item item = itemService.getByHash(hash);
        String viewerEmail = auth != null ? getEmail(auth) : null;
        return ResponseEntity.ok(ResponseDto.Response.from(item, viewerEmail));
    }

    @GetMapping("/{hash}")
    public ResponseEntity<ResponseDto.Response> get(@PathVariable String hash, OAuth2AuthenticationToken auth) {
        Item item = itemService.getByHash(hash);
        if (item.getPasswordHash() != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDto.Response.meta(item));
        }
        String viewerEmail = auth != null ? getEmail(auth) : null;
        return ResponseEntity.ok(ResponseDto.Response.from(item, viewerEmail));
    }

    @PutMapping("/{hash}")
    public ResponseDto.Response update(@PathVariable String hash,
                                       @RequestBody ResponseDto.UpdateRequest req,
                                       OAuth2AuthenticationToken auth) {
        String email = getEmail(auth);
        Item item = itemService.update(hash, email, req.getTitle(), req.getContent(), req.getPassword());
        return ResponseDto.Response.from(item, email);
    }

    @DeleteMapping("/{hash}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String hash, OAuth2AuthenticationToken auth) {
        itemService.delete(hash, getEmail(auth));
    }

    private String getEmail(OAuth2AuthenticationToken auth) {
        return auth.getPrincipal().getAttribute("email");
    }

    private String getName(OAuth2AuthenticationToken auth) {
        return auth.getPrincipal().getAttribute("name");
    }
}

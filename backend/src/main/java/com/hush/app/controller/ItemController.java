package com.hush.app.controller;

import com.hush.app.config.security.AuthUtil;
import com.hush.app.model.Item;
import com.hush.app.model.ResponseDto;
import com.hush.app.service.ItemService;
import com.hush.app.validation.ItemContext;
import com.hush.app.validation.ItemValidationChainFactory;
import com.hush.app.validation.exceptions.PostValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
    private final ItemValidationChainFactory factory;

    @GetMapping("/mine")
    public List<ResponseDto.Response> mine(OAuth2AuthenticationToken auth) {
        return itemService.getAllByOwner(AuthUtil.getEmail(auth))
                .stream()
                .map(ResponseDto.Response::list)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> create(@RequestBody ResponseDto.CreateRequest req, OAuth2AuthenticationToken auth) {
        String email = AuthUtil.getEmail(auth);
        String name = AuthUtil.getName(auth);
        Item item = itemService.create(email, name, req);
        return ResponseEntity.ok(ResponseDto.Response.list(item));
    }
    @GetMapping("/check-alias/{alias}")
    public ResponseEntity<?> aliasCheck(@PathVariable String alias){
        return ResponseEntity.ok(itemService.isAliasAvailable(alias));
    }
    @GetMapping("/{hash}")
    public ResponseEntity<?> getNew(@PathVariable String hash, @RequestHeader HttpHeaders headers, OAuth2AuthenticationToken auth) {
        String fingerprint = headers.getFirst(AuthUtil.X_DEVICE_FINGERPRINT);
        String password = headers.getFirst(AuthUtil.X_POST_PASSWORD);
        Item item = itemService.getByHash(hash);
        String email = AuthUtil.getEmail(auth);
        if (item == null) {
            throw PostValidationException.notFound();
        }
        ItemContext ctx = new ItemContext(item, password, fingerprint, email);
        factory.buildDefaultChain().validate(ctx);
        if (ctx.isEarlyExit()) {
            return ResponseEntity.ok(ctx.getEarlyExitResult());
        }
        itemService.applyChanges(ctx, item, fingerprint);
        return ResponseEntity.ok(ResponseDto.Response.from(item));
    }


    @PutMapping("/{hash}")
    public ResponseDto.Response update(@PathVariable String hash,
                                       @RequestBody ResponseDto.UpdateRequest req,
                                       OAuth2AuthenticationToken auth) {
        Item item = itemService.update(hash, AuthUtil.getEmail(auth), req);
        return ResponseDto.Response.from(item);
    }

    @DeleteMapping("/{hash}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String hash, OAuth2AuthenticationToken auth) {
        itemService.delete(hash, AuthUtil.getEmail(auth));
    }


}

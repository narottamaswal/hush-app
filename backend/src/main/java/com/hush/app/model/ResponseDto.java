package com.hush.app.model;


import lombok.Data;

import java.util.Optional;

public class ResponseDto {

//    title: [this.initialData?.title || '', [Validators.required, Validators.maxLength(200)]],
//    content: [this.initialData?.content || '', [Validators.required]],
//    password: [''],
//    viewOnce: [this.initialData?.viewOnce || false],
//    noForward: [this.initialData?.noForward || false],
//    alias
//
    @Data
    public static class CreateRequest {
        private String title;
        private String content;
        private String password;
        private String alias;
        private boolean viewOnce;
        private boolean noForward;
        private String expiresAt; // ISO-8601 datetime string, e.g. "2026-12-31T23:59"
    }

    @Data
    public static class UpdateRequest {
        private String title;
        private String content;
        private String password;
        private String alias;
        private boolean viewOnce;
        private boolean noForward;
        private String expiresAt; // ISO-8601 datetime string, e.g. "2026-12-31T23:59"
    }

    @Data
    public static class PasswordRequest {
        private String password;
    }

    @Data
    public static class Response {
        private String hash;
        private String title;
        private String content;
        private String ownerName;
        private String ownerEmail;
        private boolean passwordProtected;
        private String createdAt;
        private String updatedAt;
        private String expiresAt;
        private String alias;
        private boolean viewOnce;
        private boolean noForward;
        private boolean viewed;
        private boolean isExpired;

        public static Response viewed(){
            Response r = new Response();
            r.viewed=true;
            return r;
        }

        public static Response passwordProtected(){
            Response r = new Response();
            r.passwordProtected=true;
            return r;
        }

        public static Response list(Item item) {
            Response r = new Response();
            r.hash = item.getHash();
            r.title = item.getTitle();
            r.passwordProtected = item.getPasswordHash() != null;
            if(!r.passwordProtected){
                r.content = item.getContent();
                r.ownerName = item.getOwnerName();
                r.ownerEmail = item.getOwnerEmail();
            }
            r.isExpired = Boolean.TRUE.equals(item.getIsExpired());
            r.createdAt = item.getCreatedAt() != null ? item.getCreatedAt().toString() : null;
            r.updatedAt = item.getUpdatedAt() != null ? item.getUpdatedAt().toString() : null;
            r.expiresAt = item.getExpiresAt() != null ? item.getExpiresAt().toString() : null;
            return r;
        }

        public static Response from(Item item) {
            Response r = new Response();
            r.hash = item.getHash();
            r.title = item.getTitle();
            r.content = item.getContent();
            r.ownerName = item.getOwnerName();
            r.ownerEmail = item.getOwnerEmail();
            r.passwordProtected = item.getPasswordHash() != null;
            r.viewOnce = item.getViewOnce()!=null ? item.getViewOnce() : false;
            r.viewed = item.getViewed()!=null ? item.getViewed() : false;
            r.noForward = item.getNoForward()!=null ? item.getNoForward() : false;
            r.isExpired = Boolean.TRUE.equals(item.getIsExpired());
            r.createdAt = item.getCreatedAt() != null ? item.getCreatedAt().toString() : null;
            r.updatedAt = item.getUpdatedAt() != null ? item.getUpdatedAt().toString() : null;
            r.expiresAt = item.getExpiresAt() != null ? item.getExpiresAt().toString() : null;
            return r;
        }
    }
}

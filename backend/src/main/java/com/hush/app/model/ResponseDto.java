package com.hush.app.model;


import lombok.Data;
public class ResponseDto {

    @Data
    public static class CreateRequest {
        private String title;
        private String content;
        private String password;
    }

    @Data
    public static class UpdateRequest {
        private String title;
        private String content;
        private String password;
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

        public static Response from(Item item, String viewerEmail) {
            Response r = new Response();
            r.hash = item.getHash();
            r.title = item.getTitle();
            r.content = item.getContent();
            r.ownerName = item.getOwnerName();
            r.ownerEmail = item.getOwnerEmail();
            r.passwordProtected = item.getPasswordHash() != null;
            r.createdAt = item.getCreatedAt() != null ? item.getCreatedAt().toString() : null;
            r.updatedAt = item.getUpdatedAt() != null ? item.getUpdatedAt().toString() : null;
            return r;
        }

        public static Response meta(Item item) {
            Response r = new Response();
            r.hash = item.getHash();
            r.title = item.getTitle();
            r.passwordProtected = item.getPasswordHash() != null;
            r.ownerName = item.getOwnerName();
            r.ownerEmail = item.getOwnerEmail();
            r.createdAt = item.getCreatedAt() != null ? item.getCreatedAt().toString() : null;
            r.updatedAt = item.getUpdatedAt() != null ? item.getUpdatedAt().toString() : null;
            return r;
        }
    }
}

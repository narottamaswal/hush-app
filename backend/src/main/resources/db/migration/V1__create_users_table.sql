CREATE TABLE IF NOT EXISTS users (
                                     id            BIGSERIAL PRIMARY KEY,
                                     email         VARCHAR(255) NOT NULL UNIQUE,
    name          VARCHAR(255) NOT NULL,
    picture       TEXT,
    avatar        TEXT,
    created_at    TIMESTAMP,
    last_login_at TIMESTAMP
    );
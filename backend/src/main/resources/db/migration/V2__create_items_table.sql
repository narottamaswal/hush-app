CREATE TABLE IF NOT EXISTS items (
                                     id            BIGSERIAL PRIMARY KEY,
                                     hash          VARCHAR(10)  NOT NULL UNIQUE,
    title         VARCHAR(255) NOT NULL,
    content       TEXT         NOT NULL,
    password_hash VARCHAR(255),
    owner_email   VARCHAR(255) NOT NULL,
    owner_name    VARCHAR(255),
    created_at    TIMESTAMP,
    updated_at    TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_items_owner_email ON items (owner_email);
CREATE INDEX IF NOT EXISTS idx_items_hash        ON items (hash);
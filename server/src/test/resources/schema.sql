-- Сброс БД перед тестами
drop table bookings cascade;
drop table comments cascade;
drop table items cascade;
drop table item_requests cascade;
drop table users cascade;

-- Создание таблиц в тестовой БД
CREATE TABLE IF NOT EXISTS USERS (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name VARCHAR(64) NOT NULL,
    email VARCHAR(64) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS ITEM_REQUESTS (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    description VARCHAR(256) NOT NULL,
    created TIMESTAMP NOT NULL,
    creator_id BIGINT NOT NULL,
    CONSTRAINT pk_item_request PRIMARY KEY (id),
    CONSTRAINT fk_creator_id FOREIGN KEY (creator_id) REFERENCES USERS (id)
);

CREATE TABLE IF NOT EXISTS ITEMS (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name VARCHAR(64) NOT NULL,
    description VARCHAR(256) NOT NULL,
    is_available BOOLEAN NOT NULL,
    owner_id BIGINT NOT NULL,
    request_id BIGINT,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT fk_owner_id FOREIGN KEY (owner_id) REFERENCES USERS (id),
    CONSTRAINT fk_request_id FOREIGN KEY (request_id) REFERENCES ITEM_REQUESTS (id)
);

CREATE TABLE IF NOT EXISTS BOOKINGS (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    status VARCHAR(16) NOT NULL,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT fk_item_id FOREIGN KEY (item_id) REFERENCES ITEMS (id),
    CONSTRAINT fk_booker_id FOREIGN KEY (booker_id) REFERENCES USERS (id)
);

CREATE TABLE IF NOT EXISTS COMMENTS (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    item_id BIGINT NOT NULL,
    text VARCHAR(512) NOT NULL,
    author_name VARCHAR(64) NOT NULL,
    created TIMESTAMP NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY (id),
    CONSTRAINT fk_comment_item_id FOREIGN KEY (item_id) REFERENCES ITEMS (id)
);
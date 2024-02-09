drop TABLE IF EXISTS users CASCADE;
drop TABLE IF EXISTS items CASCADE;
drop TABLE IF EXISTS bookings CASCADE;
drop TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS requests CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id LONG auto_increment PRIMARY KEY,
    name varchar(255) NOT NULL,
    email name varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
    id LONG auto_increment PRIMARY KEY,
    name varchar(255) NOT NULL,
    description varchar(255) NOT NULL,
    is_available boolean NOT NULL,
    owner_id BIGINT REFERENCES users(id) ON DELETE RESTRICT,
    request_id BIGINT  REFERENCES requests(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
    id LONG auto_increment PRIMARY KEY,
    start_data timestamp WITHOUT TIME ZONE NOT NULL,
    end_data timestamp WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT REFERENCES items(id) ON DELETE RESTRICT,
    booker_id BIGINT REFERENCES users(id) ON DELETE RESTRICT,
    status boolean varchar(100)
);

CREATE TABLE IF NOT EXISTS comments (
    id LONG auto_increment PRIMARY KEY,
    text varchar(1000) NOT NULL,
    item_id BIGINT REFERENCES items(id) ON DELETE RESTRICT,
    author_id BIGINT REFERENCES users(id) ON DELETE RESTRICT,
    created_time timestamp WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS requests (
    id LONG auto_increment PRIMARY KEY,
    description varchar(255) NOT NULL,
    requestor_id LONG REFERENCES users(id) ON DELETE CASCADE
);

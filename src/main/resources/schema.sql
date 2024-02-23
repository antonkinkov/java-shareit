drop table IF EXISTS users CASCADE;
drop table IF EXISTS items CASCADE;
drop table IF EXISTS bookings CASCADE;
drop table IF EXISTS comments CASCADE;
drop table IF EXISTS requests CASCADE;
--
create TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL unique (email)
);
--
----CREATE TABLE IF NOT EXISTS users (
----    id LONG auto_increment PRIMARY KEY,
----    name TEXT NOT NULL,
----    email TEXT NOT NULL
----);

create TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description TEXT NOT NULL,
    requester_id BIGINT REFERENCES users(id) ON delete CASCADE,
    CONSTRAINT pk_request PRIMARY KEY (id)
);

create TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    is_available boolean NOT NULL,
    owner_id BIGINT REFERENCES users(id) ON delete RESTRICT,
    request_id BIGINT  REFERENCES requests(id) ON delete CASCADE,
    CONSTRAINT pk_items PRIMARY KEY (id)
);

create TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date timestamp WITHOUT TIME ZONE NOT NULL,
    end_date timestamp WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT REFERENCES items(id) ON delete RESTRICT,
    booker_id BIGINT REFERENCES users(id) ON delete RESTRICT,
    status varchar(100),
    CONSTRAINT pk_booking PRIMARY KEY (id)
);

create TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text varchar(1000) NOT NULL,
    item_id BIGINT REFERENCES items(id) ON delete RESTRICT,
    author_id BIGINT REFERENCES users(id) ON delete RESTRICT,
    created_time timestamp WITHOUT TIME ZONE,
    CONSTRAINT pk_comment PRIMARY KEY (id)
);

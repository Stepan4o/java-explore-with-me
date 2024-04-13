CREATE TABLE IF NOT EXISTS users(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name  VARCHAR(250) NOT NULL,
    email VARCHAR(254) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS categories(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS locations(
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    lat FLOAT,
    lon FLOAT
);

CREATE TABLE IF NOT EXISTS events(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation         VARCHAR(2000) UNIQUE NOT NULL,
    category_id        BIGINT NOT NULL REFERENCES categories(id),
    created_on         TIMESTAMP,
    description        VARCHAR(7000) NOT NULL,
    event_date         TIMESTAMP NOT NULL,
    initiator_id       BIGINT REFERENCES users(id),
    location_id        BIGINT NOT NULL REFERENCES locations(id),
    paid               BOOLEAN,
    participant_limit  INTEGER,
    confirmed_request  INTEGER,
    views              BIGINT,
    published_on       TIMESTAMP,
    request_moderation BOOLEAN,
    state              VARCHAR(10),
    title              VARCHAR(120) NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    pinned BOOLEAN,
    title  VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS requests(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created      TIMESTAMP,
    event_id     BIGINT REFERENCES events(id),
    requester_id BIGINT REFERENCES users(id),
    status       VARCHAR(50)
);
drop table IF  EXISTS comments;
drop table IF  EXISTS bookings;
drop table IF  EXISTS items;
drop table IF  EXISTS requests;
drop table IF  EXISTS users;

create TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

create TABLE IF NOT EXISTS requests (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  description VARCHAR(512) NOT NULL,
  requestor_id BIGINT references users(id) NOT NULL,
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT pk_request PRIMARY KEY (id)
);

create TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(512) NOT NULL,
  is_available BOOLEAN default true,
  owner_id BIGINT references users(id) NOT NULL,
  request_id BIGINT references requests(id),
  CONSTRAINT pk_item PRIMARY KEY (id)
);

create TABLE IF NOT EXISTS comments (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  text VARCHAR(512) NOT NULL,
  item_id BIGINT references items(id) NOT NULL,
  author_id BIGINT references users(id) NOT NULL,
  create_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT pk_comment PRIMARY KEY (id)
);

create TABLE IF NOT EXISTS bookings (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  item_id BIGINT references items(id) NOT NULL,
  booker_id BIGINT references users(id) NOT NULL,
  status VARCHAR(64) NOT NULL,
  CONSTRAINT pk_booking PRIMARY KEY (id)
);
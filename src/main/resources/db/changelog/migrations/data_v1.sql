INSERT INTO access_schema.users (username, password, enabled)
VALUES ('user@mail.ru', '$2a$10$/9AA6UVybqEma3iDn3Akf.qOpJuwETM8g00kjA/PM5JGxspfqXLci', true),
       ('admin@mail.ru', '$2a$10$/9AA6UVybqEma3iDn3Akf.qOpJuwETM8g00kjA/PM5JGxspfqXLci', true);

INSERT INTO access_schema.authorities (username, authority)
values	('user@mail.ru', 'ROLE_USER'),
          ('admin@mail.ru', 'ROLE_ADMIN'),
          ('admin@mail.ru', 'ROLE_USER');

--{bcrypt}$2a$10$/9AA6UVybqEma3iDn3Akf.qOpJuwETM8g00kjA/PM5JGxspfqXLci
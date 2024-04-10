CREATE SCHEMA access_schema
    CREATE TABLE users
    (
        id				SERIAL		    NOT NULL	PRIMARY KEY,
        login			VARCHAR(30)     NOT NULL    UNIQUE,
        password		VARCHAR(30)     NOT NULL,
        bucket			VARCHAR(255)	NOT NULL
    );
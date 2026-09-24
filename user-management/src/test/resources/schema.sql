DROP TABLE IF EXISTS user;
CREATE TABLE user (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL,
    password    VARCHAR(100) NOT NULL,
    email       VARCHAR(100) DEFAULT NULL,
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE (username)
);

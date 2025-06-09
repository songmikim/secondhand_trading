CREATE TABLE PRODUCT (
    seq BIGINT AUTO_INCREMENT,
    gid VARCHAR(45) NOT NULL,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(40),
    status ENUM('READY', 'SALE', "OUT_OF_STOCK", "STOP") DEFAULT 'READY',
    consumerPrice INT DEFAULT 0,
    salePrice INT DEFAULT 0,
    description TEXT,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    modifiedAt DATETIME,
    deletedAt DATETIME,
    PRIMARY KEY(seq)
);
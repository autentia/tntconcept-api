DROP TABLE IF EXISTS `Expense`;

CREATE TABLE `Expense`
(
    id          INT AUTO_INCREMENT,
    userId      INT            NOT NULL ,
    date        DATETIME       NOT NULL ,
    description VARCHAR(2048)  NOT NULL ,
    amount      DECIMAL(10, 2) NOT NULL ,
    type        VARCHAR(64)    NOT NULL ,
    state       VARCHAR(64)    NOT NULL ,

    PRIMARY KEY (id),
    CONSTRAINT fk_expense_userId
        FOREIGN KEY (userID) REFERENCES User (id)
)
    COMMENT 'Information related to expense sheets'
    ENGINE = innodb
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_spanish_ci;


DROP TABLE IF EXISTS `ExpenseDocument`;

CREATE TABLE ExpenseDocument
(
    expenseId    INT         NOT NULL,
    attachmentId VARCHAR(64) NOT NULL,
    insertDate   DATETIME    NOT NULL DEFAULT NOW(),

    PRIMARY KEY (expenseId,attachmentId),
    CONSTRAINT fk_expense_id FOREIGN KEY (expenseId) REFERENCES Expense (id),
    CONSTRAINT fk_attachment_id FOREIGN KEY (attachmentId) REFERENCES Attachment (id)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;
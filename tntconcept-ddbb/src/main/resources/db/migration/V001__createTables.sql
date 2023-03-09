CREATE TABLE `Version`
(
    `version` varchar(32) NOT NULL
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci COMMENT = 'The database version';

CREATE TABLE `Country`
(
    `id`           int                                       NOT NULL,
    `code`         smallint,
    `iso3166a1`    char(2)      COLLATE utf8mb4_spanish_ci,
    `iso3166a2`    char(3)      COLLATE utf8mb4_spanish_ci,
    `name`         varchar(128) COLLATE utf8mb4_spanish_ci,
    `ownerId`      int,
    `departmentId` int          UNSIGNED,

    `insertDate`   datetime                                  DEFAULT NOW(),
    `updateDate`   datetime                                  DEFAULT NOW(),

    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;

CREATE TABLE `Province`
(
    `id`           int          NOT NULL,
    `name`         varchar(64)  NOT NULL,
    `ownerId`      int          NULL,
    `departmentId` int UNSIGNED NULL,
    `insertDate`   datetime     DEFAULT NOW(),
    `updateDate`   datetime     DEFAULT NOW(),

    PRIMARY KEY (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `Document`
(
    `id`           int           UNSIGNED                   NOT NULL AUTO_INCREMENT,
    `creationDate` datetime                                 DEFAULT NULL,
    `name`         varchar(256)  COLLATE utf8mb4_spanish_ci DEFAULT NULL,
    `description`  varchar(4096) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
    `ownerId`      int                                      NULL,
    `departmentId` int           UNSIGNED                   NULL,
    `insertDate`   datetime                                 NOT NULL DEFAULT NOW(),
    `updateDate`   datetime                                 NOT NULL DEFAULT NOW(),

    PRIMARY KEY USING BTREE (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `DocumentCategory`
(
    `id`                  int UNSIGNED                             NOT NULL AUTO_INCREMENT,
    `name`                varchar(255)  COLLATE utf8mb4_spanish_ci NOT NULL,
    `description`         varchar(4096) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
    `code`                varchar(45)   COLLATE utf8mb4_spanish_ci DEFAULT NULL,
    `categoryid`          int UNSIGNED                             DEFAULT NULL,
    `documentslastupdate` datetime                                 DEFAULT NULL,
    `ownerId`             int                                      NULL,
    `departmentId`        int UNSIGNED                             NULL,
    `insertDate`          datetime                                 DEFAULT NOW(),
    `updateDate`          datetime                                 DEFAULT NOW(),

    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;

CREATE TABLE `DocumentVersion`
(
    `id`           int UNSIGNED                            NOT NULL AUTO_INCREMENT,
    `documentPath` varchar(255) COLLATE utf8mb4_spanish_ci NOT NULL,
    `creationDate` datetime                                NOT NULL,
    `version`      varchar(255) COLLATE utf8mb4_spanish_ci NOT NULL,
    `documentid`   int          UNSIGNED                   NOT NULL,
    `ownerId`      int                                     NULL,
    `departmentId` int          UNSIGNED                   NULL,
    `insertDate`   datetime                                DEFAULT NOW(),
    `updateDate`   datetime                                DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_DocumentVersion_document_id` FOREIGN KEY (`documentid`) REFERENCES `Document` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `DocumentCategoryDoc`
(
    `id`           int UNSIGNED NOT NULL AUTO_INCREMENT,
    `categoryid`   int UNSIGNED NOT NULL,
    `documentid`   int UNSIGNED NOT NULL,
    `ownerId`      int              NULL,
    `departmentId` int UNSIGNED     NULL,
    `insertDate`   datetime     DEFAULT NOW(),
    `updateDate`   datetime     DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_DocumentCategorydoc_category` FOREIGN KEY (`categoryid`) REFERENCES `DocumentCategory` (`id`),
    CONSTRAINT `fk_DocumentCategorydoc_docu` FOREIGN KEY (`documentid`) REFERENCES `Document` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;


CREATE TABLE `OrganizationType`
(
    `id`           int                    NOT NULL AUTO_INCREMENT,
    `name`         varchar(128)           NOT NULL COMMENT 'Organization type descriptive name',
    `description`  varchar(1024)                   COMMENT 'Organization type description',
    `ownerId`      int                    NULL,
    `departmentId` int           UNSIGNED NULL,
    `insertDate`   datetime               DEFAULT NOW(),
    `updateDate`   datetime               DEFAULT NOW(),

    PRIMARY KEY (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `OrganizationISOCategory`
(
    `id`           int                    NOT NULL AUTO_INCREMENT,
    `name`         varchar(128)           NOT NULL COMMENT 'ISO Organization Category descriptive name',
    `description`  varchar(1024)                   COMMENT 'ISO Organization Category description',
    `ownerId`      int                    NULL,
    `departmentId` int           UNSIGNED NULL,
    `insertDate`   datetime               DEFAULT NOW(),
    `updateDate`   datetime               DEFAULT NOW(),

    PRIMARY KEY (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;

CREATE TABLE OrganizationDocCategory
(
    id           int         NOT NULL,
    code         varchar(3)  NOT NULL,
    name         varchar(70) NOT NULL,
    ownerId      int,
    departmentId int,
    `insertDate` datetime    DEFAULT NOW(),
    `updateDate` datetime    DEFAULT NOW(),

    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;

CREATE TABLE `Organization`
(
    `id`                        int                    NOT NULL AUTO_INCREMENT,
    `organizationTypeId`        int                    NOT NULL DEFAULT 1,
    `organizationISOCategoryId` int                    NOT NULL,
    `name`                      varchar(256)           NOT NULL,
    `documentNumber`            varchar(50),
    `phone`                     varchar(15),
    `street`                    varchar(256),
    `number`                    varchar(16)                      COMMENT 'Building number in street',
    `locator`                   varchar(256)                     COMMENT 'Location information inside building',
    `postalCode`                varchar(32),
    `city`                      varchar(256),
    `provinceId`                int                    NOT NULL,
    `state`                     varchar(256),
    `countryId`                 INT                    NOT NULL,
    `fax`                       varchar(16),
    `email`                     varchar(256),
    `website`                   varchar(256),
    `ftpsite`                   varchar(256),
    `notes`                     VARCHAR(1024),
    `ownerId`                   int                    NULL,
    `departmentId`              int           UNSIGNED NULL,
    `evaluationCriteria`        VARCHAR(45)            DEFAULT NULL,
    organizationDocCategoryId   INT                    NOT NULL,
    freelance                   BOOLEAN                NOT NULL,
    `insertDate`                datetime               DEFAULT NOW(),
    `updateDate`                datetime               DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_organization_organizationTypeId` FOREIGN KEY (`organizationTypeId`) REFERENCES `OrganizationType` (`id`),
    CONSTRAINT `fk_organization_isoOrganizationCategoryId` FOREIGN KEY (`organizationISOCategoryId`) REFERENCES `OrganizationISOCategory` (`id`),
    CONSTRAINT `fk_organization_countryId` FOREIGN KEY (`countryId`) REFERENCES `Country` (`id`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,
    CONSTRAINT `fk_organization_organizationDocCategory` FOREIGN KEY (organizationDocCategoryId) REFERENCES OrganizationDocCategory (id)

) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;

CREATE TABLE IF NOT EXISTS `Department`
(
    `id`           int           UNSIGNED                   NOT NULL AUTO_INCREMENT,
    `parentId`     int           UNSIGNED,
    `name`         varchar(256)                             NOT NULL,
    `description`  varchar(2048) COLLATE utf8mb4_spanish_ci NOT NULL,
    `ownerId`      int                                      NULL,
    `departmentId` int           UNSIGNED                   NULL,
    `insertDate`   datetime                                 DEFAULT NOW(),
    `updateDate`   datetime                                 DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_department_department` FOREIGN KEY (`parentId`) REFERENCES `Department` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;

CREATE TABLE `Contact`
(
    `id`           int                   NOT NULL AUTO_INCREMENT,
    `name`         varchar(150)          NOT NULL,
    `email`        varchar(128),
    `phone`        varchar(15),
    `mobile`       varchar(15),
    `notified`     BOOLEAN               NOT NULL DEFAULT FALSE,
    `ownerId`      int                   NULL,
    `departmentId` int          UNSIGNED NULL,
    `insertDate`   datetime              DEFAULT NOW(),
    `updateDate`   datetime              DEFAULT NOW(),
    email2         varchar(128),
    phone2         varchar(15),
    fax            varchar(15),
    address        varchar(100),
    postalCode     varchar(5),
    city           varchar(100),
    country        varchar(100),
    provinceId     INTEGER,
    active         boolean               DEFAULT TRUE,

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_contact_province` FOREIGN KEY (`provinceId`) REFERENCES Province (id)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE Position
(
    id           int              NOT NULL AUTO_INCREMENT,
    name         varchar(256)     NOT NULL,
    description  varchar(1024)    NOT NULL,
    ownerId      INTEGER          NULL,
    departmentId INTEGER UNSIGNED NULL,
    deleteDate   datetime         DEFAULT NULL,
    email        varchar(128),
    phone        varchar(15),
    fax          varchar(15),
    address      varchar(100),
    postalCode   varchar(5),
    city         varchar(100),
    country      varchar(100),
    provinceId   INTEGER,
    `insertDate` datetime         DEFAULT NOW(),
    `updateDate` datetime         DEFAULT NOW(),

    PRIMARY KEY (id),
    CONSTRAINT `fk_position_province` FOREIGN KEY (`provinceId`) REFERENCES Province (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;

CREATE TABLE Position_Department
(
    id           int          NOT NULL AUTO_INCREMENT,
    positionId   int          NOT NULL,
    departmentId int UNSIGNED NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_position_department_position FOREIGN KEY (positionId) REFERENCES Position (id),
    CONSTRAINT fk_position_department_department FOREIGN KEY (departmentId) REFERENCES Department (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;

CREATE TABLE Department_Organization
(
    id             int          NOT NULL AUTO_INCREMENT,
    departmentId   int UNSIGNED NOT NULL,
    organizationId int          NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_department_organization_department FOREIGN KEY (departmentId) REFERENCES Department (id),
    CONSTRAINT fk_department_organization_organization FOREIGN KEY (organizationId) REFERENCES Organization (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;

CREATE TABLE ContactInfo
(
    id             int          NOT NULL AUTO_INCREMENT,
    contactId      int          NOT NULL,
    positionId     int          NOT NULL,
    departmentId   int UNSIGNED NOT NULL,
    organizationId int          NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_contactinfo_contact FOREIGN KEY (contactId) REFERENCES Contact (id),
    CONSTRAINT fk_contactinfo_position FOREIGN KEY (positionId) REFERENCES Position (id),
    CONSTRAINT fk_contactinfo_department FOREIGN KEY (departmentId) REFERENCES Department (id),
    CONSTRAINT fk_contactinfo_organization FOREIGN KEY (organizationId) REFERENCES Organization (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `Project`
(
    `id`             int                    NOT NULL AUTO_INCREMENT,
    `organizationId` int                    NOT NULL,
    `startDate`      date                   NOT NULL,
    `endDate`        date,
    `open`           boolean                DEFAULT FALSE,
    `name`           varchar(128)           NOT NULL,
    `description`    varchar(2048),
    `ownerId`        int                    NULL,
    `departmentId`   int           UNSIGNED NULL,
    `billable`       boolean                NOT NULL DEFAULT TRUE,
    offerId          INT                    NULL,
    `insertDate`     datetime               DEFAULT NOW(),
    `updateDate`     datetime               DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_project_organizationId` FOREIGN KEY (`organizationId`) REFERENCES `Organization` (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;

CREATE TABLE `ProjectRole`
(
    `id`            int                     NOT NULL AUTO_INCREMENT,
    `projectId`     int                     NOT NULL,
    `name`          varchar(128)            NOT NULL,
    `costPerHour`   decimal(10, 2)          NOT NULL,
    `expectedHours` int                     NOT NULL,
    requireEvidence BOOLEAN                 NOT NULL,
    `ownerId`       int                     NULL,
    `departmentId`  int            UNSIGNED NULL,
    `insertDate`    datetime                DEFAULT NOW(),
    `updateDate`    datetime                DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_projectRole_projectId` FOREIGN KEY (`projectId`) REFERENCES `Project` (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;

CREATE TABLE `ProjectCost`
(
    `id`           int                     NOT NULL AUTO_INCREMENT,
    `projectId`    int                     NOT NULL,
    `name`         varchar(128)            NOT NULL,
    `cost`         decimal(10, 2)          NOT NULL,
    `billable`     boolean                 NOT NULL DEFAULT TRUE,
    `ownerId`      int                     NULL,
    `departmentId` int            UNSIGNED NULL,
    `insertDate`   datetime                DEFAULT NOW(),
    `updateDate`   datetime                DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_projectCost_projectId` FOREIGN KEY (`projectId`) REFERENCES `Project` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `Role`
(
    `id`           int                  NOT NULL AUTO_INCREMENT,
    `name`         varchar(64)          NOT NULL,
    `ownerId`      int                  NULL,
    `departmentId` int         UNSIGNED NULL,
    `insertDate`   datetime             DEFAULT NOW(),
    `updateDate`   datetime             DEFAULT NOW(),

    PRIMARY KEY (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci COMMENT ='Security application roles';

CREATE TABLE `ContractType`
(
    `id`           int           UNSIGNED                   NOT NULL AUTO_INCREMENT,
    `name`         varchar(128)                             NOT NULL,
    `description`  varchar(2048) COLLATE utf8mb4_spanish_ci NOT NULL,
    `ownerId`      int                                      NULL,
    `departmentId` int           UNSIGNED                   NULL,
    `insertDate`   datetime                                 DEFAULT NOW(),
    `updateDate`   datetime                                 DEFAULT NOW(),

    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;

CREATE TABLE `WorkingAgreement`
(
    `id`           int                  NOT NULL AUTO_INCREMENT,
    `name`         varchar(128)         NOT NULL,
    `description`  varchar(2048),
    `holidays`     int                  NOT NULL DEFAULT 22,
    `ownerId`      int                  NULL,
    `departmentId` int         UNSIGNED NULL,
    yearDuration   integer              NOT NULL COMMENT 'In minutes',
    `insertDate`   datetime             DEFAULT NOW(),
    `updateDate`   datetime             DEFAULT NOW(),

    PRIMARY KEY (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;

CREATE TABLE IF NOT EXISTS WorkingAgreementTerms
(
    id                 int  NOT NULL AUTO_INCREMENT,
    effectiveFrom      date NOT NULL,
    vacation           int  NOT NULL,
    annualWorkingTime  int  NOT NULL,
    workingAgreementId int  NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT uk_effective_from_agreement_id UNIQUE (effectiveFrom, workingAgreementId),
    CONSTRAINT fk_WorkingAgreementTerms_WorkingAgreement FOREIGN KEY (workingAgreementId) REFERENCES WorkingAgreement (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE `UserCategory`
(
    `id`           int                  NOT NULL AUTO_INCREMENT,
    `name`         varchar(64)          NOT NULL,
    `ownerId`      int                  NULL,
    `departmentId` int         UNSIGNED NULL,
    `insertDate`   datetime             DEFAULT NOW(),
    `updateDate`   datetime             DEFAULT NOW(),

    PRIMARY KEY (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;

CREATE TABLE `User`
(
    `id`                    int                       NOT NULL AUTO_INCREMENT,

    -- Application data
    `login`                 varchar(50)               NOT NULL,
    `password`              varchar(50)               NOT NULL,
    `passwordExpireDate`    DATE,
    `roleId`                int                       NOT NULL,
    `active`                boolean                   NOT NULL DEFAULT TRUE,

    -- Personal data
    `name`                  varchar(200)              NOT NULL,
    `nif`                   varchar(16),
    `birthDate`             date,
    `academicQualification` varchar(200),

    `phone`                 varchar(12),
    `mobile`                varchar(12),

    `street`                varchar(100),
    `city`                  varchar(100),
    `postalCode`            varchar(5),
    `provinceId`            int,

    `married`               boolean                   NOT NULL COMMENT 'Married (1) or not (0)',
    `childrenNumber`        tinyint                   NOT NULL,
    `drivenLicenseType`     varchar(10),
    `vehicleType`           varchar(50),
    `licensePlate`          varchar(45),

    -- Company data
    `startDate`             date                      NOT NULL,
    `categoryId`            int                       NOT NULL,
    `socialSecurityNumber`  varchar(45),
    `bank`                  varchar(100),
    `account`               varchar(34),
    `travelAvailability`    varchar(128),
    `workingInClient`       boolean                   NOT NULL,
    `email`                 varchar(128),
    `genre`                 VARCHAR(16),
    `salary`                DECIMAL(10, 2),
    `salaryExtras`          DECIMAL(10, 2),
    `documentCategoryId`    int UNSIGNED,
    `securityCard`          VARCHAR(64),
    `healthInsurance`       VARCHAR(64),

    `notes`                 VARCHAR(1024),
    `photo`                 varchar(255),
    `endTestPeriodDate`     date,
    `endContractDate`       date,
    `departmentId`          int             UNSIGNED  NOT NULL DEFAULT 1,
    `contractTypeId`        int             UNSIGNED,
    `contractObservations`  varchar(2048),
    `dayDuration`           integer                   NOT NULL COMMENT 'In minutes',
    `agreementId`           int                       NOT NULL,
    agreementYearDuration   integer,
    `insertDate`            DATETIME                  DEFAULT NOW(),
    `updateDate`            DATETIME                  DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_user_roleId` FOREIGN KEY (`roleId`) REFERENCES `Role` (`id`),
    CONSTRAINT `fk_user_provinceId` FOREIGN KEY (`provinceId`) REFERENCES `Province` (`id`),
    CONSTRAINT `fk_user_categoryId` FOREIGN KEY (`categoryId`) REFERENCES `UserCategory` (`id`),
    CONSTRAINT `fk_user_documentCategoryId` FOREIGN KEY (`documentCategoryId`) REFERENCES `DocumentCategory` (`id`),
    CONSTRAINT `fk_user_departmentId` FOREIGN KEY (`departmentId`) REFERENCES `Department` (`id`),
    CONSTRAINT `fk_user_contractTypeId` FOREIGN KEY (`contractTypeId`) REFERENCES `ContractType` (`id`),
    CONSTRAINT `fk_user_agreementId` FOREIGN KEY (`agreementId`) REFERENCES `WorkingAgreement` (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;

CREATE TABLE AnnualWorkSummary
(
    userId       INT           NOT NULL,
    year         INT           NOT NULL,
    targetHours  DECIMAL(7, 2) NOT NULL,
    workedHours  DECIMAL(7, 2) NOT NULL,

    `insertDate` datetime      DEFAULT NOW(),
    `updateDate` datetime      DEFAULT NOW(),

    PRIMARY KEY (userId, year),
    CONSTRAINT fk_annualworksummary_user FOREIGN KEY (userId) REFERENCES User (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;

CREATE TABLE AnnualWorkSummaryJob
(
    `id`     INT      NOT NULL AUTO_INCREMENT,
    started  DATETIME NOT NULL,
    finished DATETIME NOT NULL,

    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `Activity`
(
    `id`           int            NOT NULL AUTO_INCREMENT,
    `userId`       int            NOT NULL,
    `startDate`    datetime       NOT NULL DEFAULT '2000-01-01 00:00:00',
    `duration`     int            NOT NULL COMMENT 'Duration in minutes',
    `billable`     BOOLEAN        NOT NULL DEFAULT TRUE,
    `roleId`       INTEGER,
    `description`  varchar(2048),
    `departmentId` int UNSIGNED   NULL,
    hasImage       BOOLEAN        NOT NULL,
    `insertDate`   datetime       DEFAULT NOW(),
    `updateDate`   datetime       DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_activity_userId` FOREIGN KEY (`userId`) REFERENCES `User` (`id`),
    CONSTRAINT `fk_activity_roleId` FOREIGN KEY (`roleId`)
        REFERENCES `ProjectRole` (`id`)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `AccountType`
(
    `id`           int                   NOT NULL AUTO_INCREMENT,
    `name`         varchar(128)          NOT NULL COMMENT 'Account type descriptive name',
    `ownerId`      int                   NULL,
    `departmentId` int          UNSIGNED NULL,
    `insertDate`   datetime              DEFAULT NOW(),
    `updateDate`   datetime              DEFAULT NOW(),

    PRIMARY KEY (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `Account`
(
    `id`            int                    NOT NULL AUTO_INCREMENT,
    `name`          varchar(128)           NOT NULL COMMENT 'Account descriptive name',
    `number`        varchar(20)            NOT NULL,
    `accountTypeId` int                    NOT NULL,
    `description`   varchar(2048),
    `ownerId`       int                    NULL,
    `departmentId`  int           UNSIGNED NULL,
    `insertDate`    datetime               DEFAULT NOW(),
    `updateDate`    datetime               DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_account_accountTypeId` FOREIGN KEY (`accountTypeId`) REFERENCES `AccountType` (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `AccountEntryGroup`
(
    `id`           int                     NOT NULL AUTO_INCREMENT,
    `name`         varchar(128)            NOT NULL COMMENT 'Account entry group descriptive name',
    `description`  varchar(1024),
    `ownerId`      int                     NULL,
    `departmentId` int            UNSIGNED NULL,
    `insertDate`   datetime                DEFAULT NOW(),
    `updateDate`   datetime                DEFAULT NOW(),

    PRIMARY KEY (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `AccountEntryType`
(
    `id`                  int                     NOT NULL AUTO_INCREMENT,
    `accountEntryGroupId` int                     NOT NULL,
    `name`                varchar(256)            NOT NULL COMMENT 'Account entry type descriptive name',
    `observations`        varchar(1024),
    `accountEntryTypeId`  INTEGER,
    `ownerId`             int                     NULL,
    `departmentId`        int            UNSIGNED NULL,
    `customizableId`      int                     NULL,
    `insertDate`          datetime                DEFAULT NOW(),
    `updateDate`          datetime                DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_accountEntryType_accountEntryGroupId` FOREIGN KEY (`accountEntryGroupId`) REFERENCES `AccountEntryGroup` (`id`),
    CONSTRAINT `fk_accountentrytype_accountEntryTypeId` FOREIGN KEY (`accountEntryTypeId`) REFERENCES `AccountEntryType` (`id`)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `AccountEntry`
(
    `id`                 int                     NOT NULL AUTO_INCREMENT,
    `accountId`          int                     NOT NULL COMMENT 'Account where the entry is charged',
    `accountEntryTypeId` int                     NOT NULL,
    `entryDate`          date                    NOT NULL,
    `entryAmountDate`    date                    NOT NULL COMMENT 'Account entry amount date (fecha valor)',
    `concept`            varchar(1024)           NOT NULL,
    `amount`             decimal(10, 2)          NOT NULL,
    `observations`       varchar(1024),
    `ownerId`            int                     NULL,
    `departmentId`       int            UNSIGNED NULL,
    `entryNumber`        varchar(16)             NULL,
    `docNumber`          varchar(50)             NULL,
    `insertDate`         datetime                DEFAULT NOW(),
    `updateDate`         datetime                DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_accountEntry_accountId` FOREIGN KEY (`accountId`) REFERENCES `Account` (`id`),
    CONSTRAINT `fk_accountEntry_accountEntryTypeId` FOREIGN KEY (`accountEntryTypeId`) REFERENCES `AccountEntryType` (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE IVAType
(
    id           int           NOT NULL COMMENT 'El id no es autoincremental porque ya tienen unos codigos fijos',
    iva          decimal(4, 2) DEFAULT 21.00,
    name         varchar(30)   DEFAULT 'IVA General',
    ownerId      int           DEFAULT NULL,
    departmentId int           DEFAULT NULL,
    insertDate   datetime      DEFAULT NULL,
    updateDate   datetime      DEFAULT NULL,

    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci COMMENT ='Tipos de IVA';

-- -----------------------------------------------------------------------------
-- IVAReason
-- -----------------------------------------------------------------------------

CREATE TABLE IVAReason
(
    id           int                   NOT NULL,
    code         varchar(2)            NOT NULL,
    reason       varchar(70)           NOT NULL,
    exempt       bool                  NOT NULL,
    ownerId      int,
    departmentId int         UNSIGNED,
    `insertDate` datetime              DEFAULT NOW(),
    `updateDate` datetime              DEFAULT NOW(),

    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE BillCategory
(
    id           int          NOT NULL,
    code         varchar(2)   NOT NULL,
    name         varchar(100) NOT NULL,
    rectify      bool         NOT NULL COMMENT 'Use TRUE to bills that reftify another bill',
    ownerId      int,
    departmentId int,
    `insertDate` datetime     DEFAULT NOW(),
    `updateDate` datetime     DEFAULT NOW(),

    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE RectifiedBillCategory
(
    id           int         NOT NULL,
    code         varchar(2)  NOT NULL,
    name         varchar(40) NOT NULL,
    ownerId      int,
    departmentId int,
    `insertDate` datetime    DEFAULT NOW(),
    `updateDate` datetime    DEFAULT NOW(),

    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE BillRegime
(
    id                 int                                     NOT NULL,
    code               varchar(2)                              NOT NULL,
    name               varchar(250)                            NOT NULL,
    associatedBillType varchar(16)  COLLATE utf8mb4_spanish_ci NOT NULL,
    ownerId            int,
    departmentId       int,
    `insertDate`       datetime                                DEFAULT NOW(),
    `updateDate`       datetime                                DEFAULT NOW(),

    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `Bill`
(
    `id`                    int                     NOT NULL AUTO_INCREMENT,
    `creationDate`          date                    NOT NULL,
    `paymentMode`           varchar(16),
    `state`                 varchar(16)             NOT NULL,
    `number`                VARCHAR(64)             NOT NULL,
    `name`                  VARCHAR(4096)           NOT NULL,
    `file`                  VARCHAR(512),
    `fileMime`              VARCHAR(64),
    `observations`          VARCHAR(4096),
    `projectId`             INT                     NOT NULL DEFAULT 5,
    `startBillDate`         DATE                    NOT NULL DEFAULT '1980-01-01',
    `endBillDate`           DATE                    NOT NULL DEFAULT '1980-01-01',
    `billType`              VARCHAR(16)             NOT NULL DEFAULT 'ISSUED',
    `orderNumber`           VARCHAR(64),
    `bookNumber`            VARCHAR(64),
    `contactId`             INT,
    `providerId`            INT,
    `ownerId`               INT                     NULL,
    `departmentId`          INT            UNSIGNED NULL,
    `accountId`             INTEGER,
    submitted               INT                     NOT NULL,
    billCategoryId          INT                     NOT NULL,
    rectifiedBillCategoryId INT,
    provideService          bool                    NOT NULL,
    billRegimeId            INT                     NOT NULL,
    deductibleIVAPercentage TINYINT                 NOT NULL,
    freelanceIRPFPercentage INT                     NOT NULL,
    `insertDate`            DATETIME                DEFAULT NOW(),
    `updateDate`            DATETIME                DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_bill_projectId` FOREIGN KEY (`projectId`) REFERENCES `Project` (`id`),
    CONSTRAINT `fk_bill_contactId` FOREIGN KEY (`contactId`) REFERENCES `Contact` (`id`),
    CONSTRAINT `fk_bill_providerId` FOREIGN KEY (`providerId`) REFERENCES `Organization` (`id`),
    CONSTRAINT `fk_bill_accountId` FOREIGN KEY (accountId) REFERENCES `Account` (id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,
    CONSTRAINT `fk_bill_billCategory` FOREIGN KEY (billCategoryId) REFERENCES BillCategory (id),
    CONSTRAINT `fk_bill_rectifiedBillCategory` FOREIGN KEY (rectifiedBillCategoryId) REFERENCES RectifiedBillCategory (id),
    CONSTRAINT `fk_bill_billRegime` FOREIGN KEY (billRegimeId) REFERENCES BillRegime (id)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `Bill_AccountEntry`
(
    `billId`         int NOT NULL,
    `accountEntryId` int NOT NULL,
    `observations`   varchar(2048),

    PRIMARY KEY (`billId`, `accountEntryId`),
    CONSTRAINT `fk_billAccountEntry_billId` FOREIGN KEY (`billId`) REFERENCES `Bill` (`id`),
    CONSTRAINT `fk_billAccountEntry_accountEntryId` FOREIGN KEY (`accountEntryId`) REFERENCES `AccountEntry` (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE IF NOT EXISTS `BillBreakDown`
(
    `id`               int            UNSIGNED                   NOT NULL AUTO_INCREMENT,
    `billId`           int                                       NOT NULL,
    `concept`          varchar(4096)  COLLATE utf8mb4_spanish_ci NOT NULL,
    `units`            decimal(10, 2)                            NOT NULL DEFAULT 1,
    `amount`           decimal(10, 2)                            NOT NULL,
    `iva`              decimal(4, 2)                             NOT NULL DEFAULT 16,
    `ownerId`          int                                       NULL,
    `departmentId`     int            UNSIGNED                   NULL,
    `place`            INTEGER                                   DEFAULT NULL,
    IVAReasonId        INT,
    IVAReasonIdOnlySii INT,
    ivaOnlySII         decimal(4, 2),
    `insertDate`       datetime                                  DEFAULT NOW(),
    `updateDate`       datetime                                  DEFAULT NOW(),

    PRIMARY KEY (`id`),
    INDEX `ndx_billBreakDown_bill` (`billId`),
    CONSTRAINT `fk_billBreakDown_bill` FOREIGN KEY (`billId`) REFERENCES `Bill` (`id`),
    CONSTRAINT `fk_billBreakDown_ivaReason` FOREIGN KEY (IVAReasonId) REFERENCES IVAReason (id),
    CONSTRAINT `fk_billBreakDown_ivaReasonOnlySii` FOREIGN KEY (IVAReasonIdOnlySii) REFERENCES IVAReason (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `BillPayment`
(
    `id`             INTEGER                 NOT NULL AUTO_INCREMENT,
    `billId`         INTEGER                 NOT NULL,
    `amount`         DECIMAL(10, 2)          NOT NULL DEFAULT 0,
    `expirationDate` DATE                    NOT NULL,
    `ownerId`        INTEGER                 NULL,
    `departmentId`   INTEGER        UNSIGNED NULL,
    `insertDate`     datetime                DEFAULT NOW(),
    `updateDate`     datetime                DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_bill_billPayment` FOREIGN KEY (`billId`) REFERENCES `Bill` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `BulletinBoardCategory`
(
    `id`           int                   NOT NULL AUTO_INCREMENT,
    `name`         varchar(64)           NOT NULL,
    `ownerId`      int                   NULL,
    `departmentId` int          UNSIGNED NULL,
    `insertDate`   datetime              DEFAULT NOW(),
    `updateDate`   datetime              DEFAULT NOW(),

    PRIMARY KEY (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `CompanyState`
(
    `id`           int               NOT NULL AUTO_INCREMENT,
    `userId`       int               NOT NULL,
    `creationDate` datetime          NOT NULL,
    `description`  longtext          NOT NULL,
    `departmentId` int      UNSIGNED NULL,
    `insertDate`   datetime          DEFAULT NOW(),
    `updateDate`   datetime          DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_companystate_userId` FOREIGN KEY (`userId`) REFERENCES `User` (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `Idea`
(
    `id`           int                     NOT NULL AUTO_INCREMENT,
    `userId`       int                     NOT NULL,
    `creationDate` datetime                NOT NULL,
    `description`  varchar(2048)           NOT NULL,
    `cost`         varchar(500),
    `benefits`     varchar(2048),
    `name`         varchar(300)            NOT NULL,
    `departmentId` int            UNSIGNED NULL,
    `insertDate`   datetime                DEFAULT NOW(),
    `updateDate`   datetime                DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_idea_userId` FOREIGN KEY (`userId`) REFERENCES `User` (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `Inventory`
(
    `id`           int                    NOT NULL AUTO_INCREMENT,
    `buyDate`      date,
    `asignedToId`  int,
    `renting`      boolean                NOT NULL DEFAULT FALSE COMMENT 'Renting (1) or not (0)',
    `cost`         decimal(10, 2),
    `amortizable`  boolean                NOT NULL DEFAULT FALSE COMMENT 'Amortizable (1) or not (0)consumible',
    `serialNumber` varchar(30)            NOT NULL,
    `type`         varchar(16)            NOT NULL,
    `provider`     varchar(128),
    `trademark`    varchar(128),
    `model`        varchar(128),
    `speed`        varchar(10),
    `storage`      varchar(10),
    `ram`          varchar(10),
    `location`     varchar(128),
    `description`  varchar(256),
    `ownerId`      int                    NULL,
    `departmentId` int           UNSIGNED NULL,
    `insertDate`   datetime               DEFAULT NOW(),
    `updateDate`   datetime               DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_inventory_userId` FOREIGN KEY (`asignedToId`) REFERENCES `User` (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `Objective`
(
    `id`           int                  NOT NULL AUTO_INCREMENT,
    `userId`       int                  NOT NULL,
    `projectId`    int                  NOT NULL,
    `startDate`    date                 NOT NULL,
    `endDate`      date,
    `state`        varchar(16),
    `name`         varchar(512)         NOT NULL,
    `log`          longtext,
    `departmentId` int         UNSIGNED NULL,
    `insertDate`   datetime             DEFAULT NOW(),
    `updateDate`   datetime             DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_objective_projectId` FOREIGN KEY (`userId`) REFERENCES `User` (`id`),
    CONSTRAINT `fk_objective_userId` FOREIGN KEY (`projectId`) REFERENCES `Project` (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `Magazine`
(
    `id`           int                     NOT NULL AUTO_INCREMENT,
    `name`         varchar(128)            NOT NULL,
    `description`  varchar(2048),
    `ownerId`      int                     NULL,
    `departmentId` int            UNSIGNED NULL,
    `insertDate`   datetime                DEFAULT NOW(),
    `updateDate`   datetime                DEFAULT NOW(),

    PRIMARY KEY (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `Tutorial`
(
    `id`              int                     NOT NULL AUTO_INCREMENT,
    `userId`          int                     NOT NULL,
    `maxDeliveryDate` datetime                NOT NULL,
    `endDate`         datetime                DEFAULT NULL,
    `name`            varchar(128)            NOT NULL,
    `description`     varchar(2048),
    `departmentId`    int            UNSIGNED NULL,
    `insertDate`      datetime                DEFAULT NOW(),
    `updateDate`      datetime                DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_tutorial_userId` FOREIGN KEY (`userId`) REFERENCES `User` (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `Publication`
(
    `id`                      int                   NOT NULL AUTO_INCREMENT,
    `name`                    varchar(128)          NOT NULL,
    `magazineId`              int                   NOT NULL,
    `magazineDeliveryDate`    datetime              DEFAULT NULL,
    `magazinePublicationDate` date,
    `ownPublicationDate`      date,
    `accepted`                boolean               COMMENT 'Accepted (1) or not (0)',
    `ownerId`                 int                   NULL,
    `departmentId`            int          UNSIGNED NULL,
    `insertDate`              datetime              DEFAULT NOW(),
    `updateDate`              datetime              DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_publication_magazineId` FOREIGN KEY (`magazineId`) REFERENCES `Magazine` (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `BulletinBoard`
(
    `id`                  int                    NOT NULL AUTO_INCREMENT,
    `categoryId`          int                    NOT NULL,
    `userId`              int                    NOT NULL,
    `creationDate`        datetime               NOT NULL,
    `message`             varchar(2048)          NOT NULL,
    `title`               varchar(128)           NOT NULL,
    `documentPath`        varchar(128),
    `documentContentType` varchar(128),
    `departmentId`        int           UNSIGNED NULL,
    `insertDate`          datetime               DEFAULT NOW(),
    `updateDate`          datetime               DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_bulletinboard_categoryId` FOREIGN KEY (`categoryId`) REFERENCES `BulletinBoardCategory` (`id`),
    CONSTRAINT `fk_bulletinboard_userId` FOREIGN KEY (`userId`) REFERENCES `User` (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `Book`
(
    `id`           int            UNSIGNED                   NOT NULL AUTO_INCREMENT,
    `name`         varchar(255)   COLLATE utf8mb4_spanish_ci NOT NULL,
    `author`       varchar(255)   COLLATE utf8mb4_spanish_ci DEFAULT NULL,
    `ISBN`         varchar(13)    COLLATE utf8mb4_spanish_ci DEFAULT NULL,
    `URL`          varchar(255)   COLLATE utf8mb4_spanish_ci DEFAULT NULL,
    `price`        decimal(10, 2)                            DEFAULT NULL,
    `purchaseDate` datetime                                  DEFAULT NULL,
    `userId`       int                                       DEFAULT NULL,
    `ownerId`      int                                       NULL,
    `departmentId` int            UNSIGNED                   NULL,
    `insertDate`   datetime                                  DEFAULT NOW(),
    `updateDate`   datetime                                  DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_Book_userId` FOREIGN KEY (`userId`) REFERENCES `User` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `Frequency`
(
    `id`           int                                     NOT NULL AUTO_INCREMENT,
    `name`         varchar(255) COLLATE utf8mb4_spanish_ci NOT NULL,
    `months`       INTEGER      UNSIGNED,
    `ownerId`      int                                     NULL,
    `departmentId` int          UNSIGNED                   NULL,
    `insertDate`   datetime                                DEFAULT NOW(),
    `updateDate`   datetime                                DEFAULT NOW(),

    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `PeriodicalAccountEntry`
(
    `id`                 int                     NOT NULL AUTO_INCREMENT,
    `accountId`          int                     NOT NULL COMMENT 'Account where the entry is charged',
    `accountEntryTypeId` int                     NOT NULL,
    `frequencyId`        int                     NOT NULL,
    `concept`            varchar(1024)           NOT NULL,
    `entryDate`          date                    NOT NULL,
    `amount`             decimal(10, 2)          NOT NULL,
    `rise`               decimal(4, 2),
    `observations`       varchar(1024),
    `ownerId`            int                     NULL,
    `departmentId`       int            UNSIGNED NULL,
    `organizationId`     INTEGER                 DEFAULT NULL,
    `insertDate`         datetime                DEFAULT NOW(),
    `updateDate`         datetime                DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_periodicalAccountEntry_accountId` FOREIGN KEY (`accountId`) REFERENCES `Account` (`id`),
    CONSTRAINT `fk_periodicalAccountEntry_accountEntryTypeId` FOREIGN KEY (`accountEntryTypeId`) REFERENCES `AccountEntryType` (`id`),
    CONSTRAINT `fk_periodicalAccountEntry_frequencyId` FOREIGN KEY (`frequencyId`) REFERENCES `Frequency` (`id`),
    CONSTRAINT `fk_periodicalaccountentry_organizationId` FOREIGN KEY (`organizationId`) REFERENCES `Organization` (`id`)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `Holiday`
(
    `id`           int           UNSIGNED                   NOT NULL AUTO_INCREMENT,
    `description`  varchar(1024) COLLATE utf8mb4_spanish_ci NOT NULL,
    `date`         datetime                                 NOT NULL,
    `ownerId`      int                                      NULL,
    `departmentId` int           UNSIGNED                   NULL,
    `insertDate`   datetime                                 DEFAULT NOW(),
    `updateDate`   datetime                                 DEFAULT NOW(),

    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `RequestHoliday`
(
    `id`           int           UNSIGNED                   NOT NULL AUTO_INCREMENT,
    `beginDate`    datetime                                 NOT NULL,
    `finalDate`    datetime                                 NOT NULL,
    `state`        varchar(16)   COLLATE utf8mb4_spanish_ci NOT NULL,
    `userId`       int                                      NOT NULL,
    `observations` varchar(1024) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
    `departmentId` int           UNSIGNED                   NULL,
    `userComment`  varchar(1024)                            NULL,
    `chargeYear`   date                                     NOT NULL,
    `insertDate`   datetime                                 DEFAULT NOW(),
    `updateDate`   datetime                                 DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_requestHoliday_userId` FOREIGN KEY (`userId`) REFERENCES `User` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `OfferRejectReason`
(
    `id`           int                     NOT NULL AUTO_INCREMENT,
    `title`        varchar(128)            NOT NULL,
    `description`  varchar(1024),
    `ownerId`      int                     NULL,
    `departmentId` int            UNSIGNED NULL,
    `insertDate`   datetime                DEFAULT NOW(),
    `updateDate`   datetime                DEFAULT NOW(),

    PRIMARY KEY (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `Offer`
(
    `id`                  int                    NOT NULL AUTO_INCREMENT,
    `number`              varchar(64)            NOT NULL,
    `title`               varchar(128)           NOT NULL,
    `description`         varchar(4096),
    `userId`              int                    NOT NULL,
    `organizationId`      int                    NOT NULL,
    `contactId`           int                    NOT NULL,
    `creationDate`        date                   NOT NULL,
    `validityDate`        date                   NULL,
    `maturityDate`        date                   NULL,
    `offerPotential`      varchar(16)            NOT NULL,
    `offerState`          varchar(16)            NOT NULL,
    `offerRejectReasonId` int                    NULL,
    `ownerId`             int                    NULL,
    `departmentId`        int           UNSIGNED NULL,
    `observations`        varchar(4096)          NULL,
    `showIvaIntoReport`   boolean                NOT NULL DEFAULT TRUE,
    `insertDate`          datetime               DEFAULT NOW(),
    `updateDate`          datetime               DEFAULT NOW(),

    PRIMARY KEY (`id`),
    INDEX ndx_offer_number (`number` ASC),
    CONSTRAINT `fk_offer_organizationId` FOREIGN KEY (`organizationId`) REFERENCES `Organization` (`id`),
    CONSTRAINT `fk_offer_contactId` FOREIGN KEY (`contactId`) REFERENCES `Contact` (`id`),
    CONSTRAINT `fk_offer_offerRejectReasonId` FOREIGN KEY (`offerRejectReasonId`) REFERENCES `OfferRejectReason` (`id`),
    CONSTRAINT `fk_offer_userId` FOREIGN KEY (`userId`) REFERENCES `User` (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `OfferRole`
(
    `id`            int                     NOT NULL AUTO_INCREMENT,
    `offerId`       int                     NOT NULL,
    `name`          varchar(4096)           NOT NULL,
    `costPerHour`   decimal(10, 2)          NOT NULL,
    `expectedHours` int                     NOT NULL,
    `iva`           decimal(4, 2)           NOT NULL DEFAULT 16,
    `ownerId`       int                     NULL,
    `departmentId`  int            UNSIGNED NULL,
    `place`         INTEGER                 DEFAULT NULL,
    `insertDate`    datetime                DEFAULT NOW(),
    `updateDate`    datetime                DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_offerRole_offerId` FOREIGN KEY (`offerId`) REFERENCES `Offer` (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `OfferCost`
(
    `id`           int                     NOT NULL AUTO_INCREMENT,
    `offerId`      int                     NOT NULL,
    `name`         varchar(4096)           NOT NULL,
    `cost`         decimal(10, 2)          NOT NULL,
    `billable`     boolean                 NOT NULL DEFAULT TRUE,
    `iva`          decimal(4, 2)           NOT NULL DEFAULT 16,
    `ownerId`      int                     NULL,
    `departmentId` int            UNSIGNED NULL,
    `units`        decimal(10, 2)          NOT NULL,
    `place`        INTEGER                 DEFAULT NULL,
    `insertDate`   datetime                DEFAULT NOW(),
    `updateDate`   datetime                DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_offerCost_offerId` FOREIGN KEY (`offerId`) REFERENCES `Offer` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `FinancialRatio`
(
    `id`                        int                     NOT NULL AUTO_INCREMENT,
    `title`                     varchar(128)            NOT NULL,
    `ratioDate`                 date                    NOT NULL,
    `banksAccounts`             decimal(10, 2)          NOT NULL,
    `customers`                 decimal(10, 2)          NOT NULL,
    `stocks`                    decimal(10, 2)          NOT NULL,
    `amortizations`             decimal(10, 2)          NOT NULL,
    `infrastructures`           decimal(10, 2)          NOT NULL,
    `shortTermLiability`        decimal(10, 2)          NOT NULL,
    `obligationBond`            decimal(10, 2)          NOT NULL,
    `capital`                   decimal(10, 2)          NOT NULL,
    `reserves`                  decimal(10, 2)          NOT NULL,
    `incomes`                   decimal(10, 2)          NOT NULL,
    `expenses`                  decimal(10, 2)          NOT NULL,
    `otherExploitationExpenses` decimal(10, 2)          NOT NULL,
    `financialExpenses`         decimal(10, 2)          NOT NULL,
    `taxes`                     decimal(10, 2)          NOT NULL,
    `ownerId`                   int                     NULL,
    `departmentId`              int            UNSIGNED NULL,
    `insertDate`                datetime                DEFAULT NOW(),
    `updateDate`                datetime                DEFAULT NOW(),

    PRIMARY KEY (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `InteractionType`
(
    `id`           int                    NOT NULL AUTO_INCREMENT,
    `name`         varchar(128)           NOT NULL COMMENT 'Interaction type descriptive name',
    `description`  varchar(1024)                   COMMENT 'Interaction type description',
    `ownerId`      int                    NULL,
    `departmentId` int           UNSIGNED NULL,
    `insertDate`   datetime               DEFAULT NOW(),
    `updateDate`   datetime               DEFAULT NOW(),

    PRIMARY KEY (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `Interaction`
(
    `id`                int                    NOT NULL AUTO_INCREMENT,
    `projectId`         int                    NOT NULL DEFAULT 5,
    `userId`            int                    NOT NULL DEFAULT 1,
    `interactionTypeId` int                    NOT NULL DEFAULT 6,
    `creationDate`      datetime               NOT NULL,
    `interest`          varchar(16)            NOT NULL,
    `description`       varchar(2048)          NOT NULL,
    `file`              varchar(400),
    `fileMime`          varchar(128),
    `departmentId`      int           UNSIGNED NULL,
    `offerId`           int                    NULL,
    `insertDate`        datetime               DEFAULT NOW(),
    `updateDate`        datetime               DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_interaction_projectId` FOREIGN KEY (`projectId`) REFERENCES `Project` (`id`),
    CONSTRAINT `fk_interaction_userId` FOREIGN KEY (`userId`) REFERENCES `User` (`id`),
    CONSTRAINT `fk_interaction_interactionTypeId` FOREIGN KEY (`interactionTypeId`) REFERENCES `InteractionType` (`id`),
    CONSTRAINT `fk_interaction_offerId` FOREIGN KEY (`offerId`) REFERENCES `Offer` (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `Setting`
(
    `id`           int                                      NOT NULL AUTO_INCREMENT,
    `type`         varchar(64)   COLLATE utf8mb4_spanish_ci NOT NULL,
    `name`         varchar(1024) COLLATE utf8mb4_spanish_ci NOT NULL,
    `value`        varchar(4096) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
    `ownerId`      int                                      NULL,
    `departmentId` int           UNSIGNED                   NULL,
    `insertDate`   datetime                                 DEFAULT NOW(),
    `updateDate`   datetime                                 DEFAULT NOW(),

    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci COMMENT ='User settings';


CREATE TABLE `Occupation`
(
    `id`           int                     NOT NULL AUTO_INCREMENT,
    `projectId`    int                     NOT NULL,
    `userId`       int                     NOT NULL,
    `startDate`    date                    NOT NULL,
    `endDate`      date                    NOT NULL,
    `description`  varchar(1024),
    `duration`     int                     NOT NULL COMMENT 'In minutes',
    `ownerId`      int                     NULL,
    `departmentId` int            UNSIGNED NULL,
    `insertDate`   datetime                DEFAULT NOW(),
    `updateDate`   datetime                DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_occupation_userId` FOREIGN KEY (`userId`) REFERENCES `User` (`id`),
    CONSTRAINT `fk_occupation_projectId` FOREIGN KEY (`projectId`) REFERENCES `Project` (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci COMMENT ='Future occupations of Users';


CREATE TABLE `CreditTitle`
(
    `id`             int                    NOT NULL AUTO_INCREMENT,
    `number`         varchar(64)            NOT NULL,
    `concept`        varchar(1024),
    `amount`         decimal(10, 2)         NOT NULL,
    `state`          varchar(16),
    `type`           varchar(16),
    `issueDate`      date                   NOT NULL,
    `expirationDate` date,
    `organizationId` int                    NOT NULL,
    `observations`   varchar(1024),
    `ownerId`        int                    NULL,
    `departmentId`   int           UNSIGNED NULL,
    `insertDate`     datetime               DEFAULT NOW(),
    `updateDate`     datetime               DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_credittitle_organizationId` FOREIGN KEY (`organizationId`) REFERENCES `Organization` (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `CreditTitle_Bill`
(
    `billId`        int NOT NULL,
    `creditTitleId` int NOT NULL,

    PRIMARY KEY (`billId`, `creditTitleId`),
    CONSTRAINT `fk_creditTitle_Bill_billId` FOREIGN KEY (`billId`) REFERENCES `Bill` (`id`),
    CONSTRAINT `fk_creditTitle_Bill_creditTitleId` FOREIGN KEY (`creditTitleId`) REFERENCES `CreditTitle` (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE Collaborator
(
    id             INTEGER               NOT NULL AUTO_INCREMENT,
    userId         INTEGER,
    contactId      INTEGER,
    organizationId INTEGER,
    ownerId        INTEGER               NULL,
    departmentId   INTEGER      UNSIGNED NULL,
    `insertDate`   datetime              DEFAULT NOW(),
    `updateDate`   datetime              DEFAULT NOW(),

    PRIMARY KEY (id),
    CONSTRAINT fk_collaborator_user FOREIGN KEY (userId) REFERENCES User (id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,
    CONSTRAINT fk_collaborator_contact FOREIGN KEY (contactId) REFERENCES Contact (id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,
    CONSTRAINT fk_collaborator_organization FOREIGN KEY (organizationId) REFERENCES Organization (id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE Commissioning
(
    id                   int                                      NOT NULL AUTO_INCREMENT,
    requestDate          datetime                                 NOT NULL,
    name                 varchar(512)  COLLATE utf8mb4_spanish_ci NOT NULL,
    scope                varchar(1024) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
    content              varchar(1024) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
    products             varchar(1024) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
    deliveryDate         datetime                                 NOT NULL,
    budget               decimal(10, 2)                           DEFAULT NULL,
    notes                varchar(1024) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
    authorSignature      boolean                                  NOT NULL DEFAULT FALSE,
    reviserSignature     boolean                                  NOT NULL DEFAULT FALSE,
    adminSignature       boolean                                  NOT NULL DEFAULT FALSE,
    justifyInformation   boolean                                  NOT NULL DEFAULT FALSE,
    developedActivities  varchar(1024) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
    difficultiesAppeared varchar(1024) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
    results              varchar(1024) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
    conclusions          varchar(1024) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
    evaluation           varchar(1024) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
    status               varchar(20)   COLLATE utf8mb4_spanish_ci DEFAULT NULL,
    projectId            int                                      DEFAULT NULL,
    `deleteDate`         datetime                                 DEFAULT NULL,
    `insertDate`         datetime                                 DEFAULT NOW(),
    `updateDate`         datetime                                 DEFAULT NOW(),

    PRIMARY KEY (id),
    CONSTRAINT fk_commissioning_project FOREIGN KEY (projectId) REFERENCES Project (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE CommissioningDelay
(
    id              int                                      NOT NULL AUTO_INCREMENT,
    reason          varchar(1024) COLLATE utf8mb4_spanish_ci NOT NULL,
    originalDate    datetime                                 NOT NULL,
    delayedToDate   datetime                                 NOT NULL,
    commissioningId int                                      DEFAULT NULL,
    `status`        varchar(20)                              DEFAULT NULL,
    `insertDate`    datetime                                 DEFAULT NOW(),
    `updateDate`    datetime                                 DEFAULT NOW(),

    PRIMARY KEY (id),
    CONSTRAINT fk_commissioningDelay_commissioning FOREIGN KEY (commissioningId) REFERENCES Commissioning (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE CommissioningPaymentData
(
    id              int          NOT NULL AUTO_INCREMENT,
    commissioningId int          NOT NULL,
    collaboratorId  INTEGER      NOT NULL,
    paymentMode     varchar(32),
    bankAccount     varchar(50),
    billNumber      varchar(50),
    insertDate      datetime,
    updateDate      datetime,

    PRIMARY KEY (id),
    CONSTRAINT fk_commissioning_collaborator_commissioning FOREIGN KEY (commissioningId) REFERENCES Commissioning (id),
    CONSTRAINT fk_commissioning_collaborator_collaborator FOREIGN KEY (collaboratorId) REFERENCES Collaborator (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE Commissioning_User
(
    id              int NOT NULL AUTO_INCREMENT,
    commissioningId int NOT NULL,
    userId          int NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_commissioning_user_commissioning FOREIGN KEY (commissioningId) REFERENCES Commissioning (id),
    CONSTRAINT fk_commissioning_user_user FOREIGN KEY (userId) REFERENCES User (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE CommissioningChange
(
    id              int           NOT NULL AUTO_INCREMENT,
    field           varchar(1024) NOT NULL,
    oldValue        varchar(1024) NOT NULL,
    newValue        varchar(1024) NOT NULL,
    commissioningId int           DEFAULT NULL,
    userId          int           DEFAULT NULL,
    status          varchar(20)   DEFAULT NULL,
    deleteDate      datetime      DEFAULT NULL,
    `insertDate`    datetime      DEFAULT NOW(),
    `updateDate`    datetime      DEFAULT NOW(),

    PRIMARY KEY (id),
    CONSTRAINT fk_commissioningChange_commissioning FOREIGN KEY (commissioningId) REFERENCES Commissioning (id),
    CONSTRAINT fk_commissioningChange_user FOREIGN KEY (userId) REFERENCES User (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE CommissioningFile
(
    id              int          NOT NULL AUTO_INCREMENT,
    commissioningId int          NOT NULL,
    userId          int          NOT NULL,
    file            varchar(400) NOT NULL,
    fileMime        varchar(128) DEFAULT NULL,
    `insertDate`    datetime     DEFAULT NOW(),
    `updateDate`    datetime     DEFAULT NOW(),

    PRIMARY KEY (id),
    CONSTRAINT fk_commissioningFile_commissioning FOREIGN KEY (commissioningId) REFERENCES Commissioning (id),
    CONSTRAINT fk_commissioningFile_user FOREIGN KEY (userId) REFERENCES User (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE ExternalActivity
(
    id                 INTEGER                  NOT NULL AUTO_INCREMENT,
    name               varchar(256),
    category           varchar(256),
    startDate          DATETIME                 NOT NULL,
    endDate            DATETIME                 NOT NULL,
    location           varchar(256),
    organizer          varchar(256),
    comments           varchar(2048),
    documentCategoryId INTEGER        UNSIGNED,
    ownerId            INTEGER,
    departmentId       INTEGER,
    `insertDate`       datetime                 DEFAULT NOW(),
    `updateDate`       datetime                 DEFAULT NOW(),

    PRIMARY KEY (id),
    CONSTRAINT fk_externalactivity_documentcategory FOREIGN KEY (documentCategoryId) REFERENCES DocumentCategory (id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE ActivityFile
(
    id                 int          NOT NULL AUTO_INCREMENT,
    externalActivityId int          NOT NULL,
    userId             int          NOT NULL,
    file               varchar(400) NOT NULL,
    fileMime           varchar(128) DEFAULT NULL,
    `insertDate`       datetime     DEFAULT NOW(),
    `updateDate`       datetime     DEFAULT NOW(),

    PRIMARY KEY (id),
    CONSTRAINT fk_activityFile_externalActivity FOREIGN KEY (externalActivityId) REFERENCES ExternalActivity (id),
    CONSTRAINT fk_activityFile_user FOREIGN KEY (userId) REFERENCES User (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE Tag
(
    id           int                    NOT NULL AUTO_INCREMENT,
    name         varchar(1024)          NOT NULL,
    description  varchar(1024)          NOT NULL,
    ownerId      INTEGER                NULL,
    departmentId INTEGER       UNSIGNED NULL,
    `insertDate` datetime               DEFAULT NOW(),
    `updateDate` datetime               DEFAULT NOW(),

    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE Contact_Tag
(
    id        int     NOT NULL AUTO_INCREMENT,
    tagId     int     NOT NULL,
    contactId integer NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_contact_tag_tag FOREIGN KEY (tagId) REFERENCES Tag (id),
    CONSTRAINT fk_contact_tag_contact FOREIGN KEY (contactId) REFERENCES Contact (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE Position_Tag
(
    id         int     NOT NULL AUTO_INCREMENT,
    tagId      int     NOT NULL,
    positionId integer NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_position_tag_tag FOREIGN KEY (tagId) REFERENCES Tag (id),
    CONSTRAINT fk_position_tag_position FOREIGN KEY (positionId) REFERENCES Position (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE Department_Tag
(
    id           int              NOT NULL AUTO_INCREMENT,
    tagId        int              NOT NULL,
    departmentId integer UNSIGNED NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_department_tag_tag FOREIGN KEY (tagId) REFERENCES Tag (id),
    CONSTRAINT fk_department_tag_department FOREIGN KEY (departmentId) REFERENCES Department (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE Organization_Tag
(
    id             int     NOT NULL AUTO_INCREMENT,
    tagId          int     NOT NULL,
    organizationId integer NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_organization_tag_tag FOREIGN KEY (tagId) REFERENCES Tag (id),
    CONSTRAINT fk_organization_tag_organization FOREIGN KEY (organizationId) REFERENCES Organization (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE PositionChange
(
    id           int           NOT NULL AUTO_INCREMENT,
    field        varchar(1024) NOT NULL,
    oldValue     varchar(1024) NOT NULL,
    newValue     varchar(1024) NOT NULL,
    positionId   int           DEFAULT NULL,
    userId       int           DEFAULT NULL,
    `insertDate` datetime      DEFAULT NOW(),
    `updateDate` datetime      DEFAULT NOW(),

    PRIMARY KEY (id),
    CONSTRAINT fk_positionChange_position FOREIGN KEY (positionId) REFERENCES Position (id),
    CONSTRAINT fk_positionChange_user FOREIGN KEY (userId) REFERENCES User (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `ContactOwner`
(
    `id`        int NOT NULL AUTO_INCREMENT,
    `contactId` int NOT NULL,
    `userId`    int NOT NULL,

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_contactowner_contactId` FOREIGN KEY (`contactId`) REFERENCES `Contact` (`id`),
    CONSTRAINT `fk_contactowner_userId` FOREIGN KEY (`userId`) REFERENCES `User` (`id`)
) ENGINE = innodb
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;


CREATE TABLE `EntityChange`
(
    `id`         int                                      NOT NULL AUTO_INCREMENT,
    `field`      varchar(1024) COLLATE utf8mb4_spanish_ci NOT NULL,
    `oldValue`   varchar(1024) COLLATE utf8mb4_spanish_ci NOT NULL,
    `newValue`   varchar(1024) COLLATE utf8mb4_spanish_ci NOT NULL,
    `userId`     int                                      DEFAULT NULL,
    `entityId`   int                                      NOT NULL,
    `entityName` varchar(1024) COLLATE utf8mb4_spanish_ci NOT NULL,
    `insertDate` datetime                                 DEFAULT NOW(),

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_entityChange_user` FOREIGN KEY (`userId`) REFERENCES `User` (`id`)
);


CREATE TABLE Link
(
    id           int           PRIMARY KEY AUTO_INCREMENT,
    user         varchar(128),
    link         varchar(128),
    `insertDate` datetime      DEFAULT NOW()
);

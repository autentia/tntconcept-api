-- User
INSERT INTO User(id, name, login, password, passwordExpireDate, roleId, categoryId, startDate, workingInClient, email,
                 photo, married, childrenNumber, dayDuration, agreementId, nif, academicQualification, phone, mobile,
                 street, city,
                 postalCode, drivenLicenseType, vehicleType, licensePlate, socialSecurityNumber, bank, account,
                 travelAvailability,
                 genre, salary, salaryExtras, securityCard, healthInsurance, notes, contractObservations)
VALUES (11, 'Usuario de prueba 1', 'usuario.prueba1', 'BMS0Tp2pdyCiYnI8amMaU1QX', NULL, 1, 1, '2022-04-04', 0,
        'usuario.prueba1@example.com', 'photo', 0, 0, 480, 1, '12345678X', 'OK', '666222333', '666555777', '', '', '',
        '', '', '', '', '', '', '', 'MAN', 0.0, 0.0, '', '', '', '');

INSERT INTO User(id, name, login, password, passwordExpireDate, roleId, categoryId, startDate, workingInClient, email,
                 photo, married, childrenNumber, dayDuration, agreementId, nif, academicQualification, phone, mobile,
                 street, city,
                 postalCode, drivenLicenseType, vehicleType, licensePlate, socialSecurityNumber, bank, account,
                 travelAvailability,
                 genre, salary, salaryExtras, securityCard, healthInsurance, notes, contractObservations)
VALUES (12, 'Usuario de prueba 2', 'usuario.prueba2', 'BMS0Tp2pdyCiYnI8amMaU1QX', NULL, 1, 1, '2022-04-04', 0,
        'usuario.prueba2@example.com', 'photo', 0, 0, 480, 1, '12345677X', 'OK', '666222333', '666555777', '', '', '',
        '', '', '', '', '', '', '', 'MAN', 0.0, 0.0, '', '', '', '');


insert into archimedes_security_subject (id, principal_name, attributes, audit_created_at, audit_updated_at)
values (11, 'usuario.prueba1@example.com', '{"sub": "11"}', '2023-04-24 06:33:21', '2023-04-24 06:33:21');

insert into archimedes_security_subject_role_relation (subject_id, role_name, audit_created_at, audit_updated_at)
values (11, "user", '2023-04-24 06:33:21', '2023-04-24 06:33:21');

insert into archimedes_security_subject (id, principal_name, attributes, audit_created_at, audit_updated_at)
values (12, 'usuario.prueba2@example.com', '{"sub": "12"}', '2023-04-24 06:33:21', '2023-04-24 06:33:21');

insert into archimedes_security_subject_role_relation (subject_id, role_name, audit_created_at, audit_updated_at)
values (12, "user", '2023-04-24 06:33:21', '2023-04-24 06:33:21');

-- Organization id = 3
INSERT INTO Organization (id, organizationTypeId, organizationISOCategoryId, name, documentNumber, phone,
                          street,
                          number, locator, postalCode, city, provinceId, state, countryId, fax, email,
                          website, ftpsite,
                          notes, ownerId, departmentId, evaluationCriteria, organizationDocCategoryId,
                          freelance,
                          insertDate, updateDate)
VALUES (3, 1, 1, 'Hermiston, Zieme and Monahan', '', '', '', '', '', '', '', 53, '', 1, '', '', '', '', '', 1, 1, null,
        1, 0, '2023-04-24 06:33:16', DEFAULT);

-- Project id = 6
INSERT INTO Project (id, organizationId, startDate, endDate, open, name, description, ownerId, departmentId,
                     billable, offerId, insertDate, updateDate)
VALUES (6, 3, '2023-04-24', null, 1, 'Seize distributed niches', '', null, null, DEFAULT, null, '2023-04-24 06:33:16',
        '2023-04-24 06:33:21');

-- Project role with weekly evidence
INSERT INTO ProjectRole (id, projectId, name, costPerHour, expectedHours, requireEvidence, ownerId,
                         departmentId, insertDate, updateDate, maxTimeAllowedByYear, maxTimeAllowedByActivity, timeUnit, isWorkingTime,
                         isApprovalRequired)
VALUES (6, 6, 'Project 6 weekly', 0.00, 0, 'WEEKLY', null, null, '2023-04-24 06:33:16', '2023-04-24 06:33:20', 0, 0,
        'MINUTES', 1, 0);

-- Project role with evidence once
INSERT INTO ProjectRole (id, projectId, name, costPerHour, expectedHours, requireEvidence, ownerId,
                         departmentId, insertDate, updateDate, maxTimeAllowedByYear, maxTimeAllowedByActivity, timeUnit, isWorkingTime,
                         isApprovalRequired)
VALUES (7, 6, 'Project 6 once', 0.00, 0, 'ONCE', null, null, '2023-04-24 06:33:16', '2023-04-24 06:33:20', 4800, 960,
        'DAYS', 1, 0);

-- Project id = 7
INSERT INTO Project (id, organizationId, startDate, endDate, open, name, description, ownerId, departmentId, billable,
                     offerId, insertDate, updateDate)
VALUES (7, 3, '2023-04-24', null, 1, 'Envisioneer one-to-one e-services', '', null, null, DEFAULT, null,
        '2023-04-24 06:33:16', '2023-04-24 06:33:21');

-- Project weekly
INSERT INTO ProjectRole (id, projectId, name, costPerHour, expectedHours, requireEvidence, ownerId, departmentId,
                         insertDate, updateDate, maxTimeAllowedByYear, maxTimeAllowedByActivity, timeUnit, isWorkingTime, isApprovalRequired)
VALUES (8, 7, 'Project 7 weekly', 0.00, 0, 'WEEKLY', null, null, '2023-04-24 06:33:16', '2023-04-24 06:33:20', 0, 0,
        'MINUTES', 1, 0);

-- Project once
INSERT INTO ProjectRole (id, projectId, name, costPerHour, expectedHours, requireEvidence, ownerId, departmentId,
                         insertDate, updateDate, maxTimeAllowedByYear, maxTimeAllowedByActivity, timeUnit, isWorkingTime, isApprovalRequired)
VALUES (9, 7, 'Project 7 once', 0.00, 0, 'ONCE', null, null, '2023-04-24 06:33:16', '2023-04-24 06:33:20', 0, 0, 'MINUTES',
        1, 0);

-- Project none
INSERT INTO ProjectRole (id, projectId, name, costPerHour, expectedHours, requireEvidence, ownerId, departmentId,
                         insertDate, updateDate, maxTimeAllowedByYear, maxTimeAllowedByActivity, timeUnit, isWorkingTime, isApprovalRequired)
VALUES (10, 7, 'Project 7 none', 0.00, 0, 'NO', null, null, '2023-04-24 06:33:16', '2023-04-24 06:33:20', 0, 0, 'MINUTES',
        1, 0);


-- PROJECT-ROLE for ProjectRoleDaoIT
-- Project id 8
INSERT INTO Project (id, organizationId, startDate, endDate, open, name, description, ownerId, departmentId, billable,
                     offerId, insertDate, updateDate)
VALUES (8, 3, '2023-04-24', null, 1, 'Project with roles for testing', '', null, null, DEFAULT, null,
        '2023-04-24 06:33:16', '2023-04-24 06:33:21');

INSERT INTO ProjectRole (id, projectId, name, costPerHour, expectedHours, requireEvidence, ownerId, departmentId,
                         insertDate, updateDate, maxTimeAllowedByYear, maxTimeAllowedByActivity, timeUnit, isWorkingTime, isApprovalRequired)
VALUES (11, 8, 'Project no working time', 0.00, 0, 'WEEKLY', null, null, '2023-04-24 06:33:16', '2023-04-24 06:33:20',
        0, 0, 'MINUTES', 0, 0);

INSERT INTO ProjectRole (id, projectId, name, costPerHour, expectedHours, requireEvidence, ownerId, departmentId,
                         insertDate, updateDate, maxTimeAllowedByYear, maxTimeAllowedByActivity, timeUnit, isWorkingTime, isApprovalRequired)
VALUES (12, 8, 'Project no working time 2', 0.00, 0, 'WEEKLY', null, null, '2023-04-24 06:33:16', '2023-04-24 06:33:20',
        0, 0, 'MINUTES', 0, 0);


-- Closed Project for testing purposes
INSERT INTO Project (id, organizationId, startDate, endDate, open, name, description, ownerId, departmentId, billable,
                     offerId, insertDate, updateDate)
VALUES (9, 3, '2023-04-24', null, 0, 'Closed project for testing', '', null, null, DEFAULT, null,
        '2023-04-24 06:33:16', '2023-04-24 06:33:21');


-- Attachment
INSERT INTO Attachment (id, userId, path, fileName, mimeType, uploadDate, isTemporary)
VALUES ('4d3cbe3f-369f-11ee-99c2-0242ac180003', 11, 'path/to/test/file', 'testFile,jpg', 'image/jpeg',
        '2023-08-17 00:00:00', TRUE);


INSERT INTO Expense (id, userId, date, description, amount, type, state)
VALUES (1, 1, '2023-08-17 00:00:00', 'Expense for tests', 10.0, 'MARKETING', 'PENDING');

INSERT INTO Expense (id, userId, date, description, amount, type, state)
VALUES (2, 1, '2023-08-18 00:00:00', 'Expense for tests', 10.0, 'MARKETING', 'ACCEPTED');

INSERT INTO ExpenseDocument (expenseId, attachmentId, insertDate)
VALUES (2, '4d3cbe3f-369f-11ee-99c2-0242ac180003', '2023-08-17 00:00:00');


INSERT INTO Expense (id, userId, date, description, amount, type, state)
VALUES (3, 1, '2023-08-19 00:00:00', 'Expense for tests', 30.0, 'MARKETING', 'PENDING');

INSERT INTO Expense (id, userId, date, description, amount, type, state)
VALUES (4, 1, '2023-08-20 00:00:00', 'Expense for tests', 40.0, 'MARKETING', 'ACCEPTED');


INSERT INTO Expense (id, userId, date, description, amount, type, state)
VALUES (5, 11, '2023-08-19 00:00:00', 'Expense for tests', 30.0, 'MARKETING', 'PENDING');

INSERT INTO Expense (id, userId, date, description, amount, type, state)
VALUES (6, 11, '2023-08-20 00:00:00', 'Expense for tests', 40.0, 'MARKETING', 'ACCEPTED');




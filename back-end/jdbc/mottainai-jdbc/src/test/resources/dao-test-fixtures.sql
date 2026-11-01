TRUNCATE TABLE UserAccount, Employee, Store, Address, EmployeeRole, Company, SubscriptionPlan, Administration RESTART IDENTITY CASCADE;

INSERT INTO SubscriptionPlan (PlanId, Name, Description, Price, StoreLimit, UserLimit, Active, CreatedAt) VALUES
(1, 'Free', 'Plano básico para pequenas empresas', 20.00, 1, 3, true, '2026-01-01 00:00:00'),
(5, 'Professional Plan', 'Plano de assinatura operacional', 349.90, 8, 24, true, '2026-01-01 00:00:00'),
(101, 'Plano Black', 'Plano de assinatura premium', 780.99, 100, 150, true, '2026-01-01 00:00:00'),
(102, 'Plano Black Friday', 'Plano de assinatura de 1 mês pela metade do preço', 1299.99, 50, 150, true, '2026-01-01 00:00:00');

INSERT INTO Address (AddressId, ZipCode, Street, Number, Complement, Neighborhood, City, State, CreatedAt) VALUES
(1, '10000001', 'Rua Exemplo', '1', NULL, 'Centro', 'São Paulo', 'SP', '2026-01-01 00:00:00'),
(24, '10000024', 'Rua da Loja', '24', NULL, 'Centro', 'São Paulo', 'SP', '2026-01-01 00:00:00'),
(101, '00000000', 'Registro anonimizado', '0', NULL, 'Centro', 'São Paulo', 'SP', '2026-01-01 00:00:00'),
(102, '12345678', 'Av. Manuel Bandeirantes', '9871', 'prédio', 'Vila Madalena', 'São Paulo', 'SP', '2026-01-01 00:00:00'),
(103, '10000103', 'Rua Exemplo 103', '103', NULL, 'Centro', 'São Paulo', 'SP', '2026-01-01 00:00:00'),
(104, '10000104', 'Rua Exemplo 104', '104', NULL, 'Centro', 'São Paulo', 'SP', '2026-01-01 00:00:00'),
(105, '10000105', 'Rua Exemplo 105', '105', NULL, 'Centro', 'São Paulo', 'SP', '2026-01-01 00:00:00');

INSERT INTO EmployeeRole (RoleId, Name, Description, PermissionLevel, Active, CreatedAt) VALUES
(1, 'Gerente de loja', 'Cargo operacional e gerencial', 1, true, '2026-01-01 00:00:00'),
(5, 'Repositor', 'Cargo de reposição', 5, true, '2026-01-01 00:00:00'),
(9, 'Auxiliar de Limpeza', 'Função de auxiliar na limpeza do estabelecimento', 1, true, '2026-01-01 00:00:00');

INSERT INTO Company (CompanyId, OfficialName, TradeName, CNPJ, Email, Phone, Latitude, Longitude, Active, CreatedAt, SubscriptionPlanId) VALUES
(1, 'Empresa de exemplo', 'Empresa exemplo', '90000000000001', 'empresa1@example.com', '1100000001', 0, 0, true, '2026-01-01 00:00:00', 1),
(87, 'Empresa de testes', 'Empresa testes', '90000000000087', 'empresa87@example.com', '1100000087', 0, 0, true, '2026-01-01 00:00:00', 1),
(101, 'Grupo Casas Bahia', 'Casa Bahia Comercial Ltda', '90002001001127', 'm***@empresa.com', '(11) 9****-1709', -3.987670, -5.345600, true, '2026-01-01 00:00:00', 1),
(102, 'Grupo Casas Bahia Filial', 'Casa Bahia Filial', '90002001001128', 'filial@example.com', '1100000102', -3.000000, -5.000000, true, '2026-01-01 00:00:00', 1);

INSERT INTO Store (StoreId, Name, CNPJ, Email, Phone, Latitude, Longitude, Active, CreatedAt, CompanyId, AddressId) VALUES
(1, 'Loja 001', '91000001000191', 'KJsCtcqNYn2/tfhr91nMwKEcPbyzrLTo6SA/8TlUMVU6R+3Y8LyAtWWe8uqG0sXLhpKOh1E=', 'tm0cg4OFiuzAs27uD82/GLZ6urACgTCLRCI0ATMYZiyIj0uUg/b7', 0, 0, true, '2026-01-01 00:00:00', 1, 1),
(24, 'Loja 024', '91000024000191', 'loja24@example.com', '1100000024', 0, 0, true, '2026-01-01 00:00:00', 87, 24),
(101, 'Loja 101', '91000010100191', 'loja101@example.com', '1100000101', 0, 0, true, '2026-01-01 00:00:00', 87, 104),
(102, 'Loja Pizzaria Della Roma - São Paulo', '90087649000143', 'm***@empresa.com', '(11) 9****-1709', -3.987670, -7.345600, true, '2026-01-01 00:00:00', 87, 105);

INSERT INTO Employee (EmployeeId, Name, CPF, Email, Phone, Active, HireDate, CreatedAt, StoreId, RoleId) VALUES
(101, 'EMP_901', 'CPF_TKN_9DBA1F', 'EMAIL_EMP_701', 'PHONE_EMP_701', true, '2026-01-01', '2026-01-01 00:00:00', 24, 5),
(102, 'Funcionário auxiliar', 'P7dm18cGwjc2V+hIL8m4NRtB0JTWWiZwmT0Pjkyk0DrWjxJA7noG', 'employee102@example.com', '1100000102', true, '2026-01-01', '2026-01-01 00:00:00', 24, 5),
(501, 'Usuário de teste', 'CPF_TKN_USER_501', 'employee501@example.com', '1100000501', true, '2026-01-01', '2026-01-01 00:00:00', 24, 5),
(502, 'Usuário de inserção', 'CPF_TKN_USER_502', 'employee502@example.com', '1100000502', true, '2026-01-01', '2026-01-01 00:00:00', 24, 5);

INSERT INTO UserAccount (UserId, Email, PasswordHash, LastLogin, Active, CreatedAt, EmployeeId) VALUES
(501, 'USER_901', '$9b$12$IXmU5oLQCmpUXCfYVoJ39GDKlllRwIyGjc0xgNWjLvGbc91EJqWA6', '2026-02-01 15:30:10', true, '2026-01-01 00:00:00', 501);

INSERT INTO Administration (AdministrationId, Email, PasswordHash) VALUES
(101, 'adm_mottainai@gmail.com', 'pHAPS4453!#WERTY0987'),
(102, 'mottainai.contato@gmail.com', 'another-test-password-hash');

SELECT setval(pg_get_serial_sequence('subscriptionplan', 'planid'), (SELECT max(planid) FROM subscriptionplan), true);
SELECT setval(pg_get_serial_sequence('address', 'addressid'), (SELECT max(addressid) FROM address), true);
SELECT setval(pg_get_serial_sequence('employeerole', 'roleid'), (SELECT max(roleid) FROM employeerole), true);
SELECT setval(pg_get_serial_sequence('company', 'companyid'), (SELECT max(companyid) FROM company), true);
SELECT setval(pg_get_serial_sequence('store', 'storeid'), (SELECT max(storeid) FROM store), true);
SELECT setval(pg_get_serial_sequence('employee', 'employeeid'), (SELECT max(employeeid) FROM employee), true);
SELECT setval(pg_get_serial_sequence('useraccount', 'userid'), (SELECT max(userid) FROM useraccount), true);
SELECT setval(pg_get_serial_sequence('administration', 'administrationid'), (SELECT max(administrationid) FROM administration), true);

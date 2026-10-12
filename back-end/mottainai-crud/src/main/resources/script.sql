drop table if exists UserAccount cascade;
drop table if exists Employee cascade;
drop table if exists EmployeeRole cascade;
drop table if exists Store cascade;
drop table if exists Address cascade;
drop table if exists SubscriptionPlan cascade;
drop table if exists Company cascade;
drop table if exists Administration cascade;

create table Company(
	CompanyId serial,
	OfficialName varchar(120) not null,
	TradeName varchar(120),
	CNPJ varchar(14) not null,
	Email varchar(60) not null,
	Phone varchar(15),
	Latitude decimal(9,6),
	Longitude decimal(9,6),
	Active boolean not null default true,
	CreatedAt timestamp not null default current_timestamp,
	UpdatedAt timestamp,
	DeletedAt timestamp,

	constraint pkCompany 
	primary key (CompanyId),

	constraint chkCompanyName 
	check (trim(OfficialName) <> ''),
	
	constraint unCompanyCNPJ
	unique (CNPJ),
	
	constraint unCompanyEmail 
	unique (Email),

	constraint chkCompanyLatitude
	check(Latitude between -90 and 90),

	constraint chkCompanyLongitude
	check(Longitude between -180 and 180),

	constraint chkCompanyUpdatedAt
	check(UpdatedAt is null or UpdatedAt >= CreatedAt),

	constraint chkCompanyDeletedAt
	check(DeletedAt is null or DeletedAt >= CreatedAt),

	constraint chkCompanyDeletedAfterUpdated
	check(DeletedAt is null or UpdatedAt is null or DeletedAt >= UpdatedAt),

	constraint chkCompanyActiveDeleted
	check(DeletedAt is null or Active = false),
	
	constraint chkCompanyEmail 
	check(Email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
	
	constraint chkCompanyCNPJ 
	check(length(CNPJ) = 14 and CNPJ ~ '^[0-9]+$'),

	constraint chkCompanyPhone
	check(regexp_replace(Phone, '[^0-9]', '', 'g') ~ '^[0-9]{10,11}$')
);


create table SubscriptionPlan(
	PlanId serial,
	Name varchar(100) not null,
	Description varchar(250) not null,
	Price decimal(10,2) not null,
	StoreLimit int not null,
	UserLimit int not null,
	Active boolean not null default true,
	CreatedAt timestamp not null default current_timestamp,
	UpdatedAt timestamp,
	DeletedAt timestamp,
	CompanyId int not null,
	
	constraint pkSubscriptionPlan 
	primary key(PlanId),

	constraint unSubscriptionPlanCompanyId
	unique(CompanyId),

	constraint chkSubscriptionPlanName
	check(trim(Name) <> ''),

	constraint chkSubscriptionPlanPrice
	check(Price > 0),

	constraint chkSubscriptionPlanStoreLimit
	check(StoreLimit > 0),

	constraint chkSubscriptionPlanUserLimit
	check(UserLimit > 0),

	constraint chkSubscriptionPlanUpdatedAt
	check(UpdatedAt is null or UpdatedAt >= CreatedAt),

	constraint chkSubscriptionPlanDeletedAt
	check(DeletedAt is null or DeletedAt >= CreatedAt),

	constraint chkSubscriptionPlanDeletedAfterUpdated
	check(DeletedAt is null or UpdatedAt is null or DeletedAt >= UpdatedAt),

	constraint chkSubscriptionPlanActiveDeleted
	check(DeletedAt is null or Active = false),
	
	constraint fkSubscriptionPlanCompany 
	foreign key (CompanyId) references Company(CompanyId)
	on delete cascade
	on update restrict
);


create table Address(
	AddressId serial,
	ZipCode varchar(8) not null,
	Street varchar(120) not null,
	Number varchar(60) not null,
	Complement varchar(250),
	Neighborhood varchar(60) not null,
	City varchar(100) not null,
	State varchar(80) not null,
	CreatedAt timestamp not null default current_timestamp,
	UpdatedAt timestamp,
	DeletedAt timestamp,

	constraint pkAddress
	primary key(AddressId),

	constraint chkAddressUpdatedAt
	check(UpdatedAt is null or UpdatedAt >= CreatedAt),

	constraint chkAddressDeletedAt
	check(DeletedAt is null or DeletedAt >= CreatedAt),

	constraint chkAddressDeletedAfterUpdated
	check(DeletedAt is null or UpdatedAt is null or DeletedAt >= UpdatedAt),

	constraint chkAddressZipCode
	check(length(ZipCode) = 8 and ZipCode ~ '^[0-9]+$'),

	constraint chkAddressRequiredFields
	check(trim(Street) <> '' and trim(Number) <> '' and trim(Neighborhood) <> '' and trim(City) <> '' and trim(State) <> '')
);


create table Store(
	StoreId serial,
	Name varchar(120) not null,
	CNPJ varchar(14) not null,
	Email varchar(60) not null,
	Phone varchar(15) not null,
	Latitude decimal(9,6),
	Longitude decimal(9,6),
	Active boolean not null default true,
	CreatedAt timestamp not null default current_timestamp,
	UpdatedAt timestamp,
	DeletedAt timestamp,
	CompanyId int not null,
	AddressId int not null,
	
	constraint pkStore 
	primary key (StoreId),

	constraint unStoreCNPJ
	unique (CNPJ),
	
	constraint unStoreEmail 
	unique (Email),

	constraint unStoreAddress
	unique (AddressId),

	constraint chkStoreName
	check(trim(Name) <> ''),
	
	constraint fkStoreCompany 
	foreign key (CompanyId) references Company(CompanyId)
	on delete cascade
	on update restrict,

	constraint fkStoreAddress
	foreign key (AddressId) references Address(AddressId),

	constraint chkStoreLatitude
	check(Latitude between -90 and 90),

	constraint chkStoreLongitude
	check(Longitude between -180 and 180),

	constraint chkStoreUpdatedAt
	check(UpdatedAt is null or UpdatedAt >= CreatedAt),

	constraint chkStoreDeletedAt
	check(DeletedAt is null or DeletedAt >= CreatedAt),

	constraint chkStoreDeletedAfterUpdated
	check(DeletedAt is null or UpdatedAt is null or DeletedAt >= UpdatedAt),

	constraint chkStoreActiveDeleted
	check(DeletedAt is null or Active = false),

	constraint chkStoreCNPJ 
	check(length(CNPJ) = 14 and CNPJ ~ '^[0-9]+$'),
	
	constraint chkStoreEmail 
	check (Email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),

	constraint chkStorePhone
	check(regexp_replace(Phone, '[^0-9]', '', 'g') ~ '^[0-9]{10,11}$')
);


create table EmployeeRole(
	RoleId serial,
	Name varchar(100) not null,
	Description varchar(250),
	PermissionLevel int not null,
	Active boolean not null default true,
	CreatedAt timestamp not null default current_timestamp,
	UpdatedAt timestamp,
	DeletedAt timestamp,

	constraint pkEmployeeRole
	primary key(RoleId),

	constraint chkEmployeeRoleUpdatedAt
	check(UpdatedAt is null or UpdatedAt >= CreatedAt),

	constraint chkEmployeeRoleDeletedAt
	check(DeletedAt is null or DeletedAt >= CreatedAt),

	constraint chkEmployeeRoleDeletedAfterUpdated
	check(DeletedAt is null or UpdatedAt is null or DeletedAt >= UpdatedAt),

	constraint chkEmployeeRoleActiveDeleted
	check(DeletedAt is null or Active = false),

	constraint chkEmployeeRolePermissionLevel
	check(PermissionLevel > 0),

	constraint unEmployeeRoleName
	unique(Name),

	constraint chkEmployeeRoleName
	check(trim(Name) <> '')
);


create table Employee(
	EmployeeId serial,
	Name varchar(200) not null,
	CPF varchar(11) not null,
	Email varchar(100) not null,
	Phone varchar(15) not null,
	Active boolean not null default true,
	HireDate date not null,
	CreatedAt timestamp not null default current_timestamp,
	UpdatedAt timestamp,
	DeletedAt timestamp,
	StoreId int not null,
	RoleId int not null,
	
	constraint pkEmployee 
	primary key(EmployeeId),

	constraint unEmployeeCPF
	unique(CPF),

	constraint unEmployeeEmail
	unique(Email),

	constraint chkEmployeeName
	check(trim(Name) <> '' and Name ~ '^[A-Za-zÀ-ÿ ]+$'),
	
	constraint fkEmployeeStore 
	foreign key(StoreId) references Store(StoreId)
	on delete cascade
	on update restrict,

	constraint fkEmployeeRole
	foreign key(RoleId) references EmployeeRole(RoleId),

	constraint chkEmployeeUpdatedAt
	check(UpdatedAt is null or UpdatedAt >= CreatedAt),

	constraint chkEmployeeDeletedAt
	check(DeletedAt is null or DeletedAt >= CreatedAt),

	constraint chkEmployeeDeletedAfterUpdated
	check(DeletedAt is null or UpdatedAt is null or DeletedAt >= UpdatedAt),

	constraint chkEmployeeActiveDeleted
	check(DeletedAt is null or Active = false),

	constraint chkEmployeeHireDate
	check(HireDate <= current_date),

	constraint chkEmployeeEmail 
	check (Email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
	
	constraint chkEmployeeCPF 
	check(length(CPF) = 11 and CPF ~ '^[0-9]+$'),

	constraint chkEmployeePhone
	check(regexp_replace(Phone, '[^0-9]', '', 'g') ~ '^[0-9]{10,11}$')
);


create table UserAccount(
	UserId serial,
	Email varchar(100) not null,
	PasswordHash varchar(255) not null,
	LastLogin timestamp,
	Active boolean not null default true,
	CreatedAt timestamp not null default current_timestamp,
	UpdatedAt timestamp,
	DeletedAt timestamp,
	EmployeeId int not null,
	
	constraint pkUserAccount
	primary key(UserId),
	
	constraint unUserAccountEmail 
	unique(Email),
	
	constraint unUserAccountEmployee 
	unique(EmployeeId),
	
	constraint chkUserAccountEmail 
	check(Email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
	
	constraint chkUserAccountPassword 
	check(length(PasswordHash) >= 8),

	constraint chkUserAccountUpdatedAt
	check(UpdatedAt is null or UpdatedAt >= CreatedAt),

	constraint chkUserAccountDeletedAt
	check(DeletedAt is null or DeletedAt >= CreatedAt),

	constraint chkUserAccountDeletedAfterUpdated
	check(DeletedAt is null or UpdatedAt is null or DeletedAt >= UpdatedAt),

	constraint chkUserAccountActiveDeleted
	check(DeletedAt is null or Active = false),

	constraint chkUserAccountLastLogin
	check(LastLogin >= CreatedAt),
	
	constraint fkUserAccountEmployee 
	foreign key(EmployeeId) references Employee(EmployeeId)
	on delete cascade
	on update restrict
);


create table Administration(
	AdministrationId serial,
	Email varchar(100) not null,
	PasswordHash varchar(255) not null,
	
	constraint pkAdministration 
	primary key(AdministrationId),
	
	constraint unAdministrationEmail 
	unique(Email),
	
	constraint chkAdministrationEmail 
	check(Email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- Script de carga sintética — Banco Administrativo PME
-- Gerado com seed determinística (42) para reprodutibilidade
-- Todos os dados são fictícios; CPF/CNPJ com dígito verificador válido
-- em faixa numérica sintética reservada (não correspondem a documentos reais).

BEGIN;

-- =========================================================
-- 1) COMPANY (100 registros)
-- =========================================================
INSERT INTO Company (OfficialName, TradeName, CNPJ, Email, Phone, Latitude, Longitude, Active, CreatedAt) VALUES
('Cristal Serviços Comércio e Serviços EPP', 'Cristal Serviços', '90000001000129', 'contato@cristalservicos001.com.br', '85919600133', -5.514094, -47.10872, true, '2024-03-25 11:08:00'),
('Raiz Consultoria Comércio e Serviços S.A.', 'Raiz Consultoria', '90000002000173', 'contato@raizconsultoria002.com.br', '47940265423', -12.792224, -64.902816, true, '2024-06-27 18:44:00'),
('Girassol Logística Indústria e Comércio LTDA', 'Girassol Logística', '90000003000118', 'contato@girassollogistica003.com.br', '11978161849', -1.295214, -49.954685, true, '2023-06-22 09:22:00'),
('Domo Automotivo Alimentos S.A.', 'Domo Automotivo', '90000004000162', 'contato@domoautomotivo004.com.br', '21931647525', -8.371994, -43.350335, true, '2024-07-11 09:02:00'),
('Alfa Tecnologia Tecnologia LTDA', 'Alfa Tecnologia', '90000005000107', 'contato@alfatecnologia005.com.br', '51983503056', -24.197185, -55.471852, true, '2024-08-23 16:46:00'),
('Bravo Serviços Soluções Corporativas S.A.', 'Bravo Serviços', '90000006000151', 'contato@bravoservicos006.com.br', '62972423884', -14.529212, -38.997357, true, '2023-11-03 11:41:00'),
('Âncora Consultoria Confecções S.A.', 'Âncora Consultoria', '90000007000104', 'contato@ancoraconsultoria007.com.br', '81910122691', 4.39838, -68.105062, true, '2023-05-14 13:14:00'),
('Prisma Moda Logística e Transportes EIRELI', 'Prisma Moda', '90000008000140', 'contato@prismamoda008.com.br', '21984514627', -0.790371, -73.05224, true, '2022-08-21 12:35:00'),
('Vale Metalúrgica Tecnologia S.A.', 'Vale Metalúrgica', '90000009000195', 'contato@valemetalurgica009.com.br', '62989325288', -29.456389, -49.113292, true, '2024-08-08 16:58:00'),
('Elo Comércio Logística e Transportes EPP', 'Elo Comércio', '90000010000110', 'contato@elocomercio010.com.br', '51930391171', 1.805747, -39.229847, true, '2022-11-12 08:07:00'),
('Girassol Educação Distribuidora ME', 'Girassol Educação', '90000011000164', 'contato@girassoleducacao011.com.br', '71938346578', -23.427848, -39.475111, true, '2022-12-09 16:10:00'),
('Órbita Serviços Soluções Corporativas ME', 'Órbita Serviços', '90000012000109', 'contato@orbitaservicos012.com.br', '41901031051', -11.144269, -64.525525, true, '2025-03-30 13:01:00'),
('Raiz Tecnologia Tecnologia EIRELI', 'Raiz Tecnologia', '90000013000153', 'contato@raiztecnologia013.com.br', '47973763116', -6.012602, -39.08706, true, '2024-05-27 16:08:00'),
('Alfa Moda Confecções EIRELI', 'Alfa Moda', '90000014000106', 'contato@alfamoda014.com.br', '71951333872', 3.898278, -69.661703, true, '2025-04-20 18:41:00'),
('Vetor Alimentos Tecnologia EIRELI', 'Vetor Alimentos', '90000015000142', 'contato@vetoralimentos015.com.br', '21908013267', -2.795155, -40.117291, true, '2024-03-15 09:28:00'),
('Ponta Tecnologia Confecções LTDA', 'Ponta Tecnologia', '90000016000197', 'contato@pontatecnologia016.com.br', '81946872343', 3.941736, -63.157081, true, '2024-09-05 14:00:00'),
('Boreal Consultoria Consultoria Empresarial LTDA', 'Boreal Consultoria', '90000017000131', 'contato@borealconsultoria017.com.br', '85920812191', -11.301251, -53.888835, true, '2023-11-05 08:03:00'),
('Cume Automotivo Soluções Corporativas EIRELI', 'Cume Automotivo', '90000018000186', 'contato@cumeautomotivo018.com.br', '21969985435', -11.501076, -71.950075, true, '2024-12-06 17:15:00'),
('Sul Mix Construção Confecções ME', 'Sul Mix Construção', '90000019000120', 'contato@sulmixconstrucao019.com.br', '11979911838', 1.802697, -37.006363, true, '2023-12-03 15:20:00'),
('Bravo Alimentos Alimentos LTDA', 'Bravo Alimentos', '90000020000155', 'contato@bravoalimentos020.com.br', '51998084124', -27.50562, -40.983947, true, '2024-03-26 13:18:00'),
('Cedro Farmacêutica Indústria e Comércio S.A.', 'Cedro Farmacêutica', '90000021000108', 'contato@cedrofarmaceutica021.com.br', '41948740164', -10.516467, -45.513125, true, '2024-09-24 12:18:00'),
('Boreal Comércio Alimentos ME', 'Boreal Comércio', '90000022000144', 'contato@borealcomercio022.com.br', '85901128059', -16.710251, -45.97897, true, '2024-02-19 10:47:00'),
('Trilha Alimentos Confecções ME', 'Trilha Alimentos', '90000023000199', 'contato@trilhaalimentos023.com.br', '11953315869', 0.661297, -42.45123, true, '2025-05-15 12:23:00'),
('Âncora Alimentos Soluções Corporativas ME', 'Âncora Alimentos', '90000024000133', 'contato@ancoraalimentos024.com.br', '71934216073', -26.684295, -37.444995, true, '2024-08-11 14:01:00'),
('Nortline Automotivo Materiais de Construção EPP', 'Nortline Automotivo', '90000025000188', 'contato@nortlineautomotivo025.com.br', '61941458685', -32.600751, -65.967938, true, '2023-11-22 11:14:00'),
('Vale Serviços Tecnologia ME', 'Vale Serviços', '90000026000122', 'contato@valeservicos026.com.br', '61969816934', -10.830292, -60.018203, true, '2024-02-12 08:06:00'),
('Boreal Metalúrgica Confecções LTDA', 'Boreal Metalúrgica', '90000027000177', 'contato@borealmetalurgica027.com.br', '62935615951', -6.16166, -44.563947, true, '2022-09-09 16:43:00'),
('Âncora Farmacêutica Tecnologia S.A.', 'Âncora Farmacêutica', '90000028000111', 'contato@ancorafarmaceutica028.com.br', '85923662994', -21.10505, -46.308872, true, '2023-11-14 18:26:00'),
('Prisma Educação Comércio e Serviços EPP', 'Prisma Educação', '90000029000166', 'contato@prismaeducacao029.com.br', '61977738721', -3.641165, -49.840199, true, '2023-12-30 11:27:00'),
('Central Educação Logística e Transportes EPP', 'Central Educação', '90000030000190', 'contato@centraleducacao030.com.br', '31900379176', -21.70406, -42.044047, true, '2025-01-29 11:43:00'),
('Domo Papelaria Logística e Transportes ME', 'Domo Papelaria', '90000031000135', 'contato@domopapelaria031.com.br', '11916328708', -24.228591, -47.913738, true, '2023-06-14 15:25:00'),
('Sul Mix Farmacêutica Indústria e Comércio EIRELI', 'Sul Mix Farmacêutica', '90000032000180', 'contato@sulmixfarmaceutica032.com.br', '61979868727', -13.316988, -51.701914, true, '2024-11-08 15:42:00'),
('Órbita Construção Soluções Corporativas EPP', 'Órbita Construção', '90000033000124', 'contato@orbitaconstrucao033.com.br', '51934558122', -24.408657, -56.344969, true, '2022-09-06 15:40:00'),
('Sul Mix Moda Distribuidora ME', 'Sul Mix Moda', '90000034000179', 'contato@sulmixmoda034.com.br', '11936690967', -20.926507, -55.328952, true, '2025-03-30 14:26:00'),
('Vale Logística Tecnologia EIRELI', 'Vale Logística', '90000035000113', 'contato@valelogistica035.com.br', '41973467065', -5.583906, -42.305141, true, '2023-04-03 16:47:00'),
('Cume Papelaria Confecções ME', 'Cume Papelaria', '90000036000168', 'contato@cumepapelaria036.com.br', '47990162720', -13.203302, -38.129774, true, '2022-12-25 10:39:00'),
('Bravo Moda Alimentos ME', 'Bravo Moda', '90000037000102', 'contato@bravomoda037.com.br', '71941708053', -4.573271, -58.714078, true, '2023-01-20 13:21:00'),
('Cristal Serviços Comércio e Serviços LTDA', 'Cristal Serviços', '90000038000157', 'contato@cristalservicos038.com.br', '31971937452', -32.725559, -67.557089, true, '2024-03-20 11:53:00'),
('Zênite Consultoria Indústria e Comércio ME', 'Zênite Consultoria', '90000039000100', 'contato@zeniteconsultoria039.com.br', '62966319314', -32.52422, -61.333667, true, '2023-11-10 09:37:00'),
('Coral Papelaria Logística e Transportes LTDA', 'Coral Papelaria', '90000040000126', 'contato@coralpapelaria040.com.br', '85950671657', -17.221635, -59.047736, true, '2025-05-16 13:34:00'),
('Rota Alimentos Confecções ME', 'Rota Alimentos', '90000041000170', 'contato@rotaalimentos041.com.br', '81976945314', -10.095331, -37.634911, true, '2022-09-05 18:17:00'),
('Domo Saúde Soluções Corporativas EIRELI', 'Domo Saúde', '90000042000115', 'contato@domosaude042.com.br', '81935454948', -14.716353, -60.824447, true, '2023-06-24 13:01:00'),
('Vale Educação Soluções Corporativas LTDA', 'Vale Educação', '90000043000160', 'contato@valeeducacao043.com.br', '81977014363', -12.403304, -64.127366, true, '2024-04-01 14:31:00'),
('Central Papelaria Logística e Transportes EPP', 'Central Papelaria', '90000044000104', 'contato@centralpapelaria044.com.br', '85955744431', -20.437219, -34.631373, true, '2022-12-13 16:33:00'),
('Âncora Tecnologia Alimentos LTDA', 'Âncora Tecnologia', '90000045000159', 'contato@ancoratecnologia045.com.br', '47989413435', -25.277384, -54.616255, true, '2022-08-07 10:12:00'),
('Estrela Construção Comércio e Serviços S.A.', 'Estrela Construção', '90000046000101', 'contato@estrelaconstrucao046.com.br', '31971094777', 3.523207, -51.919, true, '2024-11-22 12:02:00'),
('União Alimentos Comércio e Serviços EPP', 'União Alimentos', '90000047000148', 'contato@uniaoalimentos047.com.br', '47902294131', -31.016949, -54.321884, true, '2022-12-04 09:52:00'),
('Trilha Móveis Confecções S.A.', 'Trilha Móveis', '90000048000192', 'contato@trilhamoveis048.com.br', '47964990913', -16.38045, -56.233881, true, '2024-05-03 16:24:00'),
('Cristal Tecnologia Tecnologia LTDA', 'Cristal Tecnologia', '90000049000137', 'contato@cristaltecnologia049.com.br', '71979740344', -12.524295, -67.395228, true, '2024-09-21 11:11:00'),
('Rota Automotivo Materiais de Construção LTDA', 'Rota Automotivo', '90000050000161', 'contato@rotaautomotivo050.com.br', '41961832421', -3.432586, -50.498206, true, '2024-04-17 12:50:00'),
('Boreal Alimentos Tecnologia S.A.', 'Boreal Alimentos', '90000051000106', 'contato@borealalimentos051.com.br', '71948877190', -15.689477, -61.640444, true, '2023-12-26 15:07:00'),
('Domo Moda Alimentos S.A.', 'Domo Moda', '90000052000150', 'contato@domomoda052.com.br', '47990490278', -24.800872, -47.202364, true, '2024-03-14 08:05:00'),
('Cristal Saúde Tecnologia ME', 'Cristal Saúde', '90000053000103', 'contato@cristalsaude053.com.br', '61965512567', -14.81442, -69.941546, true, '2023-02-28 18:52:00'),
('Central Papelaria Confecções S.A.', 'Central Papelaria', '90000054000140', 'contato@centralpapelaria054.com.br', '71980876038', -21.548348, -60.892322, true, '2025-05-25 15:05:00'),
('Alfa Consultoria Materiais de Construção EIRELI', 'Alfa Consultoria', '90000055000194', 'contato@alfaconsultoria055.com.br', '81971093248', -12.629654, -37.320762, true, '2025-04-25 11:17:00'),
('Vale Educação Confecções LTDA', 'Vale Educação', '90000056000139', 'contato@valeeducacao056.com.br', '31974846773', 2.488308, -48.239322, true, '2024-05-05 09:29:00'),
('Órbita Educação Distribuidora EIRELI', 'Órbita Educação', '90000057000183', 'contato@orbitaeducacao057.com.br', '21946584044', -5.140883, -68.176175, true, '2024-07-14 17:32:00'),
('Bússola Consultoria Logística e Transportes EIRELI', 'Bússola Consultoria', '90000058000128', 'contato@bussolaconsultoria058.com.br', '85967533963', -15.095027, -60.534842, true, '2024-10-08 15:34:00'),
('Coral Móveis Confecções LTDA', 'Coral Móveis', '90000059000172', 'contato@coralmoveis059.com.br', '62927028951', 1.210346, -58.631985, true, '2023-10-27 15:45:00'),
('Coral Automotivo Materiais de Construção LTDA', 'Coral Automotivo', '90000060000105', 'contato@coralautomotivo060.com.br', '62921745961', -6.047135, -57.510834, true, '2022-08-27 15:00:00'),
('Coral Logística Consultoria Empresarial EIRELI', 'Coral Logística', '90000061000141', 'contato@corallogistica061.com.br', '85909134316', 0.22267, -54.470048, true, '2023-10-30 18:45:00'),
('Cedro Móveis Indústria e Comércio EIRELI', 'Cedro Móveis', '90000062000196', 'contato@cedromoveis062.com.br', '11945562386', -32.400005, -60.849388, true, '2024-09-02 12:57:00'),
('Marca Papelaria Distribuidora ME', 'Marca Papelaria', '90000063000130', 'contato@marcapapelaria063.com.br', '41979237474', -0.408095, -49.331296, true, '2024-08-15 09:39:00'),
('Cume Comércio Materiais de Construção EPP', 'Cume Comércio', '90000064000185', 'contato@cumecomercio064.com.br', '51964743671', 2.418954, -34.981584, true, '2024-09-19 09:28:00'),
('Sul Mix Moda Logística e Transportes EPP', 'Sul Mix Moda', '90000065000120', 'contato@sulmixmoda065.com.br', '51909097439', 3.66189, -47.829037, true, '2023-12-12 12:01:00'),
('Ipê Logística Soluções Corporativas ME', 'Ipê Logística', '90000066000174', 'contato@ipelogistica066.com.br', '31910470952', -6.104722, -47.79192, true, '2024-03-13 18:48:00'),
('Horizonte Farmacêutica Tecnologia EPP', 'Horizonte Farmacêutica', '90000067000119', 'contato@horizontefarmaceut067.com.br', '61988424745', -28.477928, -52.463097, true, '2023-04-11 10:12:00'),
('Ipê Serviços Materiais de Construção LTDA', 'Ipê Serviços', '90000068000163', 'contato@ipeservicos068.com.br', '71985160481', -7.799698, -47.198157, true, '2024-10-24 11:55:00'),
('Órbita Logística Tecnologia S.A.', 'Órbita Logística', '90000069000108', 'contato@orbitalogistica069.com.br', '11998593174', -29.382848, -64.380946, true, '2023-06-20 18:23:00'),
('Cristal Moda Indústria e Comércio ME', 'Cristal Moda', '90000070000132', 'contato@cristalmoda070.com.br', '41982675869', 4.34524, -68.971301, true, '2025-05-08 08:19:00'),
('Âncora Metalúrgica Distribuidora EIRELI', 'Âncora Metalúrgica', '90000071000187', 'contato@ancorametalurgica071.com.br', '11953773515', -17.991596, -36.390661, true, '2025-01-18 15:39:00'),
('Trilha Farmacêutica Alimentos LTDA', 'Trilha Farmacêutica', '90000072000121', 'contato@trilhafarmaceutica072.com.br', '81913900532', 3.291861, -36.488579, true, '2023-05-16 12:12:00'),
('Ipê Consultoria Soluções Corporativas LTDA', 'Ipê Consultoria', '90000073000176', 'contato@ipeconsultoria073.com.br', '31990422842', -2.603987, -64.413052, true, '2024-06-10 17:13:00'),
('Cedro Papelaria Comércio e Serviços ME', 'Cedro Papelaria', '90000074000110', 'contato@cedropapelaria074.com.br', '11924026811', -3.539602, -50.534736, true, '2025-07-09 13:50:00'),
('Ponta Saúde Alimentos S.A.', 'Ponta Saúde', '90000075000165', 'contato@pontasaude075.com.br', '62984700766', -25.081109, -49.510833, true, '2024-12-28 15:32:00'),
('Cume Serviços Materiais de Construção EIRELI', 'Cume Serviços', '90000076000100', 'contato@cumeservicos076.com.br', '51999856984', -10.384698, -70.938393, true, '2025-03-11 09:20:00'),
('Órbita Educação Logística e Transportes EIRELI', 'Órbita Educação', '90000077000154', 'contato@orbitaeducacao077.com.br', '85936736576', -8.620908, -39.269458, true, '2025-01-17 09:54:00'),
('Vetor Metalúrgica Indústria e Comércio EPP', 'Vetor Metalúrgica', '90000078000107', 'contato@vetormetalurgica078.com.br', '62971111615', -23.814188, -36.309892, true, '2023-03-18 13:42:00'),
('Ipê Alimentos Consultoria Empresarial LTDA', 'Ipê Alimentos', '90000079000143', 'contato@ipealimentos079.com.br', '71904945198', -17.889272, -39.429312, true, '2023-10-03 18:07:00'),
('Nortline Alimentos Materiais de Construção ME', 'Nortline Alimentos', '90000080000178', 'contato@nortlinealimentos080.com.br', '51993689980', -12.361912, -69.019473, true, '2024-12-30 13:54:00'),
('Zênite Farmacêutica Tecnologia LTDA', 'Zênite Farmacêutica', '90000081000112', 'contato@zenitefarmaceutica081.com.br', '61902296120', -4.528438, -37.560037, true, '2024-08-04 12:44:00'),
('Horizonte Metalúrgica Consultoria Empresarial ME', 'Horizonte Metalúrgica', '90000082000167', 'contato@horizontemetalurgi082.com.br', '61999102290', -20.545953, -59.067929, true, '2023-06-30 14:29:00'),
('Cume Serviços Tecnologia EIRELI', 'Cume Serviços', '90000083000101', 'contato@cumeservicos083.com.br', '85915614978', -16.702321, -62.846493, true, '2023-03-24 15:38:00'),
('Cume Construção Comércio e Serviços ME', 'Cume Construção', '90000084000156', 'contato@cumeconstrucao084.com.br', '41924451076', -33.207891, -61.744645, true, '2023-05-22 17:03:00'),
('Estrela Alimentos Confecções S.A.', 'Estrela Alimentos', '90000085000109', 'contato@estrelaalimentos085.com.br', '21960607159', -1.83254, -42.058193, true, '2024-04-24 16:35:00'),
('Vetor Consultoria Confecções EIRELI', 'Vetor Consultoria', '90000086000145', 'contato@vetorconsultoria086.com.br', '47975161369', -32.707451, -60.834041, true, '2023-12-25 09:25:00'),
('Prisma Educação Indústria e Comércio EIRELI', 'Prisma Educação', '90000087000190', 'contato@prismaeducacao087.com.br', '85918835523', -20.843392, -66.942824, true, '2023-11-12 13:14:00'),
('Cedro Alimentos Tecnologia ME', 'Cedro Alimentos', '90000088000134', 'contato@cedroalimentos088.com.br', '31977997995', -4.611819, -47.935149, true, '2024-08-18 17:09:00'),
('Coral Papelaria Alimentos ME', 'Coral Papelaria', '90000089000189', 'contato@coralpapelaria089.com.br', '51990581477', -16.696094, -61.690003, true, '2023-02-20 09:30:00'),
('Boreal Comércio Alimentos EPP', 'Boreal Comércio', '90000090000103', 'contato@borealcomercio090.com.br', '47986798079', 3.640004, -69.978436, true, '2025-03-04 18:55:00'),
('Cristal Tecnologia Alimentos S.A.', 'Cristal Tecnologia', '90000091000158', 'contato@cristaltecnologia091.com.br', '61918203778', 2.89345, -55.929599, true, '2022-12-08 16:09:00'),
('Raiz Consultoria Distribuidora EPP', 'Raiz Consultoria', '90000092000100', 'contato@raizconsultoria092.com.br', '47905151864', -17.966763, -60.303681, true, '2023-07-07 12:24:00'),
('Bravo Metalúrgica Logística e Transportes ME', 'Bravo Metalúrgica', '90000093000147', 'contato@bravometalurgica093.com.br', '51962914865', -8.28274, -37.746888, true, '2023-09-26 09:37:00'),
('Bússola Alimentos Consultoria Empresarial LTDA', 'Bússola Alimentos', '90000094000191', 'contato@bussolaalimentos094.com.br', '41957332214', -32.80724, -61.454223, true, '2023-03-25 16:23:00'),
('Coral Móveis Indústria e Comércio S.A.', 'Coral Móveis', '90000095000136', 'contato@coralmoveis095.com.br', '31996222927', -20.702954, -43.614003, true, '2022-08-26 08:42:00'),
('Boreal Moda Alimentos ME', 'Boreal Moda', '90000096000180', 'contato@borealmoda096.com.br', '41983473597', -4.909136, -42.931786, true, '2023-02-11 17:18:00'),
('Órbita Móveis Tecnologia EIRELI', 'Órbita Móveis', '90000097000125', 'contato@orbitamoveis097.com.br', '47924075818', 3.21819, -41.641242, true, '2022-10-14 16:26:00'),
('Coral Serviços Tecnologia LTDA', 'Coral Serviços', '90000098000170', 'contato@coralservicos098.com.br', '71913750606', 0.901636, -67.753402, true, '2024-09-14 12:28:00'),
('Raiz Logística Soluções Corporativas EIRELI', 'Raiz Logística', '90000099000114', 'contato@raizlogistica099.com.br', '62952204727', -32.428873, -37.19779, true, '2025-02-22 13:14:00'),
('Órbita Consultoria Comércio e Serviços LTDA', 'Órbita Consultoria', '90000100000100', 'contato@orbitaconsultoria100.com.br', '47986143410', -1.771402, -52.096634, true, '2025-07-01 12:13:00');

-- =========================================================
-- 2) SUBSCRIPTIONPLAN (100 registros, 1:1 com Company)
-- =========================================================
INSERT INTO SubscriptionPlan (Name, Description, Price, StoreLimit, UserLimit, Active, CreatedAt, CompanyId) VALUES
('Plano Profissional', 'Plano de assinatura profissional com suporte e recursos administrativos para PME.', 361.78, 8, 24, true, '2024-03-29 11:08:00', 1),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1540.0, 50, 150, true, '2024-06-30 18:44:00', 2),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 161.35, 3, 9, true, '2023-06-26 09:22:00', 3),
('Plano Profissional', 'Plano de assinatura profissional com suporte e recursos administrativos para PME.', 382.69, 8, 24, true, '2024-07-13 09:02:00', 4),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1520.41, 50, 150, true, '2024-08-25 16:46:00', 5),
('Plano Profissional', 'Plano de assinatura profissional com suporte e recursos administrativos para PME.', 372.34, 8, 24, true, '2023-11-04 11:41:00', 6),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 158.1, 3, 9, true, '2023-05-16 13:14:00', 7),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 170.99, 3, 9, true, '2022-08-25 12:35:00', 8),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 154.11, 3, 9, true, '2024-08-11 16:58:00', 9),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 161.46, 3, 9, true, '2022-11-14 08:07:00', 10),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 879.46, 20, 60, true, '2022-12-11 16:10:00', 11),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 152.74, 3, 9, true, '2025-04-04 13:01:00', 12),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 165.53, 3, 9, true, '2024-06-01 16:08:00', 13),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 781.25, 20, 60, true, '2025-04-22 18:41:00', 14),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 890.05, 20, 60, true, '2024-03-16 09:28:00', 15),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1833.75, 50, 150, true, '2024-09-08 14:00:00', 16),
('Plano Profissional', 'Plano de assinatura profissional com suporte e recursos administrativos para PME.', 389.19, 8, 24, true, '2023-11-05 08:03:00', 17),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1531.74, 50, 150, true, '2024-12-07 17:15:00', 18),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 166.12, 3, 9, true, '2023-12-03 15:20:00', 19),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 909.23, 20, 60, true, '2024-03-30 13:18:00', 20),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1836.4, 50, 150, true, '2024-09-28 12:18:00', 21),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1600.96, 50, 150, true, '2024-02-24 10:47:00', 22),
('Plano Profissional', 'Plano de assinatura profissional com suporte e recursos administrativos para PME.', 352.43, 8, 24, true, '2025-05-15 12:23:00', 23),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 803.61, 20, 60, true, '2024-08-13 14:01:00', 24),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1804.52, 50, 150, true, '2023-11-27 11:14:00', 25),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1806.17, 50, 150, true, '2024-02-15 08:06:00', 26),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 915.68, 20, 60, true, '2022-09-12 16:43:00', 27),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1637.45, 50, 150, true, '2023-11-18 18:26:00', 28),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 888.07, 20, 60, true, '2024-01-04 11:27:00', 29),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1545.16, 50, 150, true, '2025-02-02 11:43:00', 30),
('Plano Profissional', 'Plano de assinatura profissional com suporte e recursos administrativos para PME.', 370.58, 8, 24, true, '2023-06-16 15:25:00', 31),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 144.81, 3, 9, true, '2024-11-13 15:42:00', 32),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 808.94, 20, 60, true, '2022-09-10 15:40:00', 33),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1573.23, 50, 150, true, '2025-04-02 14:26:00', 34),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 831.44, 20, 60, true, '2023-04-08 16:47:00', 35),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 858.27, 20, 60, true, '2022-12-28 10:39:00', 36),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 862.21, 20, 60, true, '2023-01-20 13:21:00', 37),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 162.53, 3, 9, true, '2024-03-23 11:53:00', 38),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 841.99, 20, 60, true, '2023-11-15 09:37:00', 39),
('Plano Profissional', 'Plano de assinatura profissional com suporte e recursos administrativos para PME.', 399.08, 8, 24, true, '2025-05-17 13:34:00', 40),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1531.04, 50, 150, true, '2022-09-05 18:17:00', 41),
('Plano Profissional', 'Plano de assinatura profissional com suporte e recursos administrativos para PME.', 386.89, 8, 24, true, '2023-06-26 13:01:00', 42),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 821.15, 20, 60, true, '2024-04-05 14:31:00', 43),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 160.55, 3, 9, true, '2022-12-18 16:33:00', 44),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1823.51, 50, 150, true, '2022-08-09 10:12:00', 45),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1764.08, 50, 150, true, '2024-11-26 12:02:00', 46),
('Plano Profissional', 'Plano de assinatura profissional com suporte e recursos administrativos para PME.', 369.46, 8, 24, true, '2022-12-07 09:52:00', 47),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 863.82, 20, 60, true, '2024-05-04 16:24:00', 48),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 143.18, 3, 9, true, '2024-09-22 11:11:00', 49),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1685.57, 50, 150, true, '2024-04-21 12:50:00', 50),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 150.25, 3, 9, true, '2023-12-28 15:07:00', 51),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1588.56, 50, 150, true, '2024-03-18 08:05:00', 52),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 870.93, 20, 60, true, '2023-03-03 18:52:00', 53),
('Plano Profissional', 'Plano de assinatura profissional com suporte e recursos administrativos para PME.', 340.99, 8, 24, true, '2025-05-25 15:05:00', 54),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1575.15, 50, 150, true, '2025-04-30 11:17:00', 55),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1839.01, 50, 150, true, '2024-05-10 09:29:00', 56),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 866.75, 20, 60, true, '2024-07-19 17:32:00', 57),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 158.89, 3, 9, true, '2024-10-10 15:34:00', 58),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 896.11, 20, 60, true, '2023-11-01 15:45:00', 59),
('Plano Profissional', 'Plano de assinatura profissional com suporte e recursos administrativos para PME.', 387.95, 8, 24, true, '2022-08-31 15:00:00', 60),
('Plano Profissional', 'Plano de assinatura profissional com suporte e recursos administrativos para PME.', 340.9, 8, 24, true, '2023-10-31 18:45:00', 61),
('Plano Profissional', 'Plano de assinatura profissional com suporte e recursos administrativos para PME.', 348.97, 8, 24, true, '2024-09-05 12:57:00', 62),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 153.22, 3, 9, true, '2024-08-19 09:39:00', 63),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 834.68, 20, 60, true, '2024-09-23 09:28:00', 64),
('Plano Profissional', 'Plano de assinatura profissional com suporte e recursos administrativos para PME.', 375.23, 8, 24, true, '2023-12-12 12:01:00', 65),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 151.68, 3, 9, true, '2024-03-18 18:48:00', 66),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1688.12, 50, 150, true, '2023-04-14 10:12:00', 67),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 146.16, 3, 9, true, '2024-10-26 11:55:00', 68),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 155.9, 3, 9, true, '2023-06-25 18:23:00', 69),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 780.44, 20, 60, true, '2025-05-12 08:19:00', 70),
('Plano Profissional', 'Plano de assinatura profissional com suporte e recursos administrativos para PME.', 386.13, 8, 24, true, '2025-01-19 15:39:00', 71),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1680.81, 50, 150, true, '2023-05-16 12:12:00', 72),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 157.94, 3, 9, true, '2024-06-12 17:13:00', 73),
('Plano Profissional', 'Plano de assinatura profissional com suporte e recursos administrativos para PME.', 343.74, 8, 24, true, '2025-07-14 13:50:00', 74),
('Plano Profissional', 'Plano de assinatura profissional com suporte e recursos administrativos para PME.', 356.62, 8, 24, true, '2025-01-01 15:32:00', 75),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 895.24, 20, 60, true, '2025-03-13 09:20:00', 76),
('Plano Profissional', 'Plano de assinatura profissional com suporte e recursos administrativos para PME.', 376.85, 8, 24, true, '2025-01-21 09:54:00', 77),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 779.93, 20, 60, true, '2023-03-20 13:42:00', 78),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 157.47, 3, 9, true, '2023-10-04 18:07:00', 79),
('Plano Profissional', 'Plano de assinatura profissional com suporte e recursos administrativos para PME.', 344.39, 8, 24, true, '2025-01-03 13:54:00', 80),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 894.69, 20, 60, true, '2024-08-08 12:44:00', 81),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 167.15, 3, 9, true, '2023-06-30 14:29:00', 82),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 143.77, 3, 9, true, '2023-03-29 15:38:00', 83),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 864.07, 20, 60, true, '2023-05-26 17:03:00', 84),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1717.69, 50, 150, true, '2024-04-24 16:35:00', 85),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1804.37, 50, 150, true, '2023-12-29 09:25:00', 86),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 862.6, 20, 60, true, '2023-11-14 13:14:00', 87),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1598.26, 50, 150, true, '2024-08-23 17:09:00', 88),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1615.08, 50, 150, true, '2023-02-20 09:30:00', 89),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 147.14, 3, 9, true, '2025-03-07 18:55:00', 90),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1668.52, 50, 150, true, '2022-12-10 16:09:00', 91),
('Plano Profissional', 'Plano de assinatura profissional com suporte e recursos administrativos para PME.', 354.28, 8, 24, true, '2023-07-12 12:24:00', 92),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 877.36, 20, 60, true, '2023-09-28 09:37:00', 93),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1561.75, 50, 150, true, '2023-03-27 16:23:00', 94),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 151.97, 3, 9, true, '2022-08-29 08:42:00', 95),
('Plano Essencial', 'Plano de assinatura essencial com suporte e recursos administrativos para PME.', 150.42, 3, 9, true, '2023-02-12 17:18:00', 96),
('Plano Profissional', 'Plano de assinatura profissional com suporte e recursos administrativos para PME.', 339.18, 8, 24, true, '2022-10-16 16:26:00', 97),
('Plano Enterprise', 'Plano de assinatura enterprise com suporte e recursos administrativos para PME.', 1796.42, 50, 150, true, '2024-09-17 12:28:00', 98),
('Plano Profissional', 'Plano de assinatura profissional com suporte e recursos administrativos para PME.', 399.77, 8, 24, true, '2025-02-23 13:14:00', 99),
('Plano Corporativo', 'Plano de assinatura corporativo com suporte e recursos administrativos para PME.', 909.29, 20, 60, true, '2025-07-06 12:13:00', 100);

-- =========================================================
-- 3) ADDRESS (100 registros)
-- =========================================================
INSERT INTO Address (ZipCode, Street, Number, Complement, Neighborhood, City, State, CreatedAt) VALUES
('34881781', 'Rua Sete de Setembro', '4091', NULL, 'Bela Vista', 'Fortaleza', 'Ceará', '2023-05-10 12:31:00'),
('21802967', 'Rua Barão do Rio', '4150', NULL, 'Boa Vista', 'São Paulo', 'São Paulo', '2024-12-11 17:22:00'),
('16624288', 'Rua XV de Novembro', '4904', NULL, 'Bela Vista', 'Campinas', 'São Paulo', '2024-07-19 16:00:00'),
('66482137', 'Av. Rio Branco', '2176', NULL, 'Santa Cruz', 'Londrina', 'Paraná', '2024-02-06 08:25:00'),
('84747575', 'Av. Paulista Norte', '2979', NULL, 'Jardim América', 'Campinas', 'São Paulo', '2023-12-26 16:28:00'),
('46663845', 'Rua dos Andradas', '983', 'Sala 7', 'Santa Cruz', 'Natal', 'Rio Grande do Norte', '2024-01-22 13:35:00'),
('28909203', 'Rua dos Andradas', '4180', NULL, 'Centro', 'Goiânia', 'Goiás', '2025-11-24 08:08:00'),
('72094531', 'Rua XV de Novembro', '1230', NULL, 'Alto da Serra', 'Fortaleza', 'Ceará', '2025-05-14 13:59:00'),
('51750166', 'Rua Barão do Rio', '2461', NULL, 'Alto da Serra', 'Campo Grande', 'Mato Grosso do Sul', '2023-04-18 16:31:00'),
('49294486', 'Rua do Comércio', '146', 'Sala 8', 'Santa Cruz', 'João Pessoa', 'Paraíba', '2022-11-17 12:50:00'),
('88238485', 'Rua das Palmeiras', '4748', NULL, 'Centro', 'São Paulo', 'São Paulo', '2022-11-17 15:10:00'),
('92447738', 'Rua dos Andradas', '3124', 'Sala 16', 'Centro', 'Manaus', 'Amazonas', '2022-12-10 09:12:00'),
('67807321', 'Rua Barão do Rio', '1250', NULL, 'Boa Vista', 'São Paulo', 'São Paulo', '2023-11-07 16:49:00'),
('71814893', 'Rua do Comércio', '520', NULL, 'Alto da Serra', 'Campo Grande', 'Mato Grosso do Sul', '2024-12-27 16:20:00'),
('78875385', 'Av. Industrial', '1428', NULL, 'Alto da Serra', 'Brasília', 'Distrito Federal', '2024-03-25 16:22:00'),
('89963495', 'Av. Paulista Norte', '2029', 'Sala 20', 'Boa Vista', 'Salvador', 'Bahia', '2024-06-25 12:45:00'),
('74094324', 'Rua XV de Novembro', '2867', NULL, 'Industrial', 'Belo Horizonte', 'Minas Gerais', '2024-07-15 09:36:00'),
('59767917', 'Rua Barão do Rio', '2837', NULL, 'Vila Nova', 'Natal', 'Rio Grande do Norte', '2024-07-10 08:18:00'),
('55398946', 'Rua XV de Novembro', '2110', NULL, 'Boa Vista', 'Curitiba', 'Paraná', '2025-01-07 16:17:00'),
('45601140', 'Av. Brasil', '4817', 'Sala 4', 'Alto da Serra', 'Natal', 'Rio Grande do Norte', '2024-11-19 18:14:00'),
('23133423', 'Av. Industrial', '3879', 'Sala 9', 'Centro', 'Campinas', 'São Paulo', '2023-01-24 10:26:00'),
('72580666', 'Av. Paulista Norte', '2363', 'Sala 4', 'Jardim América', 'Brasília', 'Distrito Federal', '2022-12-07 11:34:00'),
('32946427', 'Av. Rio Branco', '1450', NULL, 'Centro', 'Campinas', 'São Paulo', '2023-12-04 15:11:00'),
('14910009', 'Rua das Palmeiras', '4662', NULL, 'São José', 'Recife', 'Pernambuco', '2024-07-22 15:41:00'),
('78723505', 'Av. Rio Branco', '1109', NULL, 'Bela Vista', 'Natal', 'Rio Grande do Norte', '2024-08-15 11:51:00'),
('53343166', 'Av. Independência', '3769', NULL, 'Vila Nova', 'Londrina', 'Paraná', '2026-01-27 13:50:00'),
('84420690', 'Rua das Flores', '1592', 'Sala 26', 'Santa Cruz', 'Recife', 'Pernambuco', '2023-04-06 13:05:00'),
('97720097', 'Rua Sete de Setembro', '1161', NULL, 'Boa Vista', 'Florianópolis', 'Santa Catarina', '2026-02-11 12:24:00'),
('68554771', 'Rua das Palmeiras', '2710', 'Sala 37', 'Centro', 'Uberlândia', 'Minas Gerais', '2024-09-07 18:23:00'),
('18142683', 'Av. Brasil', '3822', 'Sala 26', 'Alto da Serra', 'Uberlândia', 'Minas Gerais', '2024-05-29 09:40:00'),
('58174629', 'Av. Paulista Norte', '1805', NULL, 'Bela Vista', 'Recife', 'Pernambuco', '2025-04-17 15:47:00'),
('58971425', 'Av. Independência', '4509', NULL, 'Alto da Serra', 'Campo Grande', 'Mato Grosso do Sul', '2024-12-04 11:43:00'),
('20730538', 'Rua XV de Novembro', '4336', NULL, 'Jardim América', 'Campo Grande', 'Mato Grosso do Sul', '2022-12-27 09:03:00'),
('76255207', 'Rua dos Andradas', '4406', 'Sala 21', 'Alto da Serra', 'Natal', 'Rio Grande do Norte', '2023-09-04 09:43:00'),
('86434500', 'Av. Brasil', '4190', NULL, 'Centro', 'Belo Horizonte', 'Minas Gerais', '2023-08-11 10:32:00'),
('69873577', 'Rua das Acácias', '4587', NULL, 'Industrial', 'Joinville', 'Santa Catarina', '2026-01-11 14:16:00'),
('38560592', 'Av. Brasil', '381', NULL, 'Jardim América', 'São Paulo', 'São Paulo', '2023-02-12 08:59:00'),
('71875032', 'Rua das Palmeiras', '3359', 'Sala 9', 'Santa Cruz', 'Curitiba', 'Paraná', '2024-01-20 14:28:00'),
('59236082', 'Rua Marechal Deodoro', '4427', 'Sala 23', 'Jardim América', 'Florianópolis', 'Santa Catarina', '2025-02-24 16:25:00'),
('26691745', 'Av. Paulista Norte', '38', NULL, 'Industrial', 'Manaus', 'Amazonas', '2023-07-22 18:46:00'),
('65591544', 'Rua Barão do Rio', '1891', 'Sala 23', 'Vila Nova', 'Natal', 'Rio Grande do Norte', '2024-08-09 11:59:00'),
('14222828', 'Rua Marechal Deodoro', '3444', NULL, 'Centro', 'Londrina', 'Paraná', '2024-10-20 11:04:00'),
('87849034', 'Rua XV de Novembro', '4907', NULL, 'Centro', 'Londrina', 'Paraná', '2024-10-11 08:25:00'),
('40715552', 'Av. Paulista Norte', '472', 'Sala 19', 'Boa Vista', 'Vitória', 'Espírito Santo', '2022-12-03 13:36:00'),
('98150838', 'Av. Industrial', '1950', 'Sala 10', 'Alto da Serra', 'Campo Grande', 'Mato Grosso do Sul', '2024-11-29 14:19:00'),
('17989570', 'Rua dos Andradas', '1444', NULL, 'Santa Cruz', 'Salvador', 'Bahia', '2023-01-13 15:03:00'),
('94320687', 'Rua Barão do Rio', '4306', 'Sala 27', 'Santa Cruz', 'Goiânia', 'Goiás', '2025-04-25 12:24:00'),
('80507484', 'Av. Paulista Norte', '1856', 'Sala 10', 'Bela Vista', 'Caxias do Sul', 'Rio Grande do Sul', '2025-10-29 16:26:00'),
('83044542', 'Rua Sete de Setembro', '3191', 'Sala 14', 'São José', 'Joinville', 'Santa Catarina', '2025-09-16 15:54:00'),
('22119548', 'Av. Independência', '1570', 'Sala 25', 'Vila Industrial', 'Goiânia', 'Goiás', '2025-12-06 09:12:00'),
('98008572', 'Av. Paulista Norte', '3937', 'Sala 22', 'Industrial', 'João Pessoa', 'Paraíba', '2026-01-24 11:59:00'),
('25376764', 'Rua das Flores', '3933', NULL, 'Vila Industrial', 'Belo Horizonte', 'Minas Gerais', '2025-01-02 14:58:00'),
('82362329', 'Rua das Flores', '2328', 'Sala 35', 'São José', 'Uberlândia', 'Minas Gerais', '2024-06-03 12:23:00'),
('75179477', 'Av. Brasil', '3856', NULL, 'São José', 'Manaus', 'Amazonas', '2025-01-04 13:20:00'),
('15992576', 'Rua do Comércio', '1822', NULL, 'Centro', 'Joinville', 'Santa Catarina', '2024-09-08 16:59:00'),
('85909737', 'Rua Barão do Rio', '2428', 'Sala 22', 'Boa Vista', 'João Pessoa', 'Paraíba', '2024-01-09 17:53:00'),
('75444563', 'Rua Marechal Deodoro', '2775', 'Sala 32', 'Bela Vista', 'Uberlândia', 'Minas Gerais', '2023-07-27 10:46:00'),
('32185143', 'Av. Independência', '4487', NULL, 'Alto da Serra', 'Goiânia', 'Goiás', '2025-10-27 16:02:00'),
('97584049', 'Rua das Flores', '62', NULL, 'Boa Vista', 'Curitiba', 'Paraná', '2025-10-08 10:00:00'),
('76287466', 'Av. Industrial', '505', NULL, 'Vila Industrial', 'Belo Horizonte', 'Minas Gerais', '2023-06-11 18:31:00'),
('10866544', 'Av. Getúlio Vargas', '3379', 'Sala 34', 'Industrial', 'São Paulo', 'São Paulo', '2023-02-23 12:01:00'),
('98346440', 'Rua das Flores', '1478', 'Sala 7', 'Alto da Serra', 'Manaus', 'Amazonas', '2025-04-26 11:12:00'),
('79003258', 'Rua do Comércio', '2913', 'Sala 26', 'Jardim América', 'Campo Grande', 'Mato Grosso do Sul', '2024-01-22 14:29:00'),
('41877713', 'Av. Paulista Norte', '2467', NULL, 'Jardim América', 'João Pessoa', 'Paraíba', '2025-12-22 09:25:00'),
('59436565', 'Rua XV de Novembro', '469', NULL, 'Vila Nova', 'Florianópolis', 'Santa Catarina', '2025-09-08 15:54:00'),
('94529803', 'Av. Industrial', '4645', NULL, 'Jardim América', 'Joinville', 'Santa Catarina', '2023-03-11 08:14:00'),
('84289486', 'Rua das Palmeiras', '391', NULL, 'Industrial', 'Belo Horizonte', 'Minas Gerais', '2023-02-08 17:42:00'),
('33508951', 'Rua do Comércio', '2586', 'Sala 14', 'Vila Industrial', 'Campinas', 'São Paulo', '2025-05-05 14:59:00'),
('49226166', 'Rua dos Andradas', '1979', NULL, 'Santa Cruz', 'Curitiba', 'Paraná', '2023-02-13 13:24:00'),
('20279512', 'Av. Independência', '2843', 'Sala 28', 'Boa Vista', 'Porto Alegre', 'Rio Grande do Sul', '2025-09-22 13:38:00'),
('88067406', 'Rua das Flores', '2683', 'Sala 18', 'Bela Vista', 'Campo Grande', 'Mato Grosso do Sul', '2023-05-27 11:22:00'),
('59207442', 'Rua Sete de Setembro', '4809', NULL, 'Jardim América', 'Natal', 'Rio Grande do Norte', '2024-06-30 11:45:00'),
('20752274', 'Rua Sete de Setembro', '3122', NULL, 'Vila Nova', 'Curitiba', 'Paraná', '2023-12-20 13:23:00'),
('21995005', 'Rua das Palmeiras', '3653', 'Sala 18', 'Jardim América', 'Londrina', 'Paraná', '2025-05-29 09:11:00'),
('68830570', 'Av. Getúlio Vargas', '4207', NULL, 'Centro', 'Joinville', 'Santa Catarina', '2025-08-25 13:35:00'),
('88240612', 'Rua das Flores', '2667', NULL, 'Centro', 'Curitiba', 'Paraná', '2024-07-07 14:24:00'),
('83339926', 'Av. Paulista Norte', '4695', NULL, 'Santa Cruz', 'Curitiba', 'Paraná', '2025-03-14 10:17:00'),
('45180504', 'Rua Sete de Setembro', '528', 'Sala 18', 'Santa Cruz', 'Recife', 'Pernambuco', '2024-06-21 15:50:00'),
('57333257', 'Av. Paulista Norte', '4071', NULL, 'Boa Vista', 'Curitiba', 'Paraná', '2023-08-02 09:08:00'),
('10887971', 'Rua Barão do Rio', '2731', NULL, 'Santa Cruz', 'Recife', 'Pernambuco', '2024-04-21 15:21:00'),
('95332997', 'Rua dos Andradas', '1125', 'Sala 39', 'Boa Vista', 'Joinville', 'Santa Catarina', '2023-06-19 13:11:00'),
('51830298', 'Av. Independência', '4032', NULL, 'Boa Vista', 'Florianópolis', 'Santa Catarina', '2024-04-28 14:17:00'),
('57843116', 'Av. Rio Branco', '4629', 'Sala 35', 'Vila Nova', 'Florianópolis', 'Santa Catarina', '2023-01-25 08:46:00'),
('37479448', 'Rua das Flores', '2396', NULL, 'Jardim América', 'Vitória', 'Espírito Santo', '2023-11-10 18:31:00'),
('93012310', 'Av. Paulista Norte', '2079', NULL, 'Santa Cruz', 'Porto Alegre', 'Rio Grande do Sul', '2024-01-14 09:28:00'),
('72814596', 'Rua Barão do Rio', '4387', NULL, 'Santa Cruz', 'Campo Grande', 'Mato Grosso do Sul', '2023-02-07 08:50:00'),
('80373608', 'Rua Marechal Deodoro', '706', 'Sala 16', 'São José', 'Goiânia', 'Goiás', '2025-03-21 18:39:00'),
('83822660', 'Rua Marechal Deodoro', '3283', NULL, 'São José', 'Campinas', 'São Paulo', '2023-09-27 09:00:00'),
('43913227', 'Av. Getúlio Vargas', '4259', NULL, 'Vila Industrial', 'Londrina', 'Paraná', '2024-11-29 15:23:00'),
('70694790', 'Rua Marechal Deodoro', '4831', NULL, 'Vila Nova', 'Florianópolis', 'Santa Catarina', '2024-03-20 08:30:00'),
('48722425', 'Av. Rio Branco', '713', 'Sala 10', 'São José', 'Londrina', 'Paraná', '2024-05-27 13:29:00'),
('78303494', 'Av. Industrial', '3910', 'Sala 29', 'São José', 'Belo Horizonte', 'Minas Gerais', '2025-10-10 12:02:00'),
('12960869', 'Av. Industrial', '905', NULL, 'Vila Nova', 'Londrina', 'Paraná', '2024-10-13 16:11:00'),
('30989338', 'Av. Getúlio Vargas', '3502', NULL, 'Boa Vista', 'Natal', 'Rio Grande do Norte', '2023-11-18 18:11:00'),
('93948673', 'Rua Barão do Rio', '3250', 'Sala 40', 'Boa Vista', 'Caxias do Sul', 'Rio Grande do Sul', '2023-08-19 17:27:00'),
('10583721', 'Av. Paulista Norte', '1695', 'Sala 5', 'Vila Industrial', 'Florianópolis', 'Santa Catarina', '2025-07-31 16:11:00'),
('52727201', 'Rua XV de Novembro', '943', 'Sala 32', 'Alto da Serra', 'Goiânia', 'Goiás', '2024-05-24 17:24:00'),
('61451601', 'Av. Brasil', '2860', 'Sala 30', 'Vila Industrial', 'Campo Grande', 'Mato Grosso do Sul', '2025-03-08 18:45:00'),
('90541604', 'Av. Brasil', '1107', 'Sala 16', 'Industrial', 'Recife', 'Pernambuco', '2025-06-30 10:23:00'),
('76954397', 'Rua Barão do Rio', '4895', 'Sala 25', 'Santa Cruz', 'Porto Alegre', 'Rio Grande do Sul', '2025-02-09 15:40:00');

-- =========================================================
-- 4) STORE (100 registros, distribuição variável por empresa)
-- =========================================================
INSERT INTO Store (Name, CNPJ, Email, Phone, Latitude, Longitude, Active, CreatedAt, CompanyId, AddressId) VALUES
('Loja Prisma Educação - Fortaleza', '90001001000143', 'loja001@prismaeducacao087.com.br', '85912604223', -27.942749, -60.938228, true, '2023-11-20 13:14:00', 87, 1),
('Loja Âncora Farmacêutica - São Paulo', '90001002000198', 'loja002@ancorafarmaceu028.com.br', '62967294667', -30.630382, -42.510653, true, '2023-12-09 18:26:00', 28, 2),
('Loja Alfa Moda - Campinas', '90001003000132', 'loja003@alfamoda014.com.br', '21968457759', -33.406023, -43.308724, true, '2025-05-14 18:41:00', 14, 3),
('Loja União Alimentos - Londrina', '90001004000187', 'loja004@uniaoalimentos047.com.br', '21975186363', -14.700832, -60.908259, true, '2022-12-14 09:52:00', 47, 4),
('Loja Ipê Alimentos - Campinas', '90001005000121', 'loja005@ipealimentos079.com.br', '61989297500', -29.71616, -42.928299, true, '2023-10-18 18:07:00', 79, 5),
('Loja Girassol Logística - Natal', '90001006000176', 'loja006@girassollogist003.com.br', '11912770632', 0.021615, -61.727245, true, '2023-07-20 09:22:00', 3, 6),
('Loja Cristal Tecnologia - Goiânia', '90001007000110', 'loja007@cristaltecnolo049.com.br', '51915415206', -9.727038, -45.186454, true, '2024-10-16 11:11:00', 49, 7),
('Loja Cristal Tecnologia - Fortaleza', '90001008000165', 'loja008@cristaltecnolo091.com.br', '41961103712', -10.940179, -53.241674, true, '2022-12-23 16:09:00', 91, 8),
('Loja Vale Logística - Campo Grande', '90001009000100', 'loja009@valelogistica035.com.br', '11905162713', -18.943343, -45.000228, true, '2023-04-07 16:47:00', 35, 9),
('Loja Coral Serviços - João Pessoa', '90001010000134', 'loja010@coralservicos098.com.br', '21955426221', -13.340685, -73.169485, true, '2024-10-05 12:28:00', 98, 10),
('Loja Órbita Educação - São Paulo', '90001011000189', 'loja011@orbitaeducacao077.com.br', '31975326973', -30.205855, -69.581861, true, '2025-02-11 09:54:00', 77, 11),
('Loja Coral Automotivo - Manaus', '90001012000123', 'loja012@coralautomotiv060.com.br', '21996565234', -9.004418, -63.821486, true, '2022-09-16 15:00:00', 60, 12),
('Loja Prisma Moda - São Paulo', '90001013000178', 'loja013@prismamoda008.com.br', '47994043898', -26.331432, -58.22171, true, '2022-09-11 12:35:00', 8, 13),
('Loja Órbita Construção - Campo Grande', '90001014000112', 'loja014@orbitaconstruc033.com.br', '11937613791', -13.425113, -35.369819, true, '2022-09-18 15:40:00', 33, 14),
('Loja Ipê Logística - Brasília', '90001015000167', 'loja015@ipelogistica066.com.br', '61926965827', 2.769213, -43.455685, true, '2024-03-14 18:48:00', 66, 15),
('Loja Sul Mix Farmacêutica - Salvador', '90001016000101', 'loja016@sulmixfarmaceu032.com.br', '47910430068', -22.613464, -45.707466, true, '2024-12-03 15:42:00', 32, 16),
('Loja Raiz Logística - Belo Horizonte', '90001017000156', 'loja017@raizlogistica099.com.br', '71966188924', -30.323311, -70.418159, true, '2025-03-24 13:14:00', 99, 17),
('Loja Cedro Móveis - Natal', '90001018000109', 'loja018@cedromoveis062.com.br', '41928569146', -5.761582, -40.487744, true, '2024-09-04 12:57:00', 62, 18),
('Loja Trilha Móveis - Curitiba', '90001019000145', 'loja019@trilhamoveis048.com.br', '41916179904', -8.188976, -47.835958, true, '2024-05-09 16:24:00', 48, 19),
('Loja Vetor Metalúrgica - Natal', '90001020000170', 'loja020@vetormetalurgi078.com.br', '21902027588', -3.74649, -45.390145, true, '2023-03-24 13:42:00', 78, 20),
('Loja Central Educação - Campinas', '90001021000114', 'loja021@centraleducaca030.com.br', '61924105648', -31.072864, -45.719393, true, '2025-02-17 11:43:00', 30, 21),
('Loja Zênite Farmacêutica - Brasília', '90001022000169', 'loja022@zenitefarmaceu081.com.br', '62917785079', -27.158507, -67.364911, true, '2024-08-29 12:44:00', 81, 22),
('Loja Âncora Metalúrgica - Campinas', '90001023000103', 'loja023@ancorametalurg071.com.br', '21991380003', -31.752702, -59.304917, true, '2025-02-17 15:39:00', 71, 23),
('Loja Bravo Metalúrgica - Recife', '90001024000158', 'loja024@bravometalurgi093.com.br', '31920863536', 1.302481, -61.02253, true, '2023-10-23 09:37:00', 93, 24),
('Loja Órbita Consultoria - Natal', '90001025000100', 'loja025@orbitaconsulto100.com.br', '21995180238', 3.793727, -36.290903, true, '2025-07-04 12:13:00', 100, 25),
('Loja Cume Papelaria - Londrina', '90001026000147', 'loja026@cumepapelaria036.com.br', '81944518705', -25.874496, -51.511537, true, '2023-01-04 10:39:00', 36, 26),
('Loja Alfa Tecnologia - Recife', '90001027000191', 'loja027@alfatecnologia005.com.br', '47937597383', -27.778478, -37.136496, true, '2024-08-24 16:46:00', 5, 27),
('Loja Cristal Serviços - Florianópolis', '90001028000136', 'loja028@cristalservico038.com.br', '41985050641', -25.702614, -35.158486, true, '2024-04-03 11:53:00', 38, 28),
('Loja Prisma Educação - Uberlândia', '90001029000180', 'loja029@prismaeducacao029.com.br', '81902410386', -6.867817, -58.972813, true, '2024-01-23 11:27:00', 29, 29),
('Loja Domo Automotivo - Uberlândia', '90001030000105', 'loja030@domoautomotivo004.com.br', '21991826199', -31.133251, -47.664687, true, '2024-07-16 09:02:00', 4, 30),
('Loja Girassol Educação - Recife', '90001031000150', 'loja031@girassoleducac011.com.br', '41944565574', -24.622773, -36.701367, true, '2022-12-14 16:10:00', 11, 31),
('Loja Girassol Educação - Campo Grande', '90001032000102', 'loja032@girassoleducac011.com.br', '47912127756', -28.912004, -59.52679, true, '2023-01-03 16:10:00', 11, 32),
('Loja Coral Móveis - Campo Grande', '90001033000149', 'loja033@coralmoveis095.com.br', '41974741124', -2.097306, -46.321759, true, '2022-08-28 08:42:00', 95, 33),
('Loja Coral Móveis - Natal', '90001034000193', 'loja034@coralmoveis095.com.br', '41952135680', -7.767032, -63.353495, true, '2022-09-01 08:42:00', 95, 34),
('Loja Cume Serviços - Belo Horizonte', '90001035000138', 'loja035@cumeservicos076.com.br', '11967883171', -2.951923, -68.048071, true, '2025-03-12 09:20:00', 76, 35),
('Loja Cume Serviços - Joinville', '90001036000182', 'loja036@cumeservicos076.com.br', '11932365199', -23.664815, -41.802863, true, '2025-03-12 09:20:00', 76, 36),
('Loja Órbita Educação - São Paulo', '90001037000127', 'loja037@orbitaeducacao057.com.br', '21937215107', -31.575767, -37.458863, true, '2024-07-28 17:32:00', 57, 37),
('Loja Órbita Educação - Curitiba', '90001038000171', 'loja038@orbitaeducacao057.com.br', '71970662532', -23.050693, -56.105554, true, '2024-07-19 17:32:00', 57, 38),
('Loja Central Papelaria - Florianópolis', '90001039000116', 'loja039@centralpapelar054.com.br', '11999934768', -15.296825, -70.07886, true, '2025-06-07 15:05:00', 54, 39),
('Loja Central Papelaria - Manaus', '90001040000140', 'loja040@centralpapelar054.com.br', '31963838065', -15.271991, -43.024031, true, '2025-06-06 15:05:00', 54, 40),
('Loja Cume Construção - Natal', '90001041000195', 'loja041@cumeconstrucao084.com.br', '47985642059', -25.177233, -38.565174, true, '2023-06-16 17:03:00', 84, 41),
('Loja Cume Construção - Londrina', '90001042000130', 'loja042@cumeconstrucao084.com.br', '51907859321', -24.081432, -64.095041, true, '2023-06-09 17:03:00', 84, 42),
('Loja Cristal Saúde - Londrina', '90001043000184', 'loja043@cristalsaude053.com.br', '11939652239', -21.415834, -44.713901, true, '2023-03-12 18:52:00', 53, 43),
('Loja Cristal Saúde - Vitória', '90001044000129', 'loja044@cristalsaude053.com.br', '47905992937', -12.899265, -66.653286, true, '2023-03-02 18:52:00', 53, 44),
('Loja Cume Comércio - Campo Grande', '90001045000173', 'loja045@cumecomercio064.com.br', '21903930870', -20.894108, -49.548744, true, '2024-10-15 09:28:00', 64, 45),
('Loja Cume Comércio - Salvador', '90001046000118', 'loja046@cumecomercio064.com.br', '31952500292', -4.074073, -40.487245, true, '2024-10-17 09:28:00', 64, 46),
('Loja Sul Mix Moda - Goiânia', '90001047000162', 'loja047@sulmixmoda065.com.br', '61915691545', -28.314837, -67.357348, true, '2023-12-26 12:01:00', 65, 47),
('Loja Sul Mix Moda - Caxias do Sul', '90001048000107', 'loja048@sulmixmoda065.com.br', '62975289539', -2.550739, -58.74302, true, '2024-01-05 12:01:00', 65, 48),
('Loja Boreal Moda - Joinville', '90001049000151', 'loja049@borealmoda096.com.br', '51922096902', -10.609231, -37.57951, true, '2023-03-03 17:18:00', 96, 49),
('Loja Boreal Moda - Goiânia', '90001050000186', 'loja050@borealmoda096.com.br', '41991725135', 3.830427, -66.952391, true, '2023-03-12 17:18:00', 96, 50),
('Loja Rota Automotivo - João Pessoa', '90001051000120', 'loja051@rotaautomotivo050.com.br', '21957043315', -23.746998, -69.286651, true, '2024-04-18 12:50:00', 50, 51),
('Loja Rota Automotivo - Belo Horizonte', '90001052000175', 'loja052@rotaautomotivo050.com.br', '11967626765', -12.751089, -69.544286, true, '2024-05-03 12:50:00', 50, 52),
('Loja Nortline Automotivo - Uberlândia', '90001053000110', 'loja053@nortlineautomo025.com.br', '47932710405', -23.577147, -69.415723, true, '2023-12-03 11:14:00', 25, 53),
('Loja Nortline Automotivo - Manaus', '90001054000164', 'loja054@nortlineautomo025.com.br', '31962181599', 0.442595, -46.024319, true, '2023-12-06 11:14:00', 25, 54),
('Loja Raiz Consultoria - Joinville', '90001055000109', 'loja055@raizconsultori092.com.br', '62929620153', 3.52714, -57.538085, true, '2023-07-25 12:24:00', 92, 55),
('Loja Raiz Consultoria - João Pessoa', '90001056000153', 'loja056@raizconsultori092.com.br', '85944431069', -12.61282, -54.353017, true, '2023-07-09 12:24:00', 92, 56),
('Loja Bússola Alimentos - Uberlândia', '90001057000106', 'loja057@bussolaaliment094.com.br', '61906149731', -32.522044, -66.837545, true, '2023-04-04 16:23:00', 94, 57),
('Loja Bússola Alimentos - Goiânia', '90001058000142', 'loja058@bussolaaliment094.com.br', '21971467741', -9.109077, -58.386442, true, '2023-04-06 16:23:00', 94, 58),
('Loja Raiz Consultoria - Curitiba', '90001059000197', 'loja059@raizconsultori002.com.br', '71952704774', -24.902793, -63.026082, true, '2024-06-29 18:44:00', 2, 59),
('Loja Raiz Consultoria - Belo Horizonte', '90001060000111', 'loja060@raizconsultori002.com.br', '31911586938', -31.182618, -58.402862, true, '2024-07-14 18:44:00', 2, 60),
('Loja Estrela Construção - São Paulo', '90001061000166', 'loja061@estrelaconstru046.com.br', '71987937416', 0.805534, -45.642078, true, '2024-12-09 12:02:00', 46, 61),
('Loja Estrela Construção - Manaus', '90001062000100', 'loja062@estrelaconstru046.com.br', '47989921722', 3.442265, -41.970032, true, '2024-11-27 12:02:00', 46, 62),
('Loja Âncora Consultoria - Campo Grande', '90001063000155', 'loja063@ancoraconsulto007.com.br', '11961632945', 1.11057, -37.847216, true, '2023-05-17 13:14:00', 7, 63),
('Loja Âncora Consultoria - João Pessoa', '90001064000108', 'loja064@ancoraconsulto007.com.br', '21968685487', 1.352153, -73.017812, true, '2023-06-06 13:14:00', 7, 64),
('Loja Nortline Alimentos - Florianópolis', '90001065000144', 'loja065@nortlinealimen080.com.br', '47998616229', -11.158503, -43.923282, true, '2025-01-03 13:54:00', 80, 65),
('Loja Nortline Alimentos - Joinville', '90001066000199', 'loja066@nortlinealimen080.com.br', '21991485646', -8.618069, -51.191094, true, '2025-01-15 13:54:00', 80, 66),
('Loja Bússola Consultoria - Belo Horizonte', '90001067000133', 'loja067@bussolaconsult058.com.br', '11924629960', -18.459876, -39.220652, true, '2024-10-31 15:34:00', 58, 67),
('Loja Bússola Consultoria - Campinas', '90001068000188', 'loja068@bussolaconsult058.com.br', '41907450544', -22.846827, -54.326851, true, '2024-10-31 15:34:00', 58, 68),
('Loja Rota Alimentos - Curitiba', '90001069000122', 'loja069@rotaalimentos041.com.br', '62913247546', -10.510793, -70.017813, true, '2022-09-20 18:17:00', 41, 69),
('Loja Rota Alimentos - Porto Alegre', '90001070000157', 'loja070@rotaalimentos041.com.br', '41967850821', -21.677322, -53.734984, true, '2022-09-10 18:17:00', 41, 70),
('Loja Âncora Alimentos - Campo Grande', '90001071000100', 'loja071@ancoraalimento024.com.br', '85990233030', -16.154951, -37.696788, true, '2024-09-03 14:01:00', 24, 71),
('Loja Âncora Alimentos - Natal', '90001072000146', 'loja072@ancoraalimento024.com.br', '41945786103', -19.346263, -50.3323, true, '2024-08-17 14:01:00', 24, 72),
('Loja Âncora Alimentos - Curitiba', '90001073000190', 'loja073@ancoraalimento024.com.br', '81998552414', -32.456881, -71.563609, true, '2024-09-08 14:01:00', 24, 73),
('Loja Cume Automotivo - Londrina', '90001074000135', 'loja074@cumeautomotivo018.com.br', '47933334680', -0.644063, -35.781604, true, '2024-12-28 17:15:00', 18, 74),
('Loja Cume Automotivo - Joinville', '90001075000180', 'loja075@cumeautomotivo018.com.br', '11972290349', -2.906748, -61.587072, true, '2024-12-29 17:15:00', 18, 75),
('Loja Cume Automotivo - Curitiba', '90001076000124', 'loja076@cumeautomotivo018.com.br', '62946657437', -22.018487, -53.195122, true, '2024-12-26 17:15:00', 18, 76),
('Loja Bravo Moda - Curitiba', '90001077000179', 'loja077@bravomoda037.com.br', '71991085899', -22.758279, -46.807888, true, '2023-02-05 13:21:00', 37, 77),
('Loja Bravo Moda - Recife', '90001078000113', 'loja078@bravomoda037.com.br', '21954544432', -13.842844, -64.128219, true, '2023-01-22 13:21:00', 37, 78),
('Loja Bravo Moda - Curitiba', '90001079000168', 'loja079@bravomoda037.com.br', '47965207446', -18.125242, -44.767448, true, '2023-02-08 13:21:00', 37, 79),
('Loja Marca Papelaria - Recife', '90001080000192', 'loja080@marcapapelaria063.com.br', '47935387584', -1.374069, -40.715091, true, '2024-09-13 09:39:00', 63, 80),
('Loja Marca Papelaria - Joinville', '90001081000137', 'loja081@marcapapelaria063.com.br', '85924172482', 2.483088, -38.415945, true, '2024-09-05 09:39:00', 63, 81),
('Loja Marca Papelaria - Florianópolis', '90001082000181', 'loja082@marcapapelaria063.com.br', '21935346554', -11.248314, -55.413336, true, '2024-08-19 09:39:00', 63, 82),
('Loja Boreal Comércio - Florianópolis', '90001083000126', 'loja083@borealcomercio022.com.br', '81909991738', -28.946994, -46.952787, true, '2024-03-07 10:47:00', 22, 83),
('Loja Boreal Comércio - Vitória', '90001084000170', 'loja084@borealcomercio022.com.br', '51960223569', 0.428115, -35.020679, true, '2024-02-24 10:47:00', 22, 84),
('Loja Boreal Comércio - Porto Alegre', '90001085000115', 'loja085@borealcomercio022.com.br', '61921752500', -16.321977, -65.334088, true, '2024-03-15 10:47:00', 22, 85),
('Loja Trilha Farmacêutica - Campo Grande', '90001086000160', 'loja086@trilhafarmaceu072.com.br', '31992712966', -18.171048, -35.852214, true, '2023-05-29 12:12:00', 72, 86),
('Loja Trilha Farmacêutica - Goiânia', '90001087000104', 'loja087@trilhafarmaceu072.com.br', '11908350583', -32.819187, -73.33917, true, '2023-06-09 12:12:00', 72, 87),
('Loja Trilha Farmacêutica - Campinas', '90001088000159', 'loja088@trilhafarmaceu072.com.br', '41935421689', -21.867627, -70.953806, true, '2023-05-18 12:12:00', 72, 88),
('Loja Trilha Alimentos - Londrina', '90001089000101', 'loja089@trilhaalimento023.com.br', '51947898562', -20.476152, -38.346033, true, '2025-05-27 12:23:00', 23, 89),
('Loja Trilha Alimentos - Florianópolis', '90001090000128', 'loja090@trilhaalimento023.com.br', '41926033230', -11.091672, -66.909595, true, '2025-05-27 12:23:00', 23, 90),
('Loja Trilha Alimentos - Londrina', '90001091000172', 'loja091@trilhaalimento023.com.br', '62906489090', -8.345143, -48.010863, true, '2025-05-16 12:23:00', 23, 91),
('Loja Cume Serviços - Belo Horizonte', '90001092000117', 'loja092@cumeservicos083.com.br', '11916761847', -6.829008, -65.485698, true, '2023-04-15 15:38:00', 83, 92),
('Loja Cume Serviços - Londrina', '90001093000161', 'loja093@cumeservicos083.com.br', '62904618499', -6.872399, -57.165796, true, '2023-04-22 15:38:00', 83, 93),
('Loja Cume Serviços - Natal', '90001094000106', 'loja094@cumeservicos083.com.br', '21989141173', -10.357931, -43.159602, true, '2023-04-20 15:38:00', 83, 94),
('Loja Coral Papelaria - Caxias do Sul', '90001095000150', 'loja095@coralpapelaria089.com.br', '85935642087', 1.093552, -46.660047, true, '2023-03-08 09:30:00', 89, 95),
('Loja Coral Papelaria - Florianópolis', '90001096000103', 'loja096@coralpapelaria089.com.br', '61930011972', 2.578949, -43.098477, true, '2023-02-23 09:30:00', 89, 96),
('Loja Coral Papelaria - Goiânia', '90001097000140', 'loja097@coralpapelaria089.com.br', '21943744701', -2.59922, -39.408522, true, '2023-03-20 09:30:00', 89, 97),
('Loja Órbita Logística - Campo Grande', '90001098000194', 'loja098@orbitalogistic069.com.br', '51955611002', -8.292509, -66.850021, true, '2023-07-02 18:23:00', 69, 98),
('Loja Órbita Logística - Recife', '90001099000139', 'loja099@orbitalogistic069.com.br', '81994155216', -18.290491, -55.694991, true, '2023-07-17 18:23:00', 69, 99),
('Loja Órbita Logística - Porto Alegre', '90001100000125', 'loja100@orbitalogistic069.com.br', '71976212021', -17.690947, -50.460738, true, '2023-07-16 18:23:00', 69, 100);

-- =========================================================
-- 5) EMPLOYEEROLE
-- =========================================================
INSERT INTO EmployeeRole (Name, Description, PermissionLevel, Active, CreatedAt) VALUES
('Gerente de Loja', 'Função de gerente de loja responsável pelas atividades operacionais da loja.', 5, true, '2022-09-01 18:28:00'),
('Analista Administrativo', 'Função de analista administrativo responsável pelas atividades operacionais da loja.', 3, true, '2022-09-01 18:28:00'),
('Caixa', 'Função de caixa responsável pelas atividades operacionais da loja.', 1, true, '2022-09-01 18:28:00'),
('Assistente de Vendas', 'Função de assistente de vendas responsável pelas atividades operacionais da loja.', 2, true, '2022-09-01 18:28:00'),
('Supervisor Operacional', 'Função de supervisor operacional responsável pelas atividades operacionais da loja.', 4, true, '2022-09-01 18:28:00'),
('Estoquista', 'Função de estoquista responsável pelas atividades operacionais da loja.', 1, true, '2022-09-01 18:28:00');

-- =========================================================
-- 6) EMPLOYEE (100 registros)
-- =========================================================
INSERT INTO Employee (Name, CPF, Email, Phone, Active, HireDate, CreatedAt, StoreId, RoleId) VALUES
('Caio Correia Martins', '90000000175', 'caio.correia001@empresa-teste.example', '11927716701', true, '2023-03-10', '2023-03-10 09:00:00', 24, 5),
('William Silva Marques', '90000000256', 'william.silva002@empresa-teste.example', '85999358891', true, '2022-10-25', '2022-10-25 09:00:00', 34, 6),
('Patrícia Ribeiro Martins', '90000000337', 'patricia.ribeiro003@empresa-teste.example', '62962890028', true, '2022-11-23', '2022-11-23 09:00:00', 56, 6),
('Karina Oliveira Correia', '90000000418', 'karina.oliveira004@empresa-teste.example', '61935408386', true, '2023-11-11', '2023-11-11 09:00:00', 61, 4),
('Felipe Barbosa Barbosa', '90000000507', 'felipe.barbosa005@empresa-teste.example', '47971775170', true, '2022-02-15', '2022-02-15 09:00:00', 52, 2),
('Enzo Souza Teixeira', '90000000680', 'enzo.souza006@empresa-teste.example', '11919948837', true, '2023-07-16', '2023-07-16 09:00:00', 14, 4),
('Daniel Castro Ferreira', '90000000760', 'daniel.castro007@empresa-teste.example', '81958997571', true, '2021-11-18', '2021-11-18 09:00:00', 44, 3),
('Otávio Silva Gomes', '90000000841', 'otavio.silva008@empresa-teste.example', '62948851027', true, '2024-08-16', '2024-08-16 09:00:00', 13, 6),
('Enzo Santos Rodrigues', '90000000922', 'enzo.santos009@empresa-teste.example', '41931637351', true, '2023-03-11', '2023-03-11 09:00:00', 18, 6),
('Carla Pinto Fernandes', '90000001066', 'carla.pinto010@empresa-teste.example', '62991779827', true, '2021-12-02', '2021-12-02 09:00:00', 97, 4),
('Daniel Teixeira Pinto', '90000001147', 'daniel.teixeira011@empresa-teste.example', '47978292216', true, '2024-03-10', '2024-03-10 09:00:00', 1, 5),
('Ricardo Cavalcanti Gomes', '90000001228', 'ricardo.cavalcanti012@empresa-teste.example', '71969346547', true, '2023-10-16', '2023-10-16 09:00:00', 85, 5),
('Enzo Nascimento Marques', '90000001309', 'enzo.nascimento013@empresa-teste.example', '85948583339', true, '2022-06-03', '2022-06-03 09:00:00', 18, 2),
('Ximena Martins Pinto', '90000001490', 'ximena.martins014@empresa-teste.example', '31928851587', true, '2022-11-05', '2022-11-05 09:00:00', 92, 3),
('Wagner Marques Melo', '90000001570', 'wagner.marques015@empresa-teste.example', '85946081062', true, '2022-11-02', '2022-11-02 09:00:00', 75, 5),
('Daniel Costa Rodrigues', '90000001651', 'daniel.costa016@empresa-teste.example', '31944204380', true, '2022-05-30', '2022-05-30 09:00:00', 7, 6),
('Gabriela Nascimento Campos', '90000001732', 'gabriela.nascimento017@empresa-teste.example', '85938638406', true, '2022-10-30', '2022-10-30 09:00:00', 46, 4),
('Ximena Gomes Teixeira', '90000001813', 'ximena.gomes018@empresa-teste.example', '11943973598', true, '2026-04-25', '2026-04-25 09:00:00', 50, 4),
('Mariana Correia Machado', '90000001902', 'mariana.correia019@empresa-teste.example', '51972122816', true, '2025-07-13', '2025-07-13 09:00:00', 95, 5),
('Sabrina Teixeira Rodrigues', '90000002038', 'sabrina.teixeira020@empresa-teste.example', '31994057202', true, '2025-11-20', '2025-11-20 09:00:00', 6, 4),
('Daniel Correia Pinto', '90000002119', 'daniel.correia021@empresa-teste.example', '47947985158', true, '2023-02-10', '2023-02-10 09:00:00', 42, 5),
('Larissa Oliveira Dias', '90000002208', 'larissa.oliveira022@empresa-teste.example', '31968861533', true, '2023-10-14', '2023-10-14 09:00:00', 29, 2),
('Yasmin Martins Carvalho', '90000002380', 'yasmin.martins023@empresa-teste.example', '47987801573', true, '2025-05-23', '2025-05-23 09:00:00', 85, 3),
('Sandra Ferreira Souza', '90000002461', 'sandra.ferreira024@empresa-teste.example', '61961648408', true, '2022-10-16', '2022-10-16 09:00:00', 83, 5),
('Kevin Dias Cardoso', '90000002542', 'kevin.dias025@empresa-teste.example', '81904358112', true, '2024-02-13', '2024-02-13 09:00:00', 99, 3),
('Marcelo Silva Fernandes', '90000002623', 'marcelo.silva026@empresa-teste.example', '41936138612', true, '2026-04-01', '2026-04-01 09:00:00', 99, 5),
('Yasmin Souza Oliveira', '90000002704', 'yasmin.souza027@empresa-teste.example', '11924819346', true, '2026-03-16', '2026-03-16 09:00:00', 4, 4),
('Thiago Araújo Melo', '90000002895', 'thiago.araujo028@empresa-teste.example', '11961809190', true, '2024-11-04', '2024-11-04 09:00:00', 50, 4),
('André Pinto Cardoso', '90000002976', 'andre.pinto029@empresa-teste.example', '51949147660', true, '2026-01-08', '2026-01-08 09:00:00', 90, 1),
('Marcelo Ferreira Pereira', '90000003000', 'marcelo.ferreira030@empresa-teste.example', '71984245088', true, '2022-06-06', '2022-06-06 09:00:00', 61, 2),
('Carla Silva Moreira', '90000003190', 'carla.silva031@empresa-teste.example', '71988143536', true, '2023-04-10', '2023-04-10 09:00:00', 19, 1),
('João Almeida Almeida', '90000003271', 'joao.almeida032@empresa-teste.example', '47943129087', true, '2025-05-18', '2025-05-18 09:00:00', 83, 5),
('Eduarda Carvalho Lopes', '90000003352', 'eduarda.carvalho033@empresa-teste.example', '51913975523', true, '2026-03-29', '2026-03-29 09:00:00', 83, 5),
('Sandra Oliveira Cavalcanti', '90000003433', 'sandra.oliveira034@empresa-teste.example', '61977594929', true, '2025-07-13', '2025-07-13 09:00:00', 13, 3),
('William Melo Almeida', '90000003514', 'william.melo035@empresa-teste.example', '61933645257', true, '2026-08-04', '2026-08-04 09:00:00', 73, 3),
('André Correia Carvalho', '90000003603', 'andre.correia036@empresa-teste.example', '85912914283', true, '2023-11-20', '2023-11-20 09:00:00', 60, 4),
('Thiago Oliveira Machado', '90000003786', 'thiago.oliveira037@empresa-teste.example', '11980996930', true, '2026-03-12', '2026-03-12 09:00:00', 34, 6),
('Marcelo Cavalcanti Teixeira', '90000003867', 'marcelo.cavalcanti038@empresa-teste.example', '62925639427', true, '2023-12-21', '2023-12-21 09:00:00', 71, 3),
('Felipe Araújo Martins', '90000003948', 'felipe.araujo039@empresa-teste.example', '11939282722', true, '2023-10-20', '2023-10-20 09:00:00', 65, 5),
('Wagner Campos Pereira', '90000004081', 'wagner.campos040@empresa-teste.example', '81950373145', true, '2022-06-28', '2022-06-28 09:00:00', 73, 5),
('Wagner Costa Cardoso', '90000004162', 'wagner.costa041@empresa-teste.example', '61967700518', true, '2022-03-07', '2022-03-07 09:00:00', 30, 1),
('Rafael Moreira Teixeira', '90000004243', 'rafael.moreira042@empresa-teste.example', '31966591846', true, '2023-06-03', '2023-06-03 09:00:00', 82, 3),
('Débora Martins Castro', '90000004324', 'debora.martins043@empresa-teste.example', '81979082723', true, '2024-09-06', '2024-09-06 09:00:00', 29, 5),
('Valentina Rodrigues Costa', '90000004405', 'valentina.rodrigues044@empresa-teste.example', '31925913651', true, '2025-10-10', '2025-10-10 09:00:00', 10, 5),
('Igor Ribeiro Costa', '90000004596', 'igor.ribeiro045@empresa-teste.example', '31960302992', true, '2023-04-22', '2023-04-22 09:00:00', 62, 6),
('Kevin Souza Cavalcanti', '90000004677', 'kevin.souza046@empresa-teste.example', '31902428699', true, '2023-05-29', '2023-05-29 09:00:00', 18, 6),
('Wagner Castro Carvalho', '90000004758', 'wagner.castro047@empresa-teste.example', '41979772369', true, '2026-02-19', '2026-02-19 09:00:00', 82, 1),
('Lucas Nascimento Lopes', '90000004839', 'lucas.nascimento048@empresa-teste.example', '51925799136', true, '2023-10-02', '2023-10-02 09:00:00', 19, 5),
('Gabriela Silva Costa', '90000004910', 'gabriela.silva049@empresa-teste.example', '85962912799', true, '2023-09-08', '2023-09-08 09:00:00', 50, 2),
('Kevin Correia Santos', '90000005053', 'kevin.correia050@empresa-teste.example', '81929806865', true, '2026-02-04', '2026-02-04 09:00:00', 5, 5),
('Larissa Melo Ferreira', '90000005134', 'larissa.melo051@empresa-teste.example', '81965991624', true, '2022-02-12', '2022-02-12 09:00:00', 6, 6),
('Felipe Carvalho Nascimento', '90000005215', 'felipe.carvalho052@empresa-teste.example', '71943009818', true, '2023-07-27', '2023-07-27 09:00:00', 62, 2),
('Valentina Costa Marques', '90000005304', 'valentina.costa053@empresa-teste.example', '61965126944', true, '2022-02-08', '2022-02-08 09:00:00', 1, 4),
('Osvaldo Martins Nascimento', '90000005487', 'osvaldo.martins054@empresa-teste.example', '62963688725', true, '2025-01-26', '2025-01-26 09:00:00', 69, 4),
('Igor Rodrigues Carvalho', '90000005568', 'igor.rodrigues055@empresa-teste.example', '61962137213', true, '2023-05-18', '2023-05-18 09:00:00', 72, 5),
('Yasmin Dias Teixeira', '90000005649', 'yasmin.dias056@empresa-teste.example', '62902601279', true, '2023-03-05', '2023-03-05 09:00:00', 66, 4),
('Eduarda Rodrigues Ferreira', '90000005720', 'eduarda.rodrigues057@empresa-teste.example', '21990669849', true, '2024-07-15', '2024-07-15 09:00:00', 56, 1),
('Thiago Ribeiro Barbosa', '90000005800', 'thiago.ribeiro058@empresa-teste.example', '41984309883', true, '2026-07-23', '2026-07-23 09:00:00', 8, 2),
('Ximena Souza Martins', '90000005991', 'ximena.souza059@empresa-teste.example', '62902146566', true, '2025-04-11', '2025-04-11 09:00:00', 79, 2),
('Larissa Silva Machado', '90000006025', 'larissa.silva060@empresa-teste.example', '21918489554', true, '2022-09-02', '2022-09-02 09:00:00', 76, 2),
('Mariana Barbosa Pinto', '90000006106', 'mariana.barbosa061@empresa-teste.example', '47904268181', true, '2024-05-08', '2024-05-08 09:00:00', 29, 5),
('Yasmin Santos Dias', '90000006297', 'yasmin.santos062@empresa-teste.example', '51989476973', true, '2023-09-27', '2023-09-27 09:00:00', 65, 3),
('Helena Lopes Barbosa', '90000006378', 'helena.lopes063@empresa-teste.example', '11971416335', true, '2023-04-30', '2023-04-30 09:00:00', 97, 3),
('Larissa Teixeira Silva', '90000006459', 'larissa.teixeira064@empresa-teste.example', '51934165182', true, '2022-09-08', '2022-09-08 09:00:00', 53, 3),
('André Costa Cavalcanti', '90000006530', 'andre.costa065@empresa-teste.example', '51923733343', true, '2023-05-27', '2023-05-27 09:00:00', 61, 3),
('Patrícia Castro Nascimento', '90000006610', 'patricia.castro066@empresa-teste.example', '31911179080', true, '2024-08-14', '2024-08-14 09:00:00', 30, 2),
('Sandra Gomes Ribeiro', '90000006700', 'sandra.gomes067@empresa-teste.example', '51972846392', true, '2023-05-25', '2023-05-25 09:00:00', 62, 3),
('Fernanda Ribeiro Teixeira', '90000006882', 'fernanda.ribeiro068@empresa-teste.example', '85956271875', true, '2026-06-08', '2026-06-08 09:00:00', 7, 5),
('Ana Lopes Costa', '90000006963', 'ana.lopes069@empresa-teste.example', '47915594284', true, '2024-06-27', '2024-06-27 09:00:00', 89, 1),
('Igor Nascimento Gomes', '90000007005', 'igor.nascimento070@empresa-teste.example', '31976365564', true, '2024-07-25', '2024-07-25 09:00:00', 31, 3),
('Osvaldo Pereira Moreira', '90000007188', 'osvaldo.pereira071@empresa-teste.example', '41914386632', true, '2022-10-09', '2022-10-09 09:00:00', 58, 2),
('Igor Dias Silva', '90000007269', 'igor.dias072@empresa-teste.example', '71972885744', true, '2025-08-17', '2025-08-17 09:00:00', 95, 2),
('Daniel Araújo Santos', '90000007340', 'daniel.araujo073@empresa-teste.example', '31903471754', true, '2022-09-29', '2022-09-29 09:00:00', 72, 5),
('Natália Teixeira Pinto', '90000007420', 'natalia.teixeira074@empresa-teste.example', '51950615939', true, '2022-01-28', '2022-01-28 09:00:00', 47, 3),
('Valentina Teixeira Almeida', '90000007501', 'valentina.teixeira075@empresa-teste.example', '51955877169', true, '2022-03-29', '2022-03-29 09:00:00', 74, 6),
('Thiago Pereira Campos', '90000007692', 'thiago.pereira076@empresa-teste.example', '62956996112', true, '2025-09-04', '2025-09-04 09:00:00', 37, 4),
('William Gomes Fernandes', '90000007773', 'william.gomes077@empresa-teste.example', '51996475987', true, '2024-12-25', '2024-12-25 09:00:00', 44, 2),
('Lucas Campos Silva', '90000007854', 'lucas.campos078@empresa-teste.example', '41941326111', true, '2025-11-30', '2025-11-30 09:00:00', 53, 2),
('Daniel Silva Correia', '90000007935', 'daniel.silva079@empresa-teste.example', '85911492164', true, '2024-05-22', '2024-05-22 09:00:00', 61, 5),
('Tiago Barbosa Fernandes', '90000008079', 'tiago.barbosa080@empresa-teste.example', '31982005577', true, '2025-11-28', '2025-11-28 09:00:00', 30, 2),
('Natália Barbosa Pereira', '90000008150', 'natalia.barbosa081@empresa-teste.example', '85929979688', true, '2022-09-15', '2022-09-15 09:00:00', 70, 3),
('Igor Teixeira Ferreira', '90000008230', 'igor.teixeira082@empresa-teste.example', '21974564240', true, '2022-02-07', '2022-02-07 09:00:00', 40, 3),
('Isabela Martins Carvalho', '90000008311', 'isabela.martins083@empresa-teste.example', '85907740649', true, '2023-12-27', '2023-12-27 09:00:00', 1, 5),
('Natália Martins Costa', '90000008400', 'natalia.martins084@empresa-teste.example', '85966326634', true, '2024-04-22', '2024-04-22 09:00:00', 30, 1),
('Thiago Ferreira Cardoso', '90000008583', 'thiago.ferreira085@empresa-teste.example', '47944846312', true, '2022-05-01', '2022-05-01 09:00:00', 11, 4),
('Hugo Fernandes Barbosa', '90000008664', 'hugo.fernandes086@empresa-teste.example', '81949735020', true, '2023-02-25', '2023-02-25 09:00:00', 18, 3),
('Lucas Santos Carvalho', '90000008745', 'lucas.santos087@empresa-teste.example', '31970312073', true, '2025-02-08', '2025-02-08 09:00:00', 69, 1),
('Vanessa Dias Dias', '90000008826', 'vanessa.dias088@empresa-teste.example', '61970512046', true, '2023-10-28', '2023-10-28 09:00:00', 44, 4),
('Igor Almeida Costa', '90000008907', 'igor.almeida089@empresa-teste.example', '51960136730', true, '2021-11-24', '2021-11-24 09:00:00', 10, 5),
('Gabriela Cardoso Lopes', '90000009040', 'gabriela.cardoso090@empresa-teste.example', '81964520881', true, '2025-06-13', '2025-06-13 09:00:00', 26, 4),
('Thiago Carvalho Costa', '90000009121', 'thiago.carvalho091@empresa-teste.example', '11931365024', true, '2024-08-05', '2024-08-05 09:00:00', 33, 4),
('Débora Dias Ferreira', '90000009202', 'debora.dias092@empresa-teste.example', '11997925132', true, '2026-07-26', '2026-07-26 09:00:00', 75, 1),
('Ximena Nascimento Gomes', '90000009393', 'ximena.nascimento093@empresa-teste.example', '62900528127', true, '2022-05-11', '2022-05-11 09:00:00', 64, 1),
('Natália Dias Fernandes', '90000009474', 'natalia.dias094@empresa-teste.example', '61973065240', true, '2022-05-17', '2022-05-17 09:00:00', 30, 3),
('Gustavo Teixeira Carvalho', '90000009555', 'gustavo.teixeira095@empresa-teste.example', '81939285574', true, '2023-07-29', '2023-07-29 09:00:00', 63, 6),
('Karina Oliveira Cardoso', '90000009636', 'karina.oliveira096@empresa-teste.example', '81944857086', true, '2025-09-30', '2025-09-30 09:00:00', 77, 5),
('Bruno Nascimento Campos', '90000009717', 'bruno.nascimento097@empresa-teste.example', '47935186387', true, '2024-07-29', '2024-07-29 09:00:00', 94, 2),
('João Nascimento Carvalho', '90000009806', 'joao.nascimento098@empresa-teste.example', '71951181022', true, '2024-10-22', '2024-10-22 09:00:00', 99, 5),
('Gabriela Ribeiro Costa', '90000009989', 'gabriela.ribeiro099@empresa-teste.example', '11996789940', true, '2026-01-23', '2026-01-23 09:00:00', 5, 2),
('Ricardo Marques Oliveira', '90000010057', 'ricardo.marques100@empresa-teste.example', '41971705865', true, '2026-07-18', '2026-07-18 09:00:00', 47, 2);

-- =========================================================
-- 7) USERACCOUNT (100 registros, 1:1 com Employee)
-- =========================================================
INSERT INTO UserAccount (Email, PasswordHash, LastLogin, Active, CreatedAt, EmployeeId) VALUES
('caio.correia001@empresa-teste.example', '$2b$12$XdMGTZenGgoxlOJ6IIUVJPRAgXIFOHL53I90zNZuoHxIUwC7acrbi', '2023-05-26 10:00:00', true, '2023-03-10 10:00:00', 1),
('william.silva002@empresa-teste.example', '$2b$12$sn1bYQrII5jcwiwKoYfy4TSXkZQJ4KsTJedVvYz11oOLaWxscHjFn', '2022-11-07 07:00:00', true, '2022-10-25 10:00:00', 2),
('patricia.ribeiro003@empresa-teste.example', '$2b$12$TyBKFnryifJdyfFViiIEOmMTpeWu5fPAvtKbs9M9BjcYmxU0gd651', '2023-02-04 10:00:00', true, '2022-11-23 10:00:00', 3),
('karina.oliveira004@empresa-teste.example', '$2b$12$vXsDuTnHUIM9RPx4GpdmRqC6eOP4wgs2x8FS4ELfm7ISc5sEUbQws', '2024-03-18 18:00:00', true, '2023-11-11 10:00:00', 4),
('felipe.barbosa005@empresa-teste.example', '$2b$12$YM23YbIrj3Ofx7LMooj84uHmiHX1tRGi98i54tI6ht2TV2zzB535q', '2022-09-02 16:00:00', true, '2022-02-15 10:00:00', 5),
('enzo.souza006@empresa-teste.example', '$2b$12$glVLqJooQwQDFLSx0EilbVaqzU9Ue591EuCpzcTxtLUKG2WGBZc9G', '2023-10-26 09:00:00', true, '2023-07-16 10:00:00', 6),
('daniel.castro007@empresa-teste.example', '$2b$12$cyV7lB2Vrks4Q4COfFlK3HyubC332fuOSkTfRNfRT8LjIcku21kiV', '2021-12-01 23:00:00', true, '2021-11-18 10:00:00', 7),
('otavio.silva008@empresa-teste.example', '$2b$12$tT7rl37aVaEEYLEnOCBLa2rMYmldvjcJLufg9krORJI7UwcVu3YV0', '2024-09-01 19:00:00', true, '2024-08-16 10:00:00', 8),
('enzo.santos009@empresa-teste.example', '$2b$12$Dh1uznmdJ8GfSLWRA0qJM5METEY2osmhCXTmMzcvXnNQpesIVhuyJ', '2023-03-18 15:00:00', true, '2023-03-11 10:00:00', 9),
('carla.pinto010@empresa-teste.example', '$2b$12$mZ5DDcijC13CY5dGJQIbJu4Xej0vxHkLlhWt1Sqf2xZsGkxcmfFX4', '2022-01-10 16:00:00', true, '2021-12-02 10:00:00', 10),
('daniel.teixeira011@empresa-teste.example', '$2b$12$vD2tuVg8G9mcy1htc02u3gVUbVNnmrHVPqjh7IgHIq6XJkt3Hdb4d', '2024-06-16 11:00:00', true, '2024-03-10 10:00:00', 11),
('ricardo.cavalcanti012@empresa-teste.example', '$2b$12$fyR3NQBwumVYe5VJfXRgtNVD6i3pwl4u1uEegD0rvJFzSgIesCKop', '2023-11-26 01:00:00', true, '2023-10-16 10:00:00', 12),
('enzo.nascimento013@empresa-teste.example', '$2b$12$xvYKWAWHoPe5JEKCIYk7egYVxEOfY34NajjGyJmQtJ4s6A8vfa9zh', '2022-08-17 11:00:00', true, '2022-06-03 10:00:00', 13),
('ximena.martins014@empresa-teste.example', '$2b$12$wIdRYw11oRytxxHPDlx50j9PieaSEB9fNzCWLAlFIBwlLJ0oFzHw8', '2023-03-31 01:00:00', true, '2022-11-05 10:00:00', 14),
('wagner.marques015@empresa-teste.example', '$2b$12$RhDQiT88WCe3fDT7eNXtdvWDFDTA7Xrc5UTIdDkd9YMrfF0rir1ag', '2022-12-17 04:00:00', true, '2022-11-02 10:00:00', 15),
('daniel.costa016@empresa-teste.example', '$2b$12$TOwkWolGc97KYZUzxOTkJ0dqYdCDD65oQyqGArXxtG7gGv0LOqqXp', '2022-10-26 22:00:00', true, '2022-05-30 10:00:00', 16),
('gabriela.nascimento017@empresa-teste.example', '$2b$12$CPoPVGQ9taaezGQvSpcMdK3nLh7fLxVOXlglsOeC9VQPVpWrdy9qY', '2022-12-15 00:00:00', true, '2022-10-30 10:00:00', 17),
('ximena.gomes018@empresa-teste.example', '$2b$12$e8myOgHAMgBqceTWbEOacyjuyaRVDzTYeoxQWhH9usd9Ow0v0VrmI', '2026-08-02 07:00:00', true, '2026-04-25 10:00:00', 18),
('mariana.correia019@empresa-teste.example', '$2b$12$lR3NwfNTilSKEdHtHZB9cdWchYPCQ2jJ5ciOD4cRikdtXCkKYgEXn', '2025-09-02 23:00:00', true, '2025-07-13 10:00:00', 19),
('sabrina.teixeira020@empresa-teste.example', '$2b$12$uZTyhQHt7nckdqUkwkpKiXNxj2fa5RKrQXDhiqBokQKSLfZszIl98', '2026-04-27 21:00:00', true, '2025-11-20 10:00:00', 20),
('daniel.correia021@empresa-teste.example', '$2b$12$19FCMmdgj04Ej5dwmpM3CgdHjbFeoBJzU3ogoozm3eKdBnLlxCmwq', '2023-05-06 14:00:00', true, '2023-02-10 10:00:00', 21),
('larissa.oliveira022@empresa-teste.example', '$2b$12$bJrgCzVnsjX74JIozG1nDCaA2pAn5643zc9TFAyYRgEfUQznwo9aP', '2023-11-25 08:00:00', true, '2023-10-14 10:00:00', 22),
('yasmin.martins023@empresa-teste.example', '$2b$12$CJK8CiRl2MRaMxQGsURLJmQMu5F4wdxCcwORIViuHOTU2RUVt3FAd', '2025-08-26 21:00:00', true, '2025-05-23 10:00:00', 23),
('sandra.ferreira024@empresa-teste.example', '$2b$12$CFZDGZ3CwPQ82l2fyl7ekgjjm7bzQ5DATblbXZGuJOWEPGICJ3tWo', '2023-01-19 19:00:00', true, '2022-10-16 10:00:00', 24),
('kevin.dias025@empresa-teste.example', '$2b$12$VcvCk5Sr80QPjocthqlIctvxHks5zEjCtaT8uRDr68JjJ4BQT3iIt', '2024-05-26 05:00:00', true, '2024-02-13 10:00:00', 25),
('marcelo.silva026@empresa-teste.example', '$2b$12$0BfdNKn6rbHBPfFmtL64wj0wxrv82fTB9XrX8A3iKfmD19e7Ufz4z', '2026-08-22 00:00:00', true, '2026-04-01 10:00:00', 26),
('yasmin.souza027@empresa-teste.example', '$2b$12$SUilxIcZZsICbaq7ZDO3zEQphr9eUKJ1MDRhFJ0kdt2ikqA4wrG6F', '2026-03-16 10:00:00', true, '2026-03-16 10:00:00', 27),
('thiago.araujo028@empresa-teste.example', '$2b$12$lfUH2ZQsQQfJZaGxEv4gjnl3MZGGIUhCeY5DtwY3LVvSbYn1iohie', '2025-04-29 13:00:00', true, '2024-11-04 10:00:00', 28),
('andre.pinto029@empresa-teste.example', '$2b$12$eWUcgXFdhpePUW5m3lvf066vjFToJgJanlr3H8gnFRwKB7lvxnog9', '2026-02-09 00:00:00', true, '2026-01-08 10:00:00', 29),
('marcelo.ferreira030@empresa-teste.example', '$2b$12$giVRIcuhrLt8BIs1le0DQl9EgBOfiyKg58e8uK9hZSrSKqWNyDLzE', '2022-08-16 14:00:00', true, '2022-06-06 10:00:00', 30),
('carla.silva031@empresa-teste.example', '$2b$12$VgpEMigdeQ8xQjMfeUVXmPEqv1H7bA4xtxCcI1sREiSEgPOQJD0Fy', '2023-08-15 04:00:00', true, '2023-04-10 10:00:00', 31),
('joao.almeida032@empresa-teste.example', '$2b$12$8Mv8tbcZvUb3vgvwvOpOK4esubuC1J3qRK9xyJYgPuFVvMwBE0W0H', '2025-07-30 18:00:00', true, '2025-05-18 10:00:00', 32),
('eduarda.carvalho033@empresa-teste.example', '$2b$12$c9yoUf0fueXDnXOsXAHmyMCVlbR1Jn7UC6HMVAJMGwjjQsAtZdmzZ', '2026-09-10 15:00:00', true, '2026-03-29 10:00:00', 33),
('sandra.oliveira034@empresa-teste.example', '$2b$12$pLzhlpZCIBOcjiqXOk07Eb1fL13zwOf6SigT5GZ65p2ohYuKyUoAi', '2025-08-26 16:00:00', true, '2025-07-13 10:00:00', 34),
('william.melo035@empresa-teste.example', '$2b$12$8BpKEx6tAQ726wSYb7L0P6QWoIt7wYh11y4y8pfZon34N2Vzc1Z6D', '2026-08-04 10:00:00', true, '2026-08-04 10:00:00', 35),
('andre.correia036@empresa-teste.example', '$2b$12$S2YZRFvLYTCbdNNXiyMtfjG8ZV0QXtDTqQZXmzl262n65OM48mRky', '2024-03-09 05:00:00', true, '2023-11-20 10:00:00', 36),
('thiago.oliveira037@empresa-teste.example', '$2b$12$qfw6hSA5CPhSl1WrEi1L9rPzzjrqM4wQCaiuQQSRTpfduo4967LUc', '2026-05-16 02:00:00', true, '2026-03-12 10:00:00', 37),
('marcelo.cavalcanti038@empresa-teste.example', '$2b$12$Msyw8IJqF52itGihch2ZU5r2MVqt3t9Vj0OXdjocqRB95cwREE01h', '2024-01-13 16:00:00', true, '2023-12-21 10:00:00', 38),
('felipe.araujo039@empresa-teste.example', '$2b$12$1lkFq3KK8GJzeVP3bQQyK6ZjfZOC7Ka8O6uTT7JndNude7xyl71f0', '2023-11-30 22:00:00', true, '2023-10-20 10:00:00', 39),
('wagner.campos040@empresa-teste.example', '$2b$12$WrUOhEAXtQiPZFE2DIAUYMAAcvCnQjENGsB4Djh30GscecgAfOJpp', '2022-07-19 02:00:00', true, '2022-06-28 10:00:00', 40),
('wagner.costa041@empresa-teste.example', '$2b$12$bTCW0czRtSUBYv4Yne8qPAbyaOCzOUTsYMQ7PXfilYRlbDio01HfV', '2022-04-14 01:00:00', true, '2022-03-07 10:00:00', 41),
('rafael.moreira042@empresa-teste.example', '$2b$12$Ah3TBEbBpsZyug0AzEJT9cyFS541odbJIqO3hMOBBkTDBFKQ50SD2', '2023-12-06 08:00:00', true, '2023-06-03 10:00:00', 42),
('debora.martins043@empresa-teste.example', '$2b$12$yz6OwD8kmQfDkjEX6EoI9wqPk1FYuhoIE9WVVI302Lgd6X7U8WL1g', '2025-01-27 11:00:00', true, '2024-09-06 10:00:00', 43),
('valentina.rodrigues044@empresa-teste.example', '$2b$12$FkhmwTRVE1jwYhm8VaZDrkqSzNpUGWiA437GQ6BVSGeuyVAyc5jJ8', '2026-04-11 07:00:00', true, '2025-10-10 10:00:00', 44),
('igor.ribeiro045@empresa-teste.example', '$2b$12$28z2R2StvZ3LjzN3MMBvYOW2fHyS2GHnFtvNb3HqZsAFT0qFhU6BJ', '2023-04-24 00:00:00', true, '2023-04-22 10:00:00', 45),
('kevin.souza046@empresa-teste.example', '$2b$12$voUKS6P0osPYJixTgGlWCjAzfAinePyIW2KN9Q6TzkFEC8FGRCcjI', '2023-06-29 05:00:00', true, '2023-05-29 10:00:00', 46),
('wagner.castro047@empresa-teste.example', '$2b$12$0gwrGhsxsbiWA6srlTaHecDtCuxZZiiNVPjTfMMy3vcPBrfkbNVtN', '2026-08-03 03:00:00', true, '2026-02-19 10:00:00', 47),
('lucas.nascimento048@empresa-teste.example', '$2b$12$f7HU7GUFbOG7rmZ0JfHtKE5vlbGxf8HrlVj7Qd7lSsgi3p63doLoF', '2024-02-08 22:00:00', true, '2023-10-02 10:00:00', 48),
('gabriela.silva049@empresa-teste.example', '$2b$12$6qtIebN4bIKgVOhJB0FWiHkp77IcY7MOkd5qpQ90n0rkHefRZS92n', '2024-01-15 20:00:00', true, '2023-09-08 10:00:00', 49),
('kevin.correia050@empresa-teste.example', '$2b$12$26N8RqPtn7u4i5LaakIREyf2OH3YBh8xLx0Y8hwlaUVQRlYMYn87c', '2026-06-11 08:00:00', true, '2026-02-04 10:00:00', 50),
('larissa.melo051@empresa-teste.example', '$2b$12$RmmYy1JEIOJQJeheqJoGWlQ9QHpIC6N6d6A9qQCwV2nrABamfJbT8', '2022-05-31 12:00:00', true, '2022-02-12 10:00:00', 51),
('felipe.carvalho052@empresa-teste.example', '$2b$12$4mudMbAbX50pD7ZYGoeLBfBh84x3QWdXdLAgdd0yMz1WMCCAMgOPb', '2023-12-06 20:00:00', true, '2023-07-27 10:00:00', 52),
('valentina.costa053@empresa-teste.example', '$2b$12$KgaSK04hFwDMlichKFOwKHjDLs8GOioYIdv8NmelDQCQ0Jusug24E', '2022-02-09 06:00:00', true, '2022-02-08 10:00:00', 53),
('osvaldo.martins054@empresa-teste.example', '$2b$12$noMoT7uTml9webhsmKVcbmk2SvIlb04bQxWg6soYO68922ei5Xlxx', '2025-06-28 23:00:00', true, '2025-01-26 10:00:00', 54),
('igor.rodrigues055@empresa-teste.example', '$2b$12$8pcMlHWudX2082mfDPeFUNFECtxRSEqy5Kcnm1ys3J6xIifBvKCMA', '2023-09-14 23:00:00', true, '2023-05-18 10:00:00', 55),
('yasmin.dias056@empresa-teste.example', '$2b$12$zXtxloOwf0FNW5gecmmusYQrR99UT4asYWzzJODFVjyoUDFWUSCXt', '2023-04-22 23:00:00', true, '2023-03-05 10:00:00', 56),
('eduarda.rodrigues057@empresa-teste.example', '$2b$12$VRmBIxJh6aYZvzQzWHubW5j7uNLL18myHlgJB3fgIbQEle7roOedG', '2024-08-27 20:00:00', true, '2024-07-15 10:00:00', 57),
('thiago.ribeiro058@empresa-teste.example', '$2b$12$1zjgHDe71imARDL6KFQraidyYG42CJTr2CRSsMYkp3TC4lJy6o1qM', '2026-07-23 10:00:00', true, '2026-07-23 10:00:00', 58),
('ximena.souza059@empresa-teste.example', '$2b$12$4RSnE9I9egVuBpFCeRUxU1yah4uUEeIp6iY0kbgsHyeVPjwncPnRZ', '2025-08-23 06:00:00', true, '2025-04-11 10:00:00', 59),
('larissa.silva060@empresa-teste.example', '$2b$12$POgQBLdxmp4mabaYQd7fZPScFYuMM5yAoguaOannOrMc2zVE9SX5J', '2022-11-05 13:00:00', true, '2022-09-02 10:00:00', 60),
('mariana.barbosa061@empresa-teste.example', '$2b$12$L8RQ8RuN2t0DuLKxNoK9oM3tTUMdxsjmc5L6TurNVG8Orb0upH4Pp', '2024-10-31 08:00:00', true, '2024-05-08 10:00:00', 61),
('yasmin.santos062@empresa-teste.example', '$2b$12$SLxtBa1elNimfKZ5oeStSpkprBNqWe8ykBFTVYXbx0V5KjTpCCxLe', '2023-11-01 08:00:00', true, '2023-09-27 10:00:00', 62),
('helena.lopes063@empresa-teste.example', '$2b$12$DjlaFSE568xENKzc5xJmBp4SFEQRO5TpcYNPROqCQpL70aVN4yK3G', '2023-06-17 23:00:00', true, '2023-04-30 10:00:00', 63),
('larissa.teixeira064@empresa-teste.example', '$2b$12$kAQFH2KTU51gJXYqmURakEgVzzrAkliEFgJnmUYl82B4gNefhdboE', '2023-03-21 13:00:00', true, '2022-09-08 10:00:00', 64),
('andre.costa065@empresa-teste.example', '$2b$12$aq7PWcnXxG7qEULuQvtowyL82Xa1yZ5bhG6UpCQ1FOaN6tN28TQoE', '2023-12-03 05:00:00', true, '2023-05-27 10:00:00', 65),
('patricia.castro066@empresa-teste.example', '$2b$12$Xv66DORgxLS12WSbEfZ99C37DHpk6Rg9b5uxrSP6PP5NEXunJ5ev4', '2025-02-21 09:00:00', true, '2024-08-14 10:00:00', 66),
('sandra.gomes067@empresa-teste.example', '$2b$12$SOW7BoqsgvKl6JhtxTtKlyAVX5hPKga0tYsnQjrU0ZctFQq0JNns7', '2023-06-09 20:00:00', true, '2023-05-25 10:00:00', 67),
('fernanda.ribeiro068@empresa-teste.example', '$2b$12$jS05nT5W1TsY4jbyADvyeTr5RHguX1d5sOJBD3nXmOw1gW1x8Qkdw', '2026-06-08 10:00:00', true, '2026-06-08 10:00:00', 68),
('ana.lopes069@empresa-teste.example', '$2b$12$6hFuAjSphysKkUW9PfCYxNBahhN4nK9DRfKx9RshuS1HzRyYWGdZH', '2024-12-10 13:00:00', true, '2024-06-27 10:00:00', 69),
('igor.nascimento070@empresa-teste.example', '$2b$12$UmOh1RaBtcJOfpUfhs7k8Opc64sHiEt49LFQPcci1niIKrTAIqOkC', '2024-09-29 07:00:00', true, '2024-07-25 10:00:00', 70),
('osvaldo.pereira071@empresa-teste.example', '$2b$12$ARHeT9RlxOYLAj9AbLehgHeNwuq1nax8xg5kTtYEGR1IL3k6yvT8G', '2023-03-10 22:00:00', true, '2022-10-09 10:00:00', 71),
('igor.dias072@empresa-teste.example', '$2b$12$DhaFjhxTdS8RpLRl9XFrA62Ys16ztfeT6kdUIE27AFTDF93RzM4US', '2026-02-13 03:00:00', true, '2025-08-17 10:00:00', 72),
('daniel.araujo073@empresa-teste.example', '$2b$12$UpT6Z2GLL7P2Gne0F7Axxh4pHVhssnTJns5NZcCMEwMuva3lvgEfF', '2022-11-07 23:00:00', true, '2022-09-29 10:00:00', 73),
('natalia.teixeira074@empresa-teste.example', '$2b$12$hEjOoysAlYgB0kvAw1mlcyU3jmDPqAIXSuV2F50yggbjFVoQv8TAr', '2022-06-01 12:00:00', true, '2022-01-28 10:00:00', 74),
('valentina.teixeira075@empresa-teste.example', '$2b$12$VpjFpV19pYmRYRFl9KNoic95AMfdzsNouDFNsqrvztccdUnD4gl6w', '2022-06-14 13:00:00', true, '2022-03-29 10:00:00', 75),
('thiago.pereira076@empresa-teste.example', '$2b$12$gaI1uTwWGxOnx3iUhK4OlR5eHvS9mSclIp20483BS4ez5FbLRhcJX', '2026-02-28 16:00:00', true, '2025-09-04 10:00:00', 76),
('william.gomes077@empresa-teste.example', '$2b$12$TyQ18kUxWyqljnR2XCRGsnRIkcocj25qXNpZHavLwfRISzLudYZn7', '2025-02-08 18:00:00', true, '2024-12-25 10:00:00', 77),
('lucas.campos078@empresa-teste.example', '$2b$12$8H8WWgUxO1NIYnqKYUjoTw3fll1lETObklyLeep48gGTYLxbZVNcp', '2025-12-21 18:00:00', true, '2025-11-30 10:00:00', 78),
('daniel.silva079@empresa-teste.example', '$2b$12$iMa77gTuRmDIBkujdQMhZgsFZi4bAjOo2x2Zgkgg7wMh553LlGLQj', '2024-11-09 07:00:00', true, '2024-05-22 10:00:00', 79),
('tiago.barbosa080@empresa-teste.example', '$2b$12$sYvg4P5YSea5jOtySWDeJ7rf9lxtAQzXuXOpzEHHYxKAJQURoa9bI', '2026-01-01 13:00:00', true, '2025-11-28 10:00:00', 80),
('natalia.barbosa081@empresa-teste.example', '$2b$12$dv01Ysi084pZdi7Rxb7xRPeZ1bweeJYjlUURszuWRrj2bUFaZQ2IZ', '2023-02-09 02:00:00', true, '2022-09-15 10:00:00', 81),
('igor.teixeira082@empresa-teste.example', '$2b$12$so5qfx6UydyY8LeQDOzxdxAqgwWuokgna4lm5ofJc9Dcft8U7Ftyw', '2022-03-13 08:00:00', true, '2022-02-07 10:00:00', 82),
('isabela.martins083@empresa-teste.example', '$2b$12$Pu3FFWbXD1cjQeM7onQiS9F9LGVBisM6cSQo9L7J9gaVt1X3ECkse', '2024-05-22 21:00:00', true, '2023-12-27 10:00:00', 83),
('natalia.martins084@empresa-teste.example', '$2b$12$veSC98VB568EwrzMQBFMtfzB29c8dXRIjgxJ0n2PvDHYglg7yAydv', '2024-09-24 01:00:00', true, '2024-04-22 10:00:00', 84),
('thiago.ferreira085@empresa-teste.example', '$2b$12$nThCH6jnJ9WdYW8nnbVApkiqf2BAn3ld3A7sm6DHW7B4JZI3mSJiP', '2022-11-05 09:00:00', true, '2022-05-01 10:00:00', 85),
('hugo.fernandes086@empresa-teste.example', '$2b$12$ZTa35EtXPqS961NNJcUlRWLnnlQ7wwstYhE9zvZA8u5CxIkBrOQNR', '2023-03-22 12:00:00', true, '2023-02-25 10:00:00', 86),
('lucas.santos087@empresa-teste.example', '$2b$12$XgOMzEU5gBKcrAnCxmDh74zYRTaxnr2qZXzo3zajxYYLu79o8lNZX', '2025-07-05 23:00:00', true, '2025-02-08 10:00:00', 87),
('vanessa.dias088@empresa-teste.example', '$2b$12$hN8k4GBgXFNW0MqI6mfBMKbR5exUiCNwo3sHpWWVOr5MNJgCKgr3f', '2024-03-02 21:00:00', true, '2023-10-28 10:00:00', 88),
('igor.almeida089@empresa-teste.example', '$2b$12$4mgXzEMnA3NBjs4ON0hvv9LaGphLHGVnw3bifQhqmfROCYB65pm0S', '2022-03-04 13:00:00', true, '2021-11-24 10:00:00', 89),
('gabriela.cardoso090@empresa-teste.example', '$2b$12$KVtX7E2AUYMLAJxNdkcFKsZN7vbluXweOxWhtauCX3IxLGgrL21e3', '2025-09-29 07:00:00', true, '2025-06-13 10:00:00', 90),
('thiago.carvalho091@empresa-teste.example', '$2b$12$UttmXE9AhkuxJrN194r0y34cNefXSrUyItLyRC07hxMbcaguQBkl3', '2024-11-08 03:00:00', true, '2024-08-05 10:00:00', 91),
('debora.dias092@empresa-teste.example', '$2b$12$951qWg6euLYqkiCh1U37zBhGJO8tToqsfI2OTme8danYlgmKG1VtC', '2026-09-04 02:00:00', true, '2026-07-26 10:00:00', 92),
('ximena.nascimento093@empresa-teste.example', '$2b$12$gi4xCMmyeAQ6JgCggbCqDGvhIvnyAuBwdx5mlkrLGgdpgh3JoGr2R', '2022-08-22 22:00:00', true, '2022-05-11 10:00:00', 93),
('natalia.dias094@empresa-teste.example', '$2b$12$ynZQEkmNRral4MT0QkQkuC7PAb82kVavDa2DcbQsoFUWgXuEvHPpC', '2022-08-15 19:00:00', true, '2022-05-17 10:00:00', 94),
('gustavo.teixeira095@empresa-teste.example', '$2b$12$S3W13KgQTb81OiEpT8wUm7N72Q3B7GqQ3yz9tcGvLAC8Xr8EQCj7M', '2024-01-15 15:00:00', true, '2023-07-29 10:00:00', 95),
('karina.oliveira096@empresa-teste.example', '$2b$12$srXacrKdsAOapvr1PVjfq9mKfnDvYNA7gqstIQtJQmiIsfJMHx2Bi', '2026-01-22 08:00:00', true, '2025-09-30 10:00:00', 96),
('bruno.nascimento097@empresa-teste.example', '$2b$12$t5SDAAzbI7AUMspFhHJsUyyaY2CddZOugsDudTPTdmiTEnP0st9aE', '2025-01-21 16:00:00', true, '2024-07-29 10:00:00', 97),
('joao.nascimento098@empresa-teste.example', '$2b$12$haLhgmDZBdhz8Y0HYQ1MANlYOkTHDrP7EPyRgzAbCIasN3Mc47DSr', '2024-12-01 21:00:00', true, '2024-10-22 10:00:00', 98),
('gabriela.ribeiro099@empresa-teste.example', '$2b$12$F1n3GFkjOIC1iWO5e0D7LlRFOqRKKKZebToPEQuoV4Xg2Tx85dpcU', '2026-05-16 21:00:00', true, '2026-01-23 10:00:00', 99),
('ricardo.marques100@empresa-teste.example', '$2b$12$L05tRx9JmpBrmkYYlKNPmY3wzHelT4NEZJDvMIieAxeeWshoLun65', '2026-07-18 10:00:00', true, '2026-07-18 10:00:00', 100);

-- =========================================================
-- 8) ADMINISTRATION (100 registros)
-- =========================================================
INSERT INTO Administration (Email, PasswordHash) VALUES
('andre.oliveira001@banco-admin.example', '$2b$12$2jrHoGLtMH3K69OcAm3dmYuU4dEXILZYvezT53DiSgEnvDW2o5bWm'),
('ricardo.campos002@banco-admin.example', '$2b$12$xQbDyxhRIhhM4vv4ofazUYHcX2pddBrtL39iyOOe6mRY31BswI1p3'),
('william.marques003@banco-admin.example', '$2b$12$Hgvz6Ee40GsIhSWcoBV7bZqKs7d0xbxBnxOHObV64UuZqUnbSM1W5'),
('daniel.dias004@banco-admin.example', '$2b$12$gySQ16CJJGkxU5iaX6fpHuyMwOWYLhJtqlCBC7D0nd6rLtdlVr1hN'),
('joao.campos005@banco-admin.example', '$2b$12$MdhcQ1JcSdSqpQ1vSLveBFouxZzN2iuWuzOD8Mjc54bLYFWFKvnZc'),
('joao.araujo006@banco-admin.example', '$2b$12$VO779J9NTHh2XLOGtmJ4uX8ot8QB7zeIewEoWoZ95p4PPLXvhzFjE'),
('mariana.cavalcanti007@banco-admin.example', '$2b$12$lCkzVSDg1ZbkeFGyAhRosuZuLgSe564NTBURTSKYFjaKPCtkSrVoE'),
('daniel.moreira008@banco-admin.example', '$2b$12$GGF5rxU7c62TAr2tvkrEnk6qLgorfIFkLc3HmEYwr6ABhC4S4h3Ef'),
('isabela.rodrigues009@banco-admin.example', '$2b$12$ThveXTuFrNFWRt5A5Xt0O2cNaix6PqjYTrm261UaeImlzqxKNEOls'),
('otavio.cavalcanti010@banco-admin.example', '$2b$12$sEXywDfg2Ur8uPhCthRRVDXEzosF3WdVqtyBy4qTvYtzJkmM6MuLb'),
('bruno.dias011@banco-admin.example', '$2b$12$xWuvVddnH9ZySrkxzIpa0UGFXcrbNQcUYxyY21eFRTbUH4fKyZ1WA'),
('bruno.carvalho012@banco-admin.example', '$2b$12$qmqODsf7ID4HpbEyxHgn8yowINuY8fk1EpiMDSJoVptujtJzaQUXH'),
('william.fernandes013@banco-admin.example', '$2b$12$oCDITT5AehaGhm1h2EMGi2zah9S2qt3IkrQX7xLDnuU95kSDtlD9a'),
('zeca.costa014@banco-admin.example', '$2b$12$mfCfMKeN2valr4ATwV5INmKgnlIhGNxJ0L7TM4maon5MiDae8iYAD'),
('vanessa.cardoso015@banco-admin.example', '$2b$12$VoyVRwZteLnkNriBBRph3Ol11VvAM25lx59mwmKu44FaqONy9RqdJ'),
('bruno.campos016@banco-admin.example', '$2b$12$SDLCkUOHgwMSRk0fZhBSt1r0OTuP33k9SwaPzPdlVjqkcLQzN06tI'),
('hugo.gomes017@banco-admin.example', '$2b$12$CZf5f4lftMbkaNC26tQ61UTtxrrJzZqm8qBfwwVGZQk6KjJ9AOQrv'),
('kevin.teixeira018@banco-admin.example', '$2b$12$ZXfVqCbhOliKxxKcRN1aAuOg4sClbYCJAyZpxuQs82V3FAV7ybF52'),
('zeca.cardoso019@banco-admin.example', '$2b$12$m8Uiu78tVXCkaj2seomfpQ0xDvDQR4PZRVfhViV3YTAyeETKnLVwv'),
('ana.nascimento020@banco-admin.example', '$2b$12$LyeoEcsfxljtPDq7lyGgXvTOzYjhFQaOPAdAUhY4FWS08glAV1ea6'),
('paula.rodrigues021@banco-admin.example', '$2b$12$mYTCdrhEw3QpusYHlwbuNKwcTpffTNAr8QpZZTGCexj8N2x8e5UAb'),
('rafael.machado022@banco-admin.example', '$2b$12$O2ZAhlqsF0pXxDVBUWrPL4QqzbziqL8tnbudrSZKNuy5avvBCUnRP'),
('rafael.araujo023@banco-admin.example', '$2b$12$c07NIvm9wstPbMkvA9b8IEo5iL0gn9JljWTyzUzLtwg27VtHy1ECZ'),
('gustavo.almeida024@banco-admin.example', '$2b$12$5lrHuCk4xO8zNMiM9LT6upJDfwU2OI10kw2k65us3mCpL9X8FgLu5'),
('karina.ferreira025@banco-admin.example', '$2b$12$C7acP191ezqShWOHbDTsyYVwSFOAhjqZUgkzv4XngNe3EpqsBWRJK'),
('isabela.pereira026@banco-admin.example', '$2b$12$Gb2IStSJsSPjXhL1po0KjawF1GD38NKFGx5O1DExPenmjLThmEJNa'),
('larissa.cavalcanti027@banco-admin.example', '$2b$12$omPygueIHT28dJppTQOpQCcLIflZd3GdoKKCHWtNmhfmHnEJK1g2W'),
('mariana.moreira028@banco-admin.example', '$2b$12$XMCaULEkFF6s1prItB86Et8DkeAs5BNHlWVnFtGgsLJjjVW0Lj78O'),
('enzo.correia029@banco-admin.example', '$2b$12$Toq5ZwsTZttIVa6dzOiCrYmPpZbAxITXMBqejS6K76Wy7Rp9KddvH'),
('gustavo.pinto030@banco-admin.example', '$2b$12$rD2e8TZ2KJIBuHOIYQiV1RT7wVUntowm8qL5iNAXXZd9xJ972uHru'),
('eduarda.machado031@banco-admin.example', '$2b$12$efIgjF0JDOleQYC39julJCWT63ysMyatj1KVHOmY53SJljtgH6chv'),
('eduarda.gomes032@banco-admin.example', '$2b$12$15Omk9o5Vb26h6b10mdBmVKh8FJGkm6Tw9znfFDF6bxA7gwte6OoR'),
('valentina.fernandes033@banco-admin.example', '$2b$12$Y2a7T3c0kaGiDcKnTsMZzkGjYPw5Wbmzx0MbbbDTizHVSkgvnAe8C'),
('enzo.campos034@banco-admin.example', '$2b$12$3B1YaD3puH7PfEZJhFXiOUSrMppTsSCKG9q3uxjT0UXNX8aNZoyaD'),
('andre.costa035@banco-admin.example', '$2b$12$MYJCnMHhrV5u3pYEIPPvGdwN7hoCgIs2tB9f1Fy69m0nbTvLj1GXw'),
('otavio.marques036@banco-admin.example', '$2b$12$6GxgkNM0Uj6L1r76s383Di6apIt5M7dziYVY6pWOylZe7r3hT6jBP'),
('william.silva037@banco-admin.example', '$2b$12$NGGVLoBYHLj2US26cZpMAkS1EsGfT71o0ERAdgV0T4NuPpwaSEKnA'),
('sabrina.santos038@banco-admin.example', '$2b$12$9ZELJL1PcgZNgf7oJRLYScOBC1cyXUA4nlNuszbC3wQv7rWuRNVjI'),
('natalia.fernandes039@banco-admin.example', '$2b$12$tyvY01uMTZoZgSNtElxqRvX3U7ugK4RIUHyRmfL2zqSHy5g7Y3sJf'),
('otavio.barbosa040@banco-admin.example', '$2b$12$Zpj9bnRn2BYjs71DBI9llGyJ3yqXwXAw5oHeQtZRXJf49RHgZovR8'),
('otavio.barbosa041@banco-admin.example', '$2b$12$qZCBKe9bBO3DRdv7rp3bLAViyPtRsoXFzV8K18dUKWedzjm8V9Zb5'),
('hugo.gomes042@banco-admin.example', '$2b$12$f6J6yzRivfwR2Elyo9NvUGbL8dxk7Gaxb4I17lMlnv24LJ3R2ljYb'),
('carla.pinto043@banco-admin.example', '$2b$12$H76YtsvlG3EL9565HyBhIuwoYlzw45Y4R0gGPtGJGyHmtBm6AAfbg'),
('natalia.ribeiro044@banco-admin.example', '$2b$12$Ls9VVRGc6tK3HNTHcsl7dZ2TGlwR4g63SemmNa5mQ5aoYZ99ZxZci'),
('patricia.nascimento045@banco-admin.example', '$2b$12$P2AJ1Mm2TqQzzr3YELMyRBPnNh3Elu6pQ5yHrJghZ26IEh9DgoZlV'),
('eduarda.ferreira046@banco-admin.example', '$2b$12$BwTBVPF8VScaj1pkC3dx6FnMuSbB0LrjHtgeGbSStc3QaadyJ1VcH'),
('bruno.fernandes047@banco-admin.example', '$2b$12$ZDJxQx4oEp8ND3C2nrT7ypjTAscz209hMmz2CC7CuPiGpSJ6TWwC1'),
('enzo.martins048@banco-admin.example', '$2b$12$NnigOsZWMdnIjxGlBRxPqUIjyWmzD1i5t5LT2PWfa1d0t3mjn0w4K'),
('gustavo.ribeiro049@banco-admin.example', '$2b$12$uksXj2kh4zIMRr0ph64gc2DDVK9wBJBbEPvvfwkMP4g57mNsXWv8E'),
('sandra.cavalcanti050@banco-admin.example', '$2b$12$K9wpF84btkORq9uq9OOub25aJsmDyRqxwF9ARivqlrfBPX413bhVp'),
('lucas.martins051@banco-admin.example', '$2b$12$eJofeSTv4eK78NuPLdi45lSfAQnSxMlYRERDTK1BFe8q2JlhVl0Uo'),
('debora.fernandes052@banco-admin.example', '$2b$12$ZszNnpvoa3QrjdE3dUAP3B8PtV4RL7JTtwYwsEHUokQz2uX6z1CjT'),
('caio.melo053@banco-admin.example', '$2b$12$62Wx1jwAY4j6nbDqmEjZ0rlLEA0EPvg7RLiGEnE7NutMLLGvxQhJD'),
('bruno.barbosa054@banco-admin.example', '$2b$12$wp6Q43N3DrvXR37WJ4RLYpI3t2xjtGC36TeylVSwWDnawMIW7Et2m'),
('valentina.costa055@banco-admin.example', '$2b$12$7LhTTmXrXAYaqibOVeC4jMIKVgqrRCkh7h1IwtSIMOyuZcrJJ2dSi'),
('eduarda.pinto056@banco-admin.example', '$2b$12$ejG6oU8uzUEHGNyjZYBh4qoNId0ncnoj1FuUPbVEPRIcKmKaRKw3k'),
('nelson.pereira057@banco-admin.example', '$2b$12$H4UrIFjmLuuqhrbLOFzlcXU2mJUZP2YwdMJ2pFtnluESSCyU5DKXP'),
('patricia.oliveira058@banco-admin.example', '$2b$12$M20M165sxJjdnlA2ezbV3sq53Xm5UQb5Dafo1JIqRDTjeQbEZz8Jc'),
('eduarda.correia059@banco-admin.example', '$2b$12$2SSmqP7lQY9cI1IRXSUrfwYHx3THtNzJx8VjP3Pz7fpi5OBHQ3v48'),
('joao.nascimento060@banco-admin.example', '$2b$12$vNU0J8XZyZ29HGJpI6TQWEuZx6GYnvzUgNCZ3laXFgie6ozZHfYbb'),
('hugo.costa061@banco-admin.example', '$2b$12$lWep6AwyRsymAKDMcEbtbR1xHgQMv8EbcE4DHfSS5CUo9ctW0nzah'),
('ximena.correia062@banco-admin.example', '$2b$12$bFelrJXHSphGkC0cLAQ8eDpyUOkxsn1KESY6hki8LbEXAR3wHzcBR'),
('eduarda.oliveira063@banco-admin.example', '$2b$12$f2PDFp5ibflmOHID8OKhcmsUyxFaGqGsYtpDnPDnkIM1eP1jVD9ek'),
('thiago.correia064@banco-admin.example', '$2b$12$zM6ZzUctylTxBZn4D6Fz2NoGEeLzIRWVcrrO9vZL41DEU0KIQLWiu'),
('zeca.ribeiro065@banco-admin.example', '$2b$12$8HMdN52Ko5uol8luPrLPlMWXQlQSImCGhSspLNu3YxyS5CvpehxIy'),
('eduarda.costa066@banco-admin.example', '$2b$12$YpE5IYFAq8MEgBtihV9cegHhfUAcOdg8SronLU5vobKVk7xHbjJM8'),
('julia.santos067@banco-admin.example', '$2b$12$f0nCkkngotJLxngfSBTxMxHq2EWGtEMZLxRLRxWmtzpsoLDb5pRCX'),
('caio.moreira068@banco-admin.example', '$2b$12$vxES4fpow9Dk0NLY08wVf4fRde5Cs1r4otGofdOW76kH63oYmSegY'),
('lucas.teixeira069@banco-admin.example', '$2b$12$hCVSj5pa1VFbFdicji1ZJrpkgpNUexaCXQyHzG76vLSUUGzFEXmLw'),
('vanessa.santos070@banco-admin.example', '$2b$12$LzxxtmaBNj1XUOspboBECC2Czd635sdCxOALvFD51Dvl5k9jSqbnG'),
('bruno.barbosa071@banco-admin.example', '$2b$12$zXCMrdGhBEVcWTJe85xOBSqi4fxycs7mJzZjHp4ae1qDeD85EeC3M'),
('beatriz.campos072@banco-admin.example', '$2b$12$D7JH3UNujQSGrK3obDuFESz7s1gjnW2p45RFzyJspM6PW5dzAz8uw'),
('vanessa.ferreira073@banco-admin.example', '$2b$12$E95ynAopXuLK0gfTjA35XRjdDcs0Q4RPfktg9fca5W3ZEqsZeLiC0'),
('karina.fernandes074@banco-admin.example', '$2b$12$WFEnryukDC7sOYz5X0PnCRcd2j9hSrutoDz5PPTsR3Owyvd0XyYC7'),
('william.barbosa075@banco-admin.example', '$2b$12$iu7At4JRaW7zE9j2oL74SzVNSXxaDZFnuOTFG39LLA6XCSHHUcuzM'),
('eduarda.ribeiro076@banco-admin.example', '$2b$12$ZzC2J5Znq30YPJyaonYoaceXH36b1TVWVPEJHIW15ySkOaAupDg5T'),
('karina.lopes077@banco-admin.example', '$2b$12$spkATwVr9QNejh67eVVqlVuFZHFDMWwfkYHpb4XXfdjjYHD5Nb0XA'),
('kevin.correia078@banco-admin.example', '$2b$12$YnlNVUe9oO4EVyNmcCaw95OzZ81f0KIhiE9TvMcZttYs1ds7iqxjv'),
('enzo.oliveira079@banco-admin.example', '$2b$12$8wETzbrlfiGLgh8VthJKedtVU4eS51H4Ogz5t2fuTif6a3cYzgj63'),
('patricia.oliveira080@banco-admin.example', '$2b$12$ul4RgYDThTYQozQ2JT0EoAMyZsi8zJqM3RpLahKUQJYJ7pSopcey7'),
('sabrina.santos081@banco-admin.example', '$2b$12$JOSZagmiSW1uzOQBmNmv11PE6BpAjWZUGmgrrkVDVeCsnm107qDBi'),
('patricia.fernandes082@banco-admin.example', '$2b$12$En6F5gSGQqOXUbJ2orYUPIMXGlEGjbVVAvDlTSARqmy0LIfDmpcuD'),
('patricia.teixeira083@banco-admin.example', '$2b$12$uo7beJizcCHTg9ISnnwcaKhrKBmvohXXHNVcQHORa0YnzufK5rAvw'),
('gustavo.teixeira084@banco-admin.example', '$2b$12$RbgMbrkjTTmmiCPnih4mM2sQjd3DaxHQ3gVnjgoYqGIP653492j9H'),
('osvaldo.costa085@banco-admin.example', '$2b$12$7ALXrqnTVbzxH52hR3oR6hD4KTiGHoh4FL2oIvHquilA0mXnNGMeS'),
('marcelo.oliveira086@banco-admin.example', '$2b$12$Ttkr0bVWWQ1SfkUNfQDXB3OdN8bjvxh4OUqfYLJDE4ekVFWtDFCcd'),
('wagner.rodrigues087@banco-admin.example', '$2b$12$2xNfSrjxhzXF9wdtkJTIcDzoLMCw9VW71GhdX51pG34TqT7NHD2pm'),
('mariana.cardoso088@banco-admin.example', '$2b$12$OypPiyQTGOPtsb2UiS0Ag7pCcy6Nj6xs2lWSwHll1TPDtX1F7G4bo'),
('gabriela.martins089@banco-admin.example', '$2b$12$oRGZrSiLRPvrN9smn8ZmGK2hTYXfSCC2hc8ofQEiN9czcefUVE79f'),
('kevin.campos090@banco-admin.example', '$2b$12$zI13alY9bgWghL5pxDRqr8hSzaMnwXidtehGVrHoyDZKUzkS6LFeB'),
('yasmin.gomes091@banco-admin.example', '$2b$12$zf5R1sGzUZNWuEyAXcRhhWbcl9Gmy2f2bjCtUgkn3wYFlJE0AI3M9'),
('rafael.carvalho092@banco-admin.example', '$2b$12$Ucp3aGBmsDPI3icoxSdPBRLcwIRXrla99KkURMMVJxWsILLTQ8C3s'),
('marcelo.dias093@banco-admin.example', '$2b$12$wx7pugbFtM8mkllWYsi8zI6zR9DyhV8bBUt6SGnPLwp6i3UZ8Z373'),
('hugo.pereira094@banco-admin.example', '$2b$12$3JAbXWzVHjOfIJvN5nIYgZ2VYtu5h0WyjKV0q30ZkUaPFL4y9h8ym'),
('felipe.lopes095@banco-admin.example', '$2b$12$lLZZb6xdOeNeTdzkNmKBziTnrbEBrkSBiy1PKvIf2SZ558J5eWb6P'),
('rafael.oliveira096@banco-admin.example', '$2b$12$8mCfi5w5m4yC5Cm94QekxnAZ8tCH8xlB3HtKTkJc1scYArdCQZyH5'),
('helena.machado097@banco-admin.example', '$2b$12$XPklakfEYHlMq5AJvZWQBU1oYphFH9zuJBTERHNy5hDd1E3GY1aVx'),
('joao.oliveira098@banco-admin.example', '$2b$12$OknvPfy2wWHBgqJTGVk8iJ8U7IHQoMHW17h40X6Qo0ASSp8OsL0LI'),
('eduarda.fernandes099@banco-admin.example', '$2b$12$fvMF7UcOf5oHBDH8l5CxeMSPCLEH1JBE0ZiFm9cNtGylModqb3w9x'),
('sandra.gomes100@banco-admin.example', '$2b$12$QJbhrHWvxp5yKmvYsNNiHJqy11m755vkeQuHu9D4vkJzK1nGWwn9F');

COMMIT;

select * from store;

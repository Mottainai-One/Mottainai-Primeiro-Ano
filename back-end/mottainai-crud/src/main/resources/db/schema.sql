-- Somente para um banco NOVO e vazio. Nao apaga nem carrega dados.

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
	
	constraint pkSubscriptionPlan 
	primary key(PlanId),

	constraint unSubscriptionPlanName
	unique (Name),

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
	check(DeletedAt is null or UpdatedAt is null or UpdatedAt >= DeletedAt),

	constraint chkSubscriptionPlanActiveDeleted
	check(DeletedAt is null or Active = false)
);


create table Company(
	CompanyId serial,
	OfficialName varchar(120) not null,
	TradeName varchar(120),
	CNPJ varchar(14) not null,
	Email varchar(500) not null,
	Phone varchar(500),
	Latitude decimal(9,6),
	Longitude decimal(9,6),
	Active boolean not null default true,
	CreatedAt timestamp not null default current_timestamp,
	UpdatedAt timestamp,
	DeletedAt timestamp,
	SubscriptionPlanId int not null,

	constraint pkCompany 
	primary key (CompanyId),

	constraint chkCompanyName 
	check (trim(OfficialName) <> ''),
	
	constraint unCompanyCNPJ
	unique (CNPJ),
	
	constraint chkCompanyLatitude
	check(Latitude between -90 and 90),

	constraint chkCompanyLongitude
	check(Longitude between -180 and 180),

	constraint chkCompanyUpdatedAt
	check(UpdatedAt is null or UpdatedAt >= CreatedAt),

	constraint chkCompanyDeletedAt
	check(DeletedAt is null or DeletedAt >= CreatedAt),

	constraint chkCompanyDeletedAfterUpdated
	check(DeletedAt is null or UpdatedAt is null or UpdatedAt >= DeletedAt),

	constraint chkCompanyActiveDeleted
	check(DeletedAt is null or Active = false),
	
	constraint chkCompanyEmail
	check(trim(Email) <> ''),
	
	constraint chkCompanyCNPJ 
	check(length(CNPJ) = 14 and CNPJ ~ '^[0-9]+$'),

	constraint chkCompanyPhone
	check(Phone is null or trim(Phone) <> ''),

	constraint fkCompanySubscriptionPlan
	foreign key (SubscriptionPlanId) references SubscriptionPlan(PlanId)
	on delete restrict
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
	check(DeletedAt is null or UpdatedAt is null or UpdatedAt >= DeletedAt),

	constraint chkAddressZipCode
	check(length(ZipCode) = 8 and ZipCode ~ '^[0-9]+$'),

	constraint chkAddressRequiredFields
	check(trim(Street) <> '' and trim(Number) <> '' and trim(Neighborhood) <> '' and trim(City) <> '' and trim(State) <> '')
);


create table Store(
	StoreId serial,
	Name varchar(120) not null,
	CNPJ varchar(14) not null,
	Email varchar(500) not null,
	Phone varchar(500) not null,
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
	check(DeletedAt is null or UpdatedAt is null or UpdatedAt >= DeletedAt),

	constraint chkStoreActiveDeleted
	check(DeletedAt is null or Active = false),

	constraint chkStoreCNPJ 
	check(length(CNPJ) = 14 and CNPJ ~ '^[0-9]+$'),
	
	constraint chkStoreEmail
	check(trim(Email) <> ''),

	constraint chkStorePhone
	check(trim(Phone) <> '')
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
	check(DeletedAt is null or UpdatedAt is null or UpdatedAt >= DeletedAt),

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
	Name varchar(500) not null,
	CPF varchar(500) not null,
	Email varchar(500) not null,
	Phone varchar(500) not null,
	Active boolean not null default true,
	HireDate date not null,
	CreatedAt timestamp not null default current_timestamp,
	UpdatedAt timestamp,
	DeletedAt timestamp,
	StoreId int not null,
	RoleId int not null,
	
	constraint pkEmployee 
	primary key(EmployeeId),

	constraint chkEmployeeName
	check(trim(Name) <> ''),
	
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
	check(DeletedAt is null or UpdatedAt is null or UpdatedAt >= DeletedAt),

	constraint chkEmployeeActiveDeleted
	check(DeletedAt is null or Active = false),

	constraint chkEmployeeHireDate
	check(HireDate <= current_date),

	constraint chkEmployeeEmail
	check(trim(Email) <> ''),
	
	constraint chkEmployeeCPF
	check(trim(CPF) <> ''),

	constraint chkEmployeePhone
	check(trim(Phone) <> '')
);


create table UserAccount(
	UserId serial,
	Email varchar(500) not null,
	PasswordHash varchar(255) not null,
	LastLogin timestamp,
	Active boolean not null default true,
	CreatedAt timestamp not null default current_timestamp,
	UpdatedAt timestamp,
	DeletedAt timestamp,
	EmployeeId int not null,
	
	constraint pkUserAccount
	primary key(UserId),
	
	constraint unUserAccountEmployee 
	unique(EmployeeId),
	
	constraint chkUserAccountEmail
	check(trim(Email) <> ''),
	
	constraint chkUserAccountPassword 
	check(length(PasswordHash) >= 8),

	constraint chkUserAccountUpdatedAt
	check(UpdatedAt is null or UpdatedAt >= CreatedAt),

	constraint chkUserAccountDeletedAt
	check(DeletedAt is null or DeletedAt >= CreatedAt),

	constraint chkUserAccountDeletedAfterUpdated
	check(DeletedAt is null or UpdatedAt is null or UpdatedAt >= DeletedAt),

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


-- Opcional: executar manualmente no PostgreSQL. Não apaga nem altera registros.
-- PKs e colunas UNIQUE já possuem índices; estes cobrem FKs usadas nas consultas.
CREATE INDEX IF NOT EXISTS idx_employee_store ON Employee(StoreId);
CREATE INDEX IF NOT EXISTS idx_employee_role ON Employee(RoleId);
CREATE INDEX IF NOT EXISTS idx_store_company ON Store(CompanyId);
CREATE INDEX IF NOT EXISTS idx_company_plan ON Company(SubscriptionPlanId);

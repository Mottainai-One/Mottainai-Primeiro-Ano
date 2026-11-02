-- Somente leitura. Lista formatos incompatíveis com Base64 AES do projeto.
-- Não imprime os dados pessoais nem modifica o banco.
WITH campos AS (
    SELECT 'Employee' AS tabela, EmployeeId AS id, 'Name' AS campo, Name AS valor
    FROM Employee
    UNION ALL SELECT 'Employee', EmployeeId, 'CPF', CPF FROM Employee WHERE DeletedAt IS NULL
    UNION ALL SELECT 'Employee', EmployeeId, 'Email', Email FROM Employee WHERE DeletedAt IS NULL
    UNION ALL SELECT 'Employee', EmployeeId, 'Phone', Phone FROM Employee WHERE DeletedAt IS NULL
    UNION ALL SELECT 'UserAccount', UserId, 'Email', Email FROM UserAccount WHERE DeletedAt IS NULL
    UNION ALL SELECT 'Company', CompanyId, 'Email', Email FROM Company WHERE DeletedAt IS NULL AND CNPJ <> '00000000000000'
    UNION ALL SELECT 'Company', CompanyId, 'Phone', Phone FROM Company WHERE DeletedAt IS NULL AND CNPJ <> '00000000000000'
    UNION ALL SELECT 'Store', StoreId, 'Email', Email FROM Store WHERE DeletedAt IS NULL AND CNPJ <> '00000000000001'
    UNION ALL SELECT 'Store', StoreId, 'Phone', Phone FROM Store WHERE DeletedAt IS NULL AND CNPJ <> '00000000000001'
)
SELECT tabela, id, campo, 'Formato incompatível; verificar origem do dado' AS problema
FROM campos
WHERE valor IS NOT NULL AND (valor !~ '^[A-Za-z0-9+/]+={0,2}$' OR length(valor) < 40 OR length(valor) % 4 <> 0)
ORDER BY tabela, id, campo;

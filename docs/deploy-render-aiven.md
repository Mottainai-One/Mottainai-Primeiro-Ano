# Deploy do Mottainai: Render + Aiven

Este guia prepara o backend Spring Boot 3.4.1 (Java 21, Gradle, WAR executável) para Render, sem incluir segredos no repositório.

## Backend (Render Web Service)

- Repository: `Mottainai-One/Mottainai-Primeiro-Ano`
- Branch: `main` (depois do merge da PR)
- Runtime: **Docker**
- Root Directory: `back-end/mottainai-crud`
- Dockerfile Path: `./Dockerfile` (relativo ao Root Directory)
- Instance: escolha conforme orçamento e limites disponíveis

O Dockerfile constrói o `bootWar` e inicia o WAR executável com Java 21. O Spring Boot já usa `server.port=${PORT:8080}`.

### Variáveis de ambiente (Render → Environment)

Configure os valores **manualmente** a partir do arquivo local `config/local.properties`, sem fazer commit ou compartilhar prints dos valores:

| Render KEY | Origem |
| --- | --- |
| `DB_URL` | `spring.datasource.url` |
| `DB_USERNAME` | `spring.datasource.username` |
| `DB_PASSWORD` | `spring.datasource.password` |
| `MOTTAINAI_AES_KEY` | `mottainai.crypto.key` |

Use `SPRING_PROFILES_ACTIVE=dev` somente se esse perfil for apropriado para o ambiente; antes de produção, revisar configurações e segurança. A URL JDBC da Aiven deve incluir os parâmetros SSL exigidos pelo serviço. Não substitua o esquema da Aiven nem habilite criação automática sem avaliar os dados existentes. `spring.jpa.hibernate.ddl-auto=validate` verifica se o esquema corresponde às entidades e pode impedir o boot se estiver divergente.

## Integração e validação

1. Confirme conectividade e SSL da Aiven a partir do Render (regras de rede/IP e credenciais).
2. Confira logs de build e startup; procure `Started ...` e eventuais erros de datasource/DDL.
3. Teste páginas públicas, autenticação e endpoints CRUD, incluindo permissões, persistência e criptografia.
4. O Static Site atual pode permanecer no ar. Se a intenção for servir o mesmo site pelo Spring Boot, planeje mudança de domínio/roteamento separadamente.
5. Verifique URLs absolutas, cookies, CORS/CSRF e autenticação antes de chamar a integração de concluída.
6. Não exponha endpoints administrativos ou dados reais até concluir testes de acesso.

**Atenção:** este PR prepara o deploy do backend, mas não configura o painel Render, não migra dados e não comprova a integração end-to-end.

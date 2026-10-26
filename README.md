# Mottainai-Primeiro-Ano

## Integração contínua

O [workflow de CI](.github/workflows/ci.yml) compila o backend Maven com Java 21,
executa JUnit com PostgreSQL temporário, exige o WAR e publica cobertura JaCoCo.
Também verifica estilo e defeitos Java, credenciais, dependências, workflows,
conteúdo da landing e metadados da PR.

Consulte [docs/CI.md](docs/CI.md) para comandos locais, configuração de banco,
escopo de cada check e limitações atuais.

# Mottainai Primeiro Ano

Repositório de materiais do primeiro ano do projeto Mottainai. A branch padrão reúne uma landing page estática, um módulo Java Servlet/JSP com JDBC e verificações de qualidade para ambos.

## Componentes

- front-end/landing: páginas HTML, CSS e recursos estáticos.
- back-end/jdbc/mottainai-jdbc: aplicação Java baseada em Servlet/JSP, empacotada como WAR.
- dados: diretório reservado para materiais de dados; atualmente contém apenas um placeholder.
- scripts/ci e tests/ci: validadores e testes de apoio aos checks de CI.

## Requisitos

- Java 21 para o módulo Maven.
- Node.js 24 e npm para instalar as ferramentas de validação da landing.
- Python 3 para os validadores e testes auxiliares.
- Docker com PostgreSQL 16 somente para executar localmente o teste de integração JDBC.

## Validação local

Na raiz do repositório:

    npm ci --ignore-scripts
    npm run lint:frontend
    python -m unittest discover -s tests/ci -v
    .\back-end\jdbc\mottainai-jdbc\mvnw.cmd -B -ntp -f back-end/jdbc/mottainai-jdbc/pom.xml clean verify

No Linux/macOS, use back-end/jdbc/mottainai-jdbc/mvnw no lugar do comando Windows acima. O teste de conexão JDBC requer DB_URL, DB_USER e DB_PASSWORD e um PostgreSQL de teste disponível; veja [docs/CI.md](docs/CI.md) para o exemplo de PostgreSQL temporário e os limites de cada verificação.

O workflow [.github/workflows/ci.yml](.github/workflows/ci.yml) executa validações de backend, landing, segurança e metadados de pull request. A CI não publica a aplicação em um servidor Servlet.

## Licença

Consulte [LICENSE](LICENSE).

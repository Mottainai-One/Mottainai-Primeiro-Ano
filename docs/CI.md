# Integração contínua

O workflow `.github/workflows/ci.yml` valida o projeto Maven de
`back-end/jdbc/mottainai-jdbc`. A base desta PR é `main`; o projeto Gradle da branch
`back-end` é separado e não é migrado por esta alteração.

| Check | Comportamento |
| --- | --- |
| Backend | Java 21 (Temurin), `mvn clean verify`, WAR obrigatório e publicação por 7 dias |
| JUnit | Surefire explícito; falha sem testes e verificação extra que rejeita todos os testes ignorados |
| PostgreSQL | Serviço PostgreSQL 16 temporário com healthcheck; teste JDBC executa `SELECT 1` |
| Checkstyle | Regras versionadas: imports explícitos, nomes, chaves, espaços, tabs e linhas de até 120 caracteres |
| SpotBugs | Análise no `verify`, esforço máximo e limiar Low; defeitos fazem o build falhar |
| JaCoCo | Gera HTML/XML e publica relatórios; nenhum percentual mínimo nesta etapa |
| Segredos | Gitleaks no histórico completo, saída redigida; verificação adicional de arquivos `.env` versionados |
| Dependências | OSV Scanner consulta vulnerabilidades diretas e transitivas do POM; Dependency Review valida mudanças nos PRs |
| Workflows | Actionlint valida todos os workflows; testes Python verificam os validadores auxiliares |
| Landing | HTMLHint, Stylelint, sintaxe JavaScript e referências locais HTML/CSS |
| PR | Título Conventional Commits e seções Description, Changes Made e Validation preenchidas |

Os checks de build, testes e segurança executam ferramentas reais. Os checkboxes
do template são apoio de revisão e não substituem os jobs.

## Banco e testes locais

`DataBaseConnection` aceita `DB_URL`, `DB_USER` e `DB_PASSWORD` do ambiente. Um
`.env` local é opcional; as variáveis do ambiente têm precedência. Valores ausentes
falham com uma mensagem de configuração, sem imprimir credenciais. A fábrica mantém `getInstance().connection()`, mas cada chamada agora retorna
uma conexão independente: o consumidor deve fechá-la com try-with-resources.
Isso corrige a exposição de estado mutável compartilhado apontada pelo SpotBugs;
fechar uma conexão não invalida as demais.

Exemplo em bash, usando banco descartável (nunca produção):

```bash
docker run --rm -d --name mottainai-ci -p 5432:5432 \
  -e POSTGRES_DB=mottainai_ci -e POSTGRES_USER=ci_user \
  -e POSTGRES_PASSWORD=ci_test_only postgres:16
# Aguarde pg_isready -U ci_user -d mottainai_ci dentro do container.
export DB_URL='jdbc:postgresql://localhost:5432/mottainai_ci?connectTimeout=5&socketTimeout=10'
export DB_USER=ci_user
export DB_PASSWORD=ci_test_only
mvn -B -ntp -f back-end/jdbc/mottainai-jdbc/pom.xml clean verify
python scripts/ci/check_backend.py
docker stop mottainai-ci
```

As credenciais acima são exclusivas do banco temporário de testes. O CI não usa
secrets de produção. Não adicione um `.env` ao Git; modelos `.env.example`,
`.env.sample` e `.env.template` são permitidos e também passam pelo Gitleaks.

Os testes atuais verificam a resposta HTML do servlet e a conexão/consulta JDBC.
JaCoCo ainda não impõe cobertura mínima: o limite deve ser definido com a equipe
quando os fluxos de negócio tiverem testes representativos. Não é realizado um
deploy em servidor Servlet nem um teste HTTP do WAR nesta etapa.

## Frontend

Use Node 24 e `npm ci --ignore-scripts`, seguido de `npm run lint:frontend`.
O lockfile fixa as ferramentas; elas não são dependências de produção da landing.
Arquivos HTML/CSS/JS vazios são placeholders e essa ausência aparece no resumo do
job. Arquivos com conteúdo passam a ser validados automaticamente. Referências
locais quebradas, fora da landing ou para arquivos vazios falham. URLs externas
não são acessadas. Fragmentos de âncora, JavaScript inline e caminhos construídos
dinamicamente não são avaliados por esse verificador.

## Dependências e proteção

OSV Scanner analisa o POM e resolve transitivas; o job falha em vulnerabilidades
reportadas ou erro da ferramenta. Dependency Review falha a partir da severidade
Low para novas dependências vulneráveis. São necessários acesso às bases públicas
e o dependency graph do GitHub habilitado. Não há `continue-on-error` nem fallback
que transforme falhas desses serviços em aprovação.

Durante a configuração, o dependency graph e os alertas de vulnerabilidades foram
habilitados no repositório para permitir a execução do Dependency Review.

Actions são fixadas por SHA e ferramentas baixadas por versão com verificação de
checksum. O token possui somente leitura; os jobs não publicam comentários na PR.
Um administrador pode exigir os sete jobs no ruleset de `main` para bloquear merge.
Esta alteração não modifica as regras de proteção do repositório.

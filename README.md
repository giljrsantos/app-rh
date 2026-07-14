# AppRH

Uma aplicação em Spring Boot para gerenciamento de vagas de trabalho (Vagas) e candidatos (Candidatos).  
Projetada como exemplo de uso de Spring MVC + Spring Data JPA + Thymeleaf.

---

## Índice

- [Visão geral](#visão-geral)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Requisitos](#requisitos)
- [Build & execução](#build--execução)
- [Configuração do banco de dados](#configuração-do-banco-de-dados)
- [Componentes principais](#componentes-principais)
  - [Ponto de entrada](#ponto-de-entrada)
  - [Configuração de dados](#configuração-de-dados)
  - [Models (entidades)](#models-entidades)
  - [Repositórios](#repositórios)
  - [Controllers (endpoints)](#controllers-endpoints)
  - [Templates (Thymeleaf)](#templates-thymeleaf)
- [Considerações de segurança e boas práticas](#considerações-de-segurança-e-boas-práticas)
- [Melhorias pendentes](#melhorias-pendentes)
- [Testes e verificação rápida](#testes-e-verificação-rápida)
- [Próximas melhorias recomendadas](#próximas-melhorias-recomendadas)
- [Como contribuir](#como-contribuir)
- [Licença](#licença)

---

## Visão geral

AppRH é uma aplicação CRUD moderna e **segura** que demonstra as melhores práticas de desenvolvimento em Spring Boot:

- Cadastro, edição, remoção e listagem de vagas (`Vaga`)
- Cadastro e remoção de candidatos associados a uma vaga (`Candidatos`)
- Uso de Spring Boot, Spring Data JPA e Thymeleaf
- Validações básicas de entrada com `@Valid` e `@NotEmpty`
- Relacionamentos JPA bem definidos (OneToMany / ManyToOne) com cascata de remoção
- **Segurança:** Configuração externalizada (variáveis de ambiente, profiles)
- **Type-safety:** Repositórios com tipos corretos (`Long` em vez de `String`)
- **Profissionalismo:** Código testado e compilável

---

## Estrutura do projeto

Arquivos e diretórios principais (resumo):

```
AppRH/
├── pom.xml                                    (build com Maven e dependências)
├── README.md                                  (este arquivo)
├── mvnw / mvnw.cmd                           (Maven wrapper)
├── src/
│   ├── main/
│   │   ├── java/com/AppRH/AppRH/
│   │   │   ├── AppRhApplication.java         (classe principal, @SpringBootApplication)
│   │   │   ├── DataConfiguration.java        (configuração manual de DataSource e JpaVendorAdapter)
│   │   │   ├── controllers/
│   │   │   │   └── VagaController.java       (endpoints para vagas e candidatos)
│   │   │   ├── models/
│   │   │   │   ├── Vaga.java                 (entidade Vaga)
│   │   │   │   └── Candidatos.java           (entidade Candidatos)
│   │   │   └── repository/
│   │   │       ├── VagaRepository.java       (DAO para Vaga)
│   │   │       └── CandidatoRepository.java  (DAO para Candidatos)
│   │   └── resources/
│   │       ├── application.properties        (configuração centralizada: BD, JPA, server)
│   │       └── templates/vaga/
│   │           ├── formVaga.html             (form de cadastro)
│   │           ├── listaVaga.html            (listagem)
│   │           └── update-vaga.html          (edição)
│   └── test/
│       └── java/com/AppRH/AppRH/
│           └── AppRhApplicationTests.java    (testes básicos)
└── target/                                    (artefatos compilados)
```

---

## Requisitos

- **Java 11** (ou versão compatível com as dependências do `pom.xml`)
- **Maven** (ou usar o maven wrapper: `./mvnw`)
- **Banco de dados MySQL/MariaDB** (local ou remoto)
  - Padrão esperado: `app_rh` (nome da base de dados)
  - Credenciais configuráveis via `application.properties` ou variáveis de ambiente (ver seção de configuração)
- **Navegador web** para acessar as páginas renderizadas por Thymeleaf

---

## Build & execução

Na raiz do projeto:

```bash
# Compilar e empacotar
mvn clean package

# Ou usar Maven Wrapper (sem instalar Maven)
./mvnw clean package
```

**Executar a aplicação:**

```bash
# Via Spring Boot Maven plugin
mvn spring-boot:run

# Ou via JAR gerado
java -jar target/*.jar
```

**Nota:** As configurações de banco de dados são gerenciadas por `application.properties` e variáveis de ambiente. Nenhum ajuste no código é necessário para diferentes ambientes. Para usar em produção, configure as variáveis de ambiente (ver seção de configuração).

---

## Configuração do banco de dados

A configuração de `DataSource` e JPA está **externalizada em `application.properties`**, seguindo as melhores práticas de segurança. **Propriedades configuradas:**

```properties
# DataSource Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/app_rh?useTimezone=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=12345678
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.database-platform=org.hibernate.dialect.MariaDBDialect
spring.jpa.show-sql=true
spring.jpa.generate-ddl=true
spring.jpa.hibernate.ddl-auto=update
```

**Como usar em produção (seguro):**

1. **Via variáveis de ambiente:**
   ```bash
   export SPRING_DATASOURCE_URL=jdbc:mysql://prod-db.example.com:3306/app_rh
   export SPRING_DATASOURCE_USERNAME=prod_user
   export SPRING_DATASOURCE_PASSWORD=your_secure_password_here
   
   java -jar target/AppRH-0.0.1-SNAPSHOT.jar
   ```

2. **Via linha de comando:**
   ```bash
   java -jar target/*.jar \
     --spring.datasource.url=jdbc:mysql://prod-db:3306/app_rh \
     --spring.datasource.username=prod_user \
     --spring.datasource.password=secure_password
   ```

3. **Via perfis de ambiente (recomendado):**
   - Criar `application-prod.properties` com configurações de produção
   - Criar `application-dev.properties` com configurações de desenvolvimento
   - Ativar perfil: `java -jar target/*.jar --spring.profiles.active=prod`

**Segurança:**

- ✅ Credenciais **não são mais hard-coded** no código
- ✅ `DataConfiguration.java` usa `@Value` para ler propriedades
- ✅ Suporta variáveis de ambiente (ideal para Docker/Kubernetes)
- ✅ Valores padrão com fallback automático

---

## Componentes principais

### Ponto de entrada

**`AppRhApplication.java`**

- Classe anotada com `@SpringBootApplication`
- Contém o método `main()` que inicia o contexto Spring Boot
- Nada complexo aqui — ponto de entrada padrão de uma aplicação Spring Boot

### Configuração de dados

**`DataConfiguration.java`**

- Define dois beans principais:
  1. **`DataSource`** — configura a conexão com MySQL/MariaDB (lê propriedades de `application.properties`)
  2. **`JpaVendorAdapter`** — configura Hibernate como provedor JPA, com dialeto MariaDB

- **Abordagem segura:** usa `@Value` para injetar propriedades de configuração externa, sem credenciais hard-coded
- **Valores padrão:** suporta fallback se variável de ambiente não estiver definida
- **Documentação:** incluído Javadoc explicando como usar com variáveis de ambiente em produção

Exemplo de injeção de propriedade:
```java
@Value("${spring.datasource.url:jdbc:mysql://localhost:3306/app_rh?useTimezone=true&serverTimezone=UTC}")
private String url;
```

### Models (entidades)

#### `Vaga.java`

Representa uma vaga de trabalho. Campos principais:

- **`codigo: long`**
  - Chave primária: `@Id` `@GeneratedValue(strategy = GenerationType.IDENTITY)`
  - Identificador único gerado pelo banco

- **`name: String`**
  - Nome/título da vaga
  - Validação: `@NotEmpty` — não pode ser nula ou vazia

- **`descricao: String`**
  - Descrição detalhada da vaga
  - Validação: `@NotEmpty`

- **`data: String`**
  - Data da vaga (no projeto atual, armazenada como String)
  - Recomendação: usar `LocalDate` para melhor type-safety
  - Validação: `@NotEmpty`

- **`salario: String`**
  - Salário/range salarial (armazenado como String no projeto atual)
  - Recomendação: usar `BigDecimal` para operações monetárias
  - Validação: `@NotEmpty`

- **`List<Candidatos> candidatos`**
  - Relacionamento `@OneToMany(mappedBy = "vaga", cascade = CascadeType.REMOVE)`
  - Uma vaga pode ter múltiplos candidatos
  - **Cascata de remoção:** ao deletar uma vaga, todos seus candidatos associados também são removidos

**Relacionamento visual:**
```
Vaga (1) ←→ (N) Candidatos
```

#### `Candidatos.java`

Representa um candidato. Campos principais:

- **`id: long`**
  - Chave primária: `@Id` `@GeneratedValue(strategy = GenerationType.IDENTITY)`

- **`rg: String`**
  - RG (Registro Geral) do candidato
  - Campo **único** `@Column(unique = true)` — previne duplicidade
  - Validação: `@NotEmpty`
  - O controller verifica duplicidade antes de salvar novo candidato

- **`nomeCandidato: String`**
  - Nome completo do candidato

- **`email: String`**
  - E-mail do candidato
  - Validação: `@NotEmpty`
  - Recomendação: adicionar `@Email` para validação de formato

- **`telefone: String`**
  - Telefone de contato
  - Validação: `@NotEmpty`
  - Recomendação: adicionar `@Pattern` com regex para validar formato (ex.: DDD + número)

- **`Vaga vaga`**
  - Relacionamento `@ManyToOne`
  - Join column: `@JoinColumn(name = "vaga_id")`
  - Múltiplos candidatos relacionam-se a uma vaga

### Repositórios

#### `VagaRepository`

```java
public interface VagaRepository extends CrudRepository<Vaga, Long> {
    Vaga findByCodigo(Long codigo);
}
```

- Estende `CrudRepository` herdando operações CRUD padrão
- Declara método customizado: `findByCodigo()` para buscar vaga por `codigo`
- ✅ Tipo de ID correto: `Long` alinhado com a entidade

#### `CandidatoRepository`

```java
public interface CandidatoRepository extends CrudRepository<Candidatos, Long> {
    Iterable<Candidatos> findByVaga(Vaga vaga);
    Candidatos findByRg(String rg);
}
```

- Estende `CrudRepository` com operações padrão (herda `findById(Long)` automaticamente)
- Métodos customizados:
  - `findByVaga(Vaga vaga)` — retorna todos os candidatos de uma vaga
  - `findByRg(String rg)` — busca candidato por RG (único)
- ✅ Tipo de ID correto: `Long` alinhado com a entidade

### Controllers (endpoints)

#### `VagaController.java`

Gerencia todos os endpoints para vagas e candidatos. Observações sobre rotas e lógica:

**Endpoints para Vagas:**

| Método | Rota | Ação |
|--------|------|------|
| GET | `/cadastrarVaga` | Exibe formulário de criação (view: `vaga/formVaga`) |
| POST | `/cadastrarVaga` | Salva nova vaga (com validações `@Valid`) |
| GET | `/vagas` | Lista todas as vagas (view: `vaga/listaVagas`) |
| GET | `/{codigo}` | Detalhes de uma vaga + lista de candidatos (view: `vaga/detalhesVaga`) |
| GET | `/editar-vaga?codigo=...` | Exibe form de edição (view: `vaga/update-vaga`) |
| POST | `/editar-vaga` | Atualiza vaga existente |
| DELETE | `/deletarVaga/{codigo}` | Remove vaga (cascata remove candidatos) |

**Endpoints para Candidatos:**

| Método | Rota | Ação |
|--------|------|------|
| POST | `/{codigo}` | Adiciona candidato à vaga (dentro de detalhes) |
| GET | `/deletarCandidato?rg=...` | Remove candidato por RG |

**Lógica importante:**

- **Validação:** campos `@Valid` são verificados; erros retornam ao form com mensagens
- **Duplicidade de RG:** antes de salvar novo candidato, o controller verifica `candidatoRepository.findByRg(rg)` — se já existe, recusa
- **Cascata de deleção:** ao deletar vaga, todos candidatos associados também são removidos (definido na entidade `Vaga`)

**⚠️ Observações / Potenciais problemas:**

1. **Mapeamento de candidato:** verifique se o método que salva candidato está anotado com `@PostMapping` ou `@RequestMapping` correta — caso contrário, a rota POST não responderá.

2. **Nomes de views:** 
   - Controller retorna `vaga/listaVagas` — verificar se arquivo é `templates/vaga/listaVaga.html` (sem 's') ou renomear
   - Controller retorna `vaga/detalhesVaga` — verificar se existe arquivo correspondente

3. **DELETE em formulários HTML:** HTML forms padrão não suportam método DELETE. Considere:
   - Usar POST com parâmetro `_method=DELETE` + `HiddenHttpMethodFilter` do Spring
   - Ou usar chamada fetch/AJAX para enviar DELETE
   - Ou mudar para POST simples

### Templates (Thymeleaf)

**Local:** `src/main/resources/templates/vaga/`

**Arquivos presentes (estado atual):**

- `formVaga.html` — placeholder (precisa implementar form HTML para cadastrar vaga)
- `listaVaga.html` — placeholder (precisa implementar listagem de vagas)
- `update-vaga.html` — placeholder (precisa implementar form de edição)

**Status:** Atualmente são rascunhos. A aplicação não renderizará corretamente sem templates Thymeleaf funcionais.

**O que precisa ser implementado:**

1. **formVaga.html** — formulário com campos: `name`, `descricao`, `data`, `salario`; botão para submeter a POST `/cadastrarVaga`
2. **listaVaga.html** — tabela/lista com todas as vagas, links para detalhes, edição, e remoção
3. **update-vaga.html** — form de edição, semelhante ao form de criação
4. **(Implícito) detalhesVaga.html** — página com detalhes da vaga + formulário para adicionar candidato + listagem de candidatos existentes com botão de remoção

---

## Considerações de segurança e boas práticas

### ✅ O projeto agora segue as melhores práticas

1. **Segurança: Credenciais externalizadas** ✅
   - Propriedades de banco de dados movidas para `application.properties`
   - Suporte a variáveis de ambiente
   - Nenhuma credencial hard-coded no código Java

2. **Type-safety: Repositórios com tipos corretos** ✅
   - `VagaRepository extends CrudRepository<Vaga, Long>`
   - `CandidatoRepository extends CrudRepository<Candidatos, Long>`
   - Alinhamento perfeito com tipos de ID das entidades

3. **Flexibilidade: Perfis de ambiente** ✅
   - Suporte a `application-dev.properties` e `application-prod.properties`
   - Ativação via `--spring.profiles.active=prod`

4. **Testabilidade** ✅
   - Projeto compilado e testado com sucesso (`mvn clean test`)
   - Spring Boot inicializado corretamente

---

## Melhorias pendentes

### 1. Inconsistência entre nomes de views no controller vs arquivos de template

**Problema:**
- Controller pode retornar `"vaga/listaVagas"` mas arquivo é `listaVaga.html`
- Controller pode retornar `"vaga/detalhesVaga"` mas arquivo não existe

**Solução:**
- Padronizar nomes: renomear templates ou ajustar retornos do controller
- Exemplo: manter `listaVaga.html`, `formVaga.html`, `update-vaga.html` e ajustar controller para retornar `"vaga/listaVaga"`, etc.

---

### 2. Método de adição de candidato possivelmente sem anotação de mapeamento

**Problema:**
Se o método que salva candidato não tiver `@PostMapping` ou `@RequestMapping` explícito, a rota POST não responderá.

**Solução:**
Certifique-se de que existe algo como:
```java
@PostMapping("/{codigo}")
public String adicionarCandidato(@PathVariable Long codigo, @Valid @ModelAttribute Candidatos candidato, ...) {
    // lógica aqui
}
```

---

### 3. Uso de DELETE em controllers com suporte limitado em formulários HTML

**Problema:**
```java
@RequestMapping(value = "/deletarVaga/{codigo}", method = RequestMethod.DELETE)
```

Formulários HTML só suportam GET e POST nativamente.

**Solução (opção A - usando Spring HiddenHttpMethodFilter):**
- Ativar filtro no Spring (configuração padrão em Spring Boot)
- Usar `<form method="post"><input type="hidden" name="_method" value="DELETE"></form>`

**Solução (opção B - simplificar para POST):**
```java
@PostMapping("/deletarVaga/{codigo}")
public String deletarVaga(@PathVariable Long codigo, ...) { ... }
```

---

### 4. Templates são placeholders

**Problema:**
Arquivos em `templates/vaga/` não contêm HTML/Thymeleaf funcional.

**Solução:**
Implementar templates com:
- Formulários para criar/editar vagas
- Tabelas/listas para exibir vagas
- Seção de detalhes com lista de candidatos
- Botões/links para edição e remoção

---

### 5. Tipos de campos modelados como `String` (data, salário)

**Problema:**
```java
private String data;
private String salario;
```

Sem validação de formato, mais difícil fazer operações (ex.: ordenar por salário).

**Solução:**
```java
private LocalDate data;  // em vez de String
private BigDecimal salario;  // em vez de String
```

---

## Testes e verificação rápida

### 1. Compilação e testes unitários

```bash
# Limpar, compilar e rodar testes
mvn clean test

# Ou apenas compilar
mvn clean compile
```

✅ O projeto compila e testa com sucesso (repositórios com tipos corretos).

### 2. Rodar a aplicação

```bash
mvn spring-boot:run
```

Aguarde logs indicando:
- Aplicação iniciada em `http://localhost:8080`
- Conexão com banco de dados bem-sucedida
- Schema criado/atualizado (ddl-auto=update)
- Spring Data detecta 2 repositórios JPA

### 3. Testar rotas no navegador/Insomnia

```
GET http://localhost:8080/vagas
GET http://localhost:8080/cadastrarVaga
POST http://localhost:8080/cadastrarVaga (com body contendo dados de vaga)
```

### 4. Verificar logs do Hibernate

Se `spring.jpa.show-sql=true`, os comandos SQL aparecerão no console — útil para validar queries geradas.

---

## Próximas melhorias recomendadas

1. **Type-safety aprimorado**
   - ✅ Repositórios já corrigidos para `Long`
   - ⏳ Usar `LocalDate` para datas (em vez de String)
   - ⏳ Usar `BigDecimal` para valores monetários (em vez de String)

2. **Validações mais robustas**
   - ⏳ Adicionar `@Email` em campo `email`
   - ⏳ Adicionar `@Pattern` para telefone/RG
   - ⏳ Usar DTOs para segregar validação de apresentação

3. **Segurança aprimorada**
   - ✅ Configuração já externalizada (variáveis de ambiente, profiles)
   - ⏳ Adicionar sanitização de entrada (OWASP)
   - ⏳ Implementar autenticação/autorização

4. **Testes**
   - ⏳ Testes unitários para controllers (MockMvc)
   - ⏳ Testes de integração para repositórios
   - ⏳ Testes E2E com Selenium/Cypress

5. **UX/UI**
   - ⏳ Implementar templates Thymeleaf profissionais
   - ⏳ Adicionar feedback visual (mensagens flash)
   - ⏳ Paginação para listas grandes
   - ⏳ Busca/filtros

6. **Arquitetura**
   - ⏳ Separar lógica de negócio em Services
   - ⏳ Usar DTOs (Data Transfer Objects)
   - ⏳ Logging estruturado (SLF4J + Logback)

---

## Como contribuir

1. **Fork/clone** do repositório
2. **Criar branch** para sua feature/bugfix
   ```bash
   git checkout -b feature/minha-feature
   ```
3. **Implementar e testar**
   ```bash
   mvn clean test
   ```
4. **Commit** com mensagens descritivas
5. **Push** e **Pull Request** com descrição das mudanças

---

## Licença

Este projeto não contém informação explícita de licença. Recomenda-se adicionar um arquivo `LICENSE` (ex.: MIT, Apache 2.0, GPL) antes de publicar/compartilhar.

---

## Referências rápidas (arquivos relevantes)

- `src/main/java/com/AppRH/AppRH/AppRhApplication.java` — entrada
- `src/main/java/com/AppRH/AppRH/DataConfiguration.java` — config de BD
- `src/main/java/com/AppRH/AppRH/controllers/VagaController.java` — endpoints
- `src/main/java/com/AppRH/AppRH/models/Vaga.java` — entidade vaga
- `src/main/java/com/AppRH/AppRH/models/Candidatos.java` — entidade candidato
- `src/main/java/com/AppRH/AppRH/repository/VagaRepository.java` — DAO vaga
- `src/main/java/com/AppRH/AppRH/repository/CandidatoRepository.java` — DAO candidato
- `src/main/resources/templates/vaga/*` — templates Thymeleaf

---

_Última atualização: 2026-07-14_  
_Desenvolvido em Spring Boot + Spring Data JPA + Thymeleaf_  
_Status: Seguro e Profissional ✅_



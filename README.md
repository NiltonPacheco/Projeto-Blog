# Projeto Blog Acelera Maker

Este é o backend do projeto de um blog pessoal desenvolvido como parte do programa Acelera Maker.

## Visão Geral

O projeto consiste em uma API RESTful construída com Spring Boot, fornecendo funcionalidades para gerenciar usuários, temas e postagens de um blog. Ele inclui autenticação e autorização baseadas em JWT para proteger os endpoints.

## Funcionalidades Principais

* **Gerenciamento de Usuários:**
    * Cadastro de novos usuários.
    * Autenticação de usuários (login) gerando um token JWT.
    * Atualização de informações do usuário.
    * Deleção de usuários (requer autorização).
    * Busca de usuários por ID.
* **Gerenciamento de Temas:**
    * Listagem de todos os temas.
    * Busca de temas por ID.
    * Busca de temas por descrição (parcial ou completa).
    * Criação de novos temas (requer autorização).
    * Atualização de temas existentes (requer autorização).
    * Deleção de temas (requer autorização, somente se não houver postagens associadas).
* **Gerenciamento de Postagens:**
    * Listagem de todas as postagens.
    * Busca de postagens por ID.
    * Busca de postagens por título (parcial ou completa).
    * Busca de postagens por tema.
    * Busca de postagens por usuário.
    * Criação de novas postagens (requer autenticação).
    * Atualização de postagens existentes (requer autenticação do autor).
    * Deleção de postagens (requer autenticação do autor).
* **Segurança:**
    * Autenticação de usuários via JWT (JSON Web Tokens).
    * Autorização baseada em roles (por exemplo, ADMIN para operações administrativas).
    * Criptografia de senhas de usuários.
* **Documentação da API:**
    * Documentação interativa da API disponível através do Swagger/OpenAPI.

## Tecnologias Utilizadas

* **Java:** Linguagem de programação principal.
* **Spring Boot:** Framework para desenvolvimento rápido de aplicações Java.
* **Spring Data JPA:** Para persistência e acesso a dados com JPA (Java Persistence API).
* **MySQL:** Banco de dados relacional utilizado para armazenar os dados da aplicação.
* **Spring Security:** Para autenticação e autorização.
* **JWT (JSON Web Tokens):** Para implementação da autenticação baseada em token.
* **Lombok:** Para reduzir o boilerplate de código Java.
* **SpringDoc OpenAPI:** Para geração automática da documentação da API (Swagger UI).
* **Maven:** Ferramenta de gerenciamento de dependências e build.
* **JUnit:** Framework para testes unitários.

## Pré-requisitos

* **Java Development Kit (JDK):** Versão 17 ou superior.
* **Maven:** Versão 3.x ou superior.
* **MySQL:** Servidor MySQL instalado e em execução.

## Configuração

1.  **Clone o repositório:**
    ```bash
    git clone [https://docs.github.com/articles/referencing-and-citing-content](https://docs.github.com/articles/referencing-and-citing-content)
    cd [nome do seu repositório]
    ```

2.  **Configure o banco de dados:**
    * Crie um banco de dados MySQL para o projeto (você pode usar o nome `demo` ou outro de sua preferência).
    * Abra o arquivo `src/main/resources/application.properties` e configure as propriedades de acesso ao banco de dados:
        ```properties
        spring.datasource.url=jdbc:mysql://localhost:3306/SEU_BANCO_DE_DADOS?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=America/Sao_Paulo
        spring.datasource.username=SEU_USUARIO_MYSQL
        spring.datasource.password=SUA_SENHA_MYSQL
        spring.jpa.hibernate.ddl-auto=update
        ```
        Substitua `SEU_BANCO_DE_DADOS`, `SEU_USUARIO_MYSQL` e `SUA_SENHA_MYSQL` pelas suas configurações. A propriedade `spring.jpa.hibernate.ddl-auto=update` fará com que o Hibernate atualize o schema do banco de dados automaticamente com base nas suas entidades.

3.  **Execute a aplicação:**
    * Na raiz do projeto, execute o seguinte comando no terminal:
        ```bash
        mvn spring-boot:run
        ```

## Acesso à API

A API estará disponível em `http://localhost:8080`.

* **Documentação Swagger UI:** Acesse `http://localhost:8080/swagger-ui/index.html` para visualizar e interagir com a documentação da API.

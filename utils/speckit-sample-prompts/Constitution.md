<!-- Bloco extremamente importante de ser **reescrito** em novos projetos! -->
# Descrição do Projeto

 - Título: Sample Project
 - Descrição: Este projeto é um exemplo básico de como consumir a GEMS SDK e deve servir como um repositório base para futuros projetos pessoais.

# Stacks envolvidas

 - Backend
    - Java 21
    - Spring Boot
    - Dependências da GEMS SDK
 - Frontend
    - Angular
    - Dependêcias da GEMS SDK
 - Banco de Dados
    - PostgreSQL
 - Segurança
    - Keycloak
 - Armazenamento de arquivos
    - AWS S3 (Em ambientes locais, estou utilizando o localstack para virtualizar o recurso)
 - DevOps
    - Docker
    - GitHub Actions
    
<!-- Este bloco é importante uma leitura e a substituição da palavra sample pelo acrônimo do projeto, mas pode ser reutilizado livremente -->

# Contratos arquiteturais

## Backend

- O projeto possui os seguintes módulos maven:
    - model
        - Pacote raíz: br.com.gems.sample.model;
        - Módulo responsável pelo mapeamento das entidades do domínio. Responsável por definir os tipos de dados que trafegam no sistema.
        - Módulo público e acessível aos demais.
        - Responsável por implementar:
            - Entities;
            - DTOs;
            - Enums;
            - Events;
                - Events são os DTOs que invocam um Listener no módulo de eventos (event).
                - Eventos são utilizados para orquestrar fluxos que dependem de eventos.
    - security
        - Pacote raíz: br.com.gems.sample.security;
        - Módulo responsável por conter a segurança do sistema.
        - Integrado diretamente com o keycloak.
        - Módulo público e acessível aos demais.
    - core
        - Pacote raíz: br.com.gems.sample.core;
        - Módulo responsável por conter a regra de negócio do sistema.
        - Módulo que gerencia a lógica do negócio, regras de negócio, fluxos, etc.
        - Módulo privado e não deve ser acessível diretamente. Apenas módulos externos a ele podem acessar os recursos do core, caso necessitem.
        - Os pacotes devem ser divididos por domínios, tal como os exemplos:
            - br.com.gems.sample.core.matricula;
            - br.com.gems.sample.core.aluno;
            - br.com.gems.sample.core.professor;
        - Implementa interface por necessidade, e somente quando é de fato utilizado. Quando não há utilização, não necessita implementar interfaces.
    - event
        - Pacote raíz: br.com.gems.sample.event;
        - Módulo responsável por conter os eventos do sistema.
        - Fluxos que integram diversos domínios devem ter seus eventos implementados aqui.
        - Responsável por implementar:
            - Handlers;
            - Listeners;
        - Pode visualizar e invocar métodos do core, quando necessário, através das interfaces disponibilizadas.

## Frontend 

- Componentes são standalone por padrão (não explicitar o standalone: true);
- Componentes devem ter suas partes .ts, .css e .html em arquivos separados (não usar inline);
- A aplicação deve ser dividida em módulos, cada módulo gerencia suas rotas, componentes, stores, models, services e outros recursos necessários;
- A estrutura de pastas deve respeita o padrão

```
src/app/ 
├── commons/                         # Recursos que potencialmente podem ser elevados para a biblioteca GEMS SDK
│   ├── components/
│   ├── layout/
│   ├── pipes/
│   └── services/
│   └── ...
├── features/
│   └── {modulo}/
│       ├── models/
│       │   ├── {modulo}.model.ts               # Interfaces TypeScript da entidade
│       │   └── {modulo}-filter.model.ts        # Interface de filtro (campos opcionais)
│       ├── services/
│       │   └── {modulo}.store.ts               # Estende GemsBaseStore (gems-sdk)
│       ├── list/
│       │   ├── {modulo}-list.component.ts
│       │   ├── {modulo}-list.component.html
│       │   └── {modulo}-list.component.css     # Geralmente vazio — estilos globais
│       └── form/
│           ├── {modulo}-form.component.ts
│           ├── {modulo}-form.component.html
│           └── {modulo}-form.component.css     # Geralmente vazio — estilos globais
```

## Keycloak

- Este é um serviço que é inegociável e não precisamos nos preocupar em substituir, apenas mante-lo.
- Deve ser utilizado a integração com keycloak-angular.
- A autenticação, com tematização de login e etc deve ser feita pelo keycloak.

<!-- Bloco extremamente importante de ser **reescrito** em novos projetos! -->

# Regras de negócio

## O objetivo do projeto

Este projeto em específico tem o objetivo de ser um exemplo de implementação da GEMS SDK, por isso, ele não possui uma regra de negócio específica. A ideia é apenas disponibilizar um cadastro do usuário, com suas informações gerais e de endereço. 

## Público alvo

Usuário comum. Projeto de portifólio.

## Requisitos funcionais

 - Login
 - Cadastro do usuário
 - Edição do usuário
 - Exclusão do usuário

## Requisitos não funcionais

 - O projeto deve ser responsivo.
 - O projeto deve ser acessível.
 - O projeto deve ser seguro.

## Plataformas que ele deverá rodar

Apesar de ser um serviço web, este projeto deve ser pensado como um PWA, logo ele deve ser capaz de rodar em:
 - Dispositivos mobile;
 - Navegador web desktop;

<!-- Bloco comum para todos os projetos -->

# Manutenção

 - Toda tarefa deve ser implementada em uma branch seguindo o padrão **feature/breve-descricao**. Em caso de correção de bug, seguir o padrão **bugfix/breve-descricao**.
 - A branch de deve ser desenvolvida a partir da branch **main**. 
 - Ao concluir uma feature, deve ser aberto um Pull Request para a **main** com o seguinte padrão:
    - Título: Descrição breve da tarefa
    - Corpo da PR:
        - Descrição do problema que está sendo resolvido;
        - Recursos criados;
        - Recursos removidos ou depreciados;
        - Tarefas com pendências;


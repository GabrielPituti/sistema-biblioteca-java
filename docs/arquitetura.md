# Arquitetura do Sistema

## Visao geral

```
Frontend (React + Vite)
        |
        | HTTP/JSON
        v
Controllers  ---->  Services  ---->  Repositories  ---->  PostgreSQL
                       |
                       | HTTP
                       v
                 Google Books API
```

A aplicacao segue a separacao classica em camadas. Os controllers so recebem a requisicao e delegam pro service. A logica de negocio fica toda no service. Os repositories sao interfaces do Spring Data — nao tem SQL manual na maioria dos casos, so query derivada por nome de metodo.

## Modelo de dados

```
usuarios
  id, nome, email, data_cadastro, telefone

livros
  id, titulo, autor, isbn, data_publicacao, categoria

emprestimos
  id, usuario_id (FK), livro_id (FK), data_emprestimo, data_devolucao, status
```

Relacionamentos: um usuario pode ter varios emprestimos. Um livro pode aparecer em varios emprestimos, mas so um pode estar ATIVO ao mesmo tempo.

## Fluxo do emprestimo

1. Frontend manda `POST /api/emprestimos` com `livroId` e `usuarioId`
2. Service verifica se o livro ja tem emprestimo ATIVO via `existsByLivroIdAndStatus`
3. Se sim, lanca excecao — retorna 400
4. Se nao, busca o livro e o usuario, cria o emprestimo com status ATIVO e data de hoje
5. Retorna o emprestimo criado com 201

## Fluxo da recomendacao

1. Frontend manda `GET /api/emprestimos/recomendacoes/{usuarioId}`
2. Service busca todo o historico de emprestimos do usuario
3. Extrai as categorias dos livros que ele ja leu
4. Se nao tiver historico, retorna lista vazia
5. Busca outros livros dessas categorias que o usuario ainda nao pegou emprestado
6. Retorna a lista

## Infraestrutura local

Banco sobe via Docker: `docker-compose up -d`

O `ddl-auto: update` do Hibernate cria as tabelas automaticamente no primeiro start. Nao e necessario rodar nenhum script SQL manualmente.
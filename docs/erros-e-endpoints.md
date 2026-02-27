# Erros da API

Todos os erros seguem o mesmo formato de resposta, padronizado pelo `GlobalExceptionHandler`:

```json
{
  "timestamp": "2026-02-27T10:30:00",
  "status": 400,
  "message": "descricao do problema"
}
```

Erros de validacao de campo tem estrutura diferente — ao inves de `message`, vem um objeto `errors` com o nome de cada campo invalido.

---

**Usuarios**

| Situacao | HTTP | Mensagem |
|----------|------|----------|
| Campo obrigatorio ausente | 400 | `{ errors: { nome: "nao deve estar em branco" } }` |
| Email invalido | 400 | `{ errors: { email: "deve ser um endereco valido" } }` |
| Data de cadastro futura | 400 | `{ errors: { dataCadastro: "deve ser data presente ou passada" } }` |
| ID nao encontrado | 400 | `Usuario nao encontrado` |

**Livros**

| Situacao | HTTP | Mensagem |
|----------|------|----------|
| Campo obrigatorio ausente | 400 | `{ errors: { titulo: "nao deve estar em branco" } }` |
| ID nao encontrado | 400 | `Obra nao localizada no acervo` |

**Emprestimos**

| Situacao | HTTP | Mensagem |
|----------|------|----------|
| Livro ja emprestado | 400 | `Este livro ja possui um emprestimo ativo.` |
| ID de emprestimo nao encontrado | 400 | `Registro de emprestimo nao localizado.` |
| Devolucao de emprestimo ja devolvido | 400 | `Este emprestimo ja consta como devolvido no sistema.` |
| Violacao de constraint no banco | 409 | `Conflito de integridade: A operacao viola uma regra de unicidade.` |

**Google Books**

A busca absorve erros da API externa e retorna lista vazia para nao interromper o fluxo do usuario. O erro completo e logado internamente em nivel ERROR. Isso inclui o caso de quota esgotada (429 do Google).

**Geral**

Qualquer excecao nao tratada retorna 500 com mensagem generica — o stacktrace nunca e exposto ao cliente.

---

**Endpoints disponiveis**

```
GET    /api/usuarios
GET    /api/usuarios/{id}
POST   /api/usuarios
PUT    /api/usuarios/{id}
DELETE /api/usuarios/{id}

GET    /api/livros
POST   /api/livros
GET    /api/livros/{id}
PUT    /api/livros/{id}
DELETE /api/livros/{id}

GET    /api/emprestimos
POST   /api/emprestimos
PUT    /api/emprestimos/{id}/devolver
GET    /api/emprestimos/recomendacoes/{usuarioId}

GET    /api/google-books/search?titulo={query}

GET    /swagger-ui.html
GET    /actuator/health
```
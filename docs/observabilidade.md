# Observabilidade e Operacao

## Health check

O Spring Actuator expoe `/actuator/health` com status do banco e da aplicacao.

```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" }
  }
}
```

Util pra confirmar que a aplicacao subiu e conseguiu conectar no banco.

## Logs

O sistema loga duas coisas relevantes em tempo de execucao:

- Cada emprestimo iniciado: `INFO - Iniciando locacao: livroId=X, usuarioId=Y`
- Falhas na integracao com Google Books: `ERROR - Falha na integracao externa: {mensagem}`

As queries SQL ficam visiveis em DEBUG pelo Hibernate — da pra ver exatamente o que ta sendo executado no banco durante o desenvolvimento.

## Variaveis de ambiente

```
GOOGLE_BOOKS_API_KEY  — chave da API do Google Books (opcional)
```

Sem a chave a aplicacao funciona normalmente, mas a busca fica sujeita ao limite anonimo do Google (1000 req/dia por projeto).

## Notas sobre escala

A aplicacao e stateless — nao guarda estado em memoria entre requisicoes. Da pra rodar multiplas instancias sem configuracao adicional, desde que o unique index do banco esteja criado antes.

O unico ponto de atencao em volume maior seria o `findAll()` nos endpoints de listagem — adicionaria paginacao se o volume justificasse.
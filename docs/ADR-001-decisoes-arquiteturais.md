# ADR-001 - Decisoes Arquiteturais

Algumas decisoes que tomei durante o desenvolvimento e o raciocinio por tras delas.

---

**DTOs como Records**

Usei Records do Java 21 para todos os DTOs. O principal motivo foi imutabilidade — uma vez criado o objeto, ninguem consegue mutar os dados no meio do caminho entre as camadas. Tambem reduz bastante o codigo comparado a uma classe com getters, setters e construtor na mao.

**Protecao contra Race Condition no emprestimo**

A regra de "um emprestimo ativo por livro" eu protegi em dois niveis. No service uso `existsByLivroIdAndStatus` que gera um SELECT EXISTS direto, sem trazer a tabela inteira pra memoria como o `findAll().stream()` faria. No banco coloquei um unique index parcial `WHERE status = 'ATIVO'` como segunda barreira — se duas requisicoes simultaneas passarem pela verificacao Java, o banco rejeita a segunda com conflito de constraint, que o GlobalExceptionHandler trata como 409.

**Mapper manual**

Coloquei um metodo estatico `fromEntity()` em cada DTO em vez de usar MapStruct ou ModelMapper. Pra 3 entidades simples nao faz sentido adicionar essas dependencias. O mapeamento manual e direto e facil de rastrear.

**Google Books API Key**

A chave e injetada por variavel de ambiente com `@Value("${google.books.api.key:}")`. O fallback vazio garante que a aplicacao sobe normalmente mesmo sem a chave configurada, util pra rodar local sem precisar criar projeto no Google Cloud. No CI a chave vem de `secrets.GOOGLE_BOOKS_API_KEY`.

**Tailwind v4**

O projeto usa Tailwind v4, que mudou em relacao a v3. O import virou um unico `@import "tailwindcss"` e o plugin do PostCSS e o `@tailwindcss/postcss`. Nao precisa mais do autoprefixer porque o Lightning CSS cuida disso internamente.
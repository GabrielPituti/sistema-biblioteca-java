Esta branch compreende a etapa inicial de definicao da estrutura de dados e regras de integridade do sistema.

Entregas Tecnicas:

Mapeamento de Entidades: Criacao das classes Usuario, Livro e Emprestimo utilizando Jakarta Persistence (JPA).

Relacionamentos: Implementacao de associacoes ManyToOne para vincular movimentacoes de acervo a usuarios e titulos especificos.

Ciclo de Vida: Definicao do Enum StatusEmprestimo (ATIVO, DEVOLVIDO) para controle de estado das locacoes.

Validacoes de Banco: Configuracao de constraints de unicidade (Unique) para campos criticos como Email (Usuario) e ISBN (Livro).

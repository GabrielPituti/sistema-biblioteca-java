Sistema de Gerenciamento de Biblioteca

Este projeto consiste em uma solução para gestão de acervo bibliotecário e controle de usuários, desenvolvida como parte do processo seletivo da Elotech. A aplicação engloba desde operações fundamentais de cadastro até um motor de recomendação baseado no histórico de locações.

Arquitetura e Tecnologias

O ecossistema foi construído utilizando as seguintes tecnologias:

Backend: Java 21 com Spring Boot 3.4.

Persistência: Spring Data JPA e Hibernate.

Banco de Dados: PostgreSQL 15 rodando em container Docker.

Frontend: React com Vite e Tailwind CSS.

Testes: JUnit 5 e Mockito para validação de regras de negócio.

Integração: Google Books API para busca externa de títulos.

Decisões Técnicas

Durante o desenvolvimento, priorizei a manutenibilidade e a segurança dos dados:

Imutabilidade com Records: Todos os DTOs foram implementados utilizando Java Records, garantindo que os dados que trafegam entre as camadas não sofram mutações inesperadas.

Processamento com Streams: O algoritmo de recomendação e as filtragens de negócio utilizam a API de Streams do Java 21, o que permite um código mais declarativo e expressivo.

Encapsulamento de Domínio: As entidades JPA são protegidas por uma camada de serviço robusta, evitando que a lógica de banco de dados vaze para os controladores REST.

Tratamento de Exceções: Foi implementado um manipulador global (GlobalExceptionHandler) para interceptar erros de negócio e validações de campo, retornando respostas padronizadas ao frontend.

CORS e Segurança: Os controllers estão configurados para permitir a comunicação segura com o frontend React.

Regras de Negócio Implementadas

Disponibilidade de Livro: O sistema impede a criação de um novo empréstimo caso o livro já possua um registro com status 'ATIVO'.

Histórico de Leitura: O motor de sugestões analisa as categorias dos livros já devolvidos ou em posse do usuário para recomendar títulos semelhantes do acervo que ele ainda não leu.

Busca Externa: Através da integração com o Google Books, o administrador pode pesquisar títulos por palavra-chave e importá-los diretamente para a base de dados local.

Como Executar

Pré-requisitos

Docker e Docker Compose.

JDK 21.

Node.js instalado.

Passo 1: Banco de Dados

Na raiz do projeto, suba o container do banco:

docker-compose up -d


Passo 2: Backend Java

Execute a aplicação via sua IDE ou terminal:

./mvnw spring-boot:run


Passo 3: Frontend React

Acesse a pasta frontend, instale as dependências e inicie o servidor:

npm install
npm run dev


Desenvolvido por Gabriel Pituti.

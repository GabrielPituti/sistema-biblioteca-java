Sistema de Gestao de Biblioteca Elotech

Solucao desenvolvida para o gerenciamento de acervo literario, controle de membros e processamento de emprestimos com motor de recomendacao inteligente.

Arquitetura e Tecnologias

O projeto utiliza uma pilha tecnologica moderna focada em performance e seguranca:

Backend: Java 21 com Spring Boot 3.4.2.

Persistencia: Spring Data JPA e PostgreSQL.

Frontend: React com Vite e Tailwind CSS v4.

Observabilidade: Spring Actuator e Documentacao Swagger OpenAPI.

Testes: JUnit 5 e Mockito para validacao de regras de negocio.

Decisoes Tecnicas

Durante o desenvolvimento, foram aplicadas as seguintes praticas de engenharia:

Protecao contra Race Conditions: A validacao de disponibilidade de livros e realizada em nivel de banco de dados e camada de servico, garantindo que um exemplar possua apenas um emprestimo ativo.

Otimizacao de Consultas: O motor de recomendacao utiliza queries especificas para filtragem de categorias, evitando o carregamento de grandes volumes de dados para a memoria da aplicacao.

Padronizacao de Contratos: Implementacao de GlobalExceptionHandler para tratamento unificado de excecoes e retorno de mensagens amigaveis em formato JSON.

Resiliencia: Integracao com a API do Google Books com tratamento de limites de quota e erros de comunicacao assincrona no frontend.

Instrucoes de Execucao

Infraestrutura

Certifique-se de que o Docker esteja em execucao e inicie o banco de dados:
docker-compose up -d

Backend

Execute a aplicacao utilizando o Maven Wrapper:
./mvnw spring-boot:run
A documentacao da API estara disponivel em: http://localhost:8080/swagger-ui.html

Frontend

Acesse a pasta frontend e inicie o servidor de desenvolvimento:
cd frontend
npm install
npm run dev

Sistema de Gestao de Biblioteca Elotech

Solucao robusta para o gerenciamento de acervo literario, controle de membros e processamento de emprestimos com motor de recomendacao inteligente.

Arquitetura e Tecnologias

O projeto utiliza uma pilha tecnologica moderna focada em performance e seguranca:

Backend: Java 21 com Spring Boot 3.4.2.

Persistencia: Spring Data JPA e PostgreSQL 15.

Frontend: React com Vite e Tailwind CSS v4.

Observabilidade: Spring Actuator e Documentacao Swagger OpenAPI.

Testes: JUnit 5 e Mockito com suporte a banco H2 em memoria para suite de testes.

Decisoes Tecnicas de Engenharia

Durante o desenvolvimento, foram aplicadas as seguintes praticas para garantir a senioridade da implementacao:

Protecao contra Race Conditions: A validacao de disponibilidade de livros e realizada em nivel de banco de dados (Unique Index) e camada de servico (Atomic Query), garantindo a unicidade de emprestimos ativos.

Otimizacao de Consultas: O motor de recomendacao utiliza clausulas SQL especificas (IN / NOT IN) via Spring Data, evitando o carregamento de colecoes pesadas para processamento em memoria Java.

Padronizacao de Respostas: Implementacao de RestControllerAdvice para tratamento global de excecoes, assegurando que erros de negocio retornem status HTTP 400/409 com mensagens claras em JSON.

Resiliencia em Integracoes: O servico de busca externa (Google Books) possui tratamento de erro para limites de quota (Erro 429) e falhas de rede, garantindo que a falha de um servico externo nao comprometa a estabilidade do sistema local.

Instrucoes de Execucao

Infraestrutura (Banco de Dados)

Certifique-se de que o Docker esteja em execucao e inicie o container do PostgreSQL:
docker-compose up -d

Backend (API)

Execute a aplicacao via IntelliJ IDEA (Classe BibliotecaApplication) ou via Maven Wrapper:
./mvnw spring-boot:run

A documentacao interativa da API estara disponivel em: http://localhost:8080/swagger-ui.html
O check de saude do sistema pode ser verificado em: http://localhost:8080/actuator/health

Frontend (Interface)

Acesse a pasta frontend e inicie o servidor de desenvolvimento:
cd frontend
npm install
npm run dev

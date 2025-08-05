# UniFy

**UniFy** é um sistema robusto e modular, desenvolvido para centralizar, automatizar e aprimorar toda a gestão acadêmica e administrativa de instituições de ensino superior. Projetado para ser altamente escalável e seguro, o UniFy integra funcionalidades essenciais para universidades, campi, cursos, pessoas e processos acadêmicos, facilitando rotinas, ampliando a visão dos gestores e promovendo eficiência para toda a comunidade acadêmica.

---

## 🚩 Objetivo

O UniFy tem como missão principal facilitar a transformação digital nas universidades, unificando operações e dados de múltiplos setores acadêmicos e administrativos. Ele reduz a burocracia, aumenta o controle sobre processos e permite análises acionáveis, sempre priorizando a experiência do usuário e a segurança das informações.
Penso em atualizar e melhorar esse projeto com o tempo.
---

## 🎯 Principais Funcionalidades

### Gerenciamento Completo de Universidades
- **Cadastro e administração de múltiplas instituições**
- **Integração de universidades, campi e cursos de graduação**
- **Suporte a hierarquias e diferentes localizações/campi**

### Gestão Acadêmica e de Pessoas
- **Matrículas, rematrículas e controle de situação dos alunos**
- **Gestão de docentes, técnicos, funcionários e terceiros**
- **Relação dinâmica entre pessoas, cursos e turmas**

### Processos Acadêmicos Automatizados
- **Controle de processos seletivos, inscrições e resultados**
- **Gerenciamento de histórico escolar e desempenho**
- **Validação, emissão e autenticação de documentos acadêmicos**

### Recursos Administrativos Avançados
- **Gestão de solicitações e protocolos**
- **Notificações automatizadas por e-mail e sistema**
- **Acompanhamento e rastreabilidade de protocolos internos**

### Inteligência, Segurança e Escalabilidade
- **Dashboards em tempo real com indicadores-chave**
- **Acesso seguro baseado em perfis e níveis de permissão**

---

## 🏅 Diferenciais do UniFy

- **Altamente modular:** permite implantação gradual de módulos conforme a demanda.
- **Estrutura voltada à LGPD:** foco total na privacidade e segurança dos dados.
- **Arquitetura baseada em Java/Spring Boot:** garante robustez, flexibilidade e alta performance.
- **Configuração simples:** documentação clara, fácil personalização e implantação ágil.
- **População inicial de dados:** scripts pré-criados para testes, demos e produção inicial.

---

## 🛠️ Tecnologias Utilizadas

- **Java 21**
- **Spring Boot**
- **Jakarta EE**
- **Spring Data JPA**
- **Spring MVC**
- **Lombok**
- **SQLite (configuração default, suporte a vários bancos)**
- **APIs RESTful**
- **Swagger/OpenAPI**

---

## 📦 Estrutura de Dados e Domínios

- **Universidades, Campi, Graduações**
- **Pessoas: alunos, professores, funcionários**
- **Processos acadêmicos e administrativos**
- **Protocolos, documentos e históricos**

---

## 🚀 Como Executar Localmente

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/seuusuario/unify.git
   ```
2. **Configure o banco de dados**  
   Por padrão o sistema vem preparado para SQLite, mas pode ser facilmente adaptado para outros bancos. Edite as propriedades em `src/main/resources/application.properties`.

3. **Popule o banco (Opcional):**  
   Utilize o script `data_population_sqlite.sql` para adicionar dados de demonstração.
   ```bash
   sqlite3 unify.db < data_population_sqlite.sql
   ```

4. **Execute a aplicação:**
   ```bash
   ./mvnw spring-boot:run
   # ou para quem já usou o Maven
   mvn spring-boot:run
   ```

5. **Acesse via navegador:**  
   [http://localhost:8080](http://localhost:8080)


# 💬 Autor

Gabriel Lima

- GitHub: [github.com/gaelcoder]
- LinkedIn: [www.linkedin.com/in/limagael]

---



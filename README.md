# Projeto Tetris - Java

Estrutura gerada a partir do PDF fornecido. Projeto Maven com pacotes:
- tetris.domain
- tetris.persistence
- tetris.io
- tetris.engine
- tetris.ui

## Requisitos
- Java 17+
- Maven 3.9+
- (Opcional) SQL Server para persistência — conexão padrão em tetris.persistence.ConexaoSQL

# Tetris Project — Alterações e documentação (salvamento e persistência)

Este README documenta todas as mudanças que fizemos no projeto para habilitar salvamento da partida tanto em arquivo quanto no banco de dados (SQL Server). Está pensado como roteiro de apresentação: o que foi alterado, por quê, como testar e próximas melhorias.

## Resumo das mudanças
- Salvamento local mantido (arquivo `partida_salva.dat`) e adicionado persistência no banco (tabela `Partida`).
- Classes do domínio que compõem o estado da partida foram tornadas serializáveis para permitir serialização Java (ObjectOutputStream).
- DAOs criados/atualizados para salvar e carregar a partida como BLOB no banco, e para criar/atualizar o esquema automaticamente.
- Adicionei utilitários para inspecionar registros no DB via console (`TesteBD`, `PartidaDAO.listarUltimas`).

## Principais arquivos alterados / adicionados
- `src/main/java/tetris/io/SaveManager.java`
	- Agora grava localmente em `partida_salva.dat` e também persiste no banco chamando `JogadorDAO` e `PartidaDAO`.
	- Gera logs claros: sucesso em arquivo e sucesso/erro no DB.

- `src/main/java/tetris/persistence/PartidaDAO.java`
	- `criarTabelaSeNaoExistir()` cria tabela `Partida` com colunas: `id`, `jogador_id`, `pontuacao`, `tempo_ms`, `dados`, `data_criacao`.
	- `salvar(Partida)` insere `jogador_id`, `pontuacao`, `tempo_ms` e `dados` (serializado).
	- `listarUltimas(int)` imprime no console as últimas partidas (id, jogador_id, pontuação, tempo_ms, tamanho do blob, data).
	- `carregarUltima(UUID)` carrega a última partida pelo `jogador_id`.

- `src/main/java/tetris/persistence/JogadorDAO.java`
	- `criarTabelaSeNaoExistir()` cria tabela `Jogador` caso não exista.
	- `inserir(Jogador)` agora usa `UUID.toString()` para inserir `id` como `NVARCHAR(36)` e loga linhas afetadas.

- `src/main/java/tetris/io/SaveManager.java` + `src/main/java/tetris/engine/GameEngine.java`
	- O `GameEngine.parar()` chama `SaveManager.salvar(partida)` — assim o save é acionado ao parar o engine.

- `src/main/java/tetris/domain/*`
	- `Tetromino` e subclasses foram tornados `Serializable` (foram adicionados `serialVersionUID` quando apropriado).
	- `Posicao` passou a implementar `Serializable`.
	- `SistemaPontuacao` foi tornada `Serializable`.
	- `Tabuleiro` passou a ser `Serializable`.
	- Observação: essas alterações são necessárias para serializar corretamente o objeto `Partida` inteiro.

- `src/main/java/tetris/persistence/TesteBD.java`
	- Utilitário para testar a conexão, garantir criação de tabelas e listar as últimas partidas no console.

- `pom.xml`
	- Temporariamente alterado para facilitar execução do `TesteBD` via `mvn exec:java`. (Posso reverter quando desejar.)

## Esquema de banco utilizado (SQL Server)
Tabela `Jogador` (criada por `JogadorDAO.criarTabelaSeNaoExistir`):
- `id` NVARCHAR(36) PRIMARY KEY
- `nome` NVARCHAR(100) NOT NULL

Tabela `Partida` (criada/atualizada por `PartidaDAO.criarTabelaSeNaoExistir`):
- `id` INT IDENTITY(1,1) PRIMARY KEY
- `jogador_id` NVARCHAR(36) NOT NULL
- `pontuacao` INT NOT NULL DEFAULT 0
- `tempo_ms` BIGINT NOT NULL DEFAULT 0
- `dados` VARBINARY(MAX) NOT NULL  -- blob serializado da `Partida`
- `data_criacao` DATETIME DEFAULT GETDATE()

> Nota: se você já tinha a tabela `Partida` sem as colunas `pontuacao`/`tempo_ms`, o código aplica condicionalmente `ALTER TABLE` para adicioná-las (com default 0).

## Fluxo de salvamento (alto nível)
1. Ao `GameEngine.parar()` chamamos `SaveManager.salvar(partida)`.
2. `SaveManager` grava o objeto `Partida` em `partida_salva.dat` via `ObjectOutputStream`.
3. `SaveManager` chama `JogadorDAO.criarTabelaSeNaoExistir()` e `PartidaDAO.criarTabelaSeNaoExistir()` para garantir esquema.
4. `SaveManager` tenta inserir o `Jogador` (via `JogadorDAO.inserir`).
5. `SaveManager` chama `PartidaDAO.salvar(partida)`:
	 - Serializa `Partida` em bytes
	 - Extrai `pontuacao` via `partida.getSistemaPontuacao().getPontos()` e `tempo_ms` via `partida.getTempoDecorrido()`
	 - Insere `jogador_id` (UUID como string), `pontuacao`, `tempo_ms`, `dados` no DB

## Como rodar e verificar (PowerShell - Windows)
1. Compilar o projeto:

```powershell
cd 'C:\Users\B\Downloads\java\tetris_project'
mvn clean compile
```

2. Rodar o jogo (ou executar para disparar o save via UI/engine):

```powershell
mvn exec:java
```

Você verá logs no console; exemplo de saída esperada ao salvar:

```
[SAVE] Sucesso em partida_salva.dat
Conectando ao SQL Server...
Conectado com sucesso!
[DB] Jogador inserido, linhas afetadas: 1
[DB] Partida inserida, linhas afetadas: 1 (pontos=0, tempo_ms=4779)
[SAVE] Salvo no DB
```

3. Para listar as últimas partidas via utilitário que adicionei, execute `TesteBD` (já configurado como main no `pom.xml` durante testes):

```powershell
mvn clean compile exec:java
```

Saída exemplificada criada durante testes:

```
[DB] Últimas partidas:
	id=3 jogador_id=D9FB01FD-AD37-4F06-958C-78E5482D7CD1 pontuacao=0 tempo_ms=4779 tamanho=3082 data=2025-11-02 12:24:42.253
	id=2 jogador_id=85D73C56-6E7F-4203-AA90-B1F7BEE9D462 pontuacao=0 tempo_ms=?? tamanho=3143 data=2025-11-02 12:21:54.81
```

4. Verificação direta no SQL Server (SSMS):

```sql
USE TetrisDB; -- confirme o database em ConexaoSQL
SELECT TOP 20 id, jogador_id, pontuacao, tempo_ms, data_criacao FROM Partida ORDER BY data_criacao DESC;
```

Se você não ver resultados: verifique `ConexaoSQL` em `src/main/java/tetris/persistence/ConexaoSQL.java` e confirme `URL`, `USUARIO` e `SENHA` apontam para a instância e banco corretos.

## Problemas comuns e debugging
- "Nome de coluna 'pontuacao' inválido." — ocorre se a tabela `Partida` existia com schema antigo; `PartidaDAO.criarTabelaSeNaoExistir()` tenta aplicar `ALTER TABLE` automaticamente para adicionar `pontuacao` e `tempo_ms`. Se esse comando falhar por permissão, verifique permissões do usuário SQL Server ou atualize o schema manualmente.
- Se `Jogador` for inserido várias vezes: atualmente usamos `JogadorDAO.inserir()` simples que tentará inserir e devolverá erro em duplicação. Sugestão futura: usar `MERGE` ou `INSERT ... WHERE NOT EXISTS` para evitar duplicidade.
- Se a `pontuacao` salva estiver sempre em 0, isso significa que o `SistemaPontuacao` ainda não recebeu pontos no momento do save — garanta que pontos foram atualizados antes de encerrar o engine.

## Comandos úteis (PowerShell)
- Compilar e rodar (jogo / exec principal atual):

```powershell
cd 'C:\Users\B\Downloads\java\tetris_project'
mvn clean compile exec:java
```

- Rodar apenas o utilitário de verificação (`TesteBD`) — OBS: `pom.xml` foi alterado para facilitar execução. Se quiser reverter, eu posso:

```powershell
mvn clean compile exec:java
```

(Se preferir chamar um main específico sem alterar `pom.xml`):

```powershell
mvn -Dexec.mainClass="tetris.persistence.TesteBD" exec:java
```

## Notas técnicas e decisões
- Serialização: para garantir que `Partida` pudesse ser serializada, marquei as classes de estado importantes com `implements Serializable` e adicionei `serialVersionUID` em pontos críticos. Isso evita `NotSerializableException` ao gravar `Partida` inteira.
- UUID handling: por compatibilidade com o driver do SQL Server, gravamos `UUID` como `String` (`NVARCHAR(36)`) em vez de tentar `setObject` direto — isso evita problemas com mapeamento do driver.
- Schema evolution: a criação de tabela tenta ser idempotente e segura, adicionando colunas faltantes quando necessário.

## Próximos passos sugeridos (de melhoria)
1. Evitar duplicidade de `Jogador` usando `MERGE` ou `INSERT ... WHERE NOT EXISTS`.
2. Adicionar endpoint/ação na UI para listar as últimas partidas usando `PartidaDAO.listarUltimas(n)`.
3. Mudar as credenciais e URL do DB para variáveis de ambiente (não manter `sa`/senha hard-coded no repositório).
4. Implementar testes unitários para `PartidaDAO` (testar salvar/carregar) com um DB em memória ou com container de teste.
5. Se quiser, reverter `pom.xml` para executar `tetris.ui.Main` por padrão — atualmente está temporariamente configurado para `TesteBD`.

## Logs/outputs de exemplo (extraídos das execuções realizadas durante o desenvolvimento)
- Save bem-sucedido (arquivo + DB):

```
[SAVE] Sucesso em partida_salva.dat
Conectando ao SQL Server...
Conectado com sucesso!
[DB] Jogador inserido, linhas afetadas: 1
[DB] Partida inserida, linhas afetadas: 1 (pontos=0, tempo_ms=4779)
[SAVE] Salvo no DB
```

- Listagem via `TesteBD`:

```
[DB] Últimas partidas:
	id=3 jogador_id=D9FB01FD-AD37-4F06-958C-78E5482D7CD1 pontuacao=0 tempo_ms=4779 tamanho=3082 data=2025-11-02 12:24:42.253
	id=2 jogador_id=85D73C56-6E7F-4203-AA90-B1F7BEE9D462 pontuacao=0 tempo_ms=?? tamanho=3143 data=2025-11-02 12:21:54.81
```

## Contato / próximos passos
Se quiser que eu:
- gere a UI para listar saves; ou
- modifique `JogadorDAO.inserir` para não duplicar; ou
- reverta o `pom.xml` para executar `tetris.ui.Main` novamente;

diga qual das opções prefere que eu implemente agora e eu sigo com um novo ciclo (faço a alteração e testo). Obrigado — pronto para seguir com a próxima tarefa que você escolher.

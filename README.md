# ProjetoP3
Repositório criado para o projeto de programação da cadeira de Programação 3 do curso de Sistemas de Informação na UPE Caruaru.

## Interface gráfica (JavaFX)

As telas seguem o padrão do time: arquivos `.fxml` em `src/main/resources/fxml`, controllers em
`src/main/java/apresentacao/controllers` e estilo compartilhado em `src/main/resources/css/identidade.css`.

### Tela Relatórios

- `Relatorios.fxml` / `RelatoriosController`: menu lateral e as 4 abas.
- Cada aba é um FXML com controller próprio:
  - `RelatorioItensDisponiveis.fxml` / `ItensDisponiveisController`
  - `RelatorioHistoricoCliente.fxml` / `HistoricoClienteController`
  - `RelatorioItensAlugados.fxml` / `ItensAlugadosController`
  - `RelatorioFaturamento.fxml` / `FaturamentoController`
- Os controllers pedem os dados ao `SistemaFacade` (obtido em `main.ConfiguracaoSistema.getSistema()`).
- Complementos de estilo desta tela: `src/main/resources/css/relatorios.css`.

### Como executar

Na pasta raiz do projeto (para as pastas `dados/` e `relatorios/` serem encontradas):

```
mvn javafx:run
```

Isso abre `main.MainRelatorios`. Os botões "Exportar CSV" gravam os arquivos na pasta `relatorios/`.

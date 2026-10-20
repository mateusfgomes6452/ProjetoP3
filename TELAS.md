# Telas do sistema — Loja que Aluga de Tudo (JavaFX)

Este documento lista **todas as telas** que a interface JavaFX precisa ter, de onde
cada uma vem no código de terminal e **quem do grupo faz cada uma**.
No final há o passo a passo **"Como criar uma tela nova"** seguindo o padrão da base.

---

## 1. Levantamento das telas

Peso usado para equilibrar a divisão: **fácil = 1**, **média = 2**, **difícil = 3**.

> "Desativar", "Cancelar", "Quitar" e "Pagar" não são telas próprias: são **ações**
> (botão + janela de confirmação) dentro da tela de lista ou de detalhes.

### Acesso (login)

| # | Tela | Origem no terminal | Tipo | Dificuldade |
|---|------|--------------------|------|-------------|
| 1 | Login | `MenuLogin.realizarLogin` | formulário | média |
| 2 | Criar conta (cliente se cadastra sozinho) | reusa `MenuUsuarios.cadastrarCliente` → `sistema.cadastrarUsuario(new Cliente(...))` | formulário | fácil |

### Usuários (administrador e funcionário)

| # | Tela | Origem no terminal | Tipo | Dificuldade |
|---|------|--------------------|------|-------------|
| 3 | Lista de usuários (+ ação Desativar) | `MenuUsuarios.listarUsuarios`, `desativarUsuario` | lista | média |
| 4 | Formulário de usuário (cadastrar e editar) | `cadastrarCliente`, `cadastrarFuncionario`, `cadastrarAdministrador`, `atualizarUsuario` | formulário | média |
| 5 | Detalhes do usuário (buscar) | `MenuUsuarios.buscarUsuario` | detalhes | fácil |

### Categorias

| # | Tela | Origem no terminal | Tipo | Dificuldade |
|---|------|--------------------|------|-------------|
| 6 | Lista de categorias (+ ação Desativar) | `MenuCategorias.listarCategorias`, `desativarCategoria` | lista | fácil |
| 7 | Formulário de categoria (cadastrar e editar) | `cadastrarCategoria`, `atualizarCategoria` | formulário | fácil |
| 8 | Detalhes da categoria (buscar) | `buscarCategoria`, `exibirCategoria` | detalhes | fácil |

### Fornecedores

| # | Tela | Origem no terminal | Tipo | Dificuldade |
|---|------|--------------------|------|-------------|
| 9 | Lista de fornecedores (+ ação Desativar) | `MenuFornecedores.listarFornecedores`, `desativarFornecedor` | lista | fácil |
| 10 | Formulário de fornecedor (cadastrar e editar; CNPJ não muda na edição) | `cadastrarFornecedor`, `atualizarFornecedor` — valida CNPJ, e-mail e telefone | formulário | média |
| 11 | Detalhes do fornecedor (buscar) | `buscarFornecedor`, `exibirFornecedor` | detalhes | fácil |

### Itens

| # | Tela | Origem no terminal | Tipo | Dificuldade |
|---|------|--------------------|------|-------------|
| 12 | Lista de itens (+ ação Desativar, etiqueta de status) | `MenuItens.listarItens`, `desativarItem` (`excluirItem`) | lista | média |
| 13 | Formulário de item (cadastrar e editar) — ComboBox de estado, categoria e fornecedor; campos numéricos | `cadastrarItem`, `selecionarCategoria`, `selecionarFornecedor`, `atualizarItem` | formulário | difícil |
| 14 | Detalhes do item (buscar) | `MenuItens.buscarItem` | detalhes | fácil |

### Contratos / Aluguéis

| # | Tela | Origem no terminal | Tipo | Dificuldade |
|---|------|--------------------|------|-------------|
| 15 | Lista de contratos (+ ação Cancelar, etiqueta de status) | `MenuContratos.listarContratos`, `cancelarContrato` | lista | média |
| 16 | Registrar aluguel — escolher cliente e item, DatePicker de retirada/devolução, valor calculado na hora | `MenuContratos.registrarAluguel` | formulário | difícil |
| 17 | Detalhes do contrato (buscar) | `buscarContrato`, `exibirDetalhesContrato` | detalhes | fácil |
| 18 | Finalizar contrato (devolução) — data real, cálculo e exibição da multa por atraso | `MenuContratos.finalizarContrato` | formulário | difícil |

### Multas

| # | Tela | Origem no terminal | Tipo | Dificuldade |
|---|------|--------------------|------|-------------|
| 19 | Lista de multas — filtro Todas / Pendentes / Por contrato (+ ação Quitar) | `listarMultas`, `listarPorContrato`, `listarPendentes`, `quitarMulta` | lista | média |
| 20 | Registrar multa manual (avaria) | `MenuMultas.registrarMulta` | formulário | média |
| 21 | Detalhes da multa (buscar) | `buscarMulta`, `exibirMulta` | detalhes | fácil |

### Relatórios

| # | Tela | Origem no terminal | Tipo | Dificuldade |
|---|------|--------------------|------|-------------|
| 22 | Itens disponíveis por categoria | `MenuRelatorios` opção 1 → `gerarRelatorioItensDisponiveis` | relatório | média |
| 23 | Histórico de aluguéis de um cliente | opção 2 → `gerarHistoricoCliente` | relatório | média |
| 24 | Itens alugados no momento (situação "atrasado") | opção 3 → `gerarRelatorioItensAlugados` | relatório | média |
| 25 | Faturamento por período (2 DatePickers) | opção 4 → `gerarRelatorioFaturamento` | relatório | difícil |

### Área do cliente

| # | Tela | Origem no terminal | Tipo | Dificuldade |
|---|------|--------------------|------|-------------|
| 26 | Itens disponíveis (catálogo em cards, com preço) | `MenuCliente.verItensDisponiveis` | lista | média |
| 27 | Meus aluguéis ativos | `MenuCliente.listarAlugueisAtivos` | lista | fácil |
| 28 | Histórico de aluguéis | `MenuCliente.listarHistorico` | lista | fácil |
| 29 | Minhas multas (+ ação Pagar, com confirmação) | `MenuCliente.consultarMultas`, `pagarMulta` | lista | média |

**Total: 29 telas.**

---


## 2. O que já vem pronto (feito na base) e o que fica para o grupo

**Prontas na base (servem de modelo):**

- Tela 1 — **Login** (`Login.fxml` + `LoginController`) → modelo de formulário simples com mensagem de erro.
- Tela 3 — **Lista de usuários** (`usuarios/ListaUsuarios.fxml`) → modelo de **lista**: título + botão, busca, TableView, etiqueta de status, ação com confirmação.
- Tela 4 — **Formulário de usuário** (`usuarios/FormularioUsuario.fxml`) → modelo de **formulário**: caminho no topo, 2 colunas, ComboBox, erro embaixo do campo, cadastrar e editar no mesmo FXML.
- Tela 5 — **Detalhes do usuário** (`usuarios/DetalhesUsuario.fxml`) → modelo de **detalhes**: card com pares rótulo/valor e botões de ação.
- Toda a **base**: `styles.css`, fontes, área logada com menu lateral reutilizável, troca do conteúdo central e a tela "Em construção".

**Ficam para o grupo:** as telas 2 e 6 a 29 (25 telas).

---

## 3. Divisão entre os integrantes

| Integrante | Módulos | Telas | Pesos | Total |
|------------|---------|-------|-------|-------|
| **Arthur** | Fornecedores + Multas | 9, 10, 11, 19, 20, 21 | 1 + 2 + 1 + 2 + 2 + 1 | **9** |
| **Gaby** | Itens + Categorias | 12, 13, 14, 6, 7, 8 | 2 + 3 + 1 + 1 + 1 + 1 | **9** |
| **Marcelo** | Área do cliente + Criar conta | 26, 27, 28, 29, 2 | 2 + 1 + 1 + 2 + 1 | **7** |
| **Mateus** | Contratos / Aluguéis | 15, 16, 17, 18 | 2 + 3 + 1 + 3 | **9** |
| **Raul** | Relatórios | 22, 23, 24, 25 | 2 + 2 + 2 + 3 | **9** |

### Qual tela pronta usar como modelo

| Integrante | Para listas | Para formulários | Para detalhes |
|------------|-------------|------------------|---------------|
| Arthur | `ListaUsuarios` (desativar fornecedor e quitar multa = mesmo esquema do desativar usuário) | `FormularioUsuario` | `DetalhesUsuario` |
| Gaby | `ListaUsuarios` | `FormularioUsuario` (ComboBox de tipo → ComboBox de categoria, fornecedor e estado) | `DetalhesUsuario` |
| Marcelo | `ListaUsuarios` (minhas multas, aluguéis, histórico) | `Login` (Criar conta usa o mesmo layout) + `FormularioUsuario` (validações) | — |
| Mateus | `ListaUsuarios` (cancelar = mesmo esquema do desativar) | `FormularioUsuario` (registrar aluguel e finalizar) | `DetalhesUsuario` |
| Raul | `ListaUsuarios` (TableView + filtros no topo) | — (os filtros de data usam `DatePicker`, ver seção 5) | — |

---

## 4. Avisos importantes por módulo

- **Fornecedores (Arthur):** o `FornecedoresAdministrador.fxml` que já existe na `develop` traz o menu lateral inteiro dentro dele. Com a base, o menu lateral é **um só** (`AreaLogada.fxml`) e a tela de fornecedores passa a ter **só o conteúdo central**. Também não precisa de um FXML para administrador e outro para funcionário: o menu de fornecedores é igual para os dois.
- **Contratos (Mateus):** parte da regra está **dentro do menu de terminal**. `registrarAluguel` calcula `valorTotal = dias × taxaDiaria` e chama `item.alugar()`. `finalizarContrato` chama `sistema.calcularMulta`, `setValorMulta`, `setMultaPaga(false)`, `item.devolver()` e só então `sistema.finalizarContrato`. A tela precisa fazer **a mesma sequência**, na mesma ordem. Não mude a SistemaFacade.
- **Relatórios (Raul):** os métodos `sistema.gerarRelatorio...()` não devolvem dados: eles imprimem no console e gravam um CSV em `relatorios/`. Na tela, monte a tabela com os métodos de listagem (`listarItens`, `listarContratos`, ...) usando o mesmo filtro do `GeradorRelatorios`. Coloque também um botão **"Exportar CSV"** que chama o `sistema.gerarRelatorio...()` correspondente.
- **Área do cliente (Marcelo):** as telas filtram `sistema.listarContratos()` pelo cliente logado (`Sessao.getUsuarioLogado()`), igual ao `MenuCliente`.
- **Criar conta (Marcelo):** o `ValidaEntrada` exige e-mail terminando em `@loja.com`; mantenha a mesma regra.
- **Usuários do funcionário:** a mesma tela de usuários é usada com `acessoTotal = false`, igual ao `MenuUsuarios`: o funcionário só cadastra **cliente**, busca e lista, sem editar nem desativar.

---

## 5. Como criar uma tela nova (Scene Builder + FXML)

> Exemplo: Gaby vai criar a **Lista de categorias**.

Cada tela tem **dois arquivos**:

| Arquivo | Onde fica | Para quê |
|---------|-----------|----------|
| `ListaCategorias.fxml` | `src/main/resources/fxml/categorias/` | o **visual**, montado no Scene Builder |
| `ListaCategoriasController.java` | `src/main/java/apresentacao/controllers/` | o **comportamento** (o que acontece ao clicar) |

### Passo 1 — Criar o FXML no Scene Builder

1. Copie `fxml/usuarios/ListaUsuarios.fxml` para `fxml/categorias/ListaCategorias.fxml` e abra no Scene Builder.
2. A raiz é um `VBox` com a classe CSS `tela`. **Não coloque menu lateral**: ele já está no `AreaLogada.fxml`, e a sua tela aparece no centro dele.
3. Em *Controller* (painel esquerdo, embaixo), coloque `apresentacao.controllers.ListaCategoriasController`.
4. Dê um **fx:id** a tudo que o controller precisa ler ou mudar (tabela, colunas, campo de busca) e um **On Action** (ex.: `#novaCategoria`) aos botões.
5. O CSS já vem ligado na raiz do FXML (`stylesheets`), então o Scene Builder mostra as cores certas. Para ver também as fontes Outfit e Plus Jakarta Sans no Scene Builder, instale os `.ttf` de `src/main/resources/fontes/` no seu computador (é opcional; no programa elas são carregadas sozinhas).

### Passo 2 — Criar o controller

```java
package apresentacao.controllers;

public class ListaCategoriasController {

    @FXML private TableView<Categoria> tabela;            // mesmo nome do fx:id
    @FXML private TableColumn<Categoria, String> colNome;

    private final SistemaFacade sistema = Sessao.getSistema();

    @FXML
    private void initialize() {                            // roda sozinho depois que o FXML carrega
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        tabela.getItems().setAll(sistema.listarCategorias());
    }

    @FXML
    private void novaCategoria() {                         // ligado ao On Action do botão
        Navegador.abrirConteudo("categorias/FormularioCategoria.fxml");
    }
}
```

### Passo 3 — Registrar a tela no menu lateral

Abra `src/main/java/apresentacao/javafx/MenusPorPerfil.java` e troque a "Tela em construção" pelo seu FXML:

```java
// ANTES
ItemMenu.emConstrucao("Categorias"),
// DEPOIS
new ItemMenu("Categorias", "categorias/ListaCategorias.fxml"),
```

Telas que **não** aparecem no menu (formulário, detalhes) são abertas pela tela de lista:

```java
// abrir vazio (cadastrar)
Navegador.abrirConteudo("categorias/FormularioCategoria.fxml");

// abrir preenchido (editar): o método devolve o controller da tela aberta
FormularioCategoriaController form = Navegador.abrirConteudo("categorias/FormularioCategoria.fxml");
form.editar(categoriaSelecionada);
```

### Passo 4 — Chamar a SistemaFacade

Use **os mesmos métodos que o menu de terminal usa**, pegando o sistema com `Sessao.getSistema()`:

```java
List<Categoria> categorias = sistema.listarCategorias();          // listar
Categoria c = sistema.buscarCategoria(id);                        // buscar
boolean ok = sistema.cadastrarCategoria(new Categoria(id, nome, descricao));
```

Se o método devolve `false`, mostre a mesma mensagem do terminal:
`Alertas.erro("Erro: já existe uma categoria com esse nome")`.
Para pedir confirmação (desativar, cancelar, quitar):

```java
if (Alertas.confirmar("Desativar categoria", "Tem certeza que deseja desativar \"" + c.getNome() + "\"?")) { ... }
```

### Passo 5 — Validar os campos

No FXML, embaixo de cada campo, coloque um `Label` vazio com a classe CSS `field-error`
e um fx:id (ex.: `erroCnpj`). No controller:

```java
boolean ok = ValidacaoCampos.validar(cnpj, erroCnpj, ValidaEntrada::cnpjValido,
        "Formato inválido. Use 00.000.000/0000-00");
```

O texto aparece em vermelho embaixo do campo, e a borda do campo fica vermelha.
Use `ComboBox` para escolher opções e `DatePicker` para datas. Para converter a data,
use `datePicker.getValue().toString()`, que dá `"AAAA-MM-DD"`, o mesmo formato do terminal.

### Passo 6 — Aplicar os estilos

**Não escreva cores, fontes ou tamanhos no FXML nem no Java.** Tudo está em
`src/main/resources/css/styles.css`. No Scene Builder, selecione o componente e
adicione a classe em *Properties → Style Class*:

| Para… | Classe CSS |
|-------|------------|
| raiz da tela | `tela` |
| título da tela / título de card | `page-title` / `section-title` |
| card branco | `panel-card` |
| rótulo de campo / campo de texto | `field-label` / `input-field` |
| erro embaixo do campo | `field-error` |
| botão principal / secundário / perigo | `primary-button` / `secondary-button` / `danger-button` |
| caminho no topo do formulário | `breadcrumb` |
| preço | `preco` |
| tabela | `data-table` |

Etiquetas de status na tabela: `EtiquetaStatus.aplicar(colStatus)` (aceita Ativo, Inativo, Pendente, Atrasado, Finalizado…).
Se precisar de um estilo novo, **crie uma classe no `styles.css`** (com comentário) em vez de usar `style="..."`.

### Passo 7 — Testar

Rode `mvn javafx:run`, entre com o administrador padrão
(`jacksonmepassaporfavor@loja.com` / `obrigadojackson`) e clique no item do menu.

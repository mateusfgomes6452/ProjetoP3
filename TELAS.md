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

- Tela 1 — **Login** → modelo de formulário simples, validação e mensagem de erro.
- Tela 3 — **Lista de usuários** → modelo de **lista** (título + botão, busca, TableView, etiqueta de status, ação com confirmação).
- Tela 4 — **Formulário de usuário** → modelo de **formulário** (caminho no topo, 2 colunas, ComboBox, erro embaixo do campo, cadastrar e editar na mesma classe).
- Tela 5 — **Detalhes do usuário** → modelo de **detalhes** (card com pares rótulo/valor e botões de ação).
- Toda a **base**: `styles.css`, fontes, menu lateral, troca de conteúdo central e a tela "Em construção".

**Ficam para o grupo:** as telas 2 e 6 a 29 (25 telas).

---

## 3. Divisão entre os integrantes

Critérios: mesmo módulo com a mesma pessoa; soma de pesos parecida.

| Integrante | Módulos | Telas | Pesos | Total |
|------------|---------|-------|-------|-------|
| **Arthur** | Contratos / Aluguéis | 15, 16, 17, 18 | 2 + 3 + 1 + 3 | **9** |
| **Gaby** | Itens + Categorias | 12, 13, 14, 6, 7, 8 | 2 + 3 + 1 + 1 + 1 + 1 | **9** |
| **Marcelo** | Relatórios | 22, 23, 24, 25 | 2 + 2 + 2 + 3 | **9** |
| **Mateus** | Fornecedores + Multas | 9, 10, 11, 19, 20, 21 | 1 + 2 + 1 + 2 + 2 + 1 | **9** |
| **Raul** | Área do cliente + Criar conta | 26, 27, 28, 29, 2 | 2 + 1 + 1 + 2 + 1 | **7** |

Por que juntei assim:

- **Itens + Categorias (Gaby):** o formulário de item usa um ComboBox de categorias; quem fez categorias já conhece os dados.
- **Fornecedores + Multas (Mateus):** dois CRUDs de dificuldade parecida; o formulário de fornecedor é o que mais usa as validações (CNPJ, e-mail, telefone).
- **Área do cliente + Criar conta (Raul):** tudo que o cliente vê. Tem peso um pouco menor, mas é uma área inteira com menu lateral próprio.

### Qual tela pronta usar como modelo

| Integrante | Para listas | Para formulários | Para detalhes |
|------------|-------------|------------------|---------------|
| Arthur | `TelaListaUsuarios` (cancelar = mesmo esquema do desativar) | `TelaFormularioUsuario` (registrar e finalizar) | `TelaDetalhesUsuario` |
| Gaby | `TelaListaUsuarios` | `TelaFormularioUsuario` (ComboBox de tipo → ComboBox de categoria/fornecedor/estado) | `TelaDetalhesUsuario` |
| Marcelo | `TelaListaUsuarios` (TableView + filtros no topo) | campos de data: `DatePicker` como descrito na seção 5 | — |
| Mateus | `TelaListaUsuarios` | `TelaFormularioUsuario` | `TelaDetalhesUsuario` |
| Raul | `TelaListaUsuarios` | `TelaLogin` (Criar conta) + `TelaFormularioUsuario` | — |

---

## 4. Avisos importantes por módulo

- **Contratos (Arthur):** parte da regra está **dentro do menu de terminal** (ex.: `registrarAluguel` calcula `valorTotal = dias × taxaDiaria` e chama `item.alugar()`; `finalizarContrato` chama `sistema.calcularMulta`, `setValorMulta`, `setMultaPaga(false)`, `item.devolver()` e só então `sistema.finalizarContrato`). A tela precisa fazer **a mesma sequência**, na mesma ordem. Não mude a SistemaFacade.
- **Relatórios (Marcelo):** os métodos `sistema.gerarRelatorio...()` não devolvem dados — eles imprimem no console e gravam um CSV em `relatorios/`. Na tela: monte a tabela com os métodos de listagem (`listarItens`, `listarContratos`, ...) usando o mesmo filtro do `GeradorRelatorios`, e coloque um botão **"Exportar CSV"** que chama o `sistema.gerarRelatorio...()` correspondente.
- **Área do cliente (Raul):** as telas filtram `sistema.listarContratos()` pelo cliente logado (`area.getUsuarioLogado()`), igual ao `MenuCliente`.
- **Criar conta (Raul):** o `ValidaEntrada` exige e-mail terminando em `@loja.com`; mantenha a mesma regra.
- **Usuários do funcionário:** a mesma tela de usuários é usada com `acessoTotal = false` (funcionário só cadastra **cliente**, busca e lista — sem editar nem desativar), igual ao `MenuUsuarios`.

---

## 5. Como criar uma tela nova

> Exemplo: Gaby vai criar a **Lista de categorias**.

### Passo 1 — Criar o arquivo no pacote do módulo

Todas as telas ficam em `src/main/java/telas/<modulo>/`. Crie
`src/main/java/telas/categorias/TelaListaCategorias.java`.

Toda tela é uma classe Java que **estende um layout do JavaFX** (`VBox`) e recebe a
`AreaLogada` no construtor. A `AreaLogada` dá acesso a três coisas:

- `area.getSistema()` → a `SistemaFacade` (todas as operações do sistema);
- `area.getUsuarioLogado()` → quem está usando o sistema;
- `area.mostrar(novaTela)` → troca o conteúdo central (navegação).

```java
package telas.categorias;

import telas.base.AreaLogada;
import telas.base.Componentes;
import javafx.scene.layout.VBox;

public class TelaListaCategorias extends VBox {

    private final AreaLogada area;

    public TelaListaCategorias(AreaLogada area) {
        this.area = area;
        getStyleClass().add("tela");          // fundo, espaçamento e margens padrão
        // ... montar título, busca e tabela (copie de TelaListaUsuarios)
    }
}
```

### Passo 2 — Registrar a tela no menu lateral

Abra `src/main/java/telas/base/MenusPorPerfil.java`. Cada perfil tem uma lista de
itens de menu. Troque a "Tela em construção" pela sua tela:

```java
// ANTES
new ItemMenu("Categorias", () -> new TelaEmConstrucao("Categorias")),
// DEPOIS
new ItemMenu("Categorias", () -> new TelaListaCategorias(area)),
```

Telas que **não** aparecem no menu (formulário, detalhes) são abertas pela própria
tela de lista com `area.mostrar(new TelaFormularioCategoria(area, null))`.

### Passo 3 — Chamar a SistemaFacade

Use **os mesmos métodos que o menu de terminal usa**. Exemplo:

```java
SistemaFacade sistema = area.getSistema();

List<Categoria> categorias = sistema.listarCategorias();        // listar
Categoria c = sistema.buscarCategoria(id);                      // buscar
boolean ok = sistema.cadastrarCategoria(new Categoria(id, nome, descricao));
```

Se o método devolve `false`, mostre a mesma mensagem de erro do terminal com
`Componentes.mostrarErro("Erro: já existe uma categoria com esse nome")`.
Para pedir confirmação (desativar, cancelar, quitar):

```java
if (Componentes.confirmar("Desativar categoria", "Tem certeza que deseja desativar \"" + c.getNome() + "\"?")) {
    ...
}
```

### Passo 4 — Validar os campos

Use a classe `CampoFormulario` (rótulo + campo + mensagem de erro vermelha embaixo)
e as validações de `ValidaEntrada`:

```java
CampoFormulario campoCnpj = new CampoFormulario("CNPJ", "00.000.000/0000-00");
...
boolean valido = campoCnpj.validar(ValidaEntrada::cnpjValido, "Formato inválido. Use 00.000.000/0000-00");
```

Validações disponíveis em `ValidaEntrada`: `cpfValido`, `cnpjValido`, `emailValido`,
`telefoneValido`, `dataValida`. Para escolher uma opção use `ComboBox`; para datas use
`DatePicker` (converta com `datePicker.getValue().toString()` → `"AAAA-MM-DD"`, o
mesmo formato que o terminal usa).

### Passo 5 — Aplicar os estilos

**Não escreva cores, fontes ou tamanhos no código Java.** Tudo está em
`src/main/resources/css/styles.css`. Só adicione a classe CSS certa:

| Para… | Use |
|-------|-----|
| raiz da tela | `getStyleClass().add("tela")` |
| título da tela | `Componentes.titulo("Categorias")` |
| card branco | `Componentes.card(conteudo...)` |
| botão principal / secundário / perigo | `Componentes.botaoPrimario(...)`, `botaoSecundario(...)`, `botaoPerigo(...)` |
| etiqueta de status | `Componentes.etiquetaStatus("Ativo")` (aceita Ativo, Inativo, Pendente, Atrasado, Finalizado…) |
| caminho no topo do formulário | `Componentes.caminho("Categorias", "Nova categoria")` |
| preço | `label.getStyleClass().add("preco")` |

Se precisar de um estilo novo, **crie uma classe no `styles.css`** (com comentário)
em vez de usar `setStyle(...)`.

### Passo 6 — Testar

Rode `mvn javafx:run`, entre com o usuário administrador
(`jacksonmepassaporfavor@loja.com` / `obrigadojackson`) e clique no item do menu.

package apresentacao.controllers;

import apresentacao.navegacao.Navegacao;
import entidades.Administrador;
import entidades.Categoria;
import entidades.Fornecedor;
import entidades.Funcionario;
import entidades.Item;
import entidades.Usuario;
import facade.SistemaFacade;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ItensController implements Navegacao.ControladorComSessao {
    private static final Logger LOG = Logger.getLogger(ItensController.class.getName());

    private enum Filtro { TODOS, ATIVOS, INATIVOS }
    private SistemaFacade sistema;
    private Usuario usuario;
    private Consumer<String> navegacao;
    private Runnable sair;
    private Filtro filtro = Filtro.TODOS;
    private Integer idEmEdicao;
    private List<Item> itens = List.of();

    @FXML private TableView<Item> tabela;
    @FXML private TableColumn<Item, Number> colId;
    @FXML private TableColumn<Item, String> colNome, colDescricao, colTaxa, colEstado, colValorRep, colStatus;
    
    @FXML private TextField busca, campoNome, campoTaxa, campoDescricao, campoReposicao;
    @FXML private ComboBox<String> comboEstado;
    @FXML private ComboBox<Categoria> comboCategoria;
    @FXML private ComboBox<Fornecedor> comboFornecedor;

    @FXML private Label lblTotal, lblAtivos, lblInativos, lblResultado, tituloFormulario;
    @FXML private Label lblPerfil, lblNomeUsuario, lblAvatar;
    @FXML private Button btnEditar, btnDesativar, btnTodos, btnAtivos, btnInativos, btnSalvar;
    @FXML private VBox formulario;

    @FXML private void initialize() {
        colId.setCellValueFactory(x -> new ReadOnlyIntegerWrapper(x.getValue().getId()));
        colNome.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().getNome()));
        colDescricao.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().getDescricao()));
        colTaxa.setCellValueFactory(x -> new ReadOnlyStringWrapper(String.format("R$ %.2f", x.getValue().getTaxaDiaria())));
        colEstado.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().getEstadoConservacao()));
        colValorRep.setCellValueFactory(x -> new ReadOnlyStringWrapper(String.format("R$ %.2f", x.getValue().getValorReposicao())));
        colStatus.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().isAtivo() ? "Ativo" : "Inativo"));

        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setPlaceholder(new Label("Nenhum item encontrado."));
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, ant, atual) -> ajustarAcoes());
        busca.textProperty().addListener((obs, ant, atual) -> filtrar());

        comboEstado.setItems(FXCollections.observableArrayList("Novo", "Bom", "Regular", "Ruim"));

        comboCategoria.setConverter(new StringConverter<>() {
            @Override public String toString(Categoria c) { return c == null ? "" : c.getNome(); }
            @Override public Categoria fromString(String s) { return null; }
        });
        
        comboFornecedor.setConverter(new StringConverter<>() {
            @Override public String toString(Fornecedor f) { return f == null ? "" : f.getRazaoSocial(); }
            @Override public Fornecedor fromString(String s) { return null; }
        });

        destacarFiltro();
        ajustarAcoes();
    }

    public void configurar(SistemaFacade sistema, Usuario usuario, Consumer<String> navegacao, Runnable sair) {
        this.sistema = Objects.requireNonNull(sistema, "SistemaFacade é obrigatório");
        this.usuario = Objects.requireNonNull(usuario, "Usuário autenticado é obrigatório");
        if (!(usuario instanceof Administrador || usuario instanceof Funcionario) || !usuario.isAtivo()) {
            throw new SecurityException("Somente administrador ou funcionário ativo pode gerenciar itens.");
        }
        this.navegacao = Objects.requireNonNull(navegacao, "Callback de navegação é obrigatório");
        this.sair = Objects.requireNonNull(sair, "Callback de logout é obrigatório");
        lblPerfil.setText(usuario.getTipo());
        lblNomeUsuario.setText(usuario.getNome());
        lblAvatar.setText(usuario.getNome().isBlank() ? "U" : usuario.getNome().substring(0, 1).toUpperCase(Locale.ROOT));
        atualizar();
    }

    private void exigirSessao() {
        if (sistema == null || usuario == null || !usuario.isAtivo()
                || !(usuario instanceof Administrador || usuario instanceof Funcionario)) {
            throw new SecurityException("Sessão ausente ou sem permissão para itens.");
        }
    }

    @FXML private void atualizar() {
        executar("consultar itens", () -> {
            exigirSessao();
            itens = new ArrayList<>(sistema.listarItens());
            lblTotal.setText(String.valueOf(itens.size()));
            long ativos = itens.stream().filter(Item::isAtivo).count();
            lblAtivos.setText(String.valueOf(ativos));
            lblInativos.setText(String.valueOf(itens.size() - ativos));
            filtrar();
        });
    }

    private void filtrar() {
        String termo = busca.getText() == null ? "" : busca.getText().strip().toLowerCase(Locale.ROOT);
        List<Item> encontrados = itens.stream()
                .filter(i -> filtro == Filtro.TODOS || (filtro == Filtro.ATIVOS) == i.isAtivo())
                .filter(i -> termo.isEmpty() || String.valueOf(i.getId()).equals(termo)
                        || texto(i.getNome()).contains(termo) || texto(i.getDescricao()).contains(termo))
                .toList();
        tabela.setItems(FXCollections.observableArrayList(encontrados));
        lblResultado.setText("Exibindo " + encontrados.size() + " de " + itens.size());
        ajustarAcoes();
    }

    private String texto(String s) { return s == null ? "" : s.toLowerCase(Locale.ROOT); }
    
    @FXML private void todos() { mudarFiltro(Filtro.TODOS); }
    @FXML private void ativos() { mudarFiltro(Filtro.ATIVOS); }
    @FXML private void inativos() { mudarFiltro(Filtro.INATIVOS); }
    
    private void mudarFiltro(Filtro novo) { filtro = novo; destacarFiltro(); filtrar(); }
    
    private void destacarFiltro() {
        destacar(btnTodos, filtro == Filtro.TODOS);
        destacar(btnAtivos, filtro == Filtro.ATIVOS);
        destacar(btnInativos, filtro == Filtro.INATIVOS);
    }
    
    private void destacar(Button botao, boolean ativo) {
        if (botao == null) return;
        botao.getStyleClass().remove("filter-active");
        if (ativo) botao.getStyleClass().add("filter-active");
    }
    
    private void ajustarAcoes() {
        Item selecionado = tabela.getSelectionModel().getSelectedItem();
        if (btnEditar != null) btnEditar.setDisable(selecionado == null);
        if (btnDesativar != null) btnDesativar.setDisable(selecionado == null || !selecionado.isAtivo());
    }

    private void carregarCombos() {
        comboCategoria.setItems(FXCollections.observableArrayList(sistema.listarCategorias()));
        comboFornecedor.setItems(FXCollections.observableArrayList(sistema.listarFornecedores()));
    }

    @FXML private void novo() {
        executar("abrir cadastro de item", () -> {
            exigirSessao();
            idEmEdicao = null;
            tituloFormulario.setText("Cadastrar item");
            campoNome.clear();
            campoDescricao.clear();
            campoTaxa.clear();
            campoReposicao.clear();
            comboEstado.setValue(null);
            comboCategoria.setValue(null);
            comboFornecedor.setValue(null);
            carregarCombos();
            btnSalvar.setText("Cadastrar item");
            exibirFormulario(true);
        });
    }

    @FXML private void editar() {
        executar("editar item", () -> {
            exigirSessao();
            Item f = selecionado();
            idEmEdicao = f.getId();
            tituloFormulario.setText("Editar item #" + f.getId());
            carregarCombos();
            campoNome.setText(f.getNome());
            campoDescricao.setText(f.getDescricao());
            campoTaxa.setText(String.valueOf(f.getTaxaDiaria()));
            campoReposicao.setText(String.valueOf(f.getValorReposicao()));
            comboEstado.setValue(f.getEstadoConservacao());
            comboCategoria.setValue(f.getCategoria());
            comboFornecedor.setValue(f.getFornecedor());
            btnSalvar.setText("Salvar alterações");
            exibirFormulario(true);
        });
    }

    @FXML private void cancelar() { 
        idEmEdicao = null; 
        exibirFormulario(false); 
    }
    
    private void exibirFormulario(boolean visivel) {
        if (formulario != null) {
            formulario.setVisible(visivel); 
            formulario.setManaged(visivel);
        }
        if (visivel) campoNome.requestFocus();
    }

    @FXML private void salvar() {
        executar("salvar item", () -> {
            exigirSessao();
            String nome = validarTexto(campoNome.getText(), "Nome");
            String descricao = validarTexto(campoDescricao.getText(), "Descrição");
            String estado = comboEstado.getValue();
            Categoria categoria = comboCategoria.getValue();
            Fornecedor fornecedor = comboFornecedor.getValue();

            if (estado == null || estado.isBlank())
                throw new IllegalArgumentException("Estado de conservação é obrigatório.");
            if (categoria == null)
                throw new IllegalArgumentException("Categoria é obrigatória.");
            if (fornecedor == null)
                throw new IllegalArgumentException("Fornecedor é obrigatório.");

            double taxa;
            double reposicao;
            try {
                taxa = Double.parseDouble(validarTexto(campoTaxa.getText(), "Taxa diária").replace(",", "."));
                reposicao = Double.parseDouble(validarTexto(campoReposicao.getText(), "Valor de reposição").replace(",", "."));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Taxa diária e valor de reposição devem ser números válidos.");
            }

            if (taxa <= 0 || reposicao <= 0) {
                throw new IllegalArgumentException("Os valores de taxa e reposição devem ser maiores que zero.");
            }

            final boolean sucesso;
            if (idEmEdicao == null) {
                Item novo = new Item(sistema.gerarProximoIdItem(), nome, descricao,
                        taxa, estado, reposicao, categoria, fornecedor);
                sucesso = sistema.cadastrarItem(novo);
            } else {
                Item atual = sistema.buscarItem(idEmEdicao);
                if (atual == null) throw new IllegalArgumentException("Item não encontrado.");
                atual.setNome(nome);
                atual.setDescricao(descricao);
                atual.setTaxaDiaria(taxa);
                atual.setEstadoConservacao(estado);
                atual.setValorReposicao(reposicao);
                atual.setCategoria(categoria);
                atual.setFornecedor(fornecedor);
                sucesso = sistema.atualizarItem(atual);
            }

            if (!sucesso) throw new IllegalArgumentException("Operação não realizada. Verifique dados duplicados.");
            cancelar();
            atualizar();
            alerta(Alert.AlertType.INFORMATION, "Sucesso", "Item salvo com sucesso.");
        });
    }

    @FXML private void desativar() {
        executar("desativar item", () -> {
            exigirSessao();
            Item f = selecionado();
            if (!f.isAtivo()) throw new IllegalArgumentException("Item já está inativo.");
            if (!confirmar("Desativar item", "Confirma a desativação de " + f.getNome() + "?")) return;
            if (!sistema.excluirItem(f.getId()))
                throw new IllegalArgumentException("Não foi possível desativar o item.");
            atualizar();
            alerta(Alert.AlertType.INFORMATION, "Sucesso", "Item desativado.");
        });
    }

    private Item selecionado() {
        Item f = tabela.getSelectionModel().getSelectedItem();
        if (f == null) throw new IllegalArgumentException("Selecione um item na tabela.");
        return f;
    }

    private String validarTexto(String valor, String nome) {
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException(nome + " é obrigatório(a).");
        String limpo = valor.strip();
        if (limpo.contains(";") || limpo.contains("\n") || limpo.contains("\r"))
            throw new IllegalArgumentException(nome + " não pode conter ponto e vírgula ou quebras de linha (CSV).");
        return limpo;
    }
    
    @FXML private void abrirUsuarios() { 
        executar("navegar para usuarios", () -> { 
            exigirSessao(); 
            if (usuario instanceof entidades.Administrador) {
                navegacao.accept("UsuariosAdministrador"); 
            } else {
                navegacao.accept("UsuariosFuncionario"); 
            }
        }); 
    }
    @FXML private void abrirItens() { atualizar(); }
    @FXML private void abrirCategorias() { 
        executar("navegar para categorias", () -> { 
            exigirSessao(); 
            if (usuario instanceof entidades.Administrador) {
                navegacao.accept("CategoriasAdministrador"); 
            } else {
                navegacao.accept("CategoriasFuncionario"); 
            }
        }); 
    }
    @FXML private void abrirFornecedores() { 
        executar("navegar para fornecedores", () -> { 
            exigirSessao(); 
            if (usuario instanceof entidades.Administrador) {
                navegacao.accept("FornecedoresAdministrador"); 
            } else {
                navegacao.accept("FornecedoresFuncionario"); 
            }
        }); 
    }
    @FXML private void abrirMultas() { executar("navegar para multas", () -> { exigirSessao(); navegacao.accept("multas"); }); }
    @FXML private void encerrarSessao() { executar("encerrar sessão", () -> { exigirSessao(); sair.run(); }); }

    private void executar(String acao, Runnable operacao) {
        try { operacao.run(); }
        catch (IllegalArgumentException | SecurityException e) { alerta(Alert.AlertType.WARNING, "Atenção", e.getMessage()); }
        catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Falha ao " + acao, e);
            alerta(Alert.AlertType.ERROR, "Erro", "Não foi possível " + acao + ". Consulte o log.");
        }
    }

    private boolean confirmar(String titulo, String mensagem) {
        Alert dialogo = new Alert(Alert.AlertType.CONFIRMATION, mensagem, ButtonType.YES, ButtonType.NO);
        dialogo.setTitle(titulo); dialogo.setHeaderText(null);
        Optional<ButtonType> resposta = dialogo.showAndWait();
        return resposta.isPresent() && resposta.get() == ButtonType.YES;
    }

    private void alerta(Alert.AlertType tipo, String titulo, String texto) {
        Alert alerta = new Alert(tipo, texto, ButtonType.OK);
        alerta.setTitle(titulo); alerta.setHeaderText(null); alerta.showAndWait();
    }
}
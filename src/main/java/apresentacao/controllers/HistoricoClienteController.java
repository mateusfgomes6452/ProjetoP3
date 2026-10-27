package apresentacao.controllers;

import java.util.Comparator;
import java.util.List;

import apresentacao.base.BaseControllersRelatorios;
import entidades.Cliente;
import entidades.ContratoAluguel;
import entidades.Usuario;
import facade.SistemaFacade;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;
import apresentacao.util.Mensagens;
import apresentacao.util.Pilulas;

/** Aba "Histórico do cliente" (RelatorioHistoricoCliente.fxml): todos os contratos de um cliente. */
public final class HistoricoClienteController extends BaseControllersRelatorios {

    @FXML private ComboBox<Usuario> cbCliente;
    @FXML private Button btnExportar;
    @FXML private Label lblTotalContratos;
    @FXML private Label lblTotalAlugueis;
    @FXML private Label lblTotalMultas;
    @FXML private Label lblAviso;
    @FXML private TableView<ContratoAluguel> tabela;
    @FXML private TableColumn<ContratoAluguel, String> colId;
    @FXML private TableColumn<ContratoAluguel, String> colItem;
    @FXML private TableColumn<ContratoAluguel, String> colRetirada;
    @FXML private TableColumn<ContratoAluguel, String> colPrevista;
    @FXML private TableColumn<ContratoAluguel, ContratoAluguel> colStatus;
    @FXML private TableColumn<ContratoAluguel, String> colValor;
    @FXML private TableColumn<ContratoAluguel, String> colMulta;

    private final ObservableList<ContratoAluguel> contratos = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cbCliente.setConverter(new StringConverter<Usuario>() {
            @Override
            public String toString(Usuario u) {
                if (u == null) {
                    return "";
                }
                return u.getNome() + "  ·  #" + u.getId() + (u.isAtivo() ? "" : "  (inativo)");
            }

            @Override
            public Usuario fromString(String texto) {
                return null;
            }
        });
        cbCliente.valueProperty().addListener((obs, antes, cliente) -> carregarHistorico(cliente));

        colId.setCellValueFactory(c -> new ReadOnlyStringWrapper("#" + c.getValue().getId()));
        colItem.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getItem().getNome()));
        colRetirada.setCellValueFactory(c -> new ReadOnlyStringWrapper(Formatador.data(c.getValue().getDataRetirada())));
        colPrevista.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(Formatador.data(c.getValue().getDataDevolucaoPrevista())));
        colValor.setCellValueFactory(c -> new ReadOnlyStringWrapper(Formatador.moeda(c.getValue().getValorTotal())));
        colMulta.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getValorMulta() > 0 ? Formatador.moeda(c.getValue().getValorMulta()) : "—"));

        colStatus.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue()));
        colStatus.setCellFactory(coluna -> new TableCell<ContratoAluguel, ContratoAluguel>() {
            @Override
            protected void updateItem(ContratoAluguel contrato, boolean vazio) {
                super.updateItem(contrato, vazio);
                setText(null);
                setGraphic(vazio || contrato == null ? null : pilulaStatus(contrato));
            }
        });

        tabela.setItems(contratos);
        tabela.setPlaceholder(new Label("Selecione um cliente para ver o histórico."));
        Mensagens.esconder(lblAviso);
    }

    /** Chamado por RelatoriosController.configurar: guarda a sessão e carrega os dados. */
    public void configurar(SistemaFacade sistema, Usuario usuario) {
        iniciarSessao(sistema, usuario);
        atualizar();
    }

    /** Recarrega a lista de clientes e o histórico do cliente selecionado. */
    @FXML
    public void atualizar() {
        executar("consultar histórico do cliente", () -> {
            exigirSessao();
            Usuario anterior = cbCliente.getValue();

            List<Usuario> clientes = sistema.listarUsuarios().stream()
                    .filter(Cliente.class::isInstance)
                    .sorted(Comparator.comparing(Usuario::getNome, String.CASE_INSENSITIVE_ORDER))
                    .toList();
            cbCliente.setItems(FXCollections.observableArrayList(clientes));

            // mantém o cliente escolhido (se ainda existir)
            Usuario mantido = anterior == null ? null
                    : clientes.stream().filter(c -> c.getId() == anterior.getId()).findFirst().orElse(null);
            cbCliente.setValue(mantido);
            carregarHistorico(mantido);
            Mensagens.esconder(lblAviso);
        });
    }

    @FXML
    private void exportar() {
        executar("exportar histórico do cliente", () -> {
            exigirSessao();
            if (contratos.isEmpty()) {
                throw new IllegalArgumentException("Não há contratos para exportar.");
            }
            sistema.exportarHistoricoCliente(List.copyOf(contratos));
            Mensagens.sucesso(lblAviso, "CSV salvo em relatorios/historico_cliente.csv");
        });
    }

    private void carregarHistorico(Usuario cliente) {
        if (cliente == null) {
            contratos.clear();
            tabela.setPlaceholder(new Label("Selecione um cliente para ver o histórico."));
        } else {
            contratos.setAll(sistema.listarHistoricoCliente(cliente.getId()));
            tabela.setPlaceholder(new Label("Este cliente ainda não possui contratos."));
        }
        btnExportar.setDisable(contratos.isEmpty());
        lblTotalContratos.setText(String.valueOf(contratos.size()));
        lblTotalAlugueis.setText(Formatador.moeda(contratos.stream().mapToDouble(ContratoAluguel::getValorTotal).sum()));
        lblTotalMultas.setText(Formatador.moeda(contratos.stream().mapToDouble(ContratoAluguel::getValorMulta).sum()));
    }

    private Label pilulaStatus(ContratoAluguel contrato) {
        if (sistema.contratoEstaAtrasado(contrato)) {
            return Pilulas.perigo("Atrasado");
        }
        switch (contrato.getStatus()) {
            case "ATIVO":
                return Pilulas.sucesso("Ativo");
            case "FINALIZADO":
                return Pilulas.neutra("Finalizado");
            case "CANCELADO":
                return Pilulas.alerta("Cancelado");
            default:
                return Pilulas.neutra(contrato.getStatus());
        }
    }
}

package apresentacao.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import apresentacao.base.BaseControllersRelatorios;
import apresentacao.util.Formatador;
import entidades.ContratoAluguel;
import entidades.Usuario;
import facade.SistemaFacade;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;
import apresentacao.util.Mensagens;

/** Aba "Faturamento" (RelatorioFaturamento.fxml): contratos retirados dentro de um período, com totais. */
public final class FaturamentoController extends BaseControllersRelatorios {

    private static final String CAMPO_INVALIDO = "input-invalido";

    @FXML private DatePicker dataInicial;
    @FXML private DatePicker dataFinal;
    @FXML private Button btnExportar;
    @FXML private Button btnExportarPdf;
    @FXML private Label lblAviso;
    @FXML private Label lblTotalContratos;
    @FXML private Label lblTotalAlugueis;
    @FXML private Label lblTotalMultas;
    @FXML private Label lblTotalGeral;
    @FXML private TableView<ContratoAluguel> tabela;
    @FXML private TableColumn<ContratoAluguel, String> colId;
    @FXML private TableColumn<ContratoAluguel, String> colCliente;
    @FXML private TableColumn<ContratoAluguel, String> colItem;
    @FXML private TableColumn<ContratoAluguel, String> colRetirada;
    @FXML private TableColumn<ContratoAluguel, String> colAluguel;
    @FXML private TableColumn<ContratoAluguel, String> colMulta;
    @FXML private TableColumn<ContratoAluguel, String> colTotal;

    private final ObservableList<ContratoAluguel> contratos = FXCollections.observableArrayList();

    // período usado na última geração (é o que vai para o CSV)
    private LocalDate inicioGerado;
    private LocalDate fimGerado;

    @FXML
    private void initialize() {
        configurarDatePicker(dataInicial);
        configurarDatePicker(dataFinal);
        LocalDate hoje = LocalDate.now(ZoneId.systemDefault());
        dataInicial.setValue(hoje.withDayOfMonth(1));
        dataFinal.setValue(hoje);

        colId.setCellValueFactory(c -> new ReadOnlyStringWrapper("#" + c.getValue().getId()));
        colCliente.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getCliente().getNome()));
        colItem.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getItem().getNome()));
        colRetirada.setCellValueFactory(c -> new ReadOnlyStringWrapper(Formatador.data(c.getValue().getDataRetirada())));
        colAluguel.setCellValueFactory(c -> new ReadOnlyStringWrapper(Formatador.moeda(c.getValue().getValorTotal())));
        colMulta.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getValorMulta() > 0 ? Formatador.moeda(c.getValue().getValorMulta()) : "—"));
        colTotal.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                Formatador.moeda(c.getValue().getValorTotal() + c.getValue().getValorMulta())));

        tabela.setItems(contratos);
        tabela.setPlaceholder(new Label("Escolha o período e gere o relatório."));
        Mensagens.esconder(lblAviso);
        btnExportarPdf.setDisable(true);
    }

    /** Chamado por RelatoriosController.configurar: guarda a sessão e gera o primeiro relatório. */
    public void configurar(SistemaFacade sistema, Usuario usuario) {
        iniciarSessao(sistema, usuario);
        gerar();
    }

    /** Ao abrir a aba: refaz o último período gerado, para refletir contratos criados em outras telas. */
    public void atualizar() {
        executar("atualizar faturamento", () -> {
            exigirSessao();
            if (inicioGerado == null) {
                gerar();
            } else {
                carregar(inicioGerado, fimGerado);
            }
            Mensagens.esconder(lblAviso);
        });
    }

    @FXML
    private void gerar() {
        executar("gerar faturamento", () -> {
            exigirSessao();
            limparMarcas();

            LocalDate inicio = dataInicial.getValue();
            LocalDate fim = dataFinal.getValue();

            if (inicio == null || fim == null) {
                marcarInvalido(inicio == null ? dataInicial : dataFinal);
                throw new IllegalArgumentException("Informe a data inicial e a data final.");
            }
            if (fim.isBefore(inicio)) {
                marcarInvalido(dataFinal);
                throw new IllegalArgumentException("A data final não pode ser anterior à data inicial.");
            }
            carregar(inicio, fim);
        });
    }

    @FXML
    private void exportar() {
        executar("exportar faturamento", () -> {
            exigirSessao();
            if (contratos.isEmpty() || inicioGerado == null) {
                throw new IllegalArgumentException("Gere um relatório com contratos antes de exportar.");
            }
            sistema.exportarFaturamento(new ArrayList<>(contratos), inicioGerado.toString(), fimGerado.toString());
            Mensagens.sucesso(lblAviso, "CSV salvo em relatorios/faturamento.csv");
        });
    }

    @FXML
    private void exportarPdf() {
        executar("exportar PDF", () -> {
            exigirSessao();
            if (contratos.isEmpty() || inicioGerado == null) {
                throw new IllegalArgumentException("Gere um relatório com contratos antes de exportar.");
            }
            sistema.exportarPdfFaturamento(new ArrayList<>(contratos), inicioGerado.toString(), fimGerado.toString());
            Mensagens.sucesso(lblAviso, "PDF salvo em relatorios/faturamento.pdf");
        });
    }

    // ---------- dados e validação ----------

    private void carregar(LocalDate inicio, LocalDate fim) {
        List<ContratoAluguel> encontrados = sistema.listarContratosPorPeriodo(inicio, fim);
        contratos.setAll(encontrados);
        inicioGerado = inicio;
        fimGerado = fim;

        double alugueis = encontrados.stream().mapToDouble(ContratoAluguel::getValorTotal).sum();
        double multas = encontrados.stream().mapToDouble(ContratoAluguel::getValorMulta).sum();
        lblTotalContratos.setText(String.valueOf(encontrados.size()));
        lblTotalAlugueis.setText(Formatador.moeda(alugueis));
        lblTotalMultas.setText(Formatador.moeda(multas));
        lblTotalGeral.setText(Formatador.moeda(alugueis + multas));

        tabela.setPlaceholder(new Label("Nenhum contrato encontrado no período."));
        btnExportar.setDisable(encontrados.isEmpty());
        btnExportarPdf.setDisable(encontrados.isEmpty());
    }

    private void marcarInvalido(DatePicker campo) {
        campo.getStyleClass().add(CAMPO_INVALIDO);
    }

    private void limparMarcas() {
        dataInicial.getStyleClass().remove(CAMPO_INVALIDO);
        dataFinal.getStyleClass().remove(CAMPO_INVALIDO);
    }

    private void configurarDatePicker(DatePicker picker) {
        picker.setPromptText("dd/mm/aaaa");
        picker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate data) {
                return data == null ? "" : data.format(Formatador.DATA_BR);
            }

            @Override
            public LocalDate fromString(String texto) {
                if (texto == null || texto.isBlank()) {
                    return null;
                }
                try {
                    return LocalDate.parse(texto.trim(), Formatador.DATA_BR);
                } catch (DateTimeParseException e) {
                    return null;
                }
            }
        });
    }
}

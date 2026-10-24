package apresentacao.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import entidades.ContratoAluguel;
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
import main.ConfiguracaoSistema;

/** Aba "Faturamento" (RelatorioFaturamento.fxml): contratos retirados dentro de um período, com totais. */
public class FaturamentoController {

    @FXML private DatePicker dataInicial;
    @FXML private DatePicker dataFinal;
    @FXML private Button btnExportar;
    @FXML private Label lblErro;
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
        LocalDate hoje = LocalDate.now();
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
        Mensagens.esconder(lblErro);
        Mensagens.esconder(lblAviso);
        gerar();
    }

    /** Reaplica o período escolhido (chamado ao abrir a aba), para refletir contratos criados em outras telas. */
    public void atualizar() {
        gerar();
        Mensagens.esconder(lblAviso);
    }

    @FXML
    private void gerar() {
        limparErro();

        LocalDate inicio = dataInicial.getValue();
        LocalDate fim = dataFinal.getValue();

        if (inicio == null || fim == null) {
            mostrarErro("Informe a data inicial e a data final.", inicio == null ? dataInicial : dataFinal);
            return;
        }
        if (fim.isBefore(inicio)) {
            mostrarErro("A data final não pode ser anterior à data inicial.", dataFinal);
            return;
        }

        List<ContratoAluguel> encontrados = ConfiguracaoSistema.getSistema().listarContratosPorPeriodo(inicio, fim);
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
    }

    @FXML
    private void exportar() {
        ConfiguracaoSistema.getSistema().exportarFaturamento(
                new ArrayList<>(contratos), inicioGerado.toString(), fimGerado.toString());
        Mensagens.sucesso(lblAviso, "CSV salvo em relatorios/faturamento.csv");
    }

    // ---------- validação ----------

    private void mostrarErro(String mensagem, DatePicker campo) {
        Mensagens.erro(lblErro, mensagem);
        campo.getStyleClass().add("input-invalido");
    }

    private void limparErro() {
        Mensagens.esconder(lblErro);
        dataInicial.getStyleClass().remove("input-invalido");
        dataFinal.getStyleClass().remove("input-invalido");
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

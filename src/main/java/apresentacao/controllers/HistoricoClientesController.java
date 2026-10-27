package apresentacao.controllers;

import apresentacao.base.BaseContratosCliente;
import apresentacao.util.Formatador;
import apresentacao.util.Pilulas;
import entidades.ContratoAluguel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

import java.util.Comparator;
import java.util.List;


public final class HistoricoClientesController extends BaseContratosCliente {

    private enum Filtro { TODOS, ATIVOS, FINALIZADOS, CANCELADOS }

    @FXML private Label lblTotal;
    @FXML private Label lblFinalizados;
    @FXML private Label lblCancelados;
    @FXML private TableColumn<ContratoAluguel, String> colDevolucao;
    @FXML private TableColumn<ContratoAluguel, String> colStatus;
    @FXML private TableColumn<ContratoAluguel, String> colMulta;
    @FXML private Button btnTodos;
    @FXML private Button btnAtivos;
    @FXML private Button btnFinalizados;
    @FXML private Button btnCancelados;
    @FXML private Label lblDetalheReal;
    @FXML private Label lblDetalheStatus;
    @FXML private Label lblDetalheMulta;

    private Filtro filtro = Filtro.TODOS;

    @FXML
    private void initialize() {
        prepararTabelaContratos("Você ainda não possui contratos registrados.");

        colDevolucao.setCellValueFactory(c -> prop(Formatador.data(c.getValue().getDataDevolucaoReal())));
        colMulta.setCellValueFactory(c -> prop(Formatador.moeda(c.getValue().getValorMulta())));
        colStatus.setCellValueFactory(c -> prop(c.getValue().getStatus()));
        // Mostra o status como etiqueta colorida (identidade visual: ativo verde, finalizado cinza)
        colStatus.setCellFactory(coluna -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean vazio) {
                super.updateItem(status, vazio);
                setText(null);
                setGraphic(vazio || status == null ? null : etiquetaStatus(status));
            }
        });
    }

    @Override
    @FXML
    protected void atualizar() {
        executar("atualizar histórico", () -> {
            exigirSessao();
            List<ContratoAluguel> doCliente = sistema.listarHistoricoCliente(cliente.getId()).stream()
                    .filter(this::pertenceAoCliente)
                    .sorted(Comparator.comparingInt(ContratoAluguel::getId).reversed())
                    .toList();
            contratos.setAll(doCliente);
            lblTotal.setText(String.valueOf(contratos.size()));
            lblFinalizados.setText(String.valueOf(contratos.stream().filter(ContratoAluguel::estaFinalizado).count()));
            lblCancelados.setText(String.valueOf(contratos.stream().filter(ContratoAluguel::estaCancelado).count()));
            filtrar();
        });
    }

    @FXML private void filtrarTodos()       { mudarFiltro(Filtro.TODOS, btnTodos); }
    @FXML private void filtrarAtivos()      { mudarFiltro(Filtro.ATIVOS, btnAtivos); }
    @FXML private void filtrarFinalizados() { mudarFiltro(Filtro.FINALIZADOS, btnFinalizados); }
    @FXML private void filtrarCancelados()  { mudarFiltro(Filtro.CANCELADOS, btnCancelados); }

    private void mudarFiltro(Filtro novo, Button selecionado) {
        filtro = novo;
        destacarFiltro(selecionado, btnTodos, btnAtivos, btnFinalizados, btnCancelados);
        filtrar();
    }

    @Override
    protected void filtrar() {
        aplicarFiltro(c -> switch (filtro) {
            case TODOS -> true;
            case ATIVOS -> c.estaAtivo();
            case FINALIZADOS -> c.estaFinalizado();
            case CANCELADOS -> c.estaCancelado();
        });
    }

    @FXML
    private void verDetalhes() {
        executar("ver detalhes do contrato", () -> {
            exigirSessao();
            ContratoAluguel contrato = contratoSelecionado();
            preencherDetalhesComuns(contrato);
            lblDetalheReal.setText("Devolução efetiva: " + Formatador.data(contrato.getDataDevolucaoReal()));
            lblDetalheStatus.setText("Status: " + contrato.getStatus());
            lblDetalheMulta.setText("Multa: " + textoMulta(contrato));
            mostrarDetalhes();
        });
    }

   
    private Label etiquetaStatus(String status) {
        return switch (status) {
            case "ATIVO" -> Pilulas.sucesso("Ativo");
            case "FINALIZADO" -> Pilulas.neutra("Finalizado");
            case "CANCELADO" -> Pilulas.perigo("Cancelado");
            default -> Pilulas.neutra(status);
        };
    }


    private String textoMulta(ContratoAluguel contrato) {
        if (contrato.getValorMulta() <= 0) {
            return "Sem multa";
        }
        return Formatador.moeda(contrato.getValorMulta()) + (contrato.isMultaPaga() ? " (paga)" : " (pendente)");
    }
}

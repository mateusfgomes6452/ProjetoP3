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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;


public final class MeusAlugueisClientesController extends BaseContratosCliente {

    private enum Filtro { TODOS, EM_DIA, ATRASADOS }

    private static final String ATRASADO = "Atrasado";
    private static final String EM_DIA = "Em dia";

    @FXML private Label lblAtivos;
    @FXML private Label lblEmDia;
    @FXML private Label lblAtrasados;
    @FXML private TableColumn<ContratoAluguel, String> colSituacao;
    @FXML private Button btnTodos;
    @FXML private Button btnEmDia;
    @FXML private Button btnAtrasados;
    @FXML private Label lblDetalheSituacao;

    private Filtro filtro = Filtro.TODOS;

    @FXML
    private void initialize() {
        prepararTabelaContratos("Você não possui aluguéis ativos.");

        colSituacao.setCellValueFactory(c -> prop(situacao(c.getValue())));
        // Mostra a situação como etiqueta colorida: vermelha (Atrasado) ou verde (Em dia)
        colSituacao.setCellFactory(coluna -> new TableCell<>() {
            @Override
            protected void updateItem(String situacao, boolean vazio) {
                super.updateItem(situacao, vazio);
                setText(null);
                if (vazio || situacao == null) {
                    setGraphic(null);
                } else {
                    setGraphic(ATRASADO.equals(situacao) ? Pilulas.perigo(situacao) : Pilulas.sucesso(situacao));
                }
            }
        });
    }

    @Override
    @FXML
    protected void atualizar() {
        executar("atualizar meus aluguéis", () -> {
            exigirSessao();
            List<ContratoAluguel> ativos = sistema.listarContratosAtivos().stream()
                    .filter(c -> c.estaAtivo() && pertenceAoCliente(c))
                    .sorted(Comparator.comparingInt(ContratoAluguel::getId).reversed())
                    .toList();
            contratos.setAll(ativos);
            long atrasados = contratos.stream().filter(sistema::contratoEstaAtrasado).count();
            lblAtivos.setText(String.valueOf(contratos.size()));
            lblAtrasados.setText(String.valueOf(atrasados));
            lblEmDia.setText(String.valueOf(contratos.size() - atrasados));
            filtrar();
        });
    }

    @FXML private void filtrarTodos()     { mudarFiltro(Filtro.TODOS, btnTodos); }
    @FXML private void filtrarEmDia()     { mudarFiltro(Filtro.EM_DIA, btnEmDia); }
    @FXML private void filtrarAtrasados() { mudarFiltro(Filtro.ATRASADOS, btnAtrasados); }

    private void mudarFiltro(Filtro novo, Button selecionado) {
        filtro = novo;
        destacarFiltro(selecionado, btnTodos, btnEmDia, btnAtrasados);
        filtrar();
    }

    @Override
    protected void filtrar() {
        aplicarFiltro(c -> filtro == Filtro.TODOS
                || (filtro == Filtro.ATRASADOS) == sistema.contratoEstaAtrasado(c));
    }

    @FXML
    private void verDetalhes() {
        executar("ver detalhes do aluguel", () -> {
            exigirSessao();
            ContratoAluguel contrato = contratoSelecionado();
            preencherDetalhesComuns(contrato);
            lblDetalheSituacao.setText("Situação: " + textoSituacaoDetalhada(contrato));
            mostrarDetalhes();
        });
    }

    private String situacao(ContratoAluguel c) {
        return sistema != null && sistema.contratoEstaAtrasado(c) ? ATRASADO : EM_DIA;
    }

    private long diasDeAtraso(ContratoAluguel c) {
        if (!sistema.contratoEstaAtrasado(c)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDate.parse(c.getDataDevolucaoPrevista()), LocalDate.now(ZoneId.systemDefault()));
    }

    
    private String textoSituacaoDetalhada(ContratoAluguel c) {
        long dias = diasDeAtraso(c);
        if (dias == 0) {
            return EM_DIA;
        }
        String texto = ATRASADO + " há " + dias + " dia(s).";
        if (c.getItem() != null) {
            // A multa é calculada pelo próprio sistema (mesma regra usada ao finalizar o contrato)
            double multa = sistema.calcularMulta(dias, c.getItem().getTaxaDiaria());
            texto += " Se devolvido hoje, a multa será de " + Formatador.moeda(multa) + ".";
        }
        return texto;
    }
}

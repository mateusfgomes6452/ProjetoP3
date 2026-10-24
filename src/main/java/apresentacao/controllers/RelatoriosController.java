package apresentacao.controllers;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

/**
 * Controller de Relatorios.fxml: menu lateral, título e as 4 abas.
 * Cada aba é um arquivo FXML próprio (fx:include) com o seu controller:
 * ItensDisponiveisController, HistoricoClienteController, ItensAlugadosController e FaturamentoController.
 */
public class RelatoriosController {

    @FXML private BorderPane raiz;

    @FXML private Button btnAbaItens;
    @FXML private Button btnAbaHistorico;
    @FXML private Button btnAbaAlugados;
    @FXML private Button btnAbaFaturamento;

    // preenchidos automaticamente pelo fx:include (fx:id = abaItens -> abaItensController)
    @FXML private Node abaItens;
    @FXML private Node abaHistorico;
    @FXML private Node abaAlugados;
    @FXML private Node abaFaturamento;
    @FXML private ItensDisponiveisController abaItensController;
    @FXML private HistoricoClienteController abaHistoricoController;
    @FXML private ItensAlugadosController abaAlugadosController;
    @FXML private FaturamentoController abaFaturamentoController;

    @FXML
    private void initialize() {
        mostrarAba(btnAbaItens, abaItens);
    }

    // ---------- abas ----------

    @FXML
    private void abrirAbaItens() {
        abaItensController.atualizar();
        mostrarAba(btnAbaItens, abaItens);
    }

    @FXML
    private void abrirAbaHistorico() {
        abaHistoricoController.atualizar();
        mostrarAba(btnAbaHistorico, abaHistorico);
    }

    @FXML
    private void abrirAbaAlugados() {
        abaAlugadosController.atualizar();
        mostrarAba(btnAbaAlugados, abaAlugados);
    }

    @FXML
    private void abrirAbaFaturamento() {
        abaFaturamentoController.atualizar();
        mostrarAba(btnAbaFaturamento, abaFaturamento);
    }

    private void mostrarAba(Button botaoAtivo, Node abaAtiva) {
        for (Button botao : List.of(btnAbaItens, btnAbaHistorico, btnAbaAlugados, btnAbaFaturamento)) {
            botao.getStyleClass().remove("filter-active");
        }
        botaoAtivo.getStyleClass().add("filter-active");

        for (Node aba : List.of(abaItens, abaHistorico, abaAlugados, abaFaturamento)) {
            boolean ativa = aba == abaAtiva;
            aba.setVisible(ativa);
            aba.setManaged(ativa);
        }
    }

    // ---------- menu lateral ----------

    @FXML
    private void abrirFornecedores() {
        Navegacao.irPara(raiz, "/fxml/FornecedoresAdministrador.fxml");
    }

    @FXML
    private void abrirMultas() {
        Navegacao.irPara(raiz, "/fxml/Multas.fxml");
    }

    @FXML
    private void encerrarSessao() {
        Navegacao.sair();
    }
}

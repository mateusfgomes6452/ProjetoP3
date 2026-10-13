package apresentacao;

import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class AdministradorController {

    private static final String ESTILO_NORMAL =
            "-fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: normal;";

    private static final String ESTILO_NEGRITO =
            "-fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;";

    @FXML private StackPane conteudo;

    @FXML private Label usuarios;
    @FXML private Label itens;
    @FXML private Label categorias;
    @FXML private Label fornecedores;
    @FXML private Label contratosAlugueis;
    @FXML private Label multas;
    @FXML private Label relatorios;

    private List<Label> botoesMenu;
    private Label botaoAtivo;

    @FXML
    private void initialize() {
        botoesMenu = List.of(usuarios, itens, categorias,
                fornecedores, contratosAlugueis, multas, relatorios);

        for (Label l : botoesMenu) {
            l.setStyle(ESTILO_NORMAL);
            l.setOnMouseEntered(e -> l.setStyle(ESTILO_NEGRITO));
            l.setOnMouseExited(e -> l.setStyle(l == botaoAtivo ? ESTILO_NEGRITO : ESTILO_NORMAL));
        }
    }

    @FXML
    private void abrirUsuarios() {
        carregar("/conteudoUsuarios.fxml", usuarios);
    }

    @FXML
    private void abrirItens() {
        carregar("/conteudoItens.fxml", itens);
    }
    
    @FXML
    private void abrirCategorias() {
        carregar("/conteudoCategorias.fxml", usuarios);
    }

    @FXML
    private void abrirFornecedores() {
        carregar("/conteudoFornecedores.fxml", itens);
    }
    
    @FXML
    private void abrirContratosAlugueis() {
        carregar("/conteudoContratosAlugueis.fxml", usuarios);
    }

    @FXML
    private void abrirMultas() {
        carregar("/conteudoMultas.fxml", itens);
    }
    
    @FXML
    private void abrirRelatorios() {
        carregar("/conteudoRelatorios.fxml", itens);
    }

    private void carregar(String arquivoFxml, Label novoAtivo) {
        try {
            Parent tela = FXMLLoader.load(getClass().getResource(arquivoFxml));
            conteudo.getChildren().setAll(tela);
            marcarAtivo(novoAtivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void marcarAtivo(Label novoAtivo) {
        botaoAtivo = novoAtivo;
        for (Label l : botoesMenu) {
            l.setStyle(l == botaoAtivo ? ESTILO_NEGRITO : ESTILO_NORMAL);
        }
    }
}

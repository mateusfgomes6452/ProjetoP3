package apresentacao.controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public final class PreviaPdfController {

    private static final float DPI = 96;          
    private static final double LARGURA_PAGINA = 680;

    @FXML private Label lblTitulo;
    @FXML private Label lblArquivo;
    @FXML private VBox paginas;

    public void mostrar(String titulo, File pdf) {
        lblTitulo.setText(titulo);
        lblArquivo.setText("Arquivo salvo em " + pdf.getPath());
        paginas.getChildren().clear();

        try (PDDocument documento = Loader.loadPDF(pdf)) {
            PDFRenderer desenhista = new PDFRenderer(documento);
            for (int i = 0; i < documento.getNumberOfPages(); i++) {
                BufferedImage imagem = desenhista.renderImageWithDPI(i, DPI);
                ImageView pagina = new ImageView(SwingFXUtils.toFXImage(imagem, null));
                pagina.setPreserveRatio(true);
                pagina.setFitWidth(LARGURA_PAGINA);
                paginas.getChildren().add(pagina);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível abrir o PDF " + pdf.getPath(), e);
        }
    }

    @FXML
    private void fechar() {
        ((Stage) paginas.getScene().getWindow()).close();
    }
}
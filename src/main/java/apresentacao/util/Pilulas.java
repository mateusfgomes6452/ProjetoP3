package apresentacao.controllers

import javafx.scene.control.Label;

/** Etiquetas de status coloridas (classes status-pill-* de identidade.css e relatorios.css). */
public final class Pilulas {

    private Pilulas() {
    }

    public static Label sucesso(String texto) {
        return criar(texto, "status-pill-success");
    }

    public static Label perigo(String texto) {
        return criar(texto, "status-pill-danger");
    }

    public static Label neutra(String texto) {
        return criar(texto, "status-pill-neutral");
    }

    public static Label alerta(String texto) {
        return criar(texto, "status-pill-warning");
    }

    private static Label criar(String texto, String estilo) {
        Label etiqueta = new Label(texto);
        etiqueta.getStyleClass().add(estilo);
        return etiqueta;
    }
}

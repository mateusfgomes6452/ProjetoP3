package apresentacao.controllers;

import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;

/** Mensagens curtas de sucesso/erro exibidas em um Label da própria tela. */
public final class Mensagens {

    private Mensagens() {
    }

    /** Mostra a mensagem em verde e a esconde sozinha depois de alguns segundos. */
    public static void sucesso(Label alvo, String texto) {
        exibir(alvo, texto, "texto-sucesso");
        PauseTransition espera = new PauseTransition(Duration.seconds(6));
        espera.setOnFinished(e -> esconder(alvo));
        espera.play();
    }

    /** Mostra a mensagem em vermelho até ser limpa com {@link #esconder(Label)}. */
    public static void erro(Label alvo, String texto) {
        exibir(alvo, texto, "texto-erro");
    }

    public static void esconder(Label alvo) {
        alvo.setVisible(false);
        alvo.setManaged(false);
    }

    private static void exibir(Label alvo, String texto, String estilo) {
        alvo.getStyleClass().removeAll("texto-sucesso", "texto-erro");
        alvo.getStyleClass().add(estilo);
        alvo.setText(texto);
        alvo.setVisible(true);
        alvo.setManaged(true);
    }
}

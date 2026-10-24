package apresentacao.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Formatador")
class FormatadorTest {

    @Test
    @DisplayName("moeda usa o padrão brasileiro com separador de milhar e vírgula decimal")
    void moedaPadraoBrasileiro() {
        assertEquals("R$ 1.450,00", Formatador.moeda(1450.0));
        assertEquals("R$ 0,00", Formatador.moeda(0));
        assertEquals("R$ 12,50", Formatador.moeda(12.5));
    }

    @Test
    @DisplayName("data converte ISO para dd/MM/yyyy")
    void dataValida() {
        assertEquals("02/06/2026", Formatador.data("2026-06-02"));
    }

    @Test
    @DisplayName("data vazia, em branco ou nula vira traço")
    void dataVazia() {
        assertEquals("—", Formatador.data(null));
        assertEquals("—", Formatador.data(""));
        assertEquals("—", Formatador.data("   "));
    }

    @Test
    @DisplayName("data inválida é devolvida como veio")
    void dataInvalida() {
        assertEquals("amanhã", Formatador.data("amanhã"));
    }
}

package entidades;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Multa")
class MultaTest {

    private Multa multa;

    @BeforeEach
    void setUp() {
        multa = new Multa(1, 10, "ATRASO", "Devolução em atraso", 45.0, "2026-01-15");
    }

    @Test
    @DisplayName("deve nascer como não paga")
    void deveNascerNaoPaga() {
        assertFalse(multa.isPaga());
    }

    @Test
    @DisplayName("pagar deve marcar a multa como paga")
    void pagarDeveMarcarComoPaga() {
        multa.pagar();

        assertTrue(multa.isPaga());
    }

    @Test
    @DisplayName("deve manter os dados informados na criação")
    void deveManterDadosDeConstrucao() {
        assertEquals(1, multa.getId());
        assertEquals(10, multa.getIdContrato());
        assertEquals("ATRASO", multa.getTipo());
        assertEquals(45.0, multa.getValor(), 0.0001);
    }
}

package entidades;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ContratoAluguel")
class ContratoAluguelTest {

    private ContratoAluguel contrato;

    @BeforeEach
    void setUp() {
        Categoria categoria = new Categoria(1, "Ferramentas", "Ferramentas em geral");
        Fornecedor fornecedor = new Fornecedor(1, "Fornecedor XPTO", "00.000.000/0001-00", "contato@xpto.com", "8199999999");
        Item item = new Item(1, "Furadeira", "Furadeira de impacto", 20.0, "BOM", 500.0, categoria, fornecedor);
        Cliente cliente = new Cliente(1, "Maria Silva", "maria@email.com", "111.111.111-11", "senha123");

        contrato = new ContratoAluguel(1, cliente, item, "2026-01-01", "2026-01-10", 100.0);
    }

    @Test
    @DisplayName("deve nascer com status ATIVO e sem multa")
    void deveNascerAtivoSemMulta() {
        assertTrue(contrato.estaAtivo());
        assertFalse(contrato.estaFinalizado());
        assertFalse(contrato.estaCancelado());
        assertFalse(contrato.possuiMulta());
    }

    @Test
    @DisplayName("finalizarContrato deve mudar o status para FINALIZADO e desativar o contrato")
    void finalizarContratoDeveAlterarStatus() {
        contrato.finalizarContrato();

        assertTrue(contrato.estaFinalizado());
        assertFalse(contrato.isAtivo());
    }

    @Test
    @DisplayName("cancelarContrato deve mudar o status para CANCELADO e desativar o contrato")
    void cancelarContratoDeveAlterarStatus() {
        contrato.cancelarContrato();

        assertTrue(contrato.estaCancelado());
        assertFalse(contrato.isAtivo());
    }

    @Test
    @DisplayName("possuiMulta deve retornar true somente quando o valor da multa é maior que zero")
    void possuiMultaDeveRefletirValor() {
        assertFalse(contrato.possuiMulta());

        contrato.setValorMulta(25.0);

        assertTrue(contrato.possuiMulta());
    }

    @Test
    @DisplayName("possuiMulta deve retornar false quando o valor da multa é zero")
    void possuiMultaFalsoQuandoValorZero() {
        contrato.setValorMulta(0.0);

        assertFalse(contrato.possuiMulta());
    }
}

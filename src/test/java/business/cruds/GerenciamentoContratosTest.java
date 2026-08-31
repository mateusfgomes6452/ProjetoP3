package business.cruds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import entidades.Categoria;
import entidades.Cliente;
import entidades.ContratoAluguel;
import entidades.Fornecedor;
import entidades.Item;
import support.FakeContratoRepositorio;

@DisplayName("GerenciamentoContratos")
class GerenciamentoContratosTest {

    private FakeContratoRepositorio repositorio;
    private GerenciamentoContratos gerenciamentoContratos;

    private Cliente cliente;
    private Item item;

    @BeforeEach
    void setUp() {
        repositorio = new FakeContratoRepositorio();
        gerenciamentoContratos = new GerenciamentoContratos(repositorio);

        Categoria categoria = new Categoria(1, "Ferramentas", "Ferramentas em geral");
        Fornecedor fornecedor = new Fornecedor(1, "Fornecedor XPTO", "00.000.000/0001-00", "contato@xpto.com", "8199999999");

        cliente = new Cliente(1, "Maria Silva", "maria@email.com", "111.111.111-11", "senha123");
        item = new Item(1, "Furadeira", "Furadeira de impacto", 20.0, "BOM", 500.0, categoria, fornecedor);
    }

    private ContratoAluguel novoContrato(int id) {
        return new ContratoAluguel(id, cliente, item, "2026-01-01", "2026-01-10", 100.0);
    }

   

    @Test
    @DisplayName("cadastrarContrato deve cadastrar quando o contrato é novo e o item está livre")
    void deveCadastrarContratoValido() {
        ContratoAluguel contrato = novoContrato(1);

        boolean resultado = gerenciamentoContratos.cadastrarContrato(contrato);

        assertTrue(resultado);
        assertEquals(1, gerenciamentoContratos.listarContratos().size());
    }

    @Test
    @DisplayName("cadastrarContrato deve rejeitar quando já existe contrato com o mesmo id")
    void deveRejeitarContratoComIdDuplicado() {
        gerenciamentoContratos.cadastrarContrato(novoContrato(1));

        boolean resultado = gerenciamentoContratos.cadastrarContrato(novoContrato(1));

        assertFalse(resultado);
        assertEquals(1, gerenciamentoContratos.listarContratos().size());
    }

    @Test
    @DisplayName("cadastrarContrato deve rejeitar quando o item já possui um contrato ativo (indisponível)")
    void deveRejeitarItemJaAlugado() {
        gerenciamentoContratos.cadastrarContrato(novoContrato(1));

        boolean resultado = gerenciamentoContratos.cadastrarContrato(novoContrato(2));

        assertFalse(resultado, "Não deveria permitir alugar um item que já está em um contrato ativo");
        assertEquals(1, gerenciamentoContratos.listarContratos().size());
    }

    @Test
    @DisplayName("cadastrarContrato deve permitir novo contrato para o item quando o contrato anterior não está mais ativo")
    void devePermitirQuandoContratoAnteriorFoiFinalizado() {
        ContratoAluguel contratoAntigo = novoContrato(1);
        gerenciamentoContratos.cadastrarContrato(contratoAntigo);
        gerenciamentoContratos.finalizarContrato(1);

        boolean resultado = gerenciamentoContratos.cadastrarContrato(novoContrato(2));

        assertTrue(resultado, "Item deveria estar disponível para novo contrato após a finalização do anterior");
    }

    

    @Test
    @DisplayName("buscarContrato deve retornar o contrato quando existir")
    void deveRetornarContratoExistente() {
        gerenciamentoContratos.cadastrarContrato(novoContrato(1));

        assertNotNull(gerenciamentoContratos.buscarContrato(1));
    }

    @Test
    @DisplayName("buscarContrato deve retornar null quando não existir")
    void deveRetornarNullAoBuscarContratoInexistente() {
        assertNull(gerenciamentoContratos.buscarContrato(999));
    }

    

    @Test
    @DisplayName("finalizarContrato deve retornar false quando o contrato não existe")
    void deveRetornarFalseAoFinalizarContratoInexistente() {
        assertFalse(gerenciamentoContratos.finalizarContrato(999));
    }

    @Test
    @DisplayName("finalizarContrato deve alterar o status do contrato para FINALIZADO")
    void deveFinalizarContrato() {
        gerenciamentoContratos.cadastrarContrato(novoContrato(1));

        boolean resultado = gerenciamentoContratos.finalizarContrato(1);

        assertTrue(resultado);
        assertTrue(gerenciamentoContratos.buscarContrato(1).estaFinalizado());
    }

    

    @Test
    @DisplayName("cancelarContrato deve retornar false quando o contrato não existe")
    void deveRetornarFalseAoCancelarContratoInexistente() {
        assertFalse(gerenciamentoContratos.cancelarContrato(999));
    }

    @Test
    @DisplayName("cancelarContrato deve alterar o status do contrato para CANCELADO")
    void deveCancelarContrato() {
        gerenciamentoContratos.cadastrarContrato(novoContrato(1));

        boolean resultado = gerenciamentoContratos.cancelarContrato(1);

        assertTrue(resultado);
        assertTrue(gerenciamentoContratos.buscarContrato(1).estaCancelado());
    }

   

    @Test
    @DisplayName("quitarMultaContrato deve retornar false quando o contrato não existe")
    void deveRetornarFalseAoQuitarMultaDeContratoInexistente() {
        assertFalse(gerenciamentoContratos.quitarMultaContrato(999));
    }

    @Test
    @DisplayName("quitarMultaContrato deve retornar false quando o contrato não possui multa")
    void deveRetornarFalseQuandoContratoNaoTemMulta() {
        ContratoAluguel contrato = novoContrato(1);
        gerenciamentoContratos.cadastrarContrato(contrato);

        assertFalse(gerenciamentoContratos.quitarMultaContrato(1));
    }

    @Test
    @DisplayName("quitarMultaContrato deve retornar false quando a multa já está paga")
    void deveRetornarFalseQuandoMultaDoContratoJaPaga() {
        ContratoAluguel contrato = novoContrato(1);
        contrato.setValorMulta(50.0);
        contrato.setMultaPaga(true);
        gerenciamentoContratos.cadastrarContrato(contrato);

        assertFalse(gerenciamentoContratos.quitarMultaContrato(1));
    }

    @Test
    @DisplayName("quitarMultaContrato deve quitar a multa quando ela está pendente")
    void deveQuitarMultaPendenteDoContrato() {
        ContratoAluguel contrato = novoContrato(1);
        contrato.setValorMulta(50.0);
        contrato.setMultaPaga(false);
        gerenciamentoContratos.cadastrarContrato(contrato);

        boolean resultado = gerenciamentoContratos.quitarMultaContrato(1);

        assertTrue(resultado);
        assertTrue(contrato.isMultaPaga());
    }

    

    @Test
    @DisplayName("clientePossuiMultaPendente deve retornar true quando o cliente possui multa não paga")
    void deveRetornarTrueQuandoHaMultaPendente() {
        ContratoAluguel contrato = novoContrato(1);
        contrato.setValorMulta(30.0);
        contrato.setMultaPaga(false);
        gerenciamentoContratos.cadastrarContrato(contrato);

        assertTrue(gerenciamentoContratos.clientePossuiMultaPendente(cliente.getId()));
    }

    @Test
    @DisplayName("clientePossuiMultaPendente deve retornar false quando o cliente não possui multa")
    void deveRetornarFalseQuandoNaoHaMulta() {
        gerenciamentoContratos.cadastrarContrato(novoContrato(1));

        assertFalse(gerenciamentoContratos.clientePossuiMultaPendente(cliente.getId()));
    }

    @Test
    @DisplayName("clientePossuiMultaPendente deve retornar false quando a multa do cliente já foi paga")
    void deveRetornarFalseQuandoMultaDoClienteJaPaga() {
        ContratoAluguel contrato = novoContrato(1);
        contrato.setValorMulta(30.0);
        contrato.setMultaPaga(true);
        gerenciamentoContratos.cadastrarContrato(contrato);

        assertFalse(gerenciamentoContratos.clientePossuiMultaPendente(cliente.getId()));
    }

    

    @Test
    @DisplayName("clientePossuiHistorico deve retornar true quando o cliente possui ao menos um contrato")
    void deveRetornarTrueQuandoHaContrato() {
        gerenciamentoContratos.cadastrarContrato(novoContrato(1));

        assertTrue(gerenciamentoContratos.clientePossuiHistorico(cliente.getId()));
    }

    @Test
    @DisplayName("clientePossuiHistorico deve retornar false quando o cliente não possui nenhum contrato")
    void deveRetornarFalseQuandoNaoHaContrato() {
        assertFalse(gerenciamentoContratos.clientePossuiHistorico(cliente.getId()));
    }

    

    @ParameterizedTest(name = "{0} dia(s) de atraso, taxa diária R$ {1} -> multa R$ {2}")
    @DisplayName("calcularMulta deve aplicar multa fixa de R$10 + 5% da taxa diária por dia de atraso")
    @CsvSource({
            "1, 20.0, 11.0",
            "5, 50.0, 22.5",
            "10, 100.0, 60.0",
            "2, 0.0, 10.0"
    })
    void deveCalcularMultaComAtraso(long diasAtraso, double taxaDiaria, double multaEsperada) {
        double multa = gerenciamentoContratos.calcularMulta(diasAtraso, taxaDiaria);

        assertEquals(multaEsperada, multa, 0.0001);
    }

    @Test
    @DisplayName("calcularMulta não deve gerar multa quando não há atraso (dias = 0)")
    void naoDeveGerarMultaSemAtraso() {
        assertEquals(0.0, gerenciamentoContratos.calcularMulta(0, 20.0), 0.0001);
    }

    @Test
    @DisplayName("calcularMulta não deve gerar multa quando os dias de atraso são negativos")
    void naoDeveGerarMultaComDiasNegativos() {
        assertEquals(0.0, gerenciamentoContratos.calcularMulta(-3, 20.0), 0.0001);
    }
}
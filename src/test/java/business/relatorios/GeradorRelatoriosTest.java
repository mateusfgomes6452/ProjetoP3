package business.relatorios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import business.cruds.GerenciamentoContratos;
import business.cruds.GerenciamentoItens;
import business.cruds.GerenciamentoUsuarios;
import entidades.Categoria;
import entidades.Cliente;
import entidades.ContratoAluguel;
import entidades.Fornecedor;
import entidades.Item;
import support.FakeContratoRepositorio;
import support.FakeItemRepositorio;
import support.FakeUsuarioRepositorio;

@DisplayName("GeradorRelatorios - dados dos relatórios")
class GeradorRelatoriosTest {

    private FakeItemRepositorio itens;
    private FakeContratoRepositorio contratos;
    private GeradorRelatorios relatorios;

    private Categoria categoria;
    private Fornecedor fornecedor;
    private Cliente ana;
    private Cliente carlos;

    @BeforeEach
    void setUp() {
        itens = new FakeItemRepositorio();
        contratos = new FakeContratoRepositorio();

        GerenciamentoItens gerenciamentoItens = new GerenciamentoItens(itens);
        GerenciamentoContratos gerenciamentoContratos = new GerenciamentoContratos(contratos);
        GerenciamentoUsuarios gerenciamentoUsuarios =
                new GerenciamentoUsuarios(new FakeUsuarioRepositorio(), gerenciamentoContratos);

        relatorios = new GeradorRelatorios(gerenciamentoItens, gerenciamentoContratos, gerenciamentoUsuarios);

        categoria = new Categoria(1, "Ferramentas", "Ferramentas em geral");
        fornecedor = new Fornecedor(1, "Fornecedor XPTO", "00.000.000/0001-00", "contato@xpto.com", "8199999999");
        ana = new Cliente(3, "Ana Souza", "ana@loja.com", "333.333.333-33", "123456");
        carlos = new Cliente(4, "Carlos Lima", "carlos@loja.com", "111.111.111-55", "123456");
    }

    private Item novoItem(int id) {
        return new Item(id, "Item " + id, "descrição", 20.0, "BOM", 500.0, categoria, fornecedor);
    }

    private ContratoAluguel novoContrato(int id, Cliente cliente, Item item, LocalDate retirada, LocalDate prevista) {
        ContratoAluguel contrato = new ContratoAluguel(id, cliente, item, retirada.toString(), prevista.toString(), 100.0);
        contratos.adicionar(contrato);
        return contrato;
    }

    @Test
    @DisplayName("listarItensDisponiveis ignora itens alugados")
    void itensDisponiveisIgnoraAlugados() {
        Item livre = novoItem(1);
        Item alugado = novoItem(2);
        alugado.alugar();
        itens.adicionar(livre);
        itens.adicionar(alugado);

        List<Item> resultado = relatorios.listarItensDisponiveis();

        assertEquals(1, resultado.size());
        assertEquals(1, resultado.get(0).getId());
    }

    @Test
    @DisplayName("listarHistoricoCliente devolve só os contratos do cliente")
    void historicoSoDoCliente() {
        LocalDate hoje = LocalDate.now();
        novoContrato(1, ana, novoItem(1), hoje, hoje.plusDays(2));
        novoContrato(2, carlos, novoItem(2), hoje, hoje.plusDays(2));
        novoContrato(3, ana, novoItem(3), hoje, hoje.plusDays(2));

        List<ContratoAluguel> historico = relatorios.listarHistoricoCliente(ana.getId());

        assertEquals(2, historico.size());
        assertTrue(historico.stream().allMatch(c -> c.getCliente().getId() == ana.getId()));
    }

    @Test
    @DisplayName("listarHistoricoCliente devolve lista vazia quando não há contratos")
    void historicoVazio() {
        assertTrue(relatorios.listarHistoricoCliente(ana.getId()).isEmpty());
    }

    @Test
    @DisplayName("listarContratosAtivos ignora finalizados e cancelados")
    void contratosAtivos() {
        LocalDate hoje = LocalDate.now();
        novoContrato(1, ana, novoItem(1), hoje, hoje.plusDays(2));
        novoContrato(2, ana, novoItem(2), hoje, hoje.plusDays(2)).finalizarContrato();
        novoContrato(3, ana, novoItem(3), hoje, hoje.plusDays(2)).cancelarContrato();

        List<ContratoAluguel> ativos = relatorios.listarContratosAtivos();

        assertEquals(1, ativos.size());
        assertEquals(1, ativos.get(0).getId());
    }

    @Test
    @DisplayName("listarContratosPorPeriodo inclui as datas limite e exclui o que está fora")
    void contratosPorPeriodo() {
        LocalDate inicio = LocalDate.of(2026, 10, 1);
        LocalDate fim = LocalDate.of(2026, 10, 10);

        novoContrato(1, ana, novoItem(1), inicio.minusDays(1), inicio.plusDays(3)); // antes
        novoContrato(2, ana, novoItem(2), inicio, inicio.plusDays(3));              // limite inicial
        novoContrato(3, ana, novoItem(3), fim, fim.plusDays(3));                    // limite final
        novoContrato(4, ana, novoItem(4), fim.plusDays(1), fim.plusDays(5));        // depois

        List<ContratoAluguel> periodo = relatorios.listarContratosPorPeriodo(inicio, fim);

        assertEquals(2, periodo.size());
        assertEquals(2, periodo.get(0).getId());
        assertEquals(3, periodo.get(1).getId());
    }

    @Test
    @DisplayName("estaAtrasado só vale para contrato ativo com devolução vencida")
    void estaAtrasado() {
        LocalDate hoje = LocalDate.now();
        ContratoAluguel vencido = novoContrato(1, ana, novoItem(1), hoje.minusDays(10), hoje.minusDays(1));
        ContratoAluguel emDia = novoContrato(2, ana, novoItem(2), hoje, hoje.plusDays(1));
        ContratoAluguel venceHoje = novoContrato(3, ana, novoItem(3), hoje, hoje);
        ContratoAluguel finalizado = novoContrato(4, ana, novoItem(4), hoje.minusDays(10), hoje.minusDays(1));
        finalizado.finalizarContrato();

        assertTrue(relatorios.estaAtrasado(vencido));
        assertFalse(relatorios.estaAtrasado(emDia));
        assertFalse(relatorios.estaAtrasado(venceHoje));
        assertFalse(relatorios.estaAtrasado(finalizado));
    }
}

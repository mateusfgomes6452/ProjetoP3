package business.relatorios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
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

/**
 * Cobre os relatórios impressos no console e a exportação para CSV.
 * Os CSVs vão para "relatorios/" dentro do diretório de execução dos testes (target/, ver pom.xml).
 */
@DisplayName("GeradorRelatorios - saída no console e exportação")
class GeradorRelatoriosConsoleTest {

    private static final Path PASTA = Path.of("relatorios");

    private final PrintStream saidaOriginal = System.out;
    private ByteArrayOutputStream saida;

    private FakeItemRepositorio itens;
    private FakeContratoRepositorio contratos;
    private FakeUsuarioRepositorio usuarios;
    private GeradorRelatorios relatorios;

    private Categoria categoria;
    private Fornecedor fornecedor;
    private Cliente ana;

    @BeforeEach
    void setUp() throws IOException {
        saida = new ByteArrayOutputStream();
        System.setOut(new PrintStream(saida, true, StandardCharsets.UTF_8));

        itens = new FakeItemRepositorio();
        contratos = new FakeContratoRepositorio();
        usuarios = new FakeUsuarioRepositorio();

        GerenciamentoItens gerenciamentoItens = new GerenciamentoItens(itens);
        GerenciamentoContratos gerenciamentoContratos = new GerenciamentoContratos(contratos);
        GerenciamentoUsuarios gerenciamentoUsuarios = new GerenciamentoUsuarios(usuarios, gerenciamentoContratos);
        relatorios = new GeradorRelatorios(gerenciamentoItens, gerenciamentoContratos, gerenciamentoUsuarios);

        categoria = new Categoria(1, "Ferramentas", "Ferramentas em geral");
        fornecedor = new Fornecedor(1, "Fornecedor XPTO", "00.000.000/0001-00", "contato@xpto.com", "8199999999");
        ana = new Cliente(3, "Ana Souza", "ana@loja.com", "333.333.333-33", "123456");
        usuarios.adicionar(ana);

        for (String nome : List.of("faturamento.csv", "historico_cliente.csv",
                "itens_alugados.csv", "itens_disponiveis.csv")) {
            Files.deleteIfExists(PASTA.resolve(nome));
        }
    }

    @AfterEach
    void restauraSaida() {
        System.setOut(saidaOriginal);
    }

    private String console() {
        return saida.toString(StandardCharsets.UTF_8);
    }

    private Item novoItem(int id, Categoria cat) {
        Item item = new Item(id, "Item " + id, "descrição", 20.0, "BOM", 500.0, cat, fornecedor);
        itens.adicionar(item);
        return item;
    }

    private ContratoAluguel novoContrato(int id, LocalDate retirada, LocalDate prevista) {
        ContratoAluguel contrato = new ContratoAluguel(id, ana, novoItem(100 + id, categoria),
                retirada.toString(), prevista.toString(), 100.0);
        contratos.adicionar(contrato);
        return contrato;
    }

    // ---------------------------------------------------------------- itens disponíveis

    @Test
    @DisplayName("itens disponíveis: imprime a tabela e gera o CSV")
    void itensDisponiveis() {
        novoItem(1, categoria);
        novoItem(2, null);

        relatorios.gerarRelatorioItensDisponiveis();

        String texto = console();
        assertTrue(texto.contains("RELATÓRIO DE ITENS DISPONÍVEIS"));
        assertTrue(texto.contains("Ferramentas"));
        assertTrue(texto.contains("Sem categoria"));
        assertTrue(Files.exists(PASTA.resolve("itens_disponiveis.csv")));
    }

    // ---------------------------------------------------------------- histórico do cliente

    @Test
    @DisplayName("histórico: avisa quando o cliente não existe")
    void historicoClienteInexistente() {
        relatorios.gerarHistoricoCliente(999);

        assertTrue(console().contains("Cliente não encontrado"));
    }

    @Test
    @DisplayName("histórico: avisa quando o cliente não tem contratos")
    void historicoSemContratos() {
        relatorios.gerarHistoricoCliente(ana.getId());

        assertTrue(console().contains("O cliente não possui contratos"));
    }

    @Test
    @DisplayName("histórico: imprime os contratos e gera o CSV")
    void historicoComContratos() {
        LocalDate hoje = LocalDate.now();
        novoContrato(1, hoje, hoje.plusDays(2));

        relatorios.gerarHistoricoCliente(ana.getId());

        String texto = console();
        assertTrue(texto.contains("HISTÓRICO DE ALUGUÉIS"));
        assertTrue(texto.contains("Cliente: Ana Souza"));
        assertTrue(Files.exists(PASTA.resolve("historico_cliente.csv")));
    }

    // ---------------------------------------------------------------- itens alugados

    @Test
    @DisplayName("itens alugados: avisa quando não há contratos ativos")
    void itensAlugadosSemContratos() {
        relatorios.gerarRelatorioItensAlugados();

        assertTrue(console().contains("Não existem contratos ativos."));
    }

    @Test
    @DisplayName("itens alugados: mostra ATRASADO e EM DIA e gera o CSV")
    void itensAlugadosComContratos() {
        LocalDate hoje = LocalDate.now();
        novoContrato(1, hoje.minusDays(10), hoje.minusDays(3));
        novoContrato(2, hoje, hoje.plusDays(10));

        relatorios.gerarRelatorioItensAlugados();

        String texto = console();
        assertTrue(texto.contains("ITENS ALUGADOS ATUALMENTE"));
        assertTrue(texto.contains("ATRASADO"));
        assertTrue(texto.contains("EM DIA"));
        assertTrue(Files.exists(PASTA.resolve("itens_alugados.csv")));
    }

    // ---------------------------------------------------------------- faturamento

    @Test
    @DisplayName("faturamento: rejeita datas em formato inválido")
    void faturamentoDataInvalida() {
        relatorios.gerarRelatorioFaturamento("01/10/2026", "31/10/2026");

        assertTrue(console().contains("Formato de data inválido."));
    }

    @Test
    @DisplayName("faturamento: avisa quando não há contratos no período")
    void faturamentoSemContratos() {
        relatorios.gerarRelatorioFaturamento("2026-01-01", "2026-01-31");

        assertTrue(console().contains("Nenhum contrato encontrado no período."));
    }

    @Test
    @DisplayName("faturamento: imprime totais e gera o CSV")
    void faturamentoComContratos() {
        LocalDate dia = LocalDate.of(2026, 10, 5);
        ContratoAluguel c = novoContrato(1, dia, dia.plusDays(2));
        c.setValorMulta(10.0);

        relatorios.gerarRelatorioFaturamento("2026-10-01", "2026-10-31");

        String texto = console();
        assertTrue(texto.contains("RELATÓRIO DE FATURAMENTO"));
        assertTrue(texto.contains("Quantidade de contratos: 1"));
        assertTrue(Files.exists(PASTA.resolve("faturamento.csv")));
    }

    // ---------------------------------------------------------------- exportação direta

    @Test
    @DisplayName("exportar* grava os arquivos sem passar pelo console")
    void exportacoesDiretas() {
        LocalDate hoje = LocalDate.now();
        ContratoAluguel c = novoContrato(1, hoje, hoje.plusDays(2));
        Item item = novoItem(50, categoria);

        relatorios.exportarItensDisponiveis(List.of(item));
        relatorios.exportarHistoricoCliente(List.of(c));
        relatorios.exportarItensAlugados(List.of(c));
        relatorios.exportarFaturamento(List.of(c), hoje.toString(), hoje.toString());

        assertEquals("", console());
        assertTrue(Files.exists(PASTA.resolve("itens_disponiveis.csv")));
        assertTrue(Files.exists(PASTA.resolve("historico_cliente.csv")));
        assertTrue(Files.exists(PASTA.resolve("itens_alugados.csv")));
        assertTrue(Files.exists(PASTA.resolve("faturamento.csv")));
    }
}

package repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import entidades.Categoria;
import entidades.Cliente;
import entidades.ContratoAluguel;
import entidades.Fornecedor;
import entidades.Item;

/**
 * As classes SalvaRelatorio* gravam na pasta "relatorios" (relativa ao diretório de execução).
 * O pom.xml configura o surefire para rodar em target/, então nada aqui suja a pasta versionada.
 */
@DisplayName("SalvaRelatorio* - exportação de CSV")
class SalvaRelatoriosTest {

    private static final Path PASTA = Path.of("relatorios");

    private Categoria categoria;
    private Fornecedor fornecedor;
    private Cliente ana;

    @BeforeEach
    void setUp() throws IOException {
        categoria = new Categoria(1, "Ferramentas", "Ferramentas em geral");
        fornecedor = new Fornecedor(1, "Fornecedor XPTO", "00.000.000/0001-00", "contato@xpto.com", "8199999999");
        ana = new Cliente(3, "Ana Souza", "ana@loja.com", "333.333.333-33", "123456");

        for (String nome : List.of("faturamento.csv", "historico_cliente.csv",
                "itens_alugados.csv", "itens_disponiveis.csv")) {
            Files.deleteIfExists(PASTA.resolve(nome));
        }
    }

    private Item item(int id, String nome, Categoria cat) {
        return new Item(id, nome, "descrição", 20.0, "BOM", 500.0, cat, fornecedor);
    }

    private ContratoAluguel contrato(int id, Item item, LocalDate retirada, LocalDate prevista) {
        return new ContratoAluguel(id, ana, item, retirada.toString(), prevista.toString(), 100.0);
    }

    private List<String> ler(String arquivo) throws IOException {
        return Files.readAllLines(PASTA.resolve(arquivo));
    }

    @Test
    @DisplayName("itens disponíveis: cria pasta, grava cabeçalho e ordena por categoria")
    void itensDisponiveisOrdenadosPorCategoria() throws IOException {
        Categoria zebra = new Categoria(2, "Zebra", "última");
        Categoria abelha = new Categoria(3, "abelha", "primeira");

        List<Item> itens = new ArrayList<>(List.of(
                item(1, "Item Z", zebra),
                item(2, "Item A", abelha),
                item(3, "Item F", categoria)));

        new SalvaRelatorioItensDisponiveis().salvar(itens);

        List<String> linhas = ler("itens_disponiveis.csv");
        assertEquals("Categoria;IdItem;Nome;TaxaDiaria", linhas.get(0));
        assertEquals(4, linhas.size());
        assertTrue(linhas.get(1).startsWith("abelha;2;Item A"));
        assertTrue(linhas.get(2).startsWith("Ferramentas;3;Item F"));
        assertTrue(linhas.get(3).startsWith("Zebra;1;Item Z"));
    }

    @Test
    @DisplayName("itens disponíveis: item sem categoria vira 'Sem categoria'")
    void itensDisponiveisSemCategoria() throws IOException {
        List<Item> itens = new ArrayList<>(List.of(item(1, "Avulso", null)));

        new SalvaRelatorioItensDisponiveis().salvar(itens);

        assertTrue(ler("itens_disponiveis.csv").get(1).startsWith("Sem categoria;1;Avulso"));
    }

    @Test
    @DisplayName("itens disponíveis: lista vazia gera só o cabeçalho")
    void itensDisponiveisVazio() throws IOException {
        new SalvaRelatorioItensDisponiveis().salvar(new ArrayList<>());

        assertEquals(List.of("Categoria;IdItem;Nome;TaxaDiaria"), ler("itens_disponiveis.csv"));
    }

    @Test
    @DisplayName("histórico do cliente: grava uma linha por contrato")
    void historicoCliente() throws IOException {
        LocalDate hoje = LocalDate.now();
        ContratoAluguel c = contrato(7, item(1, "Furadeira", categoria), hoje, hoje.plusDays(3));

        new SalvaRelatorioHistoricoCliente().salvar(List.of(c));

        List<String> linhas = ler("historico_cliente.csv");
        assertEquals(2, linhas.size());
        assertTrue(linhas.get(0).startsWith("IdContrato"));
        assertEquals("7;Ana Souza;Furadeira;" + hoje + ";" + hoje.plusDays(3) + ";ATIVO;100.0;0.0", linhas.get(1));
    }

    @Test
    @DisplayName("itens alugados: marca ATRASADO e EM_DIA conforme a data prevista")
    void itensAlugadosSituacao() throws IOException {
        LocalDate hoje = LocalDate.now();
        ContratoAluguel atrasado = contrato(1, item(1, "Serra", categoria), hoje.minusDays(10), hoje.minusDays(5));
        ContratoAluguel emDia = contrato(2, item(2, "Lixadeira", categoria), hoje, hoje.plusDays(30));

        new SalvaRelatorioItensAlugados().salvar(List.of(atrasado, emDia));

        List<String> linhas = ler("itens_alugados.csv");
        assertEquals("Contrato;Cliente;Item;DataPrevista;Situacao", linhas.get(0));
        assertTrue(linhas.get(1).endsWith(";ATRASADO"));
        assertTrue(linhas.get(2).endsWith(";EM_DIA"));
    }

    @Test
    @DisplayName("faturamento: grava contratos e os totais do período")
    void faturamentoComTotais() throws IOException {
        LocalDate hoje = LocalDate.now();
        ContratoAluguel c1 = contrato(1, item(1, "Serra", categoria), hoje, hoje.plusDays(2));
        ContratoAluguel c2 = contrato(2, item(2, "Furadeira", categoria), hoje, hoje.plusDays(2));
        c2.setValorMulta(15.5);

        new SalvaRelatorioFaturamento().salvar(List.of(c1, c2), "2026-10-01", "2026-10-31");

        List<String> linhas = ler("faturamento.csv");
        assertEquals("Contrato;Cliente;ValorAluguel;Multa", linhas.get(0));
        assertEquals("1;Ana Souza;100.0;0.0", linhas.get(1));
        assertEquals("2;Ana Souza;100.0;15.5", linhas.get(2));
        assertTrue(linhas.contains("PERIODO,2026-10-01 a 2026-10-31"));
        assertTrue(linhas.contains("TOTAL_ALUGUEIS,200.0"));
        assertTrue(linhas.contains("TOTAL_MULTAS,15.5"));
        assertTrue(linhas.contains("TOTAL_GERAL,215.5"));
    }
}

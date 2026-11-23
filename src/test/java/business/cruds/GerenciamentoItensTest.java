package business.cruds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import entidades.Categoria;
import entidades.Fornecedor;
import entidades.Item;
import support.FakeItemRepositorio;

@DisplayName("GerenciamentoItens")
class GerenciamentoItensTest {

    private FakeItemRepositorio repositorio;
    private GerenciamentoItens gerenciamentoItens;

    private Categoria categoria;
    private Fornecedor fornecedor;

    @BeforeEach
    void setUp() {
        repositorio = new FakeItemRepositorio();
        gerenciamentoItens = new GerenciamentoItens(repositorio);

        categoria = new Categoria(new Categoria.Infos("Ferramentas", "Ferramentas em geral"));
        fornecedor = new Fornecedor(new Fornecedor.Infos("Fornecedor XPTO", "00.000.000/000(1L)-00", "contato@xpto.com", "8(1L)99999999"));
    }

    private Item novoItem(Long id) {
        return new Item(new Item.Infos("Furadeira", "Furadeira de impacto", "BOM"), new Item.ValoresItem(20.0, 500.0), categoria, fornecedor);
    }

    @Test
    @DisplayName("cadastrarItem deve cadastrar um item novo e deixá-lo disponível")
    void deveCadastrarItemNovo() {
        Item item = novoItem((1L));

        boolean resultado = gerenciamentoItens.cadastrarItem(item);

        assertTrue(resultado);
        assertTrue(item.estaDisponivel());
    }

    @Test
    @Disabled("desativado enquanto não foi criado o bd")
    @DisplayName("cadastrarItem deve rejeitar quando já existe item com o mesmo id")
    void deveRejeitarItemComIdDuplicado() {
        gerenciamentoItens.cadastrarItem(novoItem((1L)));

        boolean resultado = gerenciamentoItens.cadastrarItem(novoItem((1L)));

        assertFalse(resultado);
        assertEquals((1L), gerenciamentoItens.listarItens().size());
    }

    
    @Test
    @Disabled("desativado enquanto não foi criado o bd")
    @DisplayName("buscarItem deve retornar o item quando existir")
    void deveEncontrarItemExistente() {
        gerenciamentoItens.cadastrarItem(novoItem((1L)));

        assertNotNull(gerenciamentoItens.buscarItem((1L)));
    }

    @Test
    @DisplayName("buscarItem deve retornar null quando não existir")
    void deveRetornarNullAoBuscarItemInexistente() {
        assertNull(gerenciamentoItens.buscarItem(999L));
    }

    @Test
    @DisplayName("atualizarItem deve retornar false quando o item não existe")
    void deveRetornarFalseAoAtualizarItemInexistente() {
        Item itemNovo = new Item(new Item.Infos("Novo nome", "Nova descrição", "OTIMO"), new Item.ValoresItem(30.0, 600.0), null, null);
        boolean resultado = gerenciamentoItens.atualizarItem(itemNovo);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("atualizarItem deve atualizar todos os campos do item existente")
    void deveAtualizarCamposDoItem() {
        gerenciamentoItens.cadastrarItem(novoItem((1L)));

        Item itemNovo = new Item(new Item.Infos("Serra Elétrica", "Serra circular", "OTIMO"), new Item.ValoresItem(35.0, 800.0), categoria, fornecedor);
        boolean resultado = gerenciamentoItens.atualizarItem(itemNovo);

        Item atualizado = gerenciamentoItens.buscarItem((1L));

        assertTrue(resultado);
        assertEquals("Serra Elétrica", atualizado.getNome());
        assertEquals("Serra circular", atualizado.getDescricao());
        assertEquals(35.0, atualizado.getTaxaDiaria(), 0.0001);
        assertEquals("OTIMO", atualizado.getEstadoConservacao());
        assertEquals(800.0, atualizado.getValorReposicao(), 0.0001);
    }

    @Test
    @DisplayName("excluirItem deve retornar false quando o item não existe")
    void deveRetornarFalseAoExcluirItemInexistente() {
        assertFalse(gerenciamentoItens.excluirItem(999L));
    }

    @Test
    @Disabled("desativado enquanto não foi criado o bd")
    @DisplayName("excluirItem deve desativar o item, tornando-o indisponível")
    void deveDesativarItem() {
        gerenciamentoItens.cadastrarItem(novoItem((1L)));

        boolean resultado = gerenciamentoItens.excluirItem((1L));
        Item item = gerenciamentoItens.buscarItem((1L));

        assertTrue(resultado);
        assertFalse(item.isAtivo());
        assertFalse(item.estaDisponivel(), "Item desativado não pode estar disponível para aluguel");
    }

    @Test
    @Disabled("desativado enquanto não foi criado o bd")
    @DisplayName("gerarProximoId deve retornar o maior id cadastrado + 1")
    void deveIncrementarAPartirDoMaiorId() {
        gerenciamentoItens.cadastrarItem(novoItem(7L));
        gerenciamentoItens.cadastrarItem(novoItem(3L));
    }
}
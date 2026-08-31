package business.cruds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import entidades.Multa;
import support.FakeMultaRepositorio;

@DisplayName("GerenciamentoMultas")
class GerenciamentoMultasTest {

    private FakeMultaRepositorio repositorio;
    private GerenciamentoMultas gerenciamentoMultas;

    @BeforeEach
    void setUp() {
        repositorio = new FakeMultaRepositorio();
        gerenciamentoMultas = new GerenciamentoMultas(repositorio);
    }

    private Multa novaMulta(int id, int idContrato, double valor) {
        return new Multa(id, idContrato, "ATRASO", "Devolução em atraso", valor, "2026-01-15");
    }

    

    @Test
    @DisplayName("registrarMulta deve registrar quando o id da multa ainda não existe")
    void deveRegistrarMultaNova() {
        boolean resultado = gerenciamentoMultas.registrarMulta(novaMulta(1, 10, 50.0));

        assertTrue(resultado);
        assertEquals(1, gerenciamentoMultas.listarMultas().size());
    }

    @Test
    @DisplayName("registrarMulta deve rejeitar quando já existe multa com o mesmo id")
    void deveRejeitarMultaComIdDuplicado() {
        gerenciamentoMultas.registrarMulta(novaMulta(1, 10, 50.0));

        boolean resultado = gerenciamentoMultas.registrarMulta(novaMulta(1, 20, 30.0));

        assertFalse(resultado);
        assertEquals(1, gerenciamentoMultas.listarMultas().size());
    }

    

    @Test
    @DisplayName("buscarMulta deve retornar a multa quando existir")
    void deveEncontrarMultaExistente() {
        gerenciamentoMultas.registrarMulta(novaMulta(1, 10, 50.0));

        assertNotNull(gerenciamentoMultas.buscarMulta(1));
    }

    @Test
    @DisplayName("buscarMulta deve retornar null quando não existir")
    void deveRetornarNullAoBuscarMultaInexistente() {
        assertNull(gerenciamentoMultas.buscarMulta(999));
    }

   

    @Test
    @DisplayName("listarMultasPorContrato deve retornar apenas as multas do contrato informado")
    void deveFiltrarMultasPorContrato() {
        gerenciamentoMultas.registrarMulta(novaMulta(1, 10, 50.0));
        gerenciamentoMultas.registrarMulta(novaMulta(2, 10, 30.0));
        gerenciamentoMultas.registrarMulta(novaMulta(3, 20, 40.0));

        List<Multa> resultado = gerenciamentoMultas.listarMultasPorContrato(10);

        assertEquals(2, resultado.size());
    }

   

    @Test
    @DisplayName("listarMultasPendentes deve retornar somente as multas ainda não pagas")
    void deveListarSomenteMultasNaoPagas() {
        gerenciamentoMultas.registrarMulta(novaMulta(1, 10, 50.0));
        gerenciamentoMultas.registrarMulta(novaMulta(2, 11, 30.0));
        gerenciamentoMultas.quitarMulta(1);

        List<Multa> pendentes = gerenciamentoMultas.listarMultasPendentes();

        assertEquals(1, pendentes.size());
        assertEquals(2, pendentes.get(0).getId());
    }

   

    @Test
    @DisplayName("quitarMulta deve retornar false quando a multa não existe")
    void deveRetornarFalseAoQuitarMultaInexistente() {
        assertFalse(gerenciamentoMultas.quitarMulta(999));
    }

    @Test
    @DisplayName("quitarMulta deve marcar a multa como paga quando ela está pendente")
    void deveQuitarMultaPendente() {
        gerenciamentoMultas.registrarMulta(novaMulta(1, 10, 50.0));

        boolean resultado = gerenciamentoMultas.quitarMulta(1);

        assertTrue(resultado);
        assertTrue(gerenciamentoMultas.buscarMulta(1).isPaga());
    }

    @Test
    @DisplayName("quitarMulta deve retornar false quando a multa já está paga")
    void deveRetornarFalseQuandoMultaJaPaga() {
        gerenciamentoMultas.registrarMulta(novaMulta(1, 10, 50.0));
        gerenciamentoMultas.quitarMulta(1);

        boolean resultado = gerenciamentoMultas.quitarMulta(1);

        assertFalse(resultado);
    }



    @Test
    @DisplayName("gerarProximoId deve retornar 1 quando não há multas cadastradas")
    void devePrimeiroIdQuandoVazio() {
        assertEquals(1, gerenciamentoMultas.gerarProximoId());
    }

    @Test
    @DisplayName("gerarProximoId deve retornar o maior id cadastrado + 1")
    void deveIncrementarAPartirDoMaiorId() {
        gerenciamentoMultas.registrarMulta(novaMulta(5, 10, 50.0));
        gerenciamentoMultas.registrarMulta(novaMulta(2, 10, 30.0));

        assertEquals(6, gerenciamentoMultas.gerarProximoId());
    }
}
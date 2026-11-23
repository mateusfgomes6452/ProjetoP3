package business.cruds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

    private Multa novaMulta(Long idContrato, double valor) {
        return new Multa(idContrato, "ATRASO", "Devolução em atraso", valor, "2026-01-15");
    }

    @Test
    @DisplayName("registrarMulta deve registrar quando o id da multa ainda não existe")
    void deveRegistrarMultaNova() {
        boolean resultado = gerenciamentoMultas.registrarMulta(novaMulta(10L, 50.0));

        assertTrue(resultado);
        assertEquals(1, gerenciamentoMultas.listarMultas().size());
    }

    @Test
    @Disabled("desativado enquanto não foi criado o bd")
    @DisplayName("registrarMulta deve rejeitar quando já existe multa com o mesmo id")
    void deveRejeitarMultaComIdDuplicado() {
        gerenciamentoMultas.registrarMulta(novaMulta(10L, 50.0));

        boolean resultado = gerenciamentoMultas.registrarMulta(novaMulta(20L, 30.0));

        assertFalse(resultado);
        assertEquals(1, gerenciamentoMultas.listarMultas().size());
    }

    @Test
    @Disabled("desativado enquanto não foi criado o bd")
    @DisplayName("buscarMulta deve retornar a multa quando existir")
    void deveEncontrarMultaExistente() {
        gerenciamentoMultas.registrarMulta(novaMulta(10L, 50.0));

        assertNotNull(gerenciamentoMultas.buscarMulta(1L));
    }

    @Test
    @DisplayName("buscarMulta deve retornar null quando não existir")
    void deveRetornarNullAoBuscarMultaInexistente() {
        assertNull(gerenciamentoMultas.buscarMulta(999L));
    }

    @Test
    @Disabled("desativado enquanto não foi criado o bd")
    @DisplayName("listarMultasPorContrato deve retornar apenas as multas do contrato informado")
    void deveFiltrarMultasPorContrato() {
        gerenciamentoMultas.registrarMulta(novaMulta(10L, 50.0));
        gerenciamentoMultas.registrarMulta(novaMulta(10L, 30.0));
        gerenciamentoMultas.registrarMulta(novaMulta(20L, 40.0));

        List<Multa> resultado = gerenciamentoMultas.listarMultasPorContrato(10L);

        assertEquals(2, resultado.size());
    }
   
    @Test
    @Disabled("desativado enquanto não foi criado o bd")
    @DisplayName("listarMultasPendentes deve retornar somente as multas ainda não pagas")
    void deveListarSomenteMultasNaoPagas() {
        gerenciamentoMultas.registrarMulta(novaMulta(10L, 50.0));
        gerenciamentoMultas.registrarMulta(novaMulta(11L, 30.0));
        gerenciamentoMultas.quitarMulta(1L);

        List<Multa> pendentes = gerenciamentoMultas.listarMultasPendentes();

        assertEquals(1, pendentes.size());
        assertEquals(2, pendentes.get(0).getId());
    }

    @Test
    @DisplayName("quitarMulta deve retornar false quando a multa não existe")
    void deveRetornarFalseAoQuitarMultaInexistente() {
        assertFalse(gerenciamentoMultas.quitarMulta(999L));
    }

    @Test
    @Disabled("desativado enquanto não foi criado o bd")
    @DisplayName("quitarMulta deve marcar a multa como paga quando ela está pendente")
    void deveQuitarMultaPendente() {
        gerenciamentoMultas.registrarMulta(novaMulta(10L, 50.0));

        boolean resultado = gerenciamentoMultas.quitarMulta(1L);

        assertTrue(resultado);
        assertTrue(gerenciamentoMultas.buscarMulta(1L).isPaga());
    }

    @Test
    @Disabled("desativado enquanto não foi criado o bd")
    @DisplayName("quitarMulta deve retornar false quando a multa já está paga")
    void deveRetornarFalseQuandoMultaJaPaga() {
        gerenciamentoMultas.registrarMulta(novaMulta(10l, 50.0));
        gerenciamentoMultas.quitarMulta(1L);

        boolean resultado = gerenciamentoMultas.quitarMulta(1L);

        assertFalse(resultado);
    }

    @Test
    @Disabled("desativado enquanto não foi criado o bd")
    @DisplayName("gerarProximoId deve retornar o maior id cadastrado + 1")
    void deveIncrementarAPartirDoMaiorId() {
        gerenciamentoMultas.registrarMulta(novaMulta(10L, 50.0));
        gerenciamentoMultas.registrarMulta(novaMulta(10L, 30.0));

    }
}
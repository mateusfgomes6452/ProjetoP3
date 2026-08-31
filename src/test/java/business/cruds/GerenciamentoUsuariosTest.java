package business.cruds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import business.interfaces.IGerenciamentoContratos;
import entidades.Administrador;
import entidades.Cliente;
import entidades.Usuario;
import support.FakeContratoRepositorio;
import support.FakeUsuarioRepositorio;

@DisplayName("GerenciamentoUsuarios")
class GerenciamentoUsuariosTest {

    private FakeUsuarioRepositorio repositorio;
    private GerenciamentoContratos gerenciamentoContratos;
    private GerenciamentoUsuarios gerenciamentoUsuarios;

    @BeforeEach
    void setUp() {
        repositorio = new FakeUsuarioRepositorio();
        gerenciamentoContratos = new GerenciamentoContratos(new FakeContratoRepositorio());
        gerenciamentoUsuarios = new GerenciamentoUsuarios(repositorio, gerenciamentoContratos);
    }

    private Cliente novoCliente(int id, String email, String cpf) {
        return new Cliente(id, "Maria Silva", email, cpf, "senha123");
    }

   

    @Test
    @DisplayName("cadastrarUsuario deve cadastrar um usuário novo")
    void deveCadastrarUsuarioNovo() {
        boolean resultado = gerenciamentoUsuarios.cadastrarUsuario(novoCliente(1, "maria@email.com", "111.111.111-11"));

        assertTrue(resultado);
    }

    @Test
    @DisplayName("cadastrarUsuario deve rejeitar quando já existe usuário com o mesmo id")
    void deveRejeitarUsuarioComIdDuplicado() {
        gerenciamentoUsuarios.cadastrarUsuario(novoCliente(1, "maria@email.com", "111.111.111-11"));

        boolean resultado = gerenciamentoUsuarios.cadastrarUsuario(novoCliente(1, "outra@email.com", "222.222.222-22"));

        assertFalse(resultado);
    }

    @Test
    @DisplayName("cadastrarUsuario deve rejeitar quando já existe usuário com o mesmo CPF")
    void deveRejeitarUsuarioComCpfDuplicado() {
        gerenciamentoUsuarios.cadastrarUsuario(novoCliente(1, "maria@email.com", "111.111.111-11"));

        boolean resultado = gerenciamentoUsuarios.cadastrarUsuario(novoCliente(2, "outra@email.com", "111.111.111-11"));

        assertFalse(resultado);
    }

    @Test
    @DisplayName("cadastrarUsuario deve rejeitar quando já existe usuário com o mesmo e-mail")
    void deveRejeitarUsuarioComEmailDuplicado() {
        gerenciamentoUsuarios.cadastrarUsuario(novoCliente(1, "maria@email.com", "111.111.111-11"));

        boolean resultado = gerenciamentoUsuarios.cadastrarUsuario(novoCliente(2, "maria@email.com", "222.222.222-22"));

        assertFalse(resultado);
    }

    

    @Test
    @DisplayName("atualizarUsuario deve retornar false quando o usuário não existe")
    void deveRetornarFalseAoAtualizarUsuarioInexistente() {
        assertFalse(gerenciamentoUsuarios.atualizarUsuario(999, "Novo nome", "novo@email.com", "novaSenha"));
    }

    @Test
    @DisplayName("atualizarUsuario deve atualizar nome, e-mail e senha do usuário existente")
    void deveAtualizarDadosDoUsuario() {
        gerenciamentoUsuarios.cadastrarUsuario(novoCliente(1, "maria@email.com", "111.111.111-11"));

        boolean resultado = gerenciamentoUsuarios.atualizarUsuario(1, "Maria Souza", "maria.souza@email.com", "novaSenha");
        Usuario usuario = gerenciamentoUsuarios.buscarUsuario(1);

        assertTrue(resultado);
        assertEquals("Maria Souza", usuario.getNome());
        assertEquals("maria.souza@email.com", usuario.getEmail());
        assertEquals("novaSenha", usuario.getSenha());
    }

    

    @Test
    @DisplayName("desativaUsuario deve retornar false quando o usuário não existe")
    void deveRetornarFalseAoDesativarUsuarioInexistente() {
        assertFalse(gerenciamentoUsuarios.desativaUsuario(999));
    }

    @Test
    @DisplayName("desativaUsuario deve desativar o usuário quando ele não possui multa pendente")
    void deveDesativarUsuarioSemMultaPendente() {
        gerenciamentoUsuarios.cadastrarUsuario(novoCliente(1, "maria@email.com", "111.111.111-11"));

        boolean resultado = gerenciamentoUsuarios.desativaUsuario(1);

        assertTrue(resultado);
        assertFalse(gerenciamentoUsuarios.buscarUsuario(1).isAtivo());
    }

    @Test
    @DisplayName("desativaUsuario não deve desativar o usuário quando ele possui multa pendente")
    void naoDeveDesativarUsuarioComMultaPendente() {
        Cliente cliente = novoCliente(1, "maria@email.com", "111.111.111-11");
        gerenciamentoUsuarios.cadastrarUsuario(cliente);

        IGerenciamentoContratos contratosComMulta = new GerenciamentoContratosComMultaPendenteFalsa();
        GerenciamentoUsuarios gerenciamentoComMulta =
                new GerenciamentoUsuarios(repositorio, contratosComMulta);

        boolean resultado = gerenciamentoComMulta.desativaUsuario(1);

        assertFalse(resultado, "Usuário com multa pendente não deveria poder ser desativado");
        assertTrue(gerenciamentoUsuarios.buscarUsuario(1).isAtivo());
    }

   

    @Test
    @DisplayName("realizarLogin deve autenticar quando e-mail e senha estão corretos")
    void deveAutenticarComCredenciaisCorretas() {
        gerenciamentoUsuarios.cadastrarUsuario(novoCliente(1, "maria@email.com", "111.111.111-11"));

        Usuario usuario = gerenciamentoUsuarios.realizarLogin("maria@email.com", "senha123");

        assertNotNull(usuario);
        assertEquals("CLIENTE", usuario.getTipo());
    }

    @Test
    @DisplayName("realizarLogin deve negar acesso quando o e-mail não está cadastrado")
    void deveNegarAcessoEmailInexistente() {
        assertNull(gerenciamentoUsuarios.realizarLogin("naoexiste@email.com", "qualquer"));
    }

    @Test
    @DisplayName("realizarLogin deve negar acesso quando a senha está incorreta")
    void deveNegarAcessoSenhaIncorreta() {
        gerenciamentoUsuarios.cadastrarUsuario(novoCliente(1, "maria@email.com", "111.111.111-11"));

        assertNull(gerenciamentoUsuarios.realizarLogin("maria@email.com", "senhaErrada"));
    }

    @Test
    @DisplayName("realizarLogin deve negar acesso quando o usuário está inativo, mesmo com credenciais corretas")
    void deveNegarAcessoUsuarioInativo() {
        gerenciamentoUsuarios.cadastrarUsuario(novoCliente(1, "maria@email.com", "111.111.111-11"));
        gerenciamentoUsuarios.desativaUsuario(1);

        assertNull(gerenciamentoUsuarios.realizarLogin("maria@email.com", "senha123"));
    }

    @Test
    @DisplayName("realizarLogin deve reconhecer corretamente o tipo de usuário administrador")
    void deveAutenticarAdministrador() {
        Administrador admin = new Administrador(1, "Jackson", "admin@loja.com", "000.000.000-00", "admin123");
        gerenciamentoUsuarios.cadastrarUsuario(admin);

        Usuario usuario = gerenciamentoUsuarios.realizarLogin("admin@loja.com", "admin123");

        assertNotNull(usuario);
        assertEquals("ADMINISTRADOR", usuario.getTipo());
    }

   
    private static class GerenciamentoContratosComMultaPendenteFalsa implements IGerenciamentoContratos {

        @Override
        public boolean cadastrarContrato(entidades.ContratoAluguel contrato) { return false; }

        @Override
        public entidades.ContratoAluguel buscarContrato(int id) { return null; }

        @Override
        public java.util.List<entidades.ContratoAluguel> listarContratos() { return java.util.List.of(); }

        @Override
        public boolean finalizarContrato(int id) { return false; }

        @Override
        public boolean cancelarContrato(int id) { return false; }

        @Override
        public boolean quitarMultaContrato(int idContrato) { return false; }

        @Override
        public int gerarProximoId() { return 1; }

        @Override
        public boolean clientePossuiMultaPendente(int idCliente) { return true; }

        @Override
        public boolean clientePossuiHistorico(int idCliente) { return true; }

        @Override
        public double calcularMulta(long diasAtraso, double taxaDiaria) { return 0; }
    }
}
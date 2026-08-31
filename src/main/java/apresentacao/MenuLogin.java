package apresentacao;
 
import java.util.Scanner;
 
import entidades.Administrador;
import entidades.Cliente;
import entidades.Funcionario;
import entidades.Usuario;
import facade.SistemaFacade;
 
public class MenuLogin {
 
    private SistemaFacade sistema;
    private Scanner scanner;
 
    public MenuLogin(SistemaFacade sistema, Scanner scanner) {
        this.sistema = sistema;
        this.scanner = scanner;
    }
 
    public void exibir() {
 
        int opcao;
 
        do {
 
            System.out.println("\n==================================");
            System.out.println("     LOJA QUE ALUGA DE UM TUDO     ");
            System.out.println("===================================");
            System.out.println("1 - Entrar no sistema");
            System.out.println("0 - Encerrar");
            System.out.print("Opção: ");
 
            opcao = ValidaEntrada.lerInteiro(scanner);
 
            switch (opcao) {
 
                case 1:
                    realizarLogin();
                    break;
 
                case 0:
                    System.out.println("\nSistema encerrado. Até logo!");
                    break;
 
                default:
                    System.out.println("Opção inválida");
            }
 
        } while (opcao != 0);
    }
 
    private void realizarLogin() {
 
        System.out.println("\n=== LOGIN ===");
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
 
        System.out.print("Senha: ");
        String senha = scanner.nextLine().trim();
 
        Usuario usuario = sistema.realizarLogin(email, senha);
 
        if (usuario == null) {
            System.out.println("\nEmail ou senha inválidos. Tente novamente");
            
        } else {
 
            System.out.println("\nBem-vindo(a), " + usuario.getNome() + "!");
    
            if (usuario instanceof Cliente) {
    
                new MenuCliente(sistema, (Cliente) usuario, scanner).exibir();
    
            } else if (usuario instanceof Funcionario) {
    
                new MenuPrincipalFuncionario(sistema, (Funcionario) usuario, scanner).exibir();
    
            } else if (usuario instanceof Administrador) {
    
                new MenuPrincipalAdministrador(sistema, (Administrador) usuario, scanner).exibir();
    
            }
        }
    }
}
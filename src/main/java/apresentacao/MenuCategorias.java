package apresentacao;

import java.util.List;
import java.util.Scanner;

import entidades.Categoria;
import facade.SistemaFacade;


public class MenuCategorias {

    private SistemaFacade sistema;
    private Scanner scanner;

    public MenuCategorias(SistemaFacade sistema, Scanner scanner) {
        this.sistema = sistema;
        this.scanner = scanner;
    }

    public void exibir() {

        int opcao;

        do {

            System.out.println("\n===== GERENCIAR CATEGORIAS =====");
            System.out.println("1 - Cadastrar Categoria");
            System.out.println("2 - Buscar Categoria por ID");
            System.out.println("3 - Listar todas as Categorias");
            System.out.println("4 - Atualizar Categoria");
            System.out.println("5 - Desativar Categoria");
            System.out.println("0 - Voltar");
            System.out.print("Opção: ");

            opcao = ValidaEntrada.lerInteiro(scanner);

            switch (opcao) {

                case 1:
                    cadastrarCategoria();
                    break;

                case 2:
                    buscarCategoria();
                    break;

                case 3:
                    listarCategorias();
                    break;

                case 4:
                    atualizarCategoria();
                    break;

                case 5:
                    desativarCategoria();
                    break;

                case 0:
                    System.out.println("Retornando...");
                    break;

                default:
                    System.out.println("Opção inválida");
            }

        } while (opcao != 0);
    }

    public void cadastrarCategoria() {

        System.out.println("\n=== CADASTRAR CATEGORIA ===");

        int id = sistema.gerarProximoIdCategoria();

        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Descrição: ");
        String descricao = scanner.nextLine().trim();

        Categoria categoria = new Categoria(id, nome, descricao);

        if (sistema.cadastrarCategoria(categoria)) {
            System.out.println("Categoria cadastrada com sucesso. ID: " + id);
        } else {
            System.out.println("Erro: já existe uma categoria com esse nome");
        }
    }

    public void buscarCategoria() {

        System.out.print("ID da categoria: ");
        int id = ValidaEntrada.lerInteiro(scanner);

        Categoria categoria = sistema.buscarCategoria(id);

        if (categoria == null) {
            System.out.println("Categoria não encontrada");
        }else {
            exibirCategoria(categoria);
        }
    }

    public void listarCategorias() {

        List<Categoria> categorias = sistema.listarCategorias();

        System.out.println("\n===== CATEGORIAS CADASTRADAS =====");

        if (categorias.isEmpty()) {
            System.out.println("Nenhuma categoria cadastrada");
            
        }else {

            System.out.printf("%-5s %-25s %-40s %-6s%n",
                    "ID", "NOME", "DESCRIÇÃO", "ATIVA");
            System.out.println("-----------------------------------------------------------------------------------");

            for (Categoria categoria : categorias) {
                System.out.printf("%-5d %-25s %-40s %-6s%n", categoria.getId(), categoria.getNome(), categoria.getDescricao(), categoria.isAtiva());
            }
        }
    }

    public void atualizarCategoria() {

        System.out.print("ID da categoria: ");
        int id = ValidaEntrada.lerInteiro(scanner);

        Categoria categoria = sistema.buscarCategoria(id);

        if (categoria == null) {
            System.out.println("Categoria não encontrada");
            
        }else{

            System.out.println("Deixe em branco para manter o valor atual");

            System.out.print("Nome [" + categoria.getNome() + "]: ");
            String nome = scanner.nextLine().trim();
            if (nome.isEmpty()){
                nome = categoria.getNome();
            }

            System.out.print("Descrição [" + categoria.getDescricao() + "]: ");
            String descricao = scanner.nextLine().trim();
            if (descricao.isEmpty()){
                 descricao = categoria.getDescricao();
            }
            Categoria categoriaNova = new Categoria(id, nome, descricao);
            if (sistema.atualizarCategoria(categoriaNova)) {
                System.out.println("Categoria atualizada com sucesso");
            } else {
                System.out.println("Erro ao atualizar categoria");
            }
        }
    }

    public void desativarCategoria() {

        System.out.print("ID da categoria: ");
        int id = ValidaEntrada.lerInteiro(scanner);

        Categoria categoria = sistema.buscarCategoria(id);

        if (categoria == null) {
            System.out.println("Categoria não encontrada.");
           
        }else {

            System.out.println("Tem certeza que deseja desativar \"" + categoria.getNome() + "\"?");
            System.out.print("Digite 1 para confirmar ou 0 para cancelar: ");
            int confirmacao = ValidaEntrada.lerInteiro(scanner);

            if (confirmacao != 1) {
                System.out.println("Operação cancelada");
                
            }else{

                if (sistema.desativarCategoria(id)) {
                    System.out.println("Categoria desativada com sucesso");
                } else {
                    System.out.println("Erro ao desativar categoria");
                }
            }
        }
    }

    public void exibirCategoria(Categoria categoria) {

        System.out.println("\n===== DADOS DA CATEGORIA =====");
        System.out.println("ID:        " + categoria.getId());
        System.out.println("Nome:      " + categoria.getNome());
        System.out.println("Descrição: " + categoria.getDescricao());
        System.out.println("Ativa:     " + categoria.isAtiva());
    }
}
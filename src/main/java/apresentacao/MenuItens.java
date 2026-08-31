package apresentacao;

import java.util.List;
import java.util.Scanner;

import entidades.Categoria;
import entidades.Fornecedor;
import entidades.Item;
import facade.SistemaFacade;


public class MenuItens {

    private SistemaFacade sistema;
    private Scanner scanner;

    public MenuItens(SistemaFacade sistema, Scanner scanner) {
        this.sistema = sistema;
        this.scanner = scanner;
    }

    public void exibir() {

        int opcao;

        do {

            System.out.println("\n===== GERENCIAR ITENS =====");
            System.out.println("1 - Cadastrar Item");
            System.out.println("2 - Buscar Item por ID");
            System.out.println("3 - Listar todos os Itens");
            System.out.println("4 - Atualizar Item");
            System.out.println("5 - Desativar Item");
            System.out.println("0 - Voltar");
            System.out.print("Opção: ");

            opcao = ValidaEntrada.lerInteiro(scanner);

            switch (opcao) {

                case 1:
                    cadastrarItem();
                    break;

                case 2:
                    buscarItem();
                    break;

                case 3:
                    listarItens();
                    break;

                case 4:
                    atualizarItem();
                    break;

                case 5:
                    desativarItem();
                    break;

                case 0:
                    System.out.println("Retornando...");
                    break;

                default:
                    System.out.println("Opção inválida");
            }

        } while (opcao != 0);
    }

    public void cadastrarItem() {

        System.out.println("\n=== CADASTRAR ITEM ===");

        int id = sistema.gerarProximoIdItem();

        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Descrição: ");
        String descricao = scanner.nextLine().trim();

        System.out.print("Taxa diária (R$): ");
        double taxaDiaria = ValidaEntrada.lerDouble(scanner);

        System.out.print("Estado de conservação (NOVO/BOM/REGULAR/RUIM): ");
        String estado = scanner.nextLine().trim().toUpperCase();

        System.out.print("Valor de reposição (R$): ");
        double valorReposicao = ValidaEntrada.lerDouble(scanner);

        
        Categoria categoria = selecionarCategoria();

       
        Fornecedor fornecedor = selecionarFornecedor();

        Item item = new Item(id, nome, descricao, taxaDiaria, estado, valorReposicao, categoria, fornecedor);

        if (sistema.cadastrarItem(item)) {
            System.out.println("Item cadastrado com sucesso. ID: " + id);
        } else {
            System.out.println("Erro ao cadastrar item");
        }
    }

    public Categoria selecionarCategoria() {

        List<Categoria> categorias = sistema.listarCategorias();

        if (categorias.isEmpty()) {
            System.out.println("Nenhuma categoria cadastrada. O item será cadastrado sem categoria");
            return null;
        }

        System.out.println("\nCategorias disponíveis:");

        for (Categoria c : categorias) {
            System.out.println("  [" + c.getId() + "] " + c.getNome());
        }

        System.out.print("ID da categoria (0 para nenhuma): ");
        int idCategoria = ValidaEntrada.lerInteiro(scanner);

        if (idCategoria == 0) {
            return null;
        }

        Categoria categoria = sistema.buscarCategoria(idCategoria);

        if (categoria == null) {
            System.out.println("Categoria não encontrada. Item será cadastrado sem categoria");
        }

        return categoria;
    }

    public Fornecedor selecionarFornecedor() {

        List<Fornecedor> fornecedores = sistema.listarFornecedores();

        if (fornecedores.isEmpty()) {
            System.out.println("Nenhum fornecedor cadastrado. O item será cadastrado sem fornecedor");
            return null;
        }

        System.out.println("\nFornecedores disponíveis:");

        for (Fornecedor f : fornecedores) {
            System.out.println("  [" + f.getId() + "] " + f.getRazaoSocial());
        }

        System.out.print("ID do fornecedor (0 para nenhum): ");
        int idFornecedor = ValidaEntrada.lerInteiro(scanner);

        if (idFornecedor == 0) {
            return null;
        }

        Fornecedor fornecedor = sistema.buscarFornecedor(idFornecedor);

        if (fornecedor == null) {
            System.out.println("Fornecedor não encontrado. Item será cadastrado sem fornecedor");
        }

        return fornecedor;
    }

    public void buscarItem() {

        System.out.print("ID do item: ");
        int id = ValidaEntrada.lerInteiro(scanner);

        Item item = sistema.buscarItem(id);

        if (item == null) {
            System.out.println("Item não encontrado");
            
        } else {

            System.out.println("\n===== DADOS DO ITEM =====");
            System.out.println("ID:           " + item.getId());
            System.out.println("Nome:         " + item.getNome());
            System.out.println("Descrição:    " + item.getDescricao());
            System.out.println("Taxa diária:  R$ " + String.format("%.2f", item.getTaxaDiaria()));
            System.out.println("Estado:       " + item.getEstadoConservacao());
            System.out.println("Reposição:    R$ " + String.format("%.2f", item.getValorReposicao()));
            System.out.println("Categoria:    " + (item.getCategoria() != null ? item.getCategoria().getNome() : "Sem categoria"));
            System.out.println("Fornecedor:   " + (item.getFornecedor() != null ? item.getFornecedor().getRazaoSocial() : "Sem fornecedor"));
            System.out.println("Status:       " + item.getStatus());
            System.out.println("Ativo:        " + item.isAtivo());
        }
    }

    public void listarItens() {

        List<Item> itens = sistema.listarItens();

        System.out.println("\n===== ITENS CADASTRADOS =====");

        if (itens.isEmpty()) {
            System.out.println("Nenhum item cadastrado");
            
        } else {

            System.out.printf("%-5s %-25s %-20s %-10s %-12s %-6s%n","ID", "NOME", "CATEGORIA", "TAXA/DIA", "STATUS", "ATIVO");
            System.out.println("-----------------------------------------------------------------------------------------------------------");

            for (Item item : itens) {
                System.out.printf("%-5d %-25s %-20s R$%-8.2f %-12s %-6s%n", item.getId(),item.getNome(), item.getCategoria() != null ? item.getCategoria().getNome() : "-",item.getTaxaDiaria(),item.getStatus(), item.isAtivo() );
            }
        }
    }

    public void atualizarItem() {

        System.out.print("ID do item: ");
        int id = ValidaEntrada.lerInteiro(scanner);

        Item item = sistema.buscarItem(id);

        if (item == null) {
            System.out.println("Item não encontrado");
        } else {

            System.out.println("Deixe em branco para manter o valor atual.");

            System.out.print("Nome [" + item.getNome() + "]: ");
            String nome = scanner.nextLine().trim();
            if (nome.isEmpty()){ 
                nome = item.getNome();
            }

            System.out.print("Descrição [" + item.getDescricao() + "]: ");
            String descricao = scanner.nextLine().trim();
            if (descricao.isEmpty()) {
                descricao = item.getDescricao();
            }

            System.out.print("Taxa diária [" + item.getTaxaDiaria() + "]: ");
            String taxaStr = scanner.nextLine().trim();

            double taxaDiaria = taxaStr.isEmpty() ? item.getTaxaDiaria() : Double.parseDouble(taxaStr);

            System.out.print("Estado de conservação [" + item.getEstadoConservacao() + "]: ");
            String estado = scanner.nextLine().trim().toUpperCase();
            if (estado.isEmpty()) {
                estado = item.getEstadoConservacao();
            }

            System.out.print("Valor de reposição [" + item.getValorReposicao() + "]: ");
            String reposicaoStr = scanner.nextLine().trim();
            double valorReposicao = reposicaoStr.isEmpty() ? item.getValorReposicao() : Double.parseDouble(reposicaoStr);

            Item itemNovo = new Item(id, nome, descricao, taxaDiaria, estado, valorReposicao, item.getCategoria(), item.getFornecedor());
            if (sistema.atualizarItem(itemNovo)) {
                System.out.println("Item atualizado com sucesso");
            } else {
                System.out.println("Erro ao atualizar item");
            }
        }
    }

    private void desativarItem() {

        System.out.print("ID do item: ");
        int id = ValidaEntrada.lerInteiro(scanner);

        if (sistema.excluirItem(id)) {
            System.out.println("Item desativado com sucesso");
        } else {
            System.out.println("Item não encontrado ou alugado");
        }
    }
}
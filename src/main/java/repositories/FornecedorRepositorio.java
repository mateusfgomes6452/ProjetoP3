package repositories;

import java.util.ArrayList;

import java.util.List;

import entidades.Fornecedor;

public class FornecedorRepositorio implements IFornecedorRepositorio {

    private String arquivo = "dados/fornecedores.csv";

    private List<Fornecedor> fornecedores;
    private ManipulaArquivo manipulaArquivo;

    public FornecedorRepositorio() {

        manipulaArquivo = new ManipulaArquivo(arquivo);
        fornecedores = carregar();

    }

    public List<Fornecedor> carregar() {

        List<Fornecedor> lista = new ArrayList<>();

        for (String[] campos : manipulaArquivo.carregar()) {

            if (campos.length >= 6) {

                int id = Integer.parseInt(campos[0].trim());
                String razaoSocial = campos[1].trim();
                String cnpj = campos[2].trim();
                String email = campos[3].trim();
                String telefone = campos[4].trim();
                boolean ativo = Boolean.parseBoolean(campos[5].trim());

                Fornecedor fornecedor = new Fornecedor(id, razaoSocial, cnpj, email, telefone);

                fornecedor.setAtivo(ativo);

                lista.add(fornecedor);

            }

        }

        return lista;

    }
    @Override
    public void salvar() {

        List<String[]> linhas = new ArrayList<>();

        linhas.add(new String[]{"id", "razaoSocial", "cnpj", "email", "telefone", "ativo"});

        for (Fornecedor fornecedor : fornecedores) {

            linhas.add(new String[]{String.valueOf(fornecedor.getId()),fornecedor.getRazaoSocial(),fornecedor.getCnpj(),fornecedor.getEmail(),fornecedor.getTelefone(),String.valueOf(fornecedor.isAtivo())});

        }

        manipulaArquivo.salvar(linhas);

    }

    @Override
    public void adicionar(Fornecedor fornecedor) {

        fornecedores.add(fornecedor);

        salvar();

    }

    @Override
    public List<Fornecedor> listarTodos() {

        return fornecedores;

    }

    @Override
    public void desativar(int id) {

        Fornecedor fornecedor = buscarPorId(id);

        if (fornecedor != null) {

            fornecedor.desativar();

            salvar();

        }

    }

    @Override
    public Fornecedor buscarPorCnpj(String cnpj) {

        for (Fornecedor fornecedor : fornecedores) {

            if (fornecedor.getCnpj().trim().equals(cnpj.trim())) {

                return fornecedor;

            }

        }

        return null;

    }

    @Override
    public Fornecedor buscarPorId(int id) {

        for (Fornecedor fornecedor : fornecedores) {

            if (fornecedor.getId() == id) {

                return fornecedor;

            }

        }

        return null;

    }

    @Override
    public int gerarProximoId() {

        int maiorId = 0;

        for (Fornecedor fornecedor : fornecedores) {

            if (fornecedor.getId() > maiorId) {

                maiorId = fornecedor.getId();

            }

        }

        return maiorId + 1;

    }

}
package repositories;

import java.util.ArrayList;
import java.util.List;

import entidades.Multa;


public class MultaRepositorio implements IMultaRepositorio {

    private String arquivo = "dados/multas.csv";

    private List<Multa> multas;
    private ManipulaArquivo manipulaArquivo;

    public MultaRepositorio() {

        manipulaArquivo = new ManipulaArquivo(arquivo);

        multas = carregar();

    }
    public List<Multa> carregar() {

        List<Multa> lista = new ArrayList<>();

        for (String[] campos : manipulaArquivo.carregar()) {

            if (campos.length >= 7) {

                int id = Integer.parseInt(campos[0].trim());
                int idContrato = Integer.parseInt(campos[1].trim());
                String tipo = campos[2].trim();
                String descricao = campos[3].trim();
                double valor = Double.parseDouble(campos[4].trim());
                boolean paga = Boolean.parseBoolean(campos[5].trim());
                String dataCriacao = campos[6].trim();

                Multa multa = new Multa(id, idContrato, tipo, descricao, valor, dataCriacao);

                multa.setPaga(paga);

                lista.add(multa);

            }

        }

        return lista;

    }

    public void salvar() {

        List<String[]> linhas = new ArrayList<>();

        linhas.add(new String[]{"id","idContrato","tipo","descricao","valor","paga","dataCriacao"});

        for (Multa multa : multas) {

            linhas.add(new String[]{String.valueOf(multa.getId()),String.valueOf(multa.getIdContrato()),multa.getTipo(),multa.getDescricao().replace(",", ";"),String.valueOf(multa.getValor()),String.valueOf(multa.isPaga()),multa.getDataCriacao()});

        }

        manipulaArquivo.salvar(linhas);

    }

    @Override
    public void adicionar(Multa multa) {

        multas.add(multa);

        salvar();

    }

    @Override
    public List<Multa> listarTodos() {

        return multas;

    }

    @Override
    public Multa buscarPorId(int id) {

        for (Multa multa : multas) {

            if (multa.getId() == id) {

                return multa;

            }

        }

        return null;

    }

    @Override
    public List<Multa> buscarPorContrato(int idContrato) {

        List<Multa> resultado = new ArrayList<>();

        for (Multa multa : multas) {

            if (multa.getIdContrato() == idContrato) {

                resultado.add(multa);

            }

        }

        return resultado;

    }

    @Override
    public List<Multa> buscarPendentes() {

        List<Multa> resultado = new ArrayList<>();

        for (Multa multa : multas) {

            if (!multa.isPaga()) {

                resultado.add(multa);

            }

        }

        return resultado;

    }

    @Override
    public int gerarProximoId() {

        int maiorId = 0;

        for (Multa multa : multas) {

            if (multa.getId() > maiorId) {

                maiorId = multa.getId();

            }

        }

        return maiorId + 1;

    }

}

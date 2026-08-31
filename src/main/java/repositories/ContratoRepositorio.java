package repositories;

import java.util.ArrayList;
import java.util.List;

import entidades.Cliente;
import entidades.ContratoAluguel;
import entidades.Item;
import entidades.Usuario;

public class ContratoRepositorio implements IContratoRepositorio {

    private String arquivo = "dados/contratos.csv";

    private List<ContratoAluguel> contratos;
    private IUsuarioRepositorio usuarioRepositorio;
    private IItemRepositorio itemRepositorio;
    private ManipulaArquivo manipulaArquivo;

    public ContratoRepositorio(IUsuarioRepositorio usuarioRepositorio, IItemRepositorio itemRepositorio) {

        this.usuarioRepositorio = usuarioRepositorio;
        this.itemRepositorio = itemRepositorio;

        manipulaArquivo = new ManipulaArquivo(arquivo);

        contratos = carregar();

    }

    public List<ContratoAluguel> carregar() {

        List<ContratoAluguel> lista = new ArrayList<>();

        for (String[] campos : manipulaArquivo.carregar()) {

            if (campos.length >= 9) {

                int id = Integer.parseInt(campos[0].trim());
                int idCliente = Integer.parseInt(campos[1].trim());
                int idItem = Integer.parseInt(campos[2].trim());
                String dataRetirada = campos[3].trim();
                String dataPrevista = campos[4].trim();
                String dataReal = campos[5].trim();
                double valorTotal = Double.parseDouble(campos[6].trim());
                double valorMulta = Double.parseDouble(campos[7].trim());
                String status = campos[8].trim();

                boolean multaPaga = false;

                if (campos.length > 9) {

                    multaPaga = Boolean.parseBoolean(campos[9].trim());

                }

                Usuario usuario = usuarioRepositorio.buscarPorId(idCliente);
                Item item = itemRepositorio.buscarPorId(idItem);

                if (usuario instanceof Cliente && item != null) {
                    
                    Cliente cliente = (Cliente) usuario;

                    ContratoAluguel contrato = new ContratoAluguel(id, cliente, item, dataRetirada, dataPrevista, valorTotal);

                    if (dataReal.equals("-")) {

                        contrato.setDataDevolucaoReal(null);

                    } else {

                        contrato.setDataDevolucaoReal(dataReal);

                    }

                    contrato.setValorMulta(valorMulta);
                    contrato.setStatus(status);
                    contrato.setMultaPaga(multaPaga);

                    lista.add(contrato);

                }

            }

        }

        return lista;

    }
    @Override
    public void salvar() {

        List<String[]> linhas = new ArrayList<>();

        linhas.add(new String[]{"id","idCliente","idItem","dataRetirada","dataDevolucaoPrevista","dataDevolucaoReal","valorTotal","valorMulta","status","multaPaga"});

        for (ContratoAluguel contrato : contratos) {

            linhas.add(new String[]{String.valueOf(contrato.getId()),String.valueOf(contrato.getCliente().getId()),String.valueOf(contrato.getItem().getId()),contrato.getDataRetirada(),contrato.getDataDevolucaoPrevista(),contrato.getDataDevolucaoReal() != null ? contrato.getDataDevolucaoReal() : "-",String.valueOf(contrato.getValorTotal()),String.valueOf(contrato.getValorMulta()),contrato.getStatus(),String.valueOf(contrato.isMultaPaga())});

        }

        manipulaArquivo.salvar(linhas);

    }

    @Override
    public void adicionar(ContratoAluguel contrato) {

        contratos.add(contrato);

        salvar();

    }

    @Override
    public List<ContratoAluguel> listarTodos() {

        return contratos;

    }

    @Override
    public ContratoAluguel buscarPorId(int id) {

        for (ContratoAluguel contrato : contratos) {

            if (contrato.getId() == id) {

                return contrato;

            }

        }

        return null;

    }

    @Override
    public void remover(ContratoAluguel contrato) {

        contratos.remove(contrato);

        salvar();

    }

    @Override
    public List<ContratoAluguel> buscarPorCliente(int idCliente) {

        List<ContratoAluguel> resultado = new ArrayList<>();

        for (ContratoAluguel contrato : contratos) {

            if (contrato.getCliente().getId() == idCliente) {

                resultado.add(contrato);

            }

        }

        return resultado;

    }

    @Override
    public ContratoAluguel buscarContratoAtivoPorItem(int idItem) {

        for (ContratoAluguel contrato : contratos) {

            if (contrato.getItem().getId() == idItem && contrato.estaAtivo()) {

                return contrato;

            }

        }

        return null;

    }

    @Override
    public int gerarProximoId() {

        int maiorId = 0;

        for (ContratoAluguel contrato : contratos) {

            if (contrato.getId() > maiorId) {

                maiorId = contrato.getId();

            }

        }

        return maiorId + 1;

    }

}
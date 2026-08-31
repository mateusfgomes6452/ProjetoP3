package support;

import java.util.ArrayList;
import java.util.List;

import entidades.ContratoAluguel;
import repositories.IContratoRepositorio;


public class FakeContratoRepositorio implements IContratoRepositorio {

    private final List<ContratoAluguel> contratos = new ArrayList<>();
    private int chamadasSalvar = 0;

    @Override
    public void adicionar(ContratoAluguel contrato) {
        contratos.add(contrato);
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
    public void remover(ContratoAluguel contrato) {
        contratos.remove(contrato);
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

    @Override
    public void salvar() {
        chamadasSalvar++;
    }

    public int getChamadasSalvar() {
        return chamadasSalvar;
    }
}

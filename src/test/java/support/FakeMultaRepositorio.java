package support;

import java.util.ArrayList;
import java.util.List;

import entidades.Multa;
import repositories.IMultaRepositorio;

public class FakeMultaRepositorio implements IMultaRepositorio {

    private final List<Multa> multas = new ArrayList<>();
    private int chamadasSalvar = 0;

    @Override
    public void adicionar(Multa multa) {
        multas.add(multa);
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

    @Override
    public void salvar() {
        chamadasSalvar++;
    }

    public int getChamadasSalvar() {
        return chamadasSalvar;
    }
}

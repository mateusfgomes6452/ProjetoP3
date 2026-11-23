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
    public Multa buscarPorId(Long id) {
        for (Multa multa : multas) {
            if (multa.getId() == id) {
                return multa;
            }
        }
        return null;
    }

    @Override
    public List<Multa> buscarPorContrato(Long idContrato) {
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
    public void salvar() {
        chamadasSalvar++;
    }

    public int getChamadasSalvar() {
        return chamadasSalvar;
    }
}

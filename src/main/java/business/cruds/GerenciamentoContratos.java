package business.cruds;

import java.util.List;

import business.interfaces.IGerenciamentoContratos;
import entidades.ContratoAluguel;
import repositories.IContratoRepositorio;

public class GerenciamentoContratos implements IGerenciamentoContratos {

    private final IContratoRepositorio repositorio;

    public GerenciamentoContratos(IContratoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public boolean cadastrarContrato(ContratoAluguel contrato) {

        if (repositorio.buscarPorId(contrato.getId()) != null) {
            return false;
        }

        if (repositorio.buscarContratoAtivoPorItem(contrato.getItem().getId()) != null) {
            return false;
        }

        repositorio.adicionar(contrato);

        return true;
    }

    @Override
    public ContratoAluguel buscarContrato(Long id) {
        return repositorio.buscarPorId(id);
    }

    @Override
    public List<ContratoAluguel> listarContratos() {
        return repositorio.listarTodos();
    }

    @Override
    public boolean finalizarContrato(Long id) {

        ContratoAluguel contrato = repositorio.buscarPorId(id);

        if (contrato == null) {
            return false;
        }

        contrato.finalizarContrato();

        repositorio.salvar();

        return true;
    }

    @Override
    public boolean cancelarContrato(Long id) {

        ContratoAluguel contrato = repositorio.buscarPorId(id);

        if (contrato == null) {
            return false;
        }

        contrato.cancelarContrato();

        repositorio.salvar();

        return true;
    }

    @Override
    public boolean quitarMultaContrato(Long idContrato) {
    
        ContratoAluguel contrato = repositorio.buscarPorId(idContrato);
    
        if (contrato == null) {
            return false;
        }
    
        if (contrato.getValorMulta() <= 0 || contrato.isMultaPaga()) {
            return false;
        }
    
        contrato.setMultaPaga(true);
    
        repositorio.salvar();
    
        return true;
    }

    @Override
    public boolean clientePossuiMultaPendente(Long idCliente) {

        for (ContratoAluguel contrato : repositorio.listarTodos()) {

            if (contrato.getCliente().getId() == idCliente
                    && contrato.getValorMulta() > 0
                    && !contrato.isMultaPaga()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean clientePossuiHistorico(Long idCliente) {

        for (ContratoAluguel contrato : repositorio.listarTodos()) {

            if (contrato.getCliente().getId() == idCliente) {
                return true;
            }
        }

        return false;
    }

    @Override
    public double calcularMulta(long diasAtraso, double taxaDiaria) {
        if (diasAtraso <= 0) {
            return 0.0;
        }
        return 10 + (0.05 * taxaDiaria * diasAtraso);
    }
}

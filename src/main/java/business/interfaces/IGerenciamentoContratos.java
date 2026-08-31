package business.interfaces;
import java.util.List;

import entidades.ContratoAluguel;

public interface IGerenciamentoContratos {

    public abstract boolean cadastrarContrato(ContratoAluguel contrato);

    public abstract ContratoAluguel buscarContrato(int id);

    public abstract List<ContratoAluguel> listarContratos();

    public abstract boolean finalizarContrato(int id);

    public abstract boolean cancelarContrato(int id);

    public abstract boolean quitarMultaContrato(int idContrato);

    public abstract int gerarProximoId();

    public boolean clientePossuiMultaPendente(int idCliente);

    public boolean clientePossuiHistorico(int idCliente);

    public double calcularMulta(long diasAtraso, double taxaDiaria);

}
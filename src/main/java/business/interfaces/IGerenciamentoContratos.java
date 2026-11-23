package business.interfaces;
import java.util.List;

import entidades.ContratoAluguel;

public interface IGerenciamentoContratos {

    public abstract boolean cadastrarContrato(ContratoAluguel contrato);

    public abstract ContratoAluguel buscarContrato(Long id);

    public abstract List<ContratoAluguel> listarContratos();

    public abstract boolean finalizarContrato(Long id);

    public abstract boolean cancelarContrato(Long id);

    public abstract boolean quitarMultaContrato(Long idContrato);

    public abstract int gerarProximoId();

    public boolean clientePossuiMultaPendente(Long idCliente);

    public boolean clientePossuiHistorico(Long idCliente);

    public double calcularMulta(long diasAtraso, double taxaDiaria);

}
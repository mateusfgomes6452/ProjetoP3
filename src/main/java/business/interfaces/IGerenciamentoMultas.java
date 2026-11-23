package business.interfaces;

import java.util.List;

import entidades.Multa;

public interface IGerenciamentoMultas {

    public abstract boolean registrarMulta(Multa multa);

    public abstract Multa buscarMulta(Long id);

    public abstract List<Multa> listarMultas();

    public abstract List<Multa> listarMultasPorContrato(Long idContrato);

    public abstract List<Multa> listarMultasPendentes();

    public abstract boolean quitarMulta(Long id);

    public abstract int gerarProximoId();
}
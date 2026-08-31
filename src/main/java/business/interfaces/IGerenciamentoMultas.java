package business.interfaces;

import java.util.List;

import entidades.Multa;

public interface IGerenciamentoMultas {

    public abstract boolean registrarMulta(Multa multa);

    public abstract Multa buscarMulta(int id);

    public abstract List<Multa> listarMultas();

    public abstract List<Multa> listarMultasPorContrato(int idContrato);

    public abstract List<Multa> listarMultasPendentes();

    public abstract boolean quitarMulta(int id);

    public abstract int gerarProximoId();
}
package business.relatorios;
public interface IRelatorios {

    public abstract void gerarRelatorioItensDisponiveis();

    public abstract void gerarHistoricoCliente(int idCliente);

    public abstract void gerarRelatorioItensAlugados();

    public abstract void gerarRelatorioFaturamento(String dataInicial, String dataFinal);

}
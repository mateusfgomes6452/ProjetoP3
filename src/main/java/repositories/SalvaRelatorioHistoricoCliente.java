package repositories;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import entidades.ContratoAluguel;

public class SalvaRelatorioHistoricoCliente {
	private static final Logger LOG = Logger.getLogger(SalvaRelatorioHistoricoCliente.class.getName());

    public void salvar(List<ContratoAluguel> contratos) {
    	

        File pastaRelatorios = new File("relatorios");

        if (!pastaRelatorios.exists()) {
            pastaRelatorios.mkdirs();
        }

        File arquivo = new File(pastaRelatorios, "historico_cliente.csv");

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivo))) {

            escritor.write(
                    "IdContrato,Cliente,Item,DataRetirada,DataPrevista,Status,ValorTotal,Multa");

            escritor.newLine();

            for (ContratoAluguel contrato :
                    contratos) {

                escritor.write(contrato.getId() + ";" + contrato.getCliente().getNome() + ";" + contrato.getItem().getNome() + ";" + contrato.getDataRetirada() + ";" + contrato.getDataDevolucaoPrevista() + ";" + contrato.getStatus() + ";" + contrato.getValorTotal() + ";" + contrato.getValorMulta());

                escritor.newLine();

            }


        } catch (IOException e) {
        	LOG.log(Level.SEVERE, "Erro ao gerar relatório",e);

        }

    }

}
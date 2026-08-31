package repositories;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import entidades.ContratoAluguel;

public class SalvaRelatorioHistoricoCliente {

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

            System.out.println("Erro ao gerar relatório");

        }

    }

}
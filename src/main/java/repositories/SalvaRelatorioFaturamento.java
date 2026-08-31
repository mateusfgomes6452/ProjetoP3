package repositories;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import entidades.ContratoAluguel;

public class SalvaRelatorioFaturamento {

    public void salvar(List<ContratoAluguel> contratos,String dataInicial,String dataFinal) {

        File pastaRelatorios = new File("relatorios");

        if (!pastaRelatorios.exists()) {
            pastaRelatorios.mkdirs();

        }

        File arquivo =new File(pastaRelatorios, "faturamento.csv");

        double totalAlugueis = 0;
        double totalMultas = 0;

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivo))
) {

            escritor.write("Contrato;Cliente;ValorAluguel;Multa");

            escritor.newLine();

            for (ContratoAluguel contrato : contratos) {

                totalAlugueis += contrato.getValorTotal();

                totalMultas += contrato.getValorMulta();

                escritor.write(contrato.getId() + ";" + contrato.getCliente().getNome() + ";" + contrato.getValorTotal() + ";" + contrato.getValorMulta()
                );

                escritor.newLine();

            }

            escritor.newLine();

            escritor.write("PERIODO," + dataInicial +" a " + dataFinal);

            escritor.newLine();

            escritor.write("TOTAL_ALUGUEIS," + totalAlugueis);

            escritor.newLine();

            escritor.write("TOTAL_MULTAS," + totalMultas);

            escritor.newLine();

            escritor.write("TOTAL_GERAL," + (totalAlugueis + totalMultas));

        } catch (IOException e) {

            System.out.println("Erro ao gerar relatório");

        }

    }

}
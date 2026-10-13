package repositories;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import entidades.ContratoAluguel;

public class SalvaRelatorioItensAlugados {

    private static final Logger LOGGER = Logger.getLogger(SalvaRelatorioItensAlugados.class.getName());

    public void salvar(List<ContratoAluguel> contratos) {

        File pastaRelatorios = new File("relatorios");

        if (!pastaRelatorios.exists()) {
            pastaRelatorios.mkdirs();
        }

        File arquivo = new File(pastaRelatorios, "itens_alugados.csv");

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivo))) {

            escritor.write("Contrato;Cliente;Item;DataPrevista;Situacao");
            escritor.newLine();

            LocalDate hoje = LocalDate.now(java.time.ZoneId.of("America/Recife"));

            for (ContratoAluguel contrato : contratos) {

                LocalDate dataPrevista = LocalDate.parse(contrato.getDataDevolucaoPrevista());

                String situacao;

                if (hoje.isAfter(dataPrevista)) {
                    situacao = "ATRASADO";
                } else {
                    situacao = "EM_DIA";
                }

                escritor.write(contrato.getId() + ";" + contrato.getCliente().getNome() + ";" + contrato.getItem().getNome() + ";" + contrato.getDataDevolucaoPrevista() + ";" + situacao);
                escritor.newLine();
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao gerar relatório", e);
        }
    }
}

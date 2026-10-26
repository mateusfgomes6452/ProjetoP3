package repositories;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import entidades.Item;

public class SalvaRelatorioItensDisponiveis {

    public void salvar(List<Item> itens) {

        File pastaRelatorios = new File("relatorios");

        if (!pastaRelatorios.exists()) {
            pastaRelatorios.mkdirs();

        }

        for (int i = 0; i < itens.size() - 1; i++) {

            for (int j = 0; j < itens.size() - 1 - i; j++) {

                String categoriaAtual = nomeCategoria(itens.get(j));

                String proximaCategoria = nomeCategoria(itens.get(j + 1));

                if (categoriaAtual.compareToIgnoreCase(proximaCategoria) > 0) {

                    Item auxiliar =itens.get(j);

                    itens.set(j, itens.get(j + 1));

                    itens.set(j + 1, auxiliar);
                }

            }

        }

        File arquivo =new File(pastaRelatorios, "itens_disponiveis.csv");

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivo))) {

            escritor.write("Categoria;IdItem;Nome;TaxaDiaria");

            escritor.newLine();

            for (Item item : itens) {

                escritor.write(
                        nomeCategoria(item) + ";" + item.getId() + ";" + item.getNome() + ";" + item.getTaxaDiaria());

                escritor.newLine();

            }

            
        } catch (IOException e) {

            System.out.println("Erro ao gerar relatório de itens disponíveis");

        }

    }

    private String nomeCategoria(Item item) {
        return item.getCategoria() == null ? "Sem categoria" : item.getCategoria().getNome();
    }

}

package repositories;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManipulaArquivo {

    private String caminhoArquivo;

    public ManipulaArquivo(String caminhoArquivo) {

        this.caminhoArquivo = caminhoArquivo;

    }

    public void salvar(List<String[]> linhas) {

        File arquivo = new File(caminhoArquivo);

        File pasta = arquivo.getParentFile();

        if (pasta != null && !pasta.exists()) {

            pasta.mkdirs();

        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {

            for (String[] linha : linhas) {

                writer.write(String.join(";", linha));
                writer.newLine();

            }

        } catch (IOException e) {

            System.out.println("Erro ao salvar arquivo " + caminhoArquivo + ": " + e.getMessage());

        }

    }

    public List<String[]> carregar() {

        List<String[]> linhas = new ArrayList<>();

        File arquivo = new File(caminhoArquivo);

        if (!arquivo.exists()) {

            return linhas;

        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {

            reader.readLine();

            String linha;

            while ((linha = reader.readLine()) != null) {

                linhas.add(linha.split(";", -1));

            }

        } catch (IOException e) {

            System.out.println("Erro ao carregar arquivo " + caminhoArquivo + ": " + e.getMessage());

        }

        return linhas;

    }

}
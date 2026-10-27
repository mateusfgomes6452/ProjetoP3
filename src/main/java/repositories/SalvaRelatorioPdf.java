package repositories;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import entidades.ContratoAluguel;
import entidades.Item;

/**
 * Gera os relatórios em PDF (pasta "relatorios", relativa ao diretório de execução).
 * Usa as mesmas listas que as telas e o CSV já recebem, sem reler arquivos.
 * Em caso de falha lança exceção de runtime, para a tela poder avisar o usuário.
 */
public class SalvaRelatorioPdf {

    private static final String PASTA = "relatorios";
    private static final ZoneId FUSO = ZoneId.of("America/Recife");
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATA_HORA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final Color AZUL = new Color(0x07, 0x3C, 0x80);
    private static final Color FUNDO_LINHA = new Color(0xF2, 0xF5, 0xFB);
    private static final Color BORDA = new Color(0xD5, 0xE0, 0xEF);
    private static final Color CINZA = new Color(0x78, 0x89, 0xA3);

    private static final Font FONTE_TITULO = FontFactory.getFont(FontFactory.HELVETICA, 18, Font.BOLD, AZUL);
    private static final Font FONTE_SUBTITULO = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.NORMAL, Color.DARK_GRAY);
    private static final Font FONTE_DATA = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, CINZA);
    private static final Font FONTE_CABECALHO = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD, Color.WHITE);
    private static final Font FONTE_CELULA = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, Color.BLACK);
    private static final Font FONTE_RESUMO = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, Color.BLACK);
    private static final Font FONTE_RESUMO_NEGRITO = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD, AZUL);

    // ------------------------------------------------------------------
    // Os quatro relatórios
    // ------------------------------------------------------------------

    public void salvarItensDisponiveis(List<Item> itens) {
        List<Item> ordenados = new ArrayList<>(itens);
        ordenados.sort(Comparator.comparing(this::nomeCategoria, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Item::getNome, String.CASE_INSENSITIVE_ORDER));

        List<String[]> linhas = new ArrayList<>();
        double somaTaxas = 0;
        for (Item item : ordenados) {
            somaTaxas += item.getTaxaDiaria();
            linhas.add(new String[] {
                nomeCategoria(item),
                "#" + item.getId(),
                item.getNome(),
                moeda(item.getTaxaDiaria()) + "/dia" });
        }
        double media = ordenados.isEmpty() ? 0 : somaTaxas / ordenados.size();

        List<String[]> resumo = new ArrayList<>();
        resumo.add(new String[] { "Itens disponíveis", String.valueOf(ordenados.size()) });
        resumo.add(new String[] { "Taxa média por dia", moeda(media) });

        gerar("itens_disponiveis.pdf", "Relatório de itens disponíveis", null,
                new String[] { "Categoria", "Nº", "Item", "Taxa por dia" },
                new float[] { 30, 10, 38, 22 },
                new boolean[] { false, false, false, true },
                linhas, resumo);
    }

    public void salvarHistoricoCliente(List<ContratoAluguel> contratos) {
        String cliente = contratos.isEmpty() ? "-" : contratos.get(0).getCliente().getNome();

        List<String[]> linhas = new ArrayList<>();
        double totalAlugueis = 0;
        double totalMultas = 0;
        for (ContratoAluguel contrato : contratos) {
            totalAlugueis += contrato.getValorTotal();
            totalMultas += contrato.getValorMulta();
            linhas.add(new String[] {
                "#" + contrato.getId(),
                contrato.getItem().getNome(),
                data(contrato.getDataRetirada()),
                data(contrato.getDataDevolucaoPrevista()),
                contrato.getStatus(),
                moeda(contrato.getValorTotal()),
                moeda(contrato.getValorMulta()) });
        }

        List<String[]> resumo = new ArrayList<>();
        resumo.add(new String[] { "Contratos", String.valueOf(contratos.size()) });
        resumo.add(new String[] { "Total em aluguéis", moeda(totalAlugueis) });
        resumo.add(new String[] { "Total em multas", moeda(totalMultas) });

        gerar("historico_cliente.pdf", "Histórico de aluguéis do cliente", "Cliente: " + cliente,
                new String[] { "Nº", "Item", "Retirada", "Devolução prevista", "Status", "Valor", "Multa" },
                new float[] { 8, 26, 13, 15, 13, 13, 12 },
                new boolean[] { false, false, false, false, false, true, true },
                linhas, resumo);
    }

    public void salvarItensAlugados(List<ContratoAluguel> contratos) {
        LocalDate hoje = LocalDate.now(FUSO);

        List<String[]> linhas = new ArrayList<>();
        int atrasados = 0;
        for (ContratoAluguel contrato : contratos) {
            String situacao = "EM DIA";
            String atraso = "-";
            try {
                LocalDate prevista = LocalDate.parse(contrato.getDataDevolucaoPrevista());
                if (hoje.isAfter(prevista)) {
                    situacao = "ATRASADO";
                    long dias = ChronoUnit.DAYS.between(prevista, hoje);
                    atraso = dias == 1 ? "1 dia" : dias + " dias";
                    atrasados++;
                }
            } catch (DateTimeParseException e) {
                situacao = "-";
            }
            linhas.add(new String[] {
                "#" + contrato.getId(),
                contrato.getCliente().getNome(),
                contrato.getItem().getNome(),
                data(contrato.getDataRetirada()),
                data(contrato.getDataDevolucaoPrevista()),
                situacao,
                atraso });
        }

        List<String[]> resumo = new ArrayList<>();
        resumo.add(new String[] { "Itens alugados", String.valueOf(contratos.size()) });
        resumo.add(new String[] { "Em dia", String.valueOf(contratos.size() - atrasados) });
        resumo.add(new String[] { "Devoluções atrasadas", String.valueOf(atrasados) });

        gerar("itens_alugados.pdf", "Relatório de itens alugados", null,
                new String[] { "Nº", "Cliente", "Item", "Retirada", "Devolução prevista", "Situação", "Atraso" },
                new float[] { 7, 20, 22, 13, 15, 13, 10 },
                new boolean[] { false, false, false, false, false, false, false },
                linhas, resumo);
    }

    public void salvarFaturamento(List<ContratoAluguel> contratos, String dataInicial, String dataFinal) {
        List<String[]> linhas = new ArrayList<>();
        double totalAlugueis = 0;
        double totalMultas = 0;
        for (ContratoAluguel contrato : contratos) {
            totalAlugueis += contrato.getValorTotal();
            totalMultas += contrato.getValorMulta();
            linhas.add(new String[] {
                "#" + contrato.getId(),
                contrato.getCliente().getNome(),
                contrato.getItem().getNome(),
                data(contrato.getDataRetirada()),
                moeda(contrato.getValorTotal()),
                moeda(contrato.getValorMulta()),
                moeda(contrato.getValorTotal() + contrato.getValorMulta()) });
        }

        List<String[]> resumo = new ArrayList<>();
        resumo.add(new String[] { "Contratos no período", String.valueOf(contratos.size()) });
        resumo.add(new String[] { "Total de aluguéis", moeda(totalAlugueis) });
        resumo.add(new String[] { "Total de multas", moeda(totalMultas) });
        resumo.add(new String[] { "Total geral", moeda(totalAlugueis + totalMultas) });

        gerar("faturamento.pdf", "Relatório de faturamento",
                "Período: " + data(dataInicial) + " a " + data(dataFinal),
                new String[] { "Nº", "Cliente", "Item", "Retirada", "Aluguel", "Multa", "Total" },
                new float[] { 7, 20, 22, 13, 14, 12, 14 },
                new boolean[] { false, false, false, false, true, true, true },
                linhas, resumo);
    }

    // ------------------------------------------------------------------
    // Montagem do documento (igual para todos os relatórios)
    // ------------------------------------------------------------------

    private void gerar(String nomeArquivo, String titulo, String subtitulo, String[] cabecalhos,
            float[] larguras, boolean[] alinhadoDireita, List<String[]> linhas, List<String[]> resumo) {

        File pasta = new File(PASTA);
        if (!pasta.exists() && !pasta.mkdirs()) {
            throw new UncheckedIOException(new IOException("Não foi possível criar a pasta " + PASTA));
        }
        File arquivo = new File(pasta, nomeArquivo);

        Document documento = new Document(PageSize.A4, 36, 36, 40, 40);

        try (FileOutputStream saida = new FileOutputStream(arquivo)) {
            PdfWriter.getInstance(documento, saida);
            documento.open();

            Paragraph cabecalhoTitulo = new Paragraph(titulo, FONTE_TITULO);
            cabecalhoTitulo.setSpacingAfter(4);
            documento.add(cabecalhoTitulo);

            if (subtitulo != null) {
                Paragraph paragrafoSubtitulo = new Paragraph(subtitulo, FONTE_SUBTITULO);
                paragrafoSubtitulo.setSpacingAfter(2);
                documento.add(paragrafoSubtitulo);
            }

            Paragraph geradoEm = new Paragraph(
                    "Gerado em " + LocalDateTime.now(FUSO).format(DATA_HORA_BR), FONTE_DATA);
            geradoEm.setSpacingAfter(14);
            documento.add(geradoEm);

            documento.add(montarTabela(cabecalhos, larguras, alinhadoDireita, linhas));
            documento.add(montarResumo(resumo));

            documento.close();
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao gravar o PDF " + arquivo.getPath(), e);
        } catch (DocumentException e) {
            throw new IllegalStateException("Erro ao montar o PDF " + nomeArquivo, e);
        }
    }

    private PdfPTable montarTabela(String[] cabecalhos, float[] larguras, boolean[] alinhadoDireita,
            List<String[]> linhas) {

        PdfPTable tabela = new PdfPTable(cabecalhos.length);
        tabela.setWidthPercentage(100);
        tabela.setSpacingAfter(16);
        tabela.setHeaderRows(1);
        try {
            tabela.setWidths(larguras);
        } catch (DocumentException e) {
            throw new IllegalStateException("Larguras de coluna inválidas", e);
        }

        for (int i = 0; i < cabecalhos.length; i++) {
            tabela.addCell(celula(cabecalhos[i], FONTE_CABECALHO, alinhamento(alinhadoDireita[i]), AZUL));
        }

        for (int linha = 0; linha < linhas.size(); linha++) {
            Color fundo = linha % 2 == 1 ? FUNDO_LINHA : null;
            String[] valores = linhas.get(linha);
            for (int coluna = 0; coluna < valores.length; coluna++) {
                tabela.addCell(celula(valores[coluna], FONTE_CELULA, alinhamento(alinhadoDireita[coluna]), fundo));
            }
        }
        return tabela;
    }

    private PdfPTable montarResumo(List<String[]> resumo) {
        PdfPTable tabela = new PdfPTable(2);
        tabela.setWidthPercentage(48);
        tabela.setHorizontalAlignment(Element.ALIGN_RIGHT);

        for (int i = 0; i < resumo.size(); i++) {
            boolean ultima = i == resumo.size() - 1;
            Font fonte = ultima ? FONTE_RESUMO_NEGRITO : FONTE_RESUMO;
            tabela.addCell(celula(resumo.get(i)[0], fonte, Element.ALIGN_LEFT, null));
            tabela.addCell(celula(resumo.get(i)[1], fonte, Element.ALIGN_RIGHT, null));
        }
        return tabela;
    }

    private PdfPCell celula(String texto, Font fonte, int alinhamento, Color fundo) {
        PdfPCell celula = new PdfPCell(new Phrase(texto == null ? "" : texto, fonte));
        celula.setHorizontalAlignment(alinhamento);
        celula.setPadding(5);
        celula.setBorderColor(BORDA);
        if (fundo != null) {
            celula.setBackgroundColor(fundo);
        }
        return celula;
    }

    private int alinhamento(boolean direita) {
        return direita ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT;
    }

    // ------------------------------------------------------------------
    // Formatação (padrão brasileiro; não depende das classes de apresentação)
    // ------------------------------------------------------------------

    private String nomeCategoria(Item item) {
        return item.getCategoria() == null ? "Sem categoria" : item.getCategoria().getNome();
    }

    private String moeda(double valor) {
        return "R$ " + String.format(PT_BR, "%,.2f", valor);
    }

    /** "2026-06-02" -> "02/06/2026". Vazio ou inválido vira "-" (ou o próprio texto, se não for data). */
    private String data(String dataIso) {
        if (dataIso == null || dataIso.isBlank()) {
            return "-";
        }
        try {
            return LocalDate.parse(dataIso).format(DATA_BR);
        } catch (DateTimeParseException e) {
            return dataIso;
        }
    }
}

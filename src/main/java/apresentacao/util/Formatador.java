package apresentacao.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/** Formatação de valores para exibição nas telas (padrão brasileiro). */
public final class Formatador {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    public static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Formatador() {
    }

    /** 1450.0 -> "R$ 1.450,00" */
    public static String moeda(double valor) {
        return "R$ " + String.format(PT_BR, "%,.2f", valor);
    }

    /** "2026-06-02" -> "02/06/2026". Vazio ou inválido vira "—". */
    public static String data(String dataIso) {
        if (dataIso == null || dataIso.isBlank()) {
            return "—";
        }
        try {
            return LocalDate.parse(dataIso).format(DATA_BR);
        } catch (DateTimeParseException e) {
            return dataIso;
        }
    }
}

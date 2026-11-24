package banco;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.*;

public class JPAUtil {
	private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("default", lerConfiguracaoDoAmbiente());
	private JPAUtil() {
		
	}
	public static EntityManager getEntityManager() {
		return emf.createEntityManager();
	}
	public static void fechar() {
		if (emf.isOpen()) {
			emf.close();
		}
	}
	//Configuração das variáveis de ambiente 
	 private static Map<String, String> lerConfiguracaoDoAmbiente() {
	        Map<String, String> config = new HashMap<>();
	        config.put("jakarta.persistence.jdbc.url", obrigatoria("DB_URL"));
	        config.put("jakarta.persistence.jdbc.user", obrigatoria("DB_USER"));
	        config.put("jakarta.persistence.jdbc.password", obrigatoria("DB_PASSWORD"));
	        return config;
	    }
	 //Método para garantir que a variável de ambiente exista
	 private static String obrigatoria(String nome) {
	        String valor = System.getenv(nome);
	        if (valor == null || valor.isBlank()) {
	            throw new IllegalStateException("Variável de ambiente " + nome + " não definida.");
	        }
	        return valor;
	    }
}

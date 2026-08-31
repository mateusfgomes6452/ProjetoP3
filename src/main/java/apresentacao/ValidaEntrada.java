package apresentacao;

import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher; 


public class ValidaEntrada {

    public static int lerInteiro(Scanner scanner) {
		boolean continuar = true;
        while (continuar) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Digite apenas números: ");
            }
        }
		return -1;
    }

    public static double lerDouble(Scanner scanner) {
        boolean continuar = true;
        while (continuar) {
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Digite um valor numérico válido: ");
            }
        }
        return -1;
    }

    public static String lerData(Scanner scanner) {
        boolean continuar = true;
        while (continuar) {
            String entrada = scanner.nextLine().trim();
            if (entrada.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return entrada;
            }
            System.out.print("Formato inválido. Use AAAA-MM-DD: ");
        }
        return "";
    }
    
    public static String lerEmail(Scanner scanner) {	
    	boolean continuar = true;
    	
    	while (continuar) {
    		String entrada = scanner.nextLine().trim();
    		
    		if (entrada.isEmpty()) {
                return "";
            }
    		
    		if (entrada.endsWith("@loja.com")) {
    			return entrada;
    		}
    		System.out.print("Email inválido. O email cadastrado deve ser o próprio da loja (@loja.com): ");
    	}
    	return "";
    }
    
    public static String lerCpf(Scanner scanner) {
    	Pattern padraoCpf = Pattern.compile("[0-9]{3}\\.[0-9]{3}\\.[0-9]{3}\\-[0-9]{2}");
    	
    	boolean continuar = true;
    	
    	while (continuar) {
    		String entrada = scanner.nextLine().trim();
    		
    		if (entrada.isEmpty()) {
                return "";
            }
    		
    		Matcher matcher = padraoCpf.matcher(entrada);
    		
    		if (matcher.matches()) {
    			return entrada;
    		}
    		System.out.print("Formato inválido. Use 000.000.000-00: ");
    	}
    	return "";
    }
    
    public static String lerCnpj(Scanner scanner) {
    	Pattern padraoCnpjAntigo = Pattern.compile("[0-9]{2}\\.[0-9]{3}\\.[0-9]{3}/[0-9]{4}\\-[0-9]{2}");
    	Pattern padraoCnpjNovo = Pattern.compile("[A-Z 0-9]{2}\\.[A-Z 0-9]{3}\\.[A-Z 0-9]{3}/[A-Z 0-9]{4}\\-[A-Z 0-9]{2}");
    	
    	boolean continuar = true;
    	
    	while (continuar) {
    		String entrada = scanner.nextLine().trim();
    		
    		if (entrada.isEmpty()) {
                return "";
            }
    		
    		Matcher matcherAntigo = padraoCnpjAntigo.matcher(entrada);
    		Matcher matcherNovo = padraoCnpjNovo.matcher(entrada);
    		
    		if (matcherAntigo.matches() || matcherNovo.matches()) {
    			return entrada;
    		}
    		System.out.print("Formato inválido. Use 00.000.000/0000-00 ou XX.XXX.XXX/XXXX-XX: ");
    	}
    	return "";
    }
    
    public static String lerTelefone(Scanner scanner) {
    	Pattern padraoTelefoneCelular = Pattern.compile("\\([0-9]{2}\\) 9 [0-9]{4}\\-[0-9]{4}");
    	Pattern padraoTelefoneFixo = Pattern.compile("\\([0-9]{2}\\) [0-9]{4}\\-[0-9]{4}");
    	
    	boolean continuar = true;
    	
    	while (continuar) {
    		String entrada = scanner.nextLine().trim();
    		
    		if (entrada.isEmpty()) {
                return "";
            }
    		
    		Matcher matcherCelular = padraoTelefoneCelular.matcher(entrada);
    		Matcher matcherFixo = padraoTelefoneFixo.matcher(entrada);
    		
    		if (matcherCelular.matches() || matcherFixo.matches()) {
    			return entrada;
    		}
    		System.out.print("Formato inválido. Use (DDD) 9 0000-0000 ou (DDD) 0000-0000: ");
    	}
    	return "";
    }
}
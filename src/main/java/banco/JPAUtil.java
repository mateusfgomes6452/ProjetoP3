package banco;

import jakarta.persistence.*;

public class JPAUtil {
	private static final EntityManagerFactory emf = 
			Persistence.createEntityManagerFactory("atividadep3");
	
	public static EntityManager getEntityManager() {
		return emf.createEntityManager();
	}
	
	public static void fechar() {
		emf.close();
	}
}

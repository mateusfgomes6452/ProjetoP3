package banco;

import jakarta.persistence.*;

public class JPAUtil {
	private static final EntityManagerFactory emf = 
			Persistence.createEntityManagerFactory("default");
	
	public static EntityManager getEntityManager() {
		return emf.createEntityManager();
	}
	
	public static void fechar() {
		emf.close();
	}
}

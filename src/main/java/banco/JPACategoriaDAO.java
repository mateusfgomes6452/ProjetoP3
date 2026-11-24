package banco;

import java.util.List;

import entidades.Categoria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import repositories.ICategoriaRepositorio;

public class JPACategoriaDAO implements ICategoriaRepositorio {

	@Override
	public void adicionar(Categoria categoria) {
		EntityManager em = JPAUtil.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			em.persist(categoria);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			em.close();
		}
	}
	@Override
	public Categoria atualizar(Categoria categoria) {
		EntityManager em = JPAUtil.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Categoria atualizada = em.merge(categoria);
			tx.commit();
			return atualizada;
		} catch (RuntimeException e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			em.close();
		}
	}
	@Override
	public List<Categoria> listarTodos(){
		EntityManager em = JPAUtil.getEntityManager();
		try {
			return em.createQuery("SELECT c FROM Categoria c ORDER by c.id", Categoria.class).getResultList();
		} finally {
			em.close();
		}
	}
	@Override
	public Categoria buscarPorId(Long id) {
		EntityManager em = JPAUtil.getEntityManager();
		try {
			return em.find(Categoria.class,id);
		} finally {
			em.close();
		}
	}
	@Override 
	public Categoria buscarPorNome(String nome) {
		EntityManager em = JPAUtil.getEntityManager();
		try {
			List<Categoria> resultado = em.createQuery("SELECT c FROM Categoria c WHERE LOWER(c.nome) = LOWER(:nome)", Categoria.class).setParameter("nome",nome).getResultList();
			return resultado.isEmpty() ? null : resultado.get(0);
		} finally {
			em.close();
		}
	}
}

package banco;

import jakarta.persistence.*;
import repositories.*;
import java.util.List;
import java.util.function.Consumer;

import entidades.Fornecedor;

public class JPAFornecedorDAO implements IFornecedorRepositorio{
	
	@Override
    public void adicionar(Fornecedor fornecedor) {
		executar(em -> em.persist(fornecedor));
    }

	@Override
	public List<Fornecedor> listarTodos() {
		EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("select f from Fornecedor f", Fornecedor.class)
                     .getResultList();
        } finally {
            em.close();
        }
	}

	@Override
	public void desativar(Long id) {
		executar(em ->{
			Fornecedor f = em.find(Fornecedor.class, id);
			if (f!=null) {
				f.desativar();
			}
		});
	}

	@Override
	public Fornecedor buscarPorCnpj(String cnpj) {
		EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                        "select f from Fornecedor f where f.cnpj = :cnpj", Fornecedor.class)
                     .setParameter("cnpj", cnpj)
                     .getResultStream()
                     .findFirst()
                     .orElse(null);
        } finally {
            em.close();
        }
	}

	@Override
	public Fornecedor buscarPorId(Long id) {
		EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Fornecedor.class, id);
        } finally {
            em.close();
        }
	}
	
	private void executar(Consumer<EntityManager> acao) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            acao.accept(em);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

	@Override
	public void atualizar(Fornecedor fornecedor) {
		executar(em -> em.merge(fornecedor));
	}
}

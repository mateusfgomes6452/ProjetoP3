package banco;

import java.util.List;
import java.util.function.Consumer;

import entidades.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import repositories.IUsuarioRepositorio;

public class JPAUsuarioDAO implements IUsuarioRepositorio {

    @Override
    public void adicionar(Usuario usuario) {
        executar(em -> em.persist(usuario));
    }

    @Override
    public void atualizar(Usuario usuario) {
        executar(em -> em.merge(usuario));
    }

    @Override
    public void remover(Usuario usuario) {
        executar(em -> {
            Usuario existente = em.find(Usuario.class, usuario.getId());
            if (existente != null) {
                em.remove(existente);
            }
        });
    }

    @Override
    public List<Usuario> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM Usuario u ORDER BY u.id", Usuario.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Usuario buscarPorId(Long id) {
        if (id == null) {
            return null;
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public Usuario buscarPorCpf(String cpf) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Usuario> resultado = em
                    .createQuery("SELECT u FROM Usuario u WHERE u.cpf = :cpf", Usuario.class)
                    .setParameter("cpf", cpf)
                    .getResultList();
            return resultado.isEmpty() ? null : resultado.get(0);
        } finally {
            em.close();
        }
    }

    // Usada no login: o e-mail é comparado sem diferenciar maiúsculas de minúsculas
    @Override
    public Usuario buscarPorEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Usuario> resultado = em
                    .createQuery("SELECT u FROM Usuario u WHERE LOWER(u.email) = LOWER(:email)", Usuario.class)
                    .setParameter("email", email)
                    .getResultList();
            return resultado.isEmpty() ? null : resultado.get(0);
        } finally {
            em.close();
        }
    }

    private void executar(Consumer<EntityManager> acao) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            acao.accept(em);
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
}

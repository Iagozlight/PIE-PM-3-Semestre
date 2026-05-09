package projeto.repositories;

import jakarta.persistence.EntityManager;
import projeto.models.Permissoes;

import java.util.List;

public class PermissoesRepository {

    private EntityManager em;

    public PermissoesRepository (EntityManager em){
        this.em = em;
    }

    public Permissoes findById(Long id){
        return em.find(Permissoes.class, id);
    }

    public void create(Permissoes permissoes) {
        em.getTransaction().begin();
        em.persist(permissoes);
        em.getTransaction().commit();
    }

    public void update(Permissoes permissoes) {
        em.getTransaction().begin();
        em.merge(permissoes);
        em.getTransaction().commit();
    }

    public void delete(Permissoes permissoes) {
        em.getTransaction().begin();
        em.remove(em.contains(permissoes) ? permissoes : em.merge(permissoes));
        em.getTransaction().commit();
    }

    public List<Permissoes> findAll() {
        return em.createQuery("select p from Pedidos p", Permissoes.class).getResultList();
    }

    public List<Permissoes> findByName(String prefixo) {
        return em.createQuery("select p from Permissoes p where p.nome_produto like :prefixo", Permissoes.class)
                .setParameter("prefixo", prefixo + "%")
                .getResultList();
    }
}

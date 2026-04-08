package projeto.repositories;

import jakarta.persistence.EntityManager;
import projeto.models.Veiculos;

import java.util.List;

public class VeiculosRepository {

    private EntityManager em;

    public VeiculosRepository (EntityManager em){
        this.em = em;
    }

    public Veiculos findById(Long id){
        return em.find(Veiculos.class, id);
    }

    public void create(Veiculos veiculos) {
        em.getTransaction().begin();
        em.persist(veiculos);
        em.getTransaction().commit();
    }

    public void update(Veiculos veiculos) {
        em.getTransaction().begin();
        em.persist(veiculos);
        em.getTransaction().commit();
    }

    public void delete(Veiculos veiculos) {
        em.getTransaction().begin();
        em.remove(em.contains(veiculos) ? veiculos : em.merge(veiculos));
        em.getTransaction().commit();
    }

    public List<Veiculos> findAll() {
        return em.createQuery("select v from veiculos v", Veiculos.class).getResultList();
    }

    public List<Veiculos> findByName(String prefixo) {
        return em.createQuery("select v from veiculos v where v.nome like :prefixo", Veiculos.class)
                .setParameter("prefixo", prefixo + "%")
                .getResultList();
    }
}

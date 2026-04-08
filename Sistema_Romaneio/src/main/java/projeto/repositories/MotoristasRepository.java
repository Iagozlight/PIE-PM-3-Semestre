package projeto.repositories;

import jakarta.persistence.EntityManager;
import projeto.models.Motoristas;
import projeto.models.Veiculos;

import java.util.List;

public class MotoristasRepository {

    private EntityManager em;

    public MotoristasRepository (EntityManager em){
        this.em = em;
    }

    public Motoristas findById(Long id){
        return em.find(Motoristas.class, id);
    }

    public void create(Motoristas motoristas) {
        em.getTransaction().begin();
        em.persist(motoristas);
        em.getTransaction().commit();
    }

    public void update(Motoristas motoristas) {
        em.getTransaction().begin();
        em.persist(motoristas);
        em.getTransaction().commit();
    }

    public void delete(Motoristas motoristas) {
        em.getTransaction().begin();
        em.remove(em.contains(motoristas) ? motoristas : em.merge(motoristas));
        em.getTransaction().commit();
    }

    public List<Motoristas> findAll() {
        return em.createQuery("select m from motoristas m", Motoristas.class).getResultList();
    }

    public List<Motoristas> findByName(String prefixo) {
        return em.createQuery("select m from motoristas m where m.nome like :prefixo", Motoristas.class)
                .setParameter("prefixo", prefixo + "%")
                .getResultList();
    }

}

package org.example.Repository;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import org.example.models.Motoristas;

import java.util.List;

public class Motoristasrepository {
    private EntityManager em;

    public Motoristasrepository(EntityManager em) {this.em = em;}

    public Motoristas findbyId(Long id) { return (Motoristas) this.em.find(Motoristas.class, id);}

    public void create (Motoristas motoristas) {
        this.em.getTransaction().begin();
        this.em.persist(motoristas);
        this.em.getTransaction().commit();
    }

    public void update (Motoristas motoristas) {
        em.getTransaction().begin();
        em.persist(motoristas);
        em.getTransaction().commit();
    }

    public void delete (Motoristas motoristas) {
        em.getTransaction().begin();
        em.remove(em.contains(motoristas) ? motoristas : em.merge(motoristas));
        em.getTransaction().commit();
    }

    public List<Motoristas> findAll() {return em.createQuery("select u from Motoristas u", Motoristas.class).getResultList();}

    public List<Motoristas> findbyName(String prefixo) {
        return em.createQuery("select u from Motoristas u where u.nome like :prefixo", Motoristas.class)
                .setParameter("prefixo", prefixo + "%")
                .getResultList();
    }
}

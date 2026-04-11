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
        em.merge(motoristas);
        em.getTransaction().commit();
    }

    public void delete(Motoristas motoristas) {
        em.getTransaction().begin();
        em.remove(em.contains(motoristas) ? motoristas : em.merge(motoristas));
        em.getTransaction().commit();
    }

    public List<Motoristas> findAll() {
        return em.createQuery("select m from Motoristas m", Motoristas.class).getResultList();
    }

    public List<Motoristas> findByName(String prefixo) {
        return em.createQuery("select m from Motoristas m where m.nome like :prefixo", Motoristas.class)
                .setParameter("prefixo", prefixo + "%")
                .getResultList();
    }

    public Motoristas findByUsuarioId(Long usuarioId) {
        List<Motoristas> motoristas = em.createQuery("select m from Motoristas m where m.usuarios.id = :usuarioId", Motoristas.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();
        if (motoristas.isEmpty()) {
            return null;
        }
        return motoristas.get(0);
    }

}

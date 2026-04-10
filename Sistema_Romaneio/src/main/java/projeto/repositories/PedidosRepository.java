package projeto.repositories;

import jakarta.persistence.EntityManager;
import projeto.models.Pedidos;

import java.util.List;

public class PedidosRepository {

    private EntityManager em;

    public PedidosRepository (EntityManager em){
        this.em = em;
    }

    public Pedidos findById(Long id){
        return em.find(Pedidos.class, id);
    }

    public void create(Pedidos pedidos) {
        em.getTransaction().begin();
        em.persist(pedidos);
        em.getTransaction().commit();
    }

    public void update(Pedidos pedidos) {
        em.getTransaction().begin();
        em.persist(pedidos);
        em.getTransaction().commit();
    }

    public void delete(Pedidos pedidos) {
        em.getTransaction().begin();
        em.remove(em.contains(pedidos) ? pedidos : em.merge(pedidos));
        em.getTransaction().commit();
    }

    public List<Pedidos> findAll() {
        return em.createQuery("select p from Pedidos p", Pedidos.class).getResultList();
    }

    public List<Pedidos> findByName(String prefixo) {
        return em.createQuery("select p from Pedidos p where p.nome_produto like :prefixo", Pedidos.class)
                .setParameter("prefixo", prefixo + "%")
                .getResultList();
    }
}
package projeto.repositories;

import jakarta.persistence.EntityManager;
import projeto.models.ClientesRomaneio;

import java.util.List;

public class ClientesRomaneioRepository {

    private EntityManager em;

    public ClientesRomaneioRepository (EntityManager em){
        this.em = em;
    }

    public ClientesRomaneio findById(Long id){
        return em.find(ClientesRomaneio.class, id);
    }

    public void create(ClientesRomaneio clientesRomaneio) {
        em.getTransaction().begin();
        em.persist(clientesRomaneio);
        em.getTransaction().commit();
    }

    public void update(ClientesRomaneio clientesRomaneio) {
        em.getTransaction().begin();
        em.persist(clientesRomaneio);
        em.getTransaction().commit();
    }

    public void delete(ClientesRomaneio clientesRomaneio) {
        em.getTransaction().begin();
        em.remove(em.contains(clientesRomaneio) ? clientesRomaneio : em.merge(clientesRomaneio));
        em.getTransaction().commit();
    }

    public List<ClientesRomaneio> findAll() {
        return em.createQuery("select c from clientes_romaneio c", ClientesRomaneio.class).getResultList();
    }

    public List<ClientesRomaneio> findByName(String prefixo) {
        return em.createQuery("select c from clientes_romaneio c where c.nome_cliente like :prefixo", ClientesRomaneio.class)
                .setParameter("prefixo", prefixo + "%")
                .getResultList();
    }
}
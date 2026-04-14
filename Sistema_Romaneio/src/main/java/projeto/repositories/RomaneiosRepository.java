package projeto.repositories;

import jakarta.persistence.EntityManager;
import projeto.models.Motoristas;
import projeto.models.Romaneios;
import projeto.models.Veiculos;

import java.time.LocalDate;
import java.util.List;

public class RomaneiosRepository {

    private EntityManager em;

    public RomaneiosRepository (EntityManager em){
        this.em = em;
    }

    public Romaneios findById(Long id){
        return em.find(Romaneios.class, id);
    }

    public void create(Romaneios romaneios) {
        em.getTransaction().begin();
        em.persist(romaneios);
        em.getTransaction().commit();
    }

    public void update(Romaneios romaneios) {
        em.getTransaction().begin();
        em.merge(romaneios);
        em.getTransaction().commit();
    }

    public void delete(Romaneios romaneios) {
        em.getTransaction().begin();
        em.remove(em.contains(romaneios) ? romaneios : em.merge(romaneios));
        em.getTransaction().commit();
    }

    public List<Romaneios> findAll() {
        return em.createQuery("select r from Romaneios r", Romaneios.class).getResultList();
    }

    public List<Romaneios> findAllCompletos() {
        return em.createQuery(
                "SELECT r FROM Romaneios r JOIN FETCH r.veiculo JOIN FETCH r.motorista ORDER BY r.data DESC",
                Romaneios.class
        ).getResultList();
    }

    public List<Romaneios> findPendentes() {
        return em.createQuery(
                "SELECT r FROM Romaneios r LEFT JOIN FETCH r.veiculo LEFT JOIN FETCH r.motorista " +
                        "WHERE r.veiculo IS NULL OR r.motorista IS NULL ORDER BY r.data DESC",
                Romaneios.class
        ).getResultList();
    }

    public List<Object[]> getRelatorioGeral() {
        String jpql = "SELECT r.id, r.data, c.nome_cliente, p.nome_produto, p.quantidade " +
                "FROM Romaneios r " +
                "JOIN r.clientes c " +
                "LEFT JOIN c.pedidos p " +
                "ORDER BY r.data DESC";
        return em.createQuery(jpql).getResultList();
    }


    public boolean veiculoEmUso(Veiculos veiculo) {
        List<Romaneios> result = em.createQuery(
                        "select r from Romaneios r where r.veiculo = :veiculo", Romaneios.class)
                .setParameter("veiculo", veiculo)
                .getResultList();
        return !result.isEmpty();
    }

    public boolean motoristaEmUso(Motoristas motorista) {
        List<Romaneios> result = em.createQuery(
                        "select r from Romaneios r where r.motorista = :motorista", Romaneios.class)
                .setParameter("motorista", motorista)
                .getResultList();
        return !result.isEmpty();
    }
}

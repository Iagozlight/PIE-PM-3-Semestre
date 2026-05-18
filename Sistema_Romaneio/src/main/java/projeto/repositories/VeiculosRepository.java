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
        em.merge(veiculos);
        em.getTransaction().commit();
    }

    public void delete(Veiculos veiculos) {
        em.getTransaction().begin();
        em.remove(em.contains(veiculos) ? veiculos : em.merge(veiculos));
        em.getTransaction().commit();
    }

    public List<Veiculos> findAll() {
        return em.createQuery("select v from Veiculos v", Veiculos.class).getResultList();
    }

    public List<Veiculos> findByName(String prefixo) {
        return em.createQuery("select v from Veiculos v where v.modelo like :prefixo", Veiculos.class)
                .setParameter("prefixo", prefixo + "%")
                .getResultList();
    }

    public Veiculos findByPlaca(String placa) {
        List<Veiculos> veiculos = em.createQuery(
                        "select v from Veiculos v where upper(v.placa) = upper(:placa)",
                        Veiculos.class)
                .setParameter("placa", placa)
                .getResultList();
        return veiculos.isEmpty() ? null : veiculos.get(0);
    }

    public boolean estaEmUso(Veiculos veiculo) {
        List<Long> result = em.createQuery(
                        "select count(r) from Romaneios r where r.veiculo = :veiculo",
                        Long.class)
                .setParameter("veiculo", veiculo)
                .getResultList();
        return !result.isEmpty() && result.get(0) != null && result.get(0) > 0;
    }
}

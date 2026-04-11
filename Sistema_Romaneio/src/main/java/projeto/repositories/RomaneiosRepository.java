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

    public List<Romaneios> findByData(LocalDate data) {
        return em.createQuery("select r from Romaneios r where r.data = :data", Romaneios.class)
                .setParameter("data", data)
                .getResultList();
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

package projeto.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "romaneios")
public class Romaneios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data")
    private LocalDate data;

    @OneToMany(mappedBy = "romaneio", cascade = CascadeType.ALL)
    private List<ClientesRomaneio> clientes = new ArrayList<>();

    public Romaneios() {}

    public Romaneios(Long id, LocalDate data) {
        this.id = id;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public List<ClientesRomaneio> getClientes() { return clientes; }
    public void setClientes(List<ClientesRomaneio> clientes) { this.clientes = clientes; }

    @Override
    public String toString() {
        return "Romaneio{id=" + id + ", data=" + data + ", clientes=" + clientes + "}";
    }

}
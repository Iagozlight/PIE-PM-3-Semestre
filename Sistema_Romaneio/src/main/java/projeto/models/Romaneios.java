package projeto.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "romaneios")
public class Romaneios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @OneToMany(mappedBy = "romaneio", cascade = CascadeType.ALL)
    private List<ClientesRomaneio> clientes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "veiculo_id", nullable = true)
    private Veiculos veiculo;

    @ManyToOne
    @JoinColumn(name = "motorista_id", nullable = true)
    private Motoristas motorista;

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

    public Veiculos getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculos veiculo) { this.veiculo = veiculo; }

    public Motoristas getMotorista() { return motorista; }
    public void setMotorista(Motoristas motorista) { this.motorista = motorista; }

    public List<ClientesRomaneio> getClientes() { return clientes; }
    public void setClientes(List<ClientesRomaneio> clientes) { this.clientes = clientes; }

    @Override
    public String toString() {
        return "Romaneio{id=" + id + ", data=" + data +
                ", veiculo=" + veiculo +
                ", motorista=" + motorista +
                ", clientes=" + clientes + "}";
    }

}
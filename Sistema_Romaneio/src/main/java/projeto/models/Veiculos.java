package projeto.models;

import jakarta.persistence.*;

@Entity
@Table(name = "veiculos")
public class Veiculos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 50)
    private String nomeVeiculo;

    @Column(name = "placa", nullable = false, length = 50)
    private String placa;

    @Column(name = "disponibilidade", nullable = false)
    private Boolean disponibilidade;

    public Veiculos() {}

    public Veiculos(Long id, String nomeVeiculo, String placa, Boolean disponibilidade) {
        this.id = id;
        this.nomeVeiculo = nomeVeiculo;
        this.placa = placa;
        this.disponibilidade = disponibilidade;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeVeiculo() { return nomeVeiculo; }
    public void setNomeVeiculo(String nomeVeiculo) { this.nomeVeiculo = nomeVeiculo; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public Boolean getDisponibilidade() { return disponibilidade; }
    public void setDisponibilidade(Boolean disponibilidade) { this.disponibilidade = disponibilidade; }

    @Override
    public String toString() {
        return "Veiculo{id=" + id + ", nome='" + nomeVeiculo +
                "', placa='" + placa +
                "', disponibilidade=" + (disponibilidade ? "Disponível" : "Indisponível") + "}";
    }

}

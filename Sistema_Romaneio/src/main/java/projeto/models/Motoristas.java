package projeto.models;

import jakarta.persistence.*;

@Entity
@Table(name = "motoristas")
public class Motoristas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 50)
    private String nome;

    public Motoristas() {}

    public Motoristas(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    @Override
    public String toString() {
        return "Motorista{id=" + id + ", nome='" + nome + "'}";
    }
}
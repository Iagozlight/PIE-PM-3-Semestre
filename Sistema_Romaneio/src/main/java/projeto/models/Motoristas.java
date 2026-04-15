package projeto.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "motoristas")
public class Motoristas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 50)
    private String nome;

    @Column(name = "data_nascimento")
    private LocalDate data_nascimento;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuarios usuarios;

    public Motoristas() {}

    public Motoristas(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public LocalDate getData_nascimento() { return data_nascimento; }
    public void setData_nascimento(LocalDate data_nascimento) { this.data_nascimento = data_nascimento; }

    public Usuarios getUsuarios() { return usuarios; }
    public void setUsuarios(Usuarios usuarios) { this.usuarios = usuarios; }

    @Override
    public String toString() {
        return "Motorista{id=" + id + ", nome='" + nome + "', data_nascimento=" + data_nascimento + "}";
    }
}

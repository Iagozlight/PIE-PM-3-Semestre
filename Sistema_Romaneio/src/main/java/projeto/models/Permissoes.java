package projeto.models;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permissoes")
public class Permissoes {

    @ManyToMany(mappedBy = "permissoes")
    private Set<Usuarios> usuarios = new HashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    public Permissoes() {}

    public Permissoes(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
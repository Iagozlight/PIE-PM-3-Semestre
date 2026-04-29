package projeto.models;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class Usuarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String usuario;
    private String senha;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuarios_permissoes",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "permissao_id")
    )
    private Set<Permissoes> permissoes = new HashSet<>();

    public Usuarios() {}

    public Usuarios(Long id, String usuario, String senha) {
        this.id = id;
        this.usuario = usuario;
        this.senha = senha;
    }

    // --- Getters e Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Set<Permissoes> getPermissoes() { return permissoes; }
    public void setPermissoes(Set<Permissoes> permissoes) { this.permissoes = permissoes; }

    // --- Métodos Auxiliares ---

    /**
     * Verifica se o usuário possui uma permissão específica.
     */
    public boolean temPermissao(String nomePermissao) {
        if (permissoes == null) return false;

        for (Permissoes p : permissoes) {
            if (p.getNome().equalsIgnoreCase(nomePermissao)) {
                return true;
            }
        }
        return false;
    }
}
package projeto.models;
import jakarta.persistence.*;

@Entity
@Table(name = "usuario")

public class Usuarios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//isso aqui é tipo a query id serial la do sql que gera o id automaticamente
    Long id;
    private String usuario;
    private String senha;

    public Usuarios () {}; // contrutor sem parametros

    public Usuarios(Long id, String usuario, String senha) {
        this.id = id;
        this.usuario = usuario;
        this.senha = senha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

}

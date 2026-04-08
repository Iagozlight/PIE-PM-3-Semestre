package projeto.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes_romaneio")
public class ClientesRomaneio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_cliente", nullable = false, length = 50)
    private String nome_cliente;

    @Column(name = "cpf", nullable = false, length = 50)
    private String cpf;

    @Embedded
    private Endereco endereco;

    @OneToMany(mappedBy = "clientes", cascade = CascadeType.ALL)
    private List<Pedidos> pedidos = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "romaneios_id", nullable = false)
    private Romaneios romaneio;

    public ClientesRomaneio() {}

    public ClientesRomaneio(Long id, String nome_cliente, String cpf)
    {
        this.id = id;
        this.nome_cliente = nome_cliente;
        this.cpf = cpf;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getNome_cliente() {
        return nome_cliente;
    }
    public void setNome_cliente(String nome_cliente) {
        this.nome_cliente = nome_cliente;
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public List<Pedidos> getPedidos() { return pedidos; }
    public void setPedidos(List<Pedidos> pedidos) { this.pedidos = pedidos; }

    public Romaneios getRomaneio() { return romaneio; }
    public void setRomaneio(Romaneios romaneio) { this.romaneio = romaneio; }

    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }

    @Override
    public String toString() {
        return "ClientesRomaneio{id=" + id + ", nome='" + nome_cliente +
                "', cpf='" + cpf + "', endereco='" + endereco + "', pedidos='" + pedidos + "}";
    }

}
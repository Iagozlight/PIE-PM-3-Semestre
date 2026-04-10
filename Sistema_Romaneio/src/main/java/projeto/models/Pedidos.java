package projeto.models;

import jakarta.persistence.*;

@Entity
@Table(name = "pedidos")
public class Pedidos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_produto")
    private String nome_produto;

    @Column(name = "quantidade")
    private String quantidade;

    @ManyToOne
    @JoinColumn(name = "clientesRomaneio_id", nullable = false)
    private ClientesRomaneio clientes;

    public Pedidos() {}

    public Pedidos(Long id, String nome_produto, String quantidade) {
        this.id = id;
        this.nome_produto = nome_produto;
        this.quantidade = quantidade;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getNome_produto() {
        return nome_produto;
    }
    public void setNome_produto(String nome_produto) {
        this.nome_produto = nome_produto;
    }

    public String getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }

    public String getClientes() { return clientes; }
    public void setClientes(ClientesRomaneio clientes) { this.clientes = clientes; }

    @Override
    public String toString() {
        return "Pedido{id=" + id + ", nome_produto='" + nome_produto + "', quantidade='" + quantidade + "}";
    }
}
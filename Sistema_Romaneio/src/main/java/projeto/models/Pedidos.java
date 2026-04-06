package projeto.models;

import jakarta.persistence.*;

@Entity(name = "pedidos")
public class Pedidos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_produto")
    private String nome_produto;

    @Column(name = "valor")
    private Double valor;

    @Column(name = "quantidade")
    private String quantidade;

    @ManyToOne
    @JoinColumn(name = "clientesRomaneio_id", nullable = false)
    private ClientesRomaneio clientes;

    public Pedidos() {}

    public Pedidos(Long id, String nome_produto, Double valor, String quantidade) {
        this.id = id;
        this.nome_produto = nome_produto;
        this.valor = valor;
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

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public String toString() {
        return "Pedido{id=" + id + ", nome_produto='" + nome_produto +
                "', valor='" + valor + "', quantidade='" + quantidade + "}";
    }

}
package projeto.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "clientes_romaneio")
public class ClientesRomaneio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_cliente")
    private String nome_cliente;

    @Column(name = "rua")
    private String rua;

    @Column(name = "numero")
    private String numero;

    @Column(name = "bairro")
    private String bairro;

    @Column(name = "cep")
    private String cep;

    @OneToMany(mappedBy = "clientes", cascade = CascadeType.ALL)
    private List<Pedidos> pedidos = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "romaneios_id", nullable = false)
    private Romaneios romaneio;

    public ClientesRomaneio() {}

    public ClientesRomaneio(Long id, String nome_cliente, String rua, String numero,
                            String bairro, String cep)
    {
        this.id = id;
        this.nome_cliente = nome_cliente;
        this.rua = rua;
        this.numero = numero;
        this.bairro = bairro;
        this.cep = cep;
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

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public List<Pedidos> getPedidos() { return pedidos; }
    public void setPedidos(List<Pedidos> pedidos) { this.pedidos = pedidos; }

    @Override
    public String toString() {
        return "ClientesRomaneio{id=" + id + ", nome='" + nome_cliente + "', cep='" + cep + "', rua='" + rua
                + "', numero=" + numero + "', bairro='" + bairro + "', pedidos='" + pedidos + "}";
    }

    public Romaneios getRomaneio() { return romaneio; }
    public void setRomaneio(Romaneios romaneio) { this.romaneio = romaneio; }

}

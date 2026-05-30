package projeto.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    @Column(name = "telefone", nullable = false, length = 20)
    private String telefone;

    @Embedded
    private Endereco endereco;

    @Column(name = "cidades_atendidas", length = 255)
    private String cidadesAtendidas;

    @OneToMany(mappedBy = "clientes", cascade = CascadeType.ALL)
    private List<Pedidos> pedidos = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "romaneios_id")
    private Romaneios romaneio;

    @Column(name = "entregue")
    private Boolean entregue = false;

    public ClientesRomaneio() {}

    public ClientesRomaneio(Long id, String nome_cliente, String cpf) {
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

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public List<Pedidos> getPedidos() { return pedidos; }
    public void setPedidos(List<Pedidos> pedidos) { this.pedidos = pedidos; }

    public Romaneios getRomaneio() { return romaneio; }
    public void setRomaneio(Romaneios romaneio) { this.romaneio = romaneio; }

    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }

    public Boolean getEntregue() { return entregue; }
    public void setEntregue(Boolean entregue) { this.entregue = entregue; }

    public String getCidadesAtendidas() {
        return cidadesAtendidas;
    }

    public void setCidadesAtendidas(String cidadesAtendidas) {
        this.cidadesAtendidas = cidadesAtendidas;
    }

    public List<String> getListaCidadesAtendidas() {
        if (cidadesAtendidas == null || cidadesAtendidas.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(cidadesAtendidas.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public void setListaCidadesAtendidas(List<String> cidades) {
        if (cidades == null || cidades.isEmpty()) {
            this.cidadesAtendidas = null;
            return;
        }
        this.cidadesAtendidas = String.join(", ", cidades);
    }

    public String getCidadePrincipal() {
        List<String> cidades = getListaCidadesAtendidas();
        if (!cidades.isEmpty()) {
            return cidades.get(0);
        }
        return endereco != null ? endereco.getCidade() : null;
    }

    @Override
    public String toString() {
        return "ClientesRomaneio{id=" + id + ", nome='" + nome_cliente +
                "', cpf='" + cpf + "', telefone='" + telefone + "', endereco='" + endereco + "', cidades='" + cidadesAtendidas +
                "', pedidos='" + pedidos + "}";
    }

}

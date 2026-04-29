package projeto.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Scanner;

@Embeddable
public class Endereco {

    @Column(name = "cep", nullable = false, length = 50)
    private String cep;

    @Column(name = "rua", nullable = false, length = 50)
    private String rua;

    @Column(name = "numero", nullable = false, length = 50)
    private String numero;

    @Column(name = "bairro", nullable = false, length = 50)
    private String bairro;

    @Column(name = "complemento", length = 50)
    private String complemento;

    @Column(name = "referencia", length = 50)
    private String referencia;

    public Endereco() {}

    public Endereco(String cep, String rua, String numero, String bairro, String complemento, String referencia) {
        this.cep = cep;
        this.rua = rua;
        this.numero = numero;
        this.bairro = bairro;
        this.complemento = complemento;
        this.referencia = referencia;
        validar();
    }

    private void validar() {
        if (cep == null || cep.isEmpty()) {
            throw new IllegalArgumentException("CEP é obrigatório.");
        }
        if (rua == null || rua.isEmpty()) {
            throw new IllegalArgumentException("Rua é obrigatória.");
        }
        if (numero == null || numero.isEmpty()) {
            throw new IllegalArgumentException("Número é obrigatório.");
        }
        if (bairro == null || bairro.isEmpty()) {
            throw new IllegalArgumentException("Bairro é obrigatório.");
        }
    }

    public static Endereco lerEndereco(Scanner scanner) {
        while (true) {
            try {
                System.out.println("==Endereço==");
                System.out.println("CEP: ");
                String cepCasa = scanner.nextLine();
                System.out.println("Rua: ");
                String ruaCliente = scanner.nextLine();
                System.out.println("Numero da casa: ");
                String numeroCasa = scanner.nextLine();
                System.out.println("Bairro: ");
                String bairroCliente = scanner.nextLine();
                System.out.println("Complemento(Opcional): ");
                String complementoCasa = scanner.nextLine();
                System.out.println("Referencia(Opcional): ");
                String referenciaCasa = scanner.nextLine();
                System.out.println("Endereço do Cliente Confirmado!");

                return new Endereco(cepCasa, ruaCliente, numeroCasa, bairroCliente, complementoCasa, referenciaCasa);
            } catch (IllegalArgumentException e) {
                System.out.println("Erro ao criar endereço: " + e.getMessage());
                continue; // Volta para o início do loop para tentar novamente
            }
        }
    }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    @Override
    public String toString() {
        return "Endereco{cep='" + cep + "', rua='" + rua + "', numero='" + numero +
                "', bairro='" + bairro + "', complemento='" + complemento +
                "', referencia='" + referencia + "'}";
    }

}

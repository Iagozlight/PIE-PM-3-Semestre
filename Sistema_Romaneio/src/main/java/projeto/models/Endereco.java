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

    @Column(name = "cidade", nullable = false, length = 80)
    private String cidade;

    @Column(name = "complemento", length = 50)
    private String complemento;

    @Column(name = "referencia", length = 50)
    private String referencia;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    public Endereco() {}

    public Endereco(String cep, String rua, String numero, String bairro, String cidade,
                    String complemento, String referencia) {
        this(cep, rua, numero, bairro, cidade, complemento, referencia, null, null);
    }

    public Endereco(String cep, String rua, String numero, String bairro, String cidade,
                    String complemento, String referencia, Double latitude, Double longitude) {
        this.cep = cep;
        this.rua = rua;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.complemento = complemento;
        this.referencia = referencia;
        this.latitude = latitude;
        this.longitude = longitude;
        validar();
    }

    private void validar() {
        if (cep == null || cep.isEmpty()) {
            throw new IllegalArgumentException("CEP e obrigatorio.");
        }
        if (rua == null || rua.isEmpty()) {
            throw new IllegalArgumentException("Rua e obrigatoria.");
        }
        if (numero == null || numero.isEmpty()) {
            throw new IllegalArgumentException("Numero e obrigatorio.");
        }
        if (bairro == null || bairro.isEmpty()) {
            throw new IllegalArgumentException("Bairro e obrigatorio.");
        }
        if (cidade == null || cidade.isEmpty()) {
            throw new IllegalArgumentException("Cidade e obrigatoria.");
        }
        if ((latitude == null) != (longitude == null)) {
            throw new IllegalArgumentException("Latitude e longitude devem ser informadas juntas.");
        }
        if (latitude != null && (latitude < -90 || latitude > 90)) {
            throw new IllegalArgumentException("Latitude fora do intervalo permitido.");
        }
        if (longitude != null && (longitude < -180 || longitude > 180)) {
            throw new IllegalArgumentException("Longitude fora do intervalo permitido.");
        }
    }

    public static Endereco lerEndereco(Scanner scanner) {
        while (true) {
            try {
                System.out.println("==Endereco==");
                System.out.println("CEP: ");
                String cepCasa = scanner.nextLine();
                System.out.println("Rua: ");
                String ruaCliente = scanner.nextLine();
                System.out.println("Numero da casa: ");
                String numeroCasa = scanner.nextLine();
                System.out.println("Bairro: ");
                String bairroCliente = scanner.nextLine();
                System.out.println("Cidade: ");
                String cidadeCliente = scanner.nextLine();
                System.out.println("Complemento(Opcional): ");
                String complementoCasa = scanner.nextLine();
                System.out.println("Referencia(Opcional): ");
                String referenciaCasa = scanner.nextLine();
                System.out.println("Latitude(Opcional): ");
                String latitudeStr = scanner.nextLine();
                System.out.println("Longitude(Opcional): ");
                String longitudeStr = scanner.nextLine();
                System.out.println("Endereco do Cliente Confirmado!");

                Double latitude = latitudeStr == null || latitudeStr.trim().isEmpty()
                        ? null
                        : Double.parseDouble(latitudeStr.replace(",", "."));
                Double longitude = longitudeStr == null || longitudeStr.trim().isEmpty()
                        ? null
                        : Double.parseDouble(longitudeStr.replace(",", "."));

                return new Endereco(cepCasa, ruaCliente, numeroCasa, bairroCliente, cidadeCliente,
                        complementoCasa, referenciaCasa, latitude, longitude);
            } catch (NumberFormatException e) {
                System.out.println("Latitude/longitude invalidas. Use numeros validos.");
            } catch (IllegalArgumentException e) {
                System.out.println("Erro ao criar endereco: " + e.getMessage());
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

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    @Override
    public String toString() {
        return "Endereco{cep='" + cep + "', rua='" + rua + "', numero='" + numero +
                "', bairro='" + bairro + "', cidade='" + cidade + "', complemento='" + complemento +
                "', referencia='" + referencia + "', latitude=" + latitude +
                ", longitude=" + longitude + "}";
    }
}

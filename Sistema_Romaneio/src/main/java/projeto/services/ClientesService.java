package projeto.services;

import projeto.models.ClientesRomaneio;
import projeto.models.Endereco;
import projeto.models.Pedidos;
import projeto.repositories.ClientesRomaneioRepository;

import java.util.List;

public class ClientesService {

    private final ClientesRomaneioRepository clientesRomaneioRepository;
    private final NominatimService nominatimService;

    public ClientesService(ClientesRomaneioRepository clientesRomaneioRepository) {
        this.clientesRomaneioRepository = clientesRomaneioRepository;
        this.nominatimService = new NominatimService();
    }

    public boolean validarCpf(String cpf) {
        String cpfNumeros = cpf.replaceAll("\\D", "");
        return cpfNumeros.length() == 11;
    }

    public void criarCliente(String nome, String cpf, Endereco endereco, List<Pedidos> pedidos, List<String> cidadesAtendidas) {
        if (!validarCpf(cpf)) {
            throw new IllegalArgumentException("CPF invalido! Deve conter 11 digitos.");
        }
        if (cidadesAtendidas == null || cidadesAtendidas.isEmpty()) {
            throw new IllegalArgumentException("Selecione pelo menos uma cidade para o cliente.");
        }

        if (endereco != null) {
            String cidadeGeocoding = endereco.getCidade();
            if (cidadeGeocoding == null || cidadeGeocoding.isBlank()) {
                cidadeGeocoding = cidadesAtendidas.get(0);
            }

            String enderecoBusca = endereco.getRua() + ", " +
                    endereco.getNumero() + " - " +
                    endereco.getBairro() + ", " +
                    endereco.getCep() + ", " +
                    cidadeGeocoding + ", PR, Brasil";

            try {
                double[] coordenadas = nominatimService.buscarCoordenadas(endereco, cidadeGeocoding);

                if (coordenadas != null) {
                    endereco.setLatitude(coordenadas[0]);
                    endereco.setLongitude(coordenadas[1]);
                } else {
                    System.out.println("Nominatim nao encontrou coordenadas para: " + enderecoBusca);
                }
            } catch (Exception e) {
                System.err.println("Falha ao buscar coordenadas no Nominatim: " + e.getMessage());
            }
        }

        ClientesRomaneio cliente = new ClientesRomaneio(null, nome, cpf);
        cliente.setEndereco(endereco);
        cliente.setListaCidadesAtendidas(cidadesAtendidas);

        for (Pedidos pedido : pedidos) {
            pedido.setClientes(cliente);
            cliente.getPedidos().add(pedido);
        }

        clientesRomaneioRepository.create(cliente);
    }

    public List<ClientesRomaneio> listarClientes() {
        return clientesRomaneioRepository.findAll();
    }

    public List<ClientesRomaneio> listarClientesSemRomaneio() {
        return clientesRomaneioRepository.findSemRomaneio();
    }

    public ClientesRomaneio buscarPorCpf(String cpf) {
        return clientesRomaneioRepository.findByCpf(cpf);
    }
}

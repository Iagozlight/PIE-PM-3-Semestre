package projeto.services;

import projeto.models.*;
import projeto.repositories.*;

import java.util.List;

public class ClientesService {

    private ClientesRomaneioRepository clientesRomaneioRepository;
    private final NominatimService nominatimService;


    public ClientesService(ClientesRomaneioRepository clientesRomaneioRepository) {
        this.clientesRomaneioRepository = clientesRomaneioRepository;
        this.nominatimService = new NominatimService();
    }

    public boolean validarCpf(String cpf) {
        String cpfNumeros = cpf.replaceAll("\\D", "");
        return cpfNumeros.length() == 11;
    }

    public void criarCliente(String nome, String cpf, Endereco endereco, List<Pedidos> pedidos) {
        if (!validarCpf(cpf)) {
            throw new IllegalArgumentException("CPF inválido! Deve conter 11 dígitos.");
        }

        if (endereco != null) {
            String enderecoBusca = endereco.getRua() + ", " +
                    endereco.getNumero() + " - " +
                    endereco.getBairro() + ", " +
                    endereco.getCep();

            try {
                double[] coordenadas = nominatimService.buscarCoordenadas(enderecoBusca);

                if (coordenadas != null) {
                    endereco.setLatitude(coordenadas[0]);
                    endereco.setLongitude(coordenadas[1]);
                } else {
                    System.out.println("Nominatim não encontrou coordenadas para: " + enderecoBusca);
                }
            } catch (Exception e) {
                System.err.println("Falha ao buscar coordenadas no Nominatim: " + e.getMessage());
            }
        }

        ClientesRomaneio cliente = new ClientesRomaneio(null, nome, cpf);
        cliente.setEndereco(endereco);

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
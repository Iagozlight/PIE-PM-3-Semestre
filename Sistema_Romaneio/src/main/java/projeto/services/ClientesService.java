package projeto.services;

import projeto.models.ClientesRomaneio;
import projeto.models.Endereco;
import projeto.models.Pedidos;
import projeto.repositories.ClientesRomaneioRepository;

import java.util.List;
import java.util.Set;

public class ClientesService {

    private static final int CEP_MIN = 85800000;
    private static final int CEP_MAX = 85999999;

    private static final Set<Integer> DDDS_VALIDOS = Set.of(
            11, 12, 13, 14, 15, 16, 17, 18, 19,
            21, 22, 24,
            27, 28,
            31, 32, 33, 34, 35, 37, 38,
            41, 42, 43, 44, 45, 46,
            47, 48, 49,
            51, 53, 54, 55,
            61,
            62, 64,
            63,
            65, 66,
            67,
            68,
            69,
            71, 73, 74, 75, 77,
            79,
            81, 87,
            82,
            83,
            84,
            85, 88,
            86, 89,
            91, 93, 94,
            92, 97,
            95,
            96,
            98, 99
    );

    private final ClientesRomaneioRepository clientesRomaneioRepository;
    private final NominatimService nominatimService;

    public ClientesService(ClientesRomaneioRepository clientesRomaneioRepository) {
        this.clientesRomaneioRepository = clientesRomaneioRepository;
        this.nominatimService = new NominatimService();
    }

    public boolean validarCpf(String cpf) {
        if (cpf == null) return false;

        String n = cpf.replaceAll("\\D", "");

        if (n.length() != 11) return false;

        if (n.chars().distinct().count() == 1) return false;

        int soma = 0;

        for (int i = 0; i < 9; i++) {
            soma += (n.charAt(i) - '0') * (10 - i);
        }

        int r1 = (soma * 10) % 11;

        if (r1 == 10 || r1 == 11) {
            r1 = 0;
        }

        if (r1 != (n.charAt(9) - '0')) {
            return false;
        }

        soma = 0;

        for (int i = 0; i < 10; i++) {
            soma += (n.charAt(i) - '0') * (11 - i);
        }

        int r2 = (soma * 10) % 11;

        if (r2 == 10 || r2 == 11) {
            r2 = 0;
        }

        return r2 == (n.charAt(10) - '0');
    }

    public boolean validarCep(String cep) {
        if (cep == null) return false;

        String n = cep.replaceAll("\\D", "");

        if (n.length() != 8) return false;

        try {
            int num = Integer.parseInt(n);
            return num >= CEP_MIN && num <= CEP_MAX;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean validarRua(String rua) {
        if (rua == null || rua.isBlank()) return false;

        return rua.matches("[\\p{L}0-9 .\\-]+");
    }

    public boolean validarTelefone(String telefone) {
        if (telefone == null) return false;

        String n = normalizarTelefone(telefone);

        if (n.length() != 10 && n.length() != 11) {
            return false;
        }

        int ddd;

        try {
            ddd = Integer.parseInt(n.substring(0, 2));
        } catch (NumberFormatException e) {
            return false;
        }

        if (!DDDS_VALIDOS.contains(ddd)) {
            return false;
        }

        if (n.length() == 11 && n.charAt(2) != '9') {
            return false;
        }

        return true;
    }

    public String extrairDdd(String telefone) {
        if (telefone == null) return null;

        String n = normalizarTelefone(telefone);

        return n.length() >= 2 ? n.substring(0, 2) : null;
    }

    private String normalizarTelefone(String telefone) {
        if (telefone == null) return "";
        return telefone.replaceAll("\\D", "");
    }

    private String normalizarCpf(String cpf) {
        if (cpf == null) return "";
        return cpf.replaceAll("\\D", "");
    }

    public void criarCliente(String nome, String cpf, String telefone,
                             Endereco endereco, List<Pedidos> pedidos,
                             List<String> cidadesAtendidas) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome Ã© obrigatÃ³rio.");
        }

        String cpfNormalizado = normalizarCpf(cpf);
        String telefoneNormalizado = normalizarTelefone(telefone);

        if (!validarCpf(cpfNormalizado)) {
            throw new IllegalArgumentException("CPF invÃ¡lido! Deve conter 11 dÃ­gitos vÃ¡lidos.");
        }

        if (!validarTelefone(telefoneNormalizado)) {
            throw new IllegalArgumentException(
                    "Telefone invÃ¡lido! Informe DDD + nÃºmero (10 dÃ­gitos para fixo, 11 para celular).");
        }

        if (cidadesAtendidas == null || cidadesAtendidas.isEmpty()) {
            throw new IllegalArgumentException("Selecione pelo menos uma cidade para o cliente.");
        }

        if (pedidos == null || pedidos.isEmpty()) {
            throw new IllegalArgumentException("Adicione pelo menos um pedido.");
        }

        if (endereco != null) {

            if (!validarCep(endereco.getCep())) {
                throw new IllegalArgumentException(
                        "CEP invÃ¡lido! Informe um CEP da regiÃ£o oeste do ParanÃ¡ (85800-000 a 85999-999).");
            }

            if (!validarRua(endereco.getRua())) {
                throw new IllegalArgumentException(
                        "Rua invÃ¡lida! Use apenas letras, nÃºmeros, espaÃ§os, hÃ­fen ou ponto.");
            }

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
                    System.out.println("Nominatim nÃ£o encontrou coordenadas para: " + enderecoBusca);
                }

            } catch (Exception e) {
                System.err.println("Falha ao buscar coordenadas no Nominatim: " + e.getMessage());
            }
        }

        if (clientesRomaneioRepository.findByCpf(cpfNormalizado) != null) {
            throw new IllegalArgumentException("Ja existe cliente cadastrado com este CPF.");
        }

        if (clientesRomaneioRepository.findByTelefone(telefoneNormalizado) != null) {
            throw new IllegalArgumentException("Ja existe cliente cadastrado com este telefone.");
        }

        ClientesRomaneio cliente = new ClientesRomaneio(null, nome, cpfNormalizado);

        cliente.setTelefone(telefoneNormalizado);
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
        return clientesRomaneioRepository.findByCpf(normalizarCpf(cpf));
    }

    public ClientesRomaneio buscarPorTelefone(String telefone) {
        return clientesRomaneioRepository.findByTelefone(normalizarTelefone(telefone));
    }
}
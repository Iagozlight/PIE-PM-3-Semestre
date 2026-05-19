package projeto.services;

import projeto.models.*;
import projeto.repositories.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RomaneiosService {

    private RomaneiosRepository romaneiosRepository;
    private ClientesRomaneioRepository clientesRomaneioRepository;

    public RomaneiosService(RomaneiosRepository romaneiosRepository,
                            ClientesRomaneioRepository clientesRomaneioRepository) {
        this.romaneiosRepository = romaneiosRepository;
        this.clientesRomaneioRepository = clientesRomaneioRepository;
    }

    public void criarRomaneio(LocalDate data, List<ClientesRomaneio> clientes) {
        Romaneios romaneio = new Romaneios(null, data);

        List<ClientesRomaneio> clientesOrdenados = new ArrayList<>(clientes);
        clientesOrdenados.sort(Comparator
                .comparing(ClientesRomaneio::getCidadePrincipal, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(ClientesRomaneio::getNome_cliente, String.CASE_INSENSITIVE_ORDER));

        for (ClientesRomaneio cliente : clientesOrdenados) {
            cliente.setRomaneio(romaneio);
            romaneio.getClientes().add(cliente);
        }

        romaneiosRepository.create(romaneio);
    }

    public String atribuirVeiculo(Romaneios romaneio, Veiculos veiculo) {
        if (veiculo == null) {
            romaneio.setVeiculo(null);
            romaneiosRepository.update(romaneio);
            return "Veiculo removido do romaneio!";
        }
        if (veiculo.getDisponibilidade() == null || !veiculo.getDisponibilidade()) {
            return "VeÃ­culo indisponÃ­vel para romaneio!";
        }
        if (romaneiosRepository.veiculoEmUso(veiculo)) {
            return "VeÃ­culo jÃ¡ estÃ¡ em uso em outro romaneio!";
        }
        romaneio.setVeiculo(veiculo);
        romaneiosRepository.update(romaneio);
        return "Veiculo atualizado com sucesso!";
    }

    public String atribuirMotorista(Romaneios romaneio, Motoristas motorista) {
        if (motorista == null) {
            romaneio.setMotorista(null);
            romaneiosRepository.update(romaneio);
            return "Motorista removido do romaneio!";
        }
        if (romaneiosRepository.motoristaEmUso(motorista)) {
            return "Motorista jÃ¡ estÃ¡ em uso em outro romaneio!";
        }
        romaneio.setMotorista(motorista);
        romaneiosRepository.update(romaneio);
        return "Motorista atualizado com sucesso!";
    }

    public void deletarRomaneio(Romaneios romaneio) {
        romaneiosRepository.delete(romaneio);
    }

    public List<Romaneios> listarRomaneios() {
        return romaneiosRepository.findAll();
    }

    public List<ClientesRomaneio> listarClientesSemRomaneio() {
        return clientesRomaneioRepository.findSemRomaneio();
    }

    public Romaneios buscarPorId(Long id) { return romaneiosRepository.findById(id); }

    public void atualizarRomaneio(Romaneios romaneio) { romaneiosRepository.update(romaneio); }

    public void atualizarStatus(Romaneios romaneio, String status) {
        romaneio.setStatus(status);
        romaneiosRepository.update(romaneio);
    }

    public List<Romaneios> listarRomaneiosPorMotorista(Motoristas motorista) {
        return romaneiosRepository.findByMotorista(motorista);
    }
}



package projeto.services;

import projeto.models.*;
import projeto.repositories.*;

import java.time.LocalDate;
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

        for (ClientesRomaneio cliente : clientes) {
            cliente.setRomaneio(romaneio);
            romaneio.getClientes().add(cliente);
        }

        romaneiosRepository.create(romaneio);
    }

    public String atribuirVeiculo(Romaneios romaneio, Veiculos veiculo) {
        if (romaneio.getVeiculo() != null) {
            return "Este romaneio já possui o veículo: " + romaneio.getVeiculo().getNomeVeiculo();
        }
        if (veiculo.getDisponibilidade() == null || !veiculo.getDisponibilidade()) {
            return "Veículo indisponível para romaneio!";
        }
        if (romaneiosRepository.veiculoEmUso(veiculo)) {
            return "Veículo já está em uso em outro romaneio!";
        }
        romaneio.setVeiculo(veiculo);
        romaneiosRepository.update(romaneio);
        return "Veículo atribuído com sucesso!";
    }

    public String atribuirMotorista(Romaneios romaneio, Motoristas motorista) {
        if (romaneio.getMotorista() != null) {
            return "Este romaneio já possui o motorista: " + romaneio.getMotorista().getNome();
        }
        if (romaneiosRepository.motoristaEmUso(motorista)) {
            return "Motorista já está em uso em outro romaneio!";
        }
        romaneio.setMotorista(motorista);
        romaneiosRepository.update(romaneio);
        return "Motorista atribuído com sucesso!";
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
}
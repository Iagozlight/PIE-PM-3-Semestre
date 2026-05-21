package projeto.services;

import projeto.models.Veiculos;
import projeto.repositories.VeiculosRepository;

import java.util.List;
import java.util.Locale;

public class VeiculosService {

    private final VeiculosRepository veiculosRepository;

    public VeiculosService(VeiculosRepository veiculosRepository) {
        this.veiculosRepository = veiculosRepository;
    }

    public void criarVeiculo(String modelo, String placa) {
        if (modelo == null || modelo.trim().isEmpty()) {
            throw new IllegalArgumentException("Modelo do veiculo nao pode ser vazio!");
        }
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("Placa nao pode ser vazia!");
        }

        String placaNormalizada = placa.trim().toUpperCase(Locale.ROOT);
        if (!placaNormalizada.matches("^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$")) {
            throw new IllegalArgumentException("Placa invalida! Use o formato Mercosul, ex: ABC1D23.");
        }
        if (veiculosRepository.findByPlaca(placaNormalizada) != null) {
            throw new IllegalArgumentException("Ja existe um veiculo cadastrado com essa placa.");
        }

        Veiculos veiculo = new Veiculos(null, modelo.trim(), placaNormalizada, true);
        veiculosRepository.create(veiculo);
    }

    public List<Veiculos> listarVeiculos() {
        return veiculosRepository.findAll();
    }

    public void deletarVeiculo(Veiculos veiculo) {
        if (veiculosRepository.estaEmUso(veiculo)) {
            throw new IllegalArgumentException("Nao e possivel excluir um veiculo que esta em uso.");
        }
        veiculosRepository.delete(veiculo);
    }
}

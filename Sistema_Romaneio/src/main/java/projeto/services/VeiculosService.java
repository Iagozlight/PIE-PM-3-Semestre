package projeto.services;

import projeto.models.Veiculos;
import projeto.repositories.VeiculosRepository;

import java.util.List;

public class VeiculosService {

    private VeiculosRepository veiculosRepository;

    public VeiculosService(VeiculosRepository veiculosRepository) {
        this.veiculosRepository = veiculosRepository;
    }

    public void criarVeiculo(String nome, String placa) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do veículo não pode ser vazio!");
        }
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("Placa não pode ser vazia!");
        }
        if (placa.length() < 7) {
            throw new IllegalArgumentException("Placa inválida! Deve ter pelo menos 7 caracteres.");
        }

        Veiculos veiculo = new Veiculos(null, nome, placa, true);
        veiculosRepository.create(veiculo);
    }

    public List<Veiculos> listarVeiculos() {
        return veiculosRepository.findAll();
    }

    public void deletarVeiculo(Veiculos veiculo) {
        veiculosRepository.delete(veiculo);
    }
}
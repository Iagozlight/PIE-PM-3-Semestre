package projeto.services;

import projeto.util.GeoUtils;
import projeto.repositories.ClientesRomaneioRepository;

public class EntregaService {
    private ClientesRomaneioRepository repository;

    public void processarEntrega(Long clienteId, double motoboyLat, double motoboyLon) {
        var cliente = repository.findById(clienteId);

        // O Service aplica a lógica usando o utilitário
        double distancia = GeoUtils.calcularDistancia(
                motoboyLat, motoboyLon,
                cliente.getLatitude(), cliente.getLongitude()
        );

        if (distancia > 50.0) {
            throw new RuntimeException("Muito longe para entregar!");
        }
    }
}
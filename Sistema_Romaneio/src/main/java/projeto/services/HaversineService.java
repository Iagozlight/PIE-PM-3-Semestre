package projeto.services;

import projeto.models.ClientesRomaneio;
import projeto.models.Endereco;
import projeto.util.GeoUtils;

import java.util.List;

public class HaversineService {

    public double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        return GeoUtils.calcularDistancia(lat1, lon1, lat2, lon2);
    }

    public double calcularDistancia(Endereco origem, Endereco destino) {
        validarCoordenadas(origem);
        validarCoordenadas(destino);
        return calcularDistancia(origem.getLatitude(), origem.getLongitude(), destino.getLatitude(), destino.getLongitude());
    }

    public double calcularDistanciaRota(List<ClientesRomaneio> clientes) {
        if (clientes == null || clientes.size() < 2) {
            return 0.0;
        }

        double total = 0.0;
        Endereco anterior = null;
        for (ClientesRomaneio cliente : clientes) {
            Endereco endereco = cliente != null ? cliente.getEndereco() : null;
            if (!possuiCoordenadas(endereco)) {
                anterior = null;
                continue;
            }
            if (anterior != null) {
                total += calcularDistancia(anterior, endereco);
            }
            anterior = endereco;
        }
        return total;
    }

    public boolean possuiCoordenadas(Endereco endereco) {
        return endereco != null && endereco.getLatitude() != null && endereco.getLongitude() != null;
    }

    public void validarCoordenadas(Endereco endereco) {
        if (!possuiCoordenadas(endereco)) {
            throw new IllegalArgumentException("Endereco sem latitude/longitude cadastrados.");
        }
    }
}

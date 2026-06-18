package projeto.models.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jxmapviewer.viewer.GeoPosition;
import projeto.models.util.GeoUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class RotaRodoviariaService {

    private static final String ENDPOINT_BASE = "https://router.project-osrm.org/route/v1/driving/";

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(12))
            .build();

    public RotaCalculada calcularRota(List<GeoPosition> pontos) {
        if (pontos == null || pontos.size() < 2) {
            return new RotaCalculada(Collections.emptyList(), Collections.singletonList(0.0), 0.0, false);
        }

        try {
            return consultarOsrm(pontos);
        } catch (Exception e) {
            return fallbackHaversine(pontos);
        }
    }

    private RotaCalculada consultarOsrm(List<GeoPosition> pontos) throws Exception {
        StringBuilder coordenadas = new StringBuilder();
        for (GeoPosition ponto : pontos) {
            if (ponto == null) {
                return fallbackHaversine(pontos);
            }
            if (coordenadas.length() > 0) {
                coordenadas.append(';');
            }
            coordenadas.append(String.format(Locale.US, "%.6f,%.6f", ponto.getLongitude(), ponto.getLatitude()));
        }

        URI uri = URI.create(ENDPOINT_BASE + coordenadas
                + "?overview=full&geometries=geojson&steps=false&alternatives=false&continue_straight=true");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(15))
                .header("User-Agent", "DutraMoveisApp/1.0")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            return fallbackHaversine(pontos);
        }

        JsonObject body = JsonParser.parseString(response.body()).getAsJsonObject();
        if (!"Ok".equalsIgnoreCase(body.has("code") ? body.get("code").getAsString() : "")) {
            return fallbackHaversine(pontos);
        }

        JsonArray routes = body.getAsJsonArray("routes");
        if (routes == null || routes.size() == 0) {
            return fallbackHaversine(pontos);
        }

        JsonObject route = routes.get(0).getAsJsonObject();
        JsonArray legs = route.getAsJsonArray("legs");
        JsonObject geometryObject = route.getAsJsonObject("geometry");
        if (geometryObject == null) {
            return fallbackHaversine(pontos);
        }
        JsonArray geometryArray = geometryObject.getAsJsonArray("coordinates");
        if (geometryArray == null || geometryArray.size() == 0) {
            return fallbackHaversine(pontos);
        }

        List<GeoPosition> geometria = new ArrayList<>();
        for (JsonElement coordenada : geometryArray) {
            JsonArray ponto = coordenada.getAsJsonArray();
            double lon = ponto.get(0).getAsDouble();
            double lat = ponto.get(1).getAsDouble();
            geometria.add(new GeoPosition(lat, lon));
        }

        List<Double> acumuladas = new ArrayList<>();
        acumuladas.add(0.0);
        double totalKm = 0.0;

        if (legs != null) {
            for (JsonElement perna : legs) {
                JsonObject leg = perna.getAsJsonObject();
                double distanciaKm = leg.has("distance") ? leg.get("distance").getAsDouble() / 1000.0 : 0.0;
                totalKm += distanciaKm;
                acumuladas.add(totalKm);
            }
        }

        if (acumuladas.size() != pontos.size()) {
            return fallbackHaversine(pontos);
        }

        return new RotaCalculada(geometria, acumuladas, totalKm, true);
    }

    private RotaCalculada fallbackHaversine(List<GeoPosition> pontos) {
        List<GeoPosition> geometria = new ArrayList<>(pontos);
        List<Double> acumuladas = new ArrayList<>();
        acumuladas.add(0.0);

        double totalKm = 0.0;
        for (int i = 1; i < pontos.size(); i++) {
            GeoPosition anterior = pontos.get(i - 1);
            GeoPosition atual = pontos.get(i);
            if (anterior == null || atual == null) {
                acumuladas.add(totalKm);
                continue;
            }
            totalKm += GeoUtils.calcularDistancia(
                    anterior.getLatitude(),
                    anterior.getLongitude(),
                    atual.getLatitude(),
                    atual.getLongitude()
            );
            acumuladas.add(totalKm);
        }

        return new RotaCalculada(geometria, acumuladas, totalKm, false);
    }

    public static final class RotaCalculada {
        private final List<GeoPosition> geometria;
        private final List<Double> distanciasAcumuladasKm;
        private final double distanciaTotalKm;
        private final boolean rodoviaria;

        private RotaCalculada(List<GeoPosition> geometria,
                              List<Double> distanciasAcumuladasKm,
                              double distanciaTotalKm,
                              boolean rodoviaria) {
            this.geometria = Collections.unmodifiableList(new ArrayList<>(geometria));
            this.distanciasAcumuladasKm = Collections.unmodifiableList(new ArrayList<>(distanciasAcumuladasKm));
            this.distanciaTotalKm = distanciaTotalKm;
            this.rodoviaria = rodoviaria;
        }

        public List<GeoPosition> getGeometria() {
            return geometria;
        }

        public List<Double> getDistanciasAcumuladasKm() {
            return distanciasAcumuladasKm;
        }

        public double getDistanciaTotalKm() {
            return distanciaTotalKm;
        }

        public boolean isRodoviaria() {
            return rodoviaria;
        }
    }
}

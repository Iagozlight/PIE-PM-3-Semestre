package projeto.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class NominatimService {

    public double[] buscarCoordenadas(String enderecoCompleto) {
        try {
            String enderecoCodificado = URLEncoder.encode(enderecoCompleto, StandardCharsets.UTF_8);
            String url = "https://nominatim.openstreetmap.org/search?q=" + enderecoCodificado + "&format=json&limit=1";
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "DutraMoveisApp/1.0 (iagochagas03122000@gmail.com)")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();

            if (jsonArray.size() > 0) {
                JsonObject local = jsonArray.get(0).getAsJsonObject();
                double lat = local.get("lat").getAsDouble();
                double lon = local.get("lon").getAsDouble();
                return new double[]{lat, lon};
            }

        } catch (Exception e) {
            System.err.println("Erro ao buscar coordenadas: " + e.getMessage());
        }

        return null;
    }
}
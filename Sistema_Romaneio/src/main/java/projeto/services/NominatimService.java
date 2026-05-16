package projeto.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import projeto.models.Endereco;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NominatimService {

    private static final int CACHE_MAX = 256;
    private static final long CACHE_TTL_MS = 24L * 60L * 60L * 1000L;

    private final Map<String, CacheEntry> cache = new LinkedHashMap<>(CACHE_MAX, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
            return size() > CACHE_MAX;
        }
    };

    public double[] buscarCoordenadas(String enderecoCompleto) {
        return buscarCoordenadas(enderecoCompleto, null);
    }

    public double[] buscarCoordenadas(Endereco endereco, String cidadePadrao) {
        if (endereco == null) {
            return null;
        }
        String cidade = (cidadePadrao == null || cidadePadrao.isBlank()) ? "Foz do Iguacu" : cidadePadrao.trim();
        String estado = "Parana";
        String bairro = valor(endereco.getBairro());
        String rua = valor(endereco.getRua());
        String numero = valor(endereco.getNumero());
        String cep = valor(endereco.getCep());

        String[] candidatos = new String[]{
                montarUrlEstruturada(rua, numero, bairro, cep, cidade, estado),
                montarUrlEstruturada(rua, "", bairro, cep, cidade, estado),
                montarUrlEstruturada(rua, numero, "", cep, cidade, estado),
                montarUrlEstruturada(rua, numero, bairro, "", cidade, estado),
                montarUrlLivreConEndereco(endereco, cidade, estado),
                montarUrlLivre(valor(endereco.getRua()) + " " + valor(endereco.getNumero()) + " " + valor(endereco.getBairro()) + " " + cidade + " " + estado)
        };

        for (String url : candidatos) {
            try {
                double[] resultado = consultarUrl(url);
                if (resultado != null) {
                    return resultado;
                }
            } catch (Exception e) {
                System.err.println("Erro ao buscar coordenadas: " + e.getMessage());
            }
        }
        return null;
    }

    public double[] buscarCoordenadas(String enderecoCompleto, String cidadePadrao) {
        String chave = normalizar(enderecoCompleto + "|" + cidadePadrao);
        synchronized (cache) {
            CacheEntry entry = cache.get(chave);
            if (entry != null && !entry.expirado()) {
                return entry.coordenadas.clone();
            }
        }

        try {
            String[] candidatos = montarCandidatos(enderecoCompleto, cidadePadrao);
            for (String candidato : candidatos) {
                double[] coordenadas = consultarNominatim(candidato);
                if (coordenadas != null) {
                    synchronized (cache) {
                        cache.put(chave, new CacheEntry(coordenadas));
                    }
                    return coordenadas.clone();
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar coordenadas: " + e.getMessage());
        }

        return null;
    }

    private double[] consultarNominatim(String consulta) throws Exception {
        String url = montarUrlLivre(consulta);
        double[] resultado = consultarUrl(url);
        if (resultado != null) {
            return resultado;
        }
        String[] partes = separarEndereco(consulta);
        url = montarUrlEstruturada(partes[0], partes[1], partes[2], partes[3], partes[4], partes[5]);
        return consultarUrl(url);
    }

    private double[] consultarUrl(String url) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "DutraMoveisApp/1.0 (support@dutramoveis.local)")
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
        return null;
    }

    private String montarUrlLivre(String consulta) {
        String enderecoCodificado = URLEncoder.encode(consulta, StandardCharsets.UTF_8);
        return "https://nominatim.openstreetmap.org/search?q=" + enderecoCodificado
                + "&format=jsonv2&limit=1&addressdetails=1&countrycodes=br";
    }

    private String montarUrlLivreConEndereco(Endereco endereco, String cidade, String estado) {
        StringBuilder sb = new StringBuilder();
        append(sb, endereco.getRua());
        append(sb, endereco.getNumero());
        append(sb, endereco.getBairro());
        append(sb, endereco.getCep());
        append(sb, cidade);
        append(sb, estado);
        append(sb, "Brasil");
        return montarUrlLivre(sb.toString());
    }

    private void append(StringBuilder sb, String valor) {
        if (valor == null || valor.isBlank()) {
            return;
        }
        if (sb.length() > 0) {
            sb.append(' ');
        }
        sb.append(valor.trim());
    }

    private String montarUrlEstruturada(String rua, String numero, String bairro, String cep, String cidade, String estado) {
        List<String> parametros = new ArrayList<>();
        if (!rua.isBlank()) {
            String street = rua;
            if (!numero.isBlank()) {
                street += " " + numero;
            }
            if (!bairro.isBlank()) {
                street += ", " + bairro;
            }
            parametros.add("street=" + URLEncoder.encode(street, StandardCharsets.UTF_8));
        }
        if (!cep.isBlank()) {
            parametros.add("postalcode=" + URLEncoder.encode(cep, StandardCharsets.UTF_8));
        }
        if (!cidade.isBlank()) {
            parametros.add("city=" + URLEncoder.encode(cidade, StandardCharsets.UTF_8));
        }
        if (!estado.isBlank()) {
            parametros.add("state=" + URLEncoder.encode(estado, StandardCharsets.UTF_8));
        }
        parametros.add("country=" + URLEncoder.encode("Brasil", StandardCharsets.UTF_8));
        parametros.add("format=jsonv2");
        parametros.add("limit=1");
        parametros.add("addressdetails=1");
        parametros.add("countrycodes=br");
        return "https://nominatim.openstreetmap.org/search?" + String.join("&", parametros);
    }

    private String[] montarCandidatos(String enderecoCompleto, String cidadePadrao) {
        String cidade = (cidadePadrao == null || cidadePadrao.isBlank()) ? "Foz do Iguacu" : cidadePadrao.trim();
        String base = cidade + ", Parana, Brasil";
        String localidadeAlternativa = "Santa Terezinha de Itaipu, Parana, Brasil";
        return new String[]{
                enderecoCompleto + ", " + base,
                enderecoCompleto + ", " + localidadeAlternativa,
                extrairEnderecoEssencial(enderecoCompleto) + ", " + base,
                extrairEnderecoEssencial(enderecoCompleto) + ", " + localidadeAlternativa,
                cidade + ", Parana, Brasil",
                localidadeAlternativa
        };
    }

    private String valor(String texto) {
        return texto == null ? "" : texto.trim();
    }

    private String[] separarEndereco(String consulta) {
        String texto = consulta == null ? "" : consulta;
        String[] partes = new String[]{"", "", "", "", "", ""};
        String[] pedaços = texto.split(",");
        if (pedaços.length > 0) {
            partes[0] = pedaços[0].trim();
        }
        if (pedaços.length > 1) {
            partes[1] = extrairNumero(pedaços[0]);
            partes[2] = pedaços[1].trim();
        }
        if (pedaços.length > 2) {
            partes[3] = extrairCep(texto);
        }
        if (texto.toLowerCase().contains("santa terezinha")) {
            partes[4] = "Santa Terezinha de Itaipu";
        } else {
            partes[4] = "Foz do Iguacu";
        }
        partes[5] = "Parana";
        return partes;
    }

    private String extrairNumero(String texto) {
        if (texto == null) {
            return "";
        }
        String[] pedaços = texto.trim().split("\\s+");
        if (pedaços.length == 0) {
            return "";
        }
        String ultimo = pedaços[pedaços.length - 1];
        return ultimo.matches("\\d+[A-Za-z]?") ? ultimo : "";
    }

    private String extrairCep(String texto) {
        if (texto == null) {
            return "";
        }
        String[] encontrados = texto.replaceAll("\\.", "").split("\\s+");
        for (String item : encontrados) {
            if (item.matches("\\d{5}-?\\d{3}")) {
                return item;
            }
        }
        return "";
    }

    private String extrairEnderecoEssencial(String enderecoCompleto) {
        if (enderecoCompleto == null) {
            return "";
        }
        String limpo = enderecoCompleto.replaceAll("(?i)\\b\\d{5}-?\\d{3}\\b", " ");
        limpo = limpo.replaceAll("\\s+", " ").trim();
        return limpo;
    }

    private String normalizar(String texto) {
        return texto == null ? "" : texto.toLowerCase().replaceAll("\\s+", " ").trim();
    }

    private static class CacheEntry {
        private final double[] coordenadas;
        private final long armazenadoEm;

        private CacheEntry(double[] coordenadas) {
            this.coordenadas = coordenadas.clone();
            this.armazenadoEm = Instant.now().toEpochMilli();
        }

        private boolean expirado() {
            return Instant.now().toEpochMilli() - armazenadoEm > CACHE_TTL_MS;
        }
    }
}

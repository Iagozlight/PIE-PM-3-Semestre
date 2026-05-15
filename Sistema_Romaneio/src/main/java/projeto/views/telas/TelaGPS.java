package projeto.views.telas;

import projeto.models.ClientesRomaneio;
import projeto.models.Endereco;
import projeto.models.Romaneios;
import projeto.services.HaversineService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TelaGPS extends JFrame {

    private final Romaneios romaneio;
    private final HaversineService haversineService = new HaversineService();

    private JTable tabelaPontos;
    private DefaultTableModel modeloTabela;
    private MapaRotaPanel painelMapa;
    private JLabel lblDistancia;
    private JLabel lblResumo;

    private final Color corFundo = new Color(245, 240, 225);
    private final Color corMarrom = new Color(60, 42, 33);
    private final Color corBege = new Color(220, 198, 150);
    private final Color corBranco = new Color(252, 249, 241);

    public TelaGPS(Romaneios romaneio) {
        this.romaneio = romaneio;
        configurarJanela();
        iniciarComponentes();
        carregarDados();
        setVisible(true);
    }

    private void configurarJanela() {
        setTitle("DUTRA MOVEIS - GPS");
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(corFundo);
    }

    private void iniciarComponentes() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(corFundo);
        topo.setBorder(BorderFactory.createEmptyBorder(16, 18, 12, 18));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel titulo = new JLabel("GPS do Romaneio");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(corMarrom);

        lblResumo = new JLabel("Carregando romaneio...");
        lblResumo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblResumo.setForeground(corMarrom);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(lblResumo);

        lblDistancia = new JLabel("Distancia da rota: 0.00 km");
        lblDistancia.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDistancia.setForeground(corMarrom);
        lblDistancia.setHorizontalAlignment(SwingConstants.RIGHT);

        topo.add(textos, BorderLayout.WEST);
        topo.add(lblDistancia, BorderLayout.EAST);
        add(topo, BorderLayout.NORTH);

        modeloTabela = new DefaultTableModel(new Object[]{"Cliente", "Endereco", "Latitude", "Longitude"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaPontos = new JTable(modeloTabela);
        tabelaPontos.setRowHeight(26);
        tabelaPontos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabelaPontos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabelaPontos.getTableHeader().setBackground(corBege);
        tabelaPontos.getTableHeader().setForeground(corMarrom);

        JScrollPane scrollTabela = new JScrollPane(tabelaPontos);
        scrollTabela.setPreferredSize(new Dimension(410, 0));

        painelMapa = new MapaRotaPanel();
        painelMapa.setBackground(corBranco);
        painelMapa.setBorder(BorderFactory.createLineBorder(new Color(210, 190, 160)));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTabela, painelMapa);
        split.setResizeWeight(0.38);
        split.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        split.setDividerSize(8);
        add(split, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.setBackground(corFundo);
        rodape.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));

        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.setBackground(corBege);
        btnAtualizar.setForeground(corMarrom);
        btnAtualizar.addActionListener(e -> carregarDados());

        JButton btnFechar = new JButton("Fechar");
        btnFechar.setBackground(corBranco);
        btnFechar.setForeground(corMarrom);
        btnFechar.addActionListener(e -> dispose());

        rodape.add(btnAtualizar);
        rodape.add(btnFechar);
        add(rodape, BorderLayout.SOUTH);
    }

    private void carregarDados() {
        modeloTabela.setRowCount(0);

        List<PontoRota> pontos = new ArrayList<>();
        List<ClientesRomaneio> clientes = romaneio.getClientes();
        for (ClientesRomaneio cliente : clientes) {
            Endereco endereco = cliente.getEndereco();
            Double lat = endereco != null ? endereco.getLatitude() : null;
            Double lon = endereco != null ? endereco.getLongitude() : null;
            String enderecoTexto = endereco != null
                    ? endereco.getRua() + ", " + endereco.getNumero() + " - " + endereco.getBairro()
                    : "Sem endereco";

            modeloTabela.addRow(new Object[]{
                    cliente.getNome_cliente(),
                    enderecoTexto,
                    lat != null ? formatarNumero(lat) : "-",
                    lon != null ? formatarNumero(lon) : "-"
            });

            if (lat != null && lon != null) {
                pontos.add(new PontoRota(cliente.getNome_cliente(), lat, lon));
            }
        }

        double distancia = haversineService.calcularDistanciaRota(clientes);
        lblDistancia.setText("Distancia da rota: " + formatarNumero(distancia) + " km");

        String data = romaneio.getData() != null
                ? romaneio.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "-";
        String status = romaneio.getStatus() != null ? romaneio.getStatus() : "SEM ROTA";
        String veiculo = romaneio.getVeiculo() != null ? romaneio.getVeiculo().getNomeVeiculo() : "Sem veiculo";
        String motorista = romaneio.getMotorista() != null ? romaneio.getMotorista().getNome() : "Sem motorista";
        lblResumo.setText("Romaneio " + data + " | " + veiculo + " | " + motorista + " | " + status
                + " | Clientes com coordenadas: " + pontos.size());

        painelMapa.setPontos(pontos);
    }

    private String formatarNumero(double valor) {
        return String.format(java.util.Locale.US, "%.6f", valor);
    }

    private static class PontoRota {
        private final String nome;
        private final double latitude;
        private final double longitude;

        private PontoRota(String nome, double latitude, double longitude) {
            this.nome = nome;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    private class MapaRotaPanel extends JPanel {

        private List<PontoRota> pontos = new ArrayList<>();

        private MapaRotaPanel() {
            setPreferredSize(new Dimension(640, 520));
        }

        private void setPontos(List<PontoRota> pontos) {
            this.pontos = new ArrayList<>(pontos);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(238, 232, 220));
                for (int x = 0; x < getWidth(); x += 40) {
                    g2.drawLine(x, 0, x, getHeight());
                }
                for (int y = 0; y < getHeight(); y += 40) {
                    g2.drawLine(0, y, getWidth(), y);
                }

                g2.setColor(corMarrom);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                if (pontos.isEmpty()) {
                    g2.drawString("Sem coordenadas cadastradas para desenhar a rota.", 24, 32);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    g2.drawString("Cadastre o endereço completo de cada cliente para visualizar o trajeto.", 24, 54);
                    return;
                }

                double minLat = pontos.stream().mapToDouble(p -> p.latitude).min().orElse(0);
                double maxLat = pontos.stream().mapToDouble(p -> p.latitude).max().orElse(0);
                double minLon = pontos.stream().mapToDouble(p -> p.longitude).min().orElse(0);
                double maxLon = pontos.stream().mapToDouble(p -> p.longitude).max().orElse(0);

                double margem = 40;
                double largura = Math.max(1, getWidth() - 2 * margem);
                double altura = Math.max(1, getHeight() - 2 * margem);
                double latRange = Math.max(0.0001, maxLat - minLat);
                double lonRange = Math.max(0.0001, maxLon - minLon);

                List<Point> desenhados = new ArrayList<>();
                for (PontoRota ponto : pontos) {
                    double x = margem + ((ponto.longitude - minLon) / lonRange) * largura;
                    double y = margem + (1.0 - ((ponto.latitude - minLat) / latRange)) * altura;
                    desenhados.add(new Point((int) x, (int) y));
                }

                g2.setStroke(new BasicStroke(2.2f));
                g2.setColor(new Color(33, 150, 243));
                for (int i = 0; i < desenhados.size() - 1; i++) {
                    Point a = desenhados.get(i);
                    Point b = desenhados.get(i + 1);
                    g2.draw(new Line2D.Double(a, b));
                }

                for (int i = 0; i < desenhados.size(); i++) {
                    Point p = desenhados.get(i);
                    PontoRota ponto = pontos.get(i);
                    g2.setColor(i == 0 ? new Color(46, 125, 50) : new Color(211, 47, 47));
                    g2.fill(new Ellipse2D.Double(p.x - 6, p.y - 6, 12, 12));
                    g2.setColor(corMarrom);
                    g2.drawString(ponto.nome, p.x + 10, p.y - 8);
                }
            } finally {
                g2.dispose();
            }
        }
    }
}

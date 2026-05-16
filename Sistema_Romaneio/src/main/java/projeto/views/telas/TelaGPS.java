package projeto.views.telas;

import jakarta.persistence.EntityManager;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;
import projeto.config.FlyWayconfig;
import projeto.models.ClientesRomaneio;
import projeto.models.Endereco;
import projeto.models.Pedidos;
import projeto.models.Romaneios;
import projeto.repositories.ClientesRomaneioRepository;
import projeto.repositories.CustomizerFactory;
import projeto.services.HaversineService;
import projeto.services.NominatimService;
import projeto.services.RomaneiosService;
import projeto.util.GeoUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TelaGPS extends JFrame {

    private static final GeoPosition BASE_MOTORISTA =
            new GeoPosition(-25.551361, -54.572111);
    private static final GeoPosition CENTRO_OESTE_PR =
            new GeoPosition(-24.900000, -53.500000);

    private final Romaneios romaneio;
    private final ClientesRomaneioRepository clientesRomaneioRepository;
    private final RomaneiosService romaneiosService;
    private final NominatimService nominatimService = new NominatimService();
    private final HaversineService haversineService = new HaversineService();

    private final List<EntregaMarcada> entregasAtivas = new ArrayList<>();
    private final List<EntregaMarcada> entregasTodas = new ArrayList<>();

    private JXMapViewer mapa;
    private JTable tabelaEntregas;
    private DefaultTableModel modeloTabela;
    private JLabel lblResumo;
    private JLabel lblDistancia;
    private JLabel lblMapa;

    private final Color corFundo = new Color(245, 240, 225);
    private final Color corMarrom = new Color(60, 42, 33);
    private final Color corBege = new Color(220, 198, 150);
    private final Color corBranco = new Color(252, 249, 241);
    private final Color corLaranja = new Color(245, 124, 0);

    public TelaGPS(Romaneios romaneio, RomaneiosService romaneiosService) {
        this.romaneio = romaneio;
        this.romaneiosService = romaneiosService;
        EntityManager em = CustomizerFactory.getEntityManager();
        this.clientesRomaneioRepository = new ClientesRomaneioRepository(em);
        configurarJanela();
        iniciarComponentes();
        carregarDados();
        setVisible(true);
    }

    private void configurarJanela() {
        setTitle("DUTRA MOVEIS - GPS");
        setSize(1320, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(corFundo);
    }

    private void iniciarComponentes() {
        add(criarTopo(), BorderLayout.NORTH);
        add(criarConteudo(), BorderLayout.CENTER);
        add(criarRodape(), BorderLayout.SOUTH);
        configurarMapa();
    }

    private JComponent criarTopo() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(corFundo);
        topo.setBorder(BorderFactory.createEmptyBorder(14, 18, 10, 18));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("GPS do Romaneio");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(corMarrom);

        lblResumo = new JLabel("Carregando entregas...");
        lblResumo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblResumo.setForeground(corMarrom);

        lblDistancia = new JLabel("Rota: 0.00 km");
        lblDistancia.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDistancia.setForeground(corMarrom);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(3));
        textos.add(lblResumo);

        topo.add(textos, BorderLayout.WEST);
        topo.add(lblDistancia, BorderLayout.EAST);
        return topo;
    }

    private JComponent criarConteudo() {
        modeloTabela = new DefaultTableModel(new Object[]{
                "Cliente", "Endereco", "Produtos", "Status"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaEntregas = new JTable(modeloTabela);
        tabelaEntregas.setRowHeight(28);
        tabelaEntregas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabelaEntregas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabelaEntregas.getTableHeader().setBackground(corBege);
        tabelaEntregas.getTableHeader().setForeground(corMarrom);
        tabelaEntregas.setSelectionBackground(new Color(52, 152, 219));
        tabelaEntregas.setSelectionForeground(Color.WHITE);
        tabelaEntregas.setGridColor(new Color(216, 206, 184));

        JScrollPane scrollTabela = new JScrollPane(tabelaEntregas);
        scrollTabela.setPreferredSize(new Dimension(400, 0));

        mapa = new JXMapViewer();
        mapa.setBackground(corBranco);
        mapa.setBorder(BorderFactory.createLineBorder(new Color(213, 198, 171)));
        mapa.setTileFactory(new DefaultTileFactory(new OSMTileFactoryInfo(
                "OpenStreetMap",
                "https://tile.openstreetmap.org"
        )));
        mapa.setZoom(10);
        mapa.setCenterPosition(CENTRO_OESTE_PR);
        mapa.setAddressLocation(CENTRO_OESTE_PR);
        mapa.setRestrictOutsidePanning(false);
        mapa.setHorizontalWrapped(false);
        mapa.setOverlayPainter(new CompoundPainter<JXMapViewer>());
        mapa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tratarCliqueNoMapa(e);
            }
        });

        lblMapa = new JLabel("Regiao oeste do Parana");
        lblMapa.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMapa.setForeground(corMarrom);
        lblMapa.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));

        JPanel mapaContainer = new JPanel(new BorderLayout());
        mapaContainer.setBackground(corBranco);
        mapaContainer.add(mapa, BorderLayout.CENTER);
        mapaContainer.add(lblMapa, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTabela, mapaContainer);
        split.setResizeWeight(0.34);
        split.setDividerSize(8);
        split.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        return split;
    }

    private JComponent criarRodape() {
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
        return rodape;
    }

    private void configurarMapa() {
        // base fixa no oeste do Parana
        atualizarOverlay();
    }

    private void carregarDados() {
        modeloTabela.setRowCount(0);
        entregasAtivas.clear();
        entregasTodas.clear();

        List<ClientesRomaneio> clientes = romaneio.getClientes();
        int entregasPendentes = 0;
        for (ClientesRomaneio cliente : clientes) {
            EntregaMarcada entrega = montarEntrega(cliente);
            entregasTodas.add(entrega);

            String end = entrega.enderecoTexto;
            String produtos = entrega.produtosTexto;
            String status = entrega.entregue ? "ENTREGUE" : "PENDENTE";
            modeloTabela.addRow(new Object[]{cliente.getNome_cliente(), end, produtos, status});

            if (!entrega.entregue && entrega.posicao != null) {
                entregasAtivas.add(entrega);
                entregasPendentes++;
            }
        }

        double distancia = calcularDistanciaDaRota();
        lblDistancia.setText("Rota: " + formatarNumero(distancia) + " km");

        String data = romaneio.getData() != null ? romaneio.getData().toString() : "-";
        String veiculo = romaneio.getVeiculo() != null ? romaneio.getVeiculo().getNomeVeiculo() : "Sem veiculo";
        String motorista = romaneio.getMotorista() != null ? romaneio.getMotorista().getNome() : "Sem motorista";
        lblResumo.setText("Romaneio " + data + " | " + veiculo + " | " + motorista
                + " | entregas pendentes: " + entregasPendentes);

        atualizarOverlay();
        enquadrarMapaSePossivel();
    }

    private EntregaMarcada montarEntrega(ClientesRomaneio cliente) {
        Endereco endereco = cliente.getEndereco();
        String enderecoTexto = endereco != null
                ? endereco.getCep() + " - " + endereco.getRua() + ", " + endereco.getNumero() + " - " + endereco.getBairro()
                : "Sem endereco";
        String produtosTexto = listarProdutos(cliente);
        boolean entregue = Boolean.TRUE.equals(cliente.getEntregue());

        GeoPosition posicao = null;
        if (endereco != null) {
            if (endereco.getLatitude() != null && endereco.getLongitude() != null) {
                posicao = new GeoPosition(endereco.getLatitude(), endereco.getLongitude());
            } else {
                double[] coordenadas = nominatimService.buscarCoordenadas(enderecoTexto);
                if (coordenadas != null) {
                    endereco.setLatitude(coordenadas[0]);
                    endereco.setLongitude(coordenadas[1]);
                    posicao = new GeoPosition(coordenadas[0], coordenadas[1]);
                    cliente.setEndereco(endereco);
                    clientesRomaneioRepository.update(cliente);
                }
            }
        }

        return new EntregaMarcada(cliente, enderecoTexto, produtosTexto, entregue, posicao);
    }

    private String listarProdutos(ClientesRomaneio cliente) {
        if (cliente.getPedidos() == null || cliente.getPedidos().isEmpty()) {
            return "-";
        }
        StringBuilder sb = new StringBuilder();
        for (Pedidos pedido : cliente.getPedidos()) {
            if (sb.length() > 0) {
                sb.append(" | ");
            }
            sb.append(pedido.getNome_produto()).append(" x").append(pedido.getQuantidade());
        }
        return sb.toString();
    }

    private double calcularDistanciaDaRota() {
        List<GeoPosition> pontos = new ArrayList<>();
        pontos.add(BASE_MOTORISTA);
        for (EntregaMarcada entrega : entregasAtivas) {
            if (entrega.posicao != null) {
                pontos.add(entrega.posicao);
            }
        }
        if (pontos.size() < 2) {
            return 0.0;
        }

        double total = 0.0;
        for (int i = 0; i < pontos.size() - 1; i++) {
            GeoPosition a = pontos.get(i);
            GeoPosition b = pontos.get(i + 1);
            total += GeoUtils.calcularDistancia(
                    a.getLatitude(), a.getLongitude(),
                    b.getLatitude(), b.getLongitude()
            );
        }
        return total;
    }

    private void atualizarOverlay() {
        WaypointPainter<MarcadorEntrega> waypointPainter = new WaypointPainter<>();
        waypointPainter.setRenderer((g, map, waypoint) -> {
            Point ponto = new Point();
            Point local = SwingUtilities.convertPoint(
                    mapa,
                    new Point(0, 0),
                    mapa
            );
            // local is not used, but keeps the compiler quiet in older IDE settings.
            java.awt.geom.Point2D pt = map.convertGeoPositionToPoint(waypoint.getPosition());
            ponto.setLocation(pt);

            int tamanho = waypoint.origem ? 18 : 16;
            int x = (int) Math.round(ponto.x - tamanho / 2.0);
            int y = (int) Math.round(ponto.y - tamanho / 2.0);

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(waypoint.origem ? new Color(46, 125, 50) : corLaranja);
            g.fillOval(x, y, tamanho, tamanho);
            g.setColor(Color.WHITE);
            g.drawOval(x, y, tamanho, tamanho);
        });

        Set<MarcadorEntrega> waypoints = new LinkedHashSet<>();
        waypoints.add(new MarcadorEntrega("Motorista", BASE_MOTORISTA, true, null));
        for (EntregaMarcada entrega : entregasAtivas) {
            if (entrega.posicao != null) {
                waypoints.add(new MarcadorEntrega(entrega.cliente.getNome_cliente(), entrega.posicao, false, entrega));
            }
        }
        waypointPainter.setWaypoints(waypoints);

        Painter<JXMapViewer> routePainter = (g, map, width, height) -> {
            if (entregasAtivas.isEmpty()) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(33, 150, 243));
                g2.setStroke(new BasicStroke(2.5f));

                GeoPosition anterior = BASE_MOTORISTA;
                for (EntregaMarcada entrega : entregasAtivas) {
                    if (entrega.posicao == null) {
                        continue;
                    }
                    java.awt.geom.Point2D p1 = map.convertGeoPositionToPoint(anterior);
                    java.awt.geom.Point2D p2 = map.convertGeoPositionToPoint(entrega.posicao);
                    g2.draw(new Line2D.Double(p1, p2));
                    anterior = entrega.posicao;
                }
            } finally {
                g2.dispose();
            }
        };

        mapa.setOverlayPainter(new CompoundPainter<>(routePainter, waypointPainter));
        mapa.repaint();
    }

    private void enquadrarMapaSePossivel() {
        List<GeoPosition> posicoes = new ArrayList<>();
        posicoes.add(BASE_MOTORISTA);
        for (EntregaMarcada entrega : entregasAtivas) {
            if (entrega.posicao != null) {
                posicoes.add(entrega.posicao);
            }
        }

        if (posicoes.size() > 1) {
            Set<GeoPosition> conjunto = new LinkedHashSet<>(posicoes);
            mapa.zoomToBestFit(conjunto, 0.72);
        } else {
            mapa.setCenterPosition(CENTRO_OESTE_PR);
            mapa.setZoom(10);
        }
    }

    private void tratarCliqueNoMapa(MouseEvent e) {
        for (EntregaMarcada entrega : entregasAtivas) {
            if (entrega.posicao == null) {
                continue;
            }
            java.awt.geom.Point2D ponto = mapa.convertGeoPositionToPoint(entrega.posicao);
            if (ponto.distance(e.getPoint()) <= 12) {
                abrirDetalhesEntrega(entrega);
                return;
            }
        }
    }

    private void abrirDetalhesEntrega(EntregaMarcada entrega) {
        JDialog dialog = new JDialog(this, "Detalhes da entrega", true);
        dialog.setSize(520, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(corFundo);

        JPanel painel = new JPanel();
        painel.setBackground(corFundo);
        painel.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        painel.add(criarLinha("Cliente", entrega.cliente.getNome_cliente()));
        painel.add(criarLinha("Endereco", entrega.enderecoTexto));
        painel.add(criarLinha("Produtos", entrega.produtosTexto));
        painel.add(criarLinha("Coordenadas",
                entrega.posicao != null
                        ? formatarNumero(entrega.posicao.getLatitude()) + ", " + formatarNumero(entrega.posicao.getLongitude())
                        : "-"));

        JTextArea observacao = new JTextArea();
        observacao.setEditable(false);
        observacao.setLineWrap(true);
        observacao.setWrapStyleWord(true);
        observacao.setText(entrega.produtosTexto);
        observacao.setBackground(corBranco);
        observacao.setBorder(BorderFactory.createTitledBorder("Produtos em transporte"));
        painel.add(Box.createVerticalStrut(10));
        painel.add(observacao);

        dialog.add(painel, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.setBackground(corFundo);
        JButton btnEncerrar = new JButton("Encerrar entrega");
        btnEncerrar.setBackground(new Color(46, 125, 50));
        btnEncerrar.setForeground(Color.WHITE);
        btnEncerrar.addActionListener(ev -> {
            int opcao = JOptionPane.showConfirmDialog(
                    dialog,
                    "Finalizar esta entrega e remover o ponto do mapa?",
                    "Encerrar entrega",
                    JOptionPane.YES_NO_OPTION
            );
            if (opcao == JOptionPane.YES_OPTION) {
                encerrarEntrega(entrega);
                dialog.dispose();
            }
        });

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(ev -> dialog.dispose());

        botoes.add(btnFechar);
        botoes.add(btnEncerrar);
        dialog.add(botoes, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void encerrarEntrega(EntregaMarcada entrega) {
        entrega.cliente.setEntregue(true);
        clientesRomaneioRepository.update(entrega.cliente);

        boolean aindaHaPendencias = false;
        for (EntregaMarcada item : entregasTodas) {
            if (!Boolean.TRUE.equals(item.cliente.getEntregue())) {
                aindaHaPendencias = true;
                break;
            }
        }

        if (!aindaHaPendencias) {
            romaneiosService.atualizarStatus(romaneio, "ENTREGUE");
        }

        carregarDados();
        JOptionPane.showMessageDialog(this, "Entrega encerrada.");
    }

    private JPanel criarLinha(String rotulo, String valor) {
        JPanel linha = new JPanel(new BorderLayout(10, 0));
        linha.setOpaque(false);
        JLabel lblRotulo = new JLabel(rotulo + ":");
        lblRotulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblRotulo.setForeground(corMarrom);
        JLabel lblValor = new JLabel(valor);
        lblValor.setForeground(corMarrom);
        linha.add(lblRotulo, BorderLayout.WEST);
        linha.add(lblValor, BorderLayout.CENTER);
        return linha;
    }

    private String formatarNumero(double valor) {
        return String.format(Locale.US, "%.6f", valor);
    }

    private static class EntregaMarcada {
        private final ClientesRomaneio cliente;
        private final String enderecoTexto;
        private final String produtosTexto;
        private final boolean entregue;
        private final GeoPosition posicao;

        private EntregaMarcada(ClientesRomaneio cliente, String enderecoTexto, String produtosTexto,
                               boolean entregue, GeoPosition posicao) {
            this.cliente = cliente;
            this.enderecoTexto = enderecoTexto;
            this.produtosTexto = produtosTexto;
            this.entregue = entregue;
            this.posicao = posicao;
        }
    }

    private static class MarcadorEntrega extends DefaultWaypoint {
        private final String nome;
        private final boolean origem;
        private final EntregaMarcada entrega;

        private MarcadorEntrega(String nome, GeoPosition posicao, boolean origem, EntregaMarcada entrega) {
            super(posicao);
            this.nome = nome;
            this.origem = origem;
            this.entrega = entrega;
        }
    }
}

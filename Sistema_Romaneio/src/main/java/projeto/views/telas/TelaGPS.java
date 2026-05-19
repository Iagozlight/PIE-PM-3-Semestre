package projeto.views.telas;

import jakarta.persistence.EntityManager;
import projeto.Main;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;
import org.jxmapviewer.viewer.WaypointRenderer;
import projeto.models.ClientesRomaneio;
import projeto.models.Endereco;
import projeto.models.Pedidos;
import projeto.models.Romaneios;
import projeto.repositories.ClientesRomaneioRepository;
import projeto.repositories.CustomizerFactory;
import projeto.services.HaversineService;
import projeto.services.NominatimService;
import projeto.services.RomaneiosService;
import projeto.views.componentes.JanelaUtil;
import projeto.util.GeoUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class TelaGPS extends JFrame {

    private static final GeoPosition BASE_MOTORISTA = new GeoPosition(-25.551361, -54.572111);
    private static final GeoPosition CENTRO_OESTE_PR = new GeoPosition(-25.120000, -54.300000);
    private static final GeoPosition FOZ_DO_IGUACU_CENTRO = new GeoPosition(-25.516335, -54.585376);
    private static final GeoPosition SANTA_TEREZINHA_CENTRO = new GeoPosition(-25.448000, -54.399000);
    private static final int BACKUP_TIMEOUT_MS = 30000;
    private static final int ATUALIZACAO_MS = 15000;
    private static final long REINTENTO_GEOCODE_MS = 10L * 60L * 1000L;

    private final Romaneios romaneio;
    private final RomaneiosService romaneiosService;
    private final Main.SessaoUsuario sessaoUsuario;
    private final ClientesRomaneioRepository clientesRomaneioRepository;
    private final NominatimService nominatimService = new NominatimService();
    private final HaversineService haversineService = new HaversineService();

    private final List<EntregaMarcada> entregasAtivas = new ArrayList<>();
    private final List<EntregaMarcada> entregasTodas = new ArrayList<>();
    private final Deque<EntregaBackup> historicoDesfazer = new ArrayDeque<>();
    private final Map<Long, Long> ultimaTentativaGeocode = new HashMap<>();
    private final Map<Long, EntregaMarcada> entregasPorCliente = new HashMap<>();

    private JXMapViewer mapa;
    private PanMouseInputListener panListener;
    private JTable tabelaEntregas;
    private DefaultTableModel modeloTabela;
    private JTable tabelaProdutos;
    private DefaultTableModel modeloProdutos;
    private JLabel lblResumo;
    private JLabel lblDistancia;
    private JLabel lblClienteDetalhe;
    private JLabel lblTelefoneDetalhe;
    private JLabel lblEnderecoDetalhe;
    private JLabel lblStatusDetalhe;
    private JLabel lblDistanciaDetalhe;
    private JLabel lblTotalProdutos;
    private JPanel painelDesfazer;
    private JLabel lblDesfazer;
    private JButton btnDesfazer;
    private JButton btnEncerrarEntrega;
    private Timer timerDesfazer;
    private Timer timerAtualizacao;
    private boolean bloqueandoSelecaoTabela;
    private Long clienteSelecionadoId;

    private BufferedImage imagemDelivery;

    private final Color corFundo = new Color(245, 240, 225);
    private final Color corMarrom = new Color(60, 42, 33);
    private final Color corBege = new Color(220, 198, 150);
    private final Color corBranco = new Color(252, 249, 241);
    private final Color corLaranja = new Color(245, 124, 0);

    public TelaGPS(Romaneios romaneio, RomaneiosService romaneiosService) {
        this(romaneio, romaneiosService, null);
    }

    public TelaGPS(Romaneios romaneio, RomaneiosService romaneiosService, Main.SessaoUsuario sessaoUsuario) {
        this.romaneio = romaneio;
        this.romaneiosService = romaneiosService;
        this.sessaoUsuario = sessaoUsuario;
        EntityManager em = CustomizerFactory.getEntityManager();
        this.clientesRomaneioRepository = new ClientesRomaneioRepository(em);
        carregarImagemDelivery();
        configurarJanela();
        iniciarComponentes();
        carregarDados();
        iniciarAtualizacaoPeriodica();
        JanelaUtil.configurarJanela(this, new Dimension(1360, 800), new Dimension(1100, 720));
        setVisible(true);
    }

    private void carregarImagemDelivery() {
        try {
            imagemDelivery = ImageIO.read(getClass().getResource("/view/icons/delivery.png"));
        } catch (IOException | IllegalArgumentException e) {
            imagemDelivery = null;
        }
    }

    private void configurarJanela() {
        setTitle("DUTRA MOVEIS - GPS");
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

        lblDistancia = new JLabel("Distancia total: 0.00 km");
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
                "Cliente", "Endereco", "Distancia (km)", "Status"
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
        tabelaEntregas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaEntregas.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || bloqueandoSelecaoTabela) {
                return;
            }
            int linha = tabelaEntregas.getSelectedRow();
            if (linha >= 0 && linha < entregasTodas.size()) {
                EntregaMarcada entrega = entregasTodas.get(linha);
                selecionarEntrega(entrega, true);
            }
        });

        JScrollPane scrollTabela = new JScrollPane(tabelaEntregas);
        scrollTabela.setPreferredSize(new Dimension(360, 0));

        mapa = new JXMapViewer();
        mapa.setBackground(corBranco);
        mapa.setBorder(BorderFactory.createLineBorder(new Color(213, 198, 171)));
        mapa.setTileFactory(new DefaultTileFactory(new OSMTileFactoryInfo(
                "OpenStreetMap",
                "https://tile.openstreetmap.org"
        )));
        mapa.setZoom(11);
        mapa.setCenterPosition(FOZ_DO_IGUACU_CENTRO);
        mapa.setAddressLocation(FOZ_DO_IGUACU_CENTRO);
        mapa.setRestrictOutsidePanning(false);
        mapa.setHorizontalWrapped(false);
        mapa.setPanEnabled(true);
        panListener = new PanMouseInputListener(mapa);
        mapa.addMouseListener(panListener);
        mapa.addMouseMotionListener(panListener);
        mapa.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapa));
        mapa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tratarCliqueNoMapa(e);
            }
        });

        JPanel painelDetalhe = criarPainelDetalheEntrega();

        JSplitPane splitMapaDetalhe = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mapa, painelDetalhe);
        splitMapaDetalhe.setResizeWeight(0.74);
        splitMapaDetalhe.setDividerSize(8);
        splitMapaDetalhe.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JLabel lblMapa = new JLabel("Regiao oeste do Parana");
        lblMapa.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMapa.setForeground(corMarrom);
        lblMapa.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));

        JPanel mapaContainer = new JPanel(new BorderLayout());
        mapaContainer.setBackground(corBranco);
        mapa.setPreferredSize(new Dimension(820, 620));
        painelDetalhe.setPreferredSize(new Dimension(800, 240));
        mapaContainer.add(splitMapaDetalhe, BorderLayout.CENTER);
        mapaContainer.add(lblMapa, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTabela, mapaContainer);
        split.setResizeWeight(0.34);
        split.setDividerSize(8);
        split.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        split.setPreferredSize(new Dimension(1280, 760));
        return split;
    }

    private JComponent criarRodape() {
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(corFundo);
        rodape.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));

        painelDesfazer = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        painelDesfazer.setBackground(new Color(255, 244, 229));
        painelDesfazer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(245, 124, 0)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        lblDesfazer = new JLabel("Entrega finalizada.");
        lblDesfazer.setForeground(corMarrom);
        btnDesfazer = new JButton("Desfazer");
        btnDesfazer.addActionListener(e -> desfazerUltimaEntrega());
        painelDesfazer.add(lblDesfazer);
        painelDesfazer.add(btnDesfazer);
        painelDesfazer.setVisible(false);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.setBackground(corFundo);
        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.setBackground(corBege);
        btnAtualizar.setForeground(corMarrom);
        btnAtualizar.addActionListener(e -> carregarDados());

        JButton btnFechar = new JButton("Fechar");
        btnFechar.setBackground(corBranco);
        btnFechar.setForeground(corMarrom);
        btnFechar.addActionListener(e -> dispose());

        botoes.add(btnAtualizar);
        botoes.add(btnFechar);

        rodape.add(painelDesfazer, BorderLayout.WEST);
        rodape.add(botoes, BorderLayout.EAST);
        return rodape;
    }

    private JPanel criarPainelDetalheEntrega() {
        JPanel painel = new JPanel(new BorderLayout(10, 8));
        painel.setBackground(corBranco);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(213, 198, 171)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JPanel resumo = new JPanel();
        resumo.setOpaque(false);
        resumo.setLayout(new BoxLayout(resumo, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Entrega selecionada");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(corMarrom);

        lblClienteDetalhe = criarLabelDetalhe("Cliente: -");
        lblTelefoneDetalhe = criarLabelDetalhe("Telefone: -");
        lblEnderecoDetalhe = criarLabelDetalhe("Endereco: -");
        lblStatusDetalhe = criarLabelDetalhe("Status: -");
        lblDistanciaDetalhe = criarLabelDetalhe("Distancia: -");
        lblTotalProdutos = criarLabelDetalhe("Total de produtos: 0");

        resumo.add(titulo);
        resumo.add(Box.createVerticalStrut(6));
        resumo.add(lblClienteDetalhe);
        resumo.add(lblTelefoneDetalhe);
        resumo.add(lblEnderecoDetalhe);
        resumo.add(lblStatusDetalhe);
        resumo.add(lblDistanciaDetalhe);
        resumo.add(lblTotalProdutos);

        modeloProdutos = new DefaultTableModel(new Object[]{"Produto", "Quantidade"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaProdutos = new JTable(modeloProdutos);
        tabelaProdutos.setRowHeight(26);
        tabelaProdutos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabelaProdutos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabelaProdutos.getTableHeader().setBackground(corBege);
        tabelaProdutos.getTableHeader().setForeground(corMarrom);
        tabelaProdutos.setGridColor(new Color(216, 206, 184));

        JScrollPane scrollProdutos = new JScrollPane(tabelaProdutos);
        scrollProdutos.setBorder(BorderFactory.createTitledBorder("Produtos"));

        btnEncerrarEntrega = new JButton("Encerrar entrega");
        btnEncerrarEntrega.setBackground(new Color(46, 125, 50));
        btnEncerrarEntrega.setForeground(Color.WHITE);
        btnEncerrarEntrega.addActionListener(e -> encerrarEntregaSelecionada());

        JPanel rodapeDetalhe = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rodapeDetalhe.setOpaque(false);
        rodapeDetalhe.add(btnEncerrarEntrega);

        painel.add(resumo, BorderLayout.NORTH);
        painel.add(scrollProdutos, BorderLayout.CENTER);
        painel.add(rodapeDetalhe, BorderLayout.SOUTH);

        atualizarPainelDetalhe(null);
        return painel;
    }

    private JLabel criarLabelDetalhe(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(corMarrom);
        return label;
    }

    private void configurarMapa() {
        mapa.setCenterPosition(FOZ_DO_IGUACU_CENTRO);
        mapa.setZoom(11);
        atualizarOverlay();
    }

    private void carregarDados() {
        carregarDados(true);
    }

    private void carregarDados(boolean enquadrarMapa) {
        modeloTabela.setRowCount(0);
        entregasAtivas.clear();
        entregasTodas.clear();
        entregasPorCliente.clear();

        List<ClientesRomaneio> clientes = romaneio.getClientes();
        List<EntregaMarcada> entregasOrdenadas = new ArrayList<>();
        for (ClientesRomaneio cliente : clientes) {
            entregasOrdenadas.add(montarEntrega(cliente));
        }

        entregasOrdenadas.sort(Comparator
                .comparing((EntregaMarcada e) -> prioridadeCidade(e.cliente)).thenComparing(e -> e.cliente.getNome_cliente(), String.CASE_INSENSITIVE_ORDER)
                .thenComparingDouble(e -> e.distanciaDoCaminhaoKm));

        int entregasPendentes = 0;
        for (EntregaMarcada entrega : entregasOrdenadas) {
            entregasTodas.add(entrega);
            if (entrega.cliente.getId() != null) {
                entregasPorCliente.put(entrega.cliente.getId(), entrega);
            }

            String status = entrega.entregue ? "ENTREGUE" : "PENDENTE";
            modeloTabela.addRow(new Object[]{
                    entrega.cliente.getNome_cliente(),
                    entrega.enderecoTexto,
                    entrega.posicao != null ? formatarNumero(entrega.distanciaDoCaminhaoKm) + " km" : "-",
                    status
            });

            if (!entrega.entregue && entrega.posicao != null) {
                entregasAtivas.add(entrega);
                entregasPendentes++;
            }
        }

        double distancia = calcularDistanciaDaRota();
        lblDistancia.setText("Distancia total aproximada: " + formatarNumero(distancia) + " km");

        String data = romaneio.getData() != null ? romaneio.getData().toString() : "-";
        String veiculo = romaneio.getVeiculo() != null ? romaneio.getVeiculo().getNomeVeiculo() : "Sem veiculo";
        String motorista = romaneio.getMotorista() != null ? romaneio.getMotorista().getNome() : "Sem motorista";
        lblResumo.setText("Romaneio " + data + " | " + veiculo + " | " + motorista
                + " | entregas pendentes: " + entregasPendentes);

        atualizarOverlay();
        if (enquadrarMapa) {
            enquadrarMapaSePossivel();
        }

        if (clienteSelecionadoId != null) {
            selecionarEntregaPorClienteId(clienteSelecionadoId, false);
        } else if (!entregasTodas.isEmpty()) {
            selecionarEntrega(entregasTodas.get(0), false);
        } else {
            atualizarPainelDetalhe(null);
        }
    }

    private EntregaMarcada montarEntrega(ClientesRomaneio cliente) {
        Endereco endereco = cliente.getEndereco();
        String enderecoTexto = montarEnderecoTexto(endereco);
        String produtosTexto = listarProdutos(cliente);
        boolean entregue = Boolean.TRUE.equals(cliente.getEntregue());

        GeoPosition posicao = null;
        if (endereco != null) {
            if (endereco.getLatitude() != null && endereco.getLongitude() != null) {
                posicao = new GeoPosition(endereco.getLatitude(), endereco.getLongitude());
            } else {
                if (podeTentarGeocode(cliente)) {
                    double[] coordenadas = nominatimService.buscarCoordenadas(
                            endereco,
                            prioridadeCidade(cliente)
                    );
                    if (coordenadas != null) {
                        endereco.setLatitude(coordenadas[0]);
                        endereco.setLongitude(coordenadas[1]);
                        posicao = new GeoPosition(coordenadas[0], coordenadas[1]);
                        cliente.setEndereco(endereco);
                        clientesRomaneioRepository.update(cliente);
                        ultimaTentativaGeocode.remove(cliente.getId());
                    } else {
                        registrarTentativaGeocode(cliente);
                        posicao = fallbackRegional(cliente, enderecoTexto);
                    }
                } else {
                    posicao = fallbackRegional(cliente, enderecoTexto);
                }
            }
        } else {
            posicao = FOZ_DO_IGUACU_CENTRO;
        }

        double distancia = posicao != null
                ? GeoUtils.calcularDistancia(BASE_MOTORISTA.getLatitude(), BASE_MOTORISTA.getLongitude(),
                posicao.getLatitude(), posicao.getLongitude())
                : 0.0;

        return new EntregaMarcada(cliente, enderecoTexto, produtosTexto, entregue, posicao, distancia);
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

    private Map<String, Integer> agruparProdutos(ClientesRomaneio cliente) {
        Map<String, Integer> agrupados = new java.util.LinkedHashMap<>();
        if (cliente == null || cliente.getPedidos() == null) {
            return agrupados;
        }
        for (Pedidos pedido : cliente.getPedidos()) {
            String nome = pedido.getNome_produto() != null ? pedido.getNome_produto().trim() : "Produto";
            int quantidade = parseQuantidade(pedido.getQuantidade());
            agrupados.merge(nome, quantidade, Integer::sum);
        }
        return agrupados;
    }

    private int parseQuantidade(String quantidade) {
        if (quantidade == null || quantidade.isBlank()) {
            return 0;
        }
        try {
            return Math.max(0, Integer.parseInt(quantidade.trim()));
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private String montarEnderecoTexto(Endereco endereco) {
        if (endereco == null) {
            return "Sem endereco";
        }

        StringBuilder sb = new StringBuilder();
        appendParte(sb, endereco.getCep());
        appendParte(sb, endereco.getRua());
        appendParte(sb, endereco.getNumero());
        appendParte(sb, endereco.getBairro());
        appendParte(sb, endereco.getCidade());
        appendParte(sb, "Parana");
        appendParte(sb, "Brasil");
        return sb.length() > 0 ? sb.toString() : "Sem endereco";
    }

    private void appendParte(StringBuilder sb, String valor) {
        if (valor == null || valor.isBlank()) {
            return;
        }
        if (sb.length() > 0) {
            sb.append(", ");
        }
        sb.append(valor.trim());
    }

    private GeoPosition fallbackRegional(ClientesRomaneio cliente, String enderecoTexto) {
        String normalizado = (prioridadeCidade(cliente) + " " + (enderecoTexto == null ? "" : enderecoTexto))
                .toLowerCase(Locale.ROOT);
        if (normalizado.contains("santa terezinha")) {
            return SANTA_TEREZINHA_CENTRO;
        }
        if (normalizado.contains("medianeira")) {
            return CENTRO_OESTE_PR;
        }
        return FOZ_DO_IGUACU_CENTRO;
    }

    private String prioridadeCidade(ClientesRomaneio cliente) {
        if (cliente == null) {
            return "Foz do Iguacu";
        }
        List<String> cidades = cliente.getListaCidadesAtendidas();
        if (!cidades.isEmpty()) {
            return cidades.get(0);
        }
        Endereco endereco = cliente.getEndereco();
        if (endereco != null && endereco.getCidade() != null && !endereco.getCidade().isBlank()) {
            return endereco.getCidade();
        }
        return "Foz do Iguacu";
    }

    private boolean podeTentarGeocode(ClientesRomaneio cliente) {
        if (cliente.getId() == null) {
            return false;
        }
        Long ultimaTentativa = ultimaTentativaGeocode.get(cliente.getId());
        if (ultimaTentativa == null) {
            return true;
        }
        return System.currentTimeMillis() - ultimaTentativa >= REINTENTO_GEOCODE_MS;
    }

    private void registrarTentativaGeocode(ClientesRomaneio cliente) {
        if (cliente.getId() != null) {
            ultimaTentativaGeocode.put(cliente.getId(), System.currentTimeMillis());
        }
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
            total += haversineService.calcularDistancia(a.getLatitude(), a.getLongitude(), b.getLatitude(), b.getLongitude());
        }
        return total;
    }

    private void atualizarOverlay() {
        WaypointPainter<MarcadorEntrega> waypointPainter = new WaypointPainter<>();
        waypointPainter.setRenderer(new MarcadorRenderer());

        Set<MarcadorEntrega> waypoints = new LinkedHashSet<>();
        waypoints.add(new MarcadorEntrega("Motorista", BASE_MOTORISTA, true, null, 0.0));
        for (EntregaMarcada entrega : entregasAtivas) {
            if (entrega.posicao != null) {
                waypoints.add(new MarcadorEntrega(entrega.cliente.getNome_cliente(), entrega.posicao, false, entrega, entrega.distanciaDoCaminhaoKm));
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
            mapa.setCenterPosition(FOZ_DO_IGUACU_CENTRO);
            mapa.setZoom(11);
        }
    }

    private void tratarCliqueNoMapa(MouseEvent e) {
        for (MarcadorEntrega marcador : getMarcadores()) {
            java.awt.geom.Point2D ponto = mapa.convertGeoPositionToPoint(marcador.getPosition());
            if (ponto.distance(e.getPoint()) <= 12) {
                if (marcador.entrega != null) {
                    selecionarEntrega(marcador.entrega, true);
                }
                return;
            }
        }
    }

    private List<MarcadorEntrega> getMarcadores() {
        List<MarcadorEntrega> marcadores = new ArrayList<>();
        marcadores.add(new MarcadorEntrega("Motorista", BASE_MOTORISTA, true, null, 0.0));
        for (EntregaMarcada entrega : entregasAtivas) {
            if (entrega.posicao != null) {
                marcadores.add(new MarcadorEntrega(entrega.cliente.getNome_cliente(), entrega.posicao, false, entrega, entrega.distanciaDoCaminhaoKm));
            }
        }
        return marcadores;
    }

    private void selecionarEntregaPorClienteId(Long clienteId, boolean centralizarMapa) {
        if (clienteId == null) {
            return;
        }
        EntregaMarcada entrega = entregasPorCliente.get(clienteId);
        if (entrega != null) {
            selecionarEntrega(entrega, centralizarMapa);
        }
    }

    private void selecionarEntrega(EntregaMarcada entrega, boolean centralizarMapa) {
        if (entrega == null) {
            atualizarPainelDetalhe(null);
            return;
        }

        clienteSelecionadoId = entrega.cliente.getId();
        atualizarPainelDetalhe(entrega);

        if (centralizarMapa && entrega.posicao != null) {
            mapa.setCenterPosition(entrega.posicao);
            mapa.setZoom(Math.max(mapa.getZoom(), 14));
        }

        int indice = entregasTodas.indexOf(entrega);
        if (indice >= 0 && indice < tabelaEntregas.getRowCount()) {
            bloqueandoSelecaoTabela = true;
            try {
                tabelaEntregas.getSelectionModel().setSelectionInterval(indice, indice);
                tabelaEntregas.scrollRectToVisible(tabelaEntregas.getCellRect(indice, 0, true));
            } finally {
                bloqueandoSelecaoTabela = false;
            }
        }
    }

    private void atualizarPainelDetalhe(EntregaMarcada entrega) {
        if (lblClienteDetalhe == null) {
            return;
        }
        if (entrega == null) {
            lblClienteDetalhe.setText("Cliente: -");
            lblTelefoneDetalhe.setText("Telefone: -");
            lblEnderecoDetalhe.setText("Endereco: -");
            lblStatusDetalhe.setText("Status: -");
            lblDistanciaDetalhe.setText("Distancia: -");
            lblTotalProdutos.setText("Total de produtos: 0");
            if (modeloProdutos != null) {
                modeloProdutos.setRowCount(0);
            }
            if (btnEncerrarEntrega != null) {
                btnEncerrarEntrega.setEnabled(false);
            }
            return;
        }

        lblClienteDetalhe.setText("Cliente: " + entrega.cliente.getNome_cliente());
        lblTelefoneDetalhe.setText("Telefone: " + formatarTelefone(entrega.cliente.getTelefone()));
        lblEnderecoDetalhe.setText("Endereco: " + entrega.enderecoTexto);
        lblStatusDetalhe.setText("Status: " + (entrega.entregue ? "ENTREGUE" : "PENDENTE"));
        lblDistanciaDetalhe.setText("Distancia: " + formatarNumero(entrega.distanciaDoCaminhaoKm) + " km");

        if (modeloProdutos != null) {
            modeloProdutos.setRowCount(0);
            Map<String, Integer> agrupados = agruparProdutos(entrega.cliente);
            int total = 0;
            for (Map.Entry<String, Integer> entry : agrupados.entrySet()) {
                modeloProdutos.addRow(new Object[]{entry.getKey(), entry.getValue()});
                total += entry.getValue();
            }
            lblTotalProdutos.setText("Total de produtos: " + total);
        }

        if (btnEncerrarEntrega != null) {
            btnEncerrarEntrega.setEnabled(podeEncerrarEntrega(entrega) && !entrega.entregue);
            btnEncerrarEntrega.setText(podeEncerrarEntrega(entrega) ? "Encerrar entrega" : "Sem permissão");
        }
    }

    private boolean podeEncerrarEntrega(EntregaMarcada entrega) {
        if (entrega == null || entrega.cliente == null) {
            return false;
        }
        if (sessaoUsuario == null) {
            return true;
        }
        if (sessaoUsuario.isAdmin()) {
            return true;
        }
        if (sessaoUsuario.getMotorista() == null || romaneio.getMotorista() == null) {
            return false;
        }
        Long motoristaSessaoId = sessaoUsuario.getMotorista().getId();
        Long motoristaRomaneioId = romaneio.getMotorista().getId();
        return motoristaSessaoId != null && motoristaSessaoId.equals(motoristaRomaneioId);
    }

    private void encerrarEntregaSelecionada() {
        if (clienteSelecionadoId == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma entrega primeiro.");
            return;
        }
        EntregaMarcada entrega = entregasPorCliente.get(clienteSelecionadoId);
        if (entrega == null) {
            JOptionPane.showMessageDialog(this, "Entrega nao encontrada.");
            return;
        }
        if (!podeEncerrarEntrega(entrega)) {
            JOptionPane.showMessageDialog(this, "Voce nao tem permissao para concluir esta entrega.");
            return;
        }
        int opcao = JOptionPane.showConfirmDialog(
                this,
                "Finalizar a entrega de " + entrega.cliente.getNome_cliente() + "?",
                "Encerrar entrega",
                JOptionPane.YES_NO_OPTION
        );
        if (opcao == JOptionPane.YES_OPTION) {
            encerrarEntrega(entrega);
        }
    }

    private void iniciarAtualizacaoPeriodica() {
        if (timerAtualizacao != null) {
            timerAtualizacao.stop();
        }
        timerAtualizacao = new Timer(ATUALIZACAO_MS, e -> carregarDados(false));
        timerAtualizacao.setRepeats(true);
        timerAtualizacao.start();
    }

    private void abrirDetalhesEntrega(EntregaMarcada entrega) {
        JDialog dialog = new JDialog(this, "Detalhes da entrega", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(corFundo);

        JPanel painel = new JPanel();
        painel.setBackground(corFundo);
        painel.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        painel.add(criarLinha("Cliente", entrega.cliente.getNome_cliente()));
        painel.add(criarLinha("Endereco", entrega.enderecoTexto));
        painel.add(criarLinha("Produtos", entrega.produtosTexto));
        painel.add(criarLinha("Distancia do caminhao", formatarNumero(entrega.distanciaDoCaminhaoKm) + " km"));
        painel.add(criarLinha("Coordenadas", entrega.posicao != null
                ? formatarNumero(entrega.posicao.getLatitude()) + ", " + formatarNumero(entrega.posicao.getLongitude())
                : "-"));

        JTextArea observacao = new JTextArea(entrega.produtosTexto);
        observacao.setEditable(false);
        observacao.setLineWrap(true);
        observacao.setWrapStyleWord(true);
        observacao.setBackground(corBranco);
        observacao.setBorder(BorderFactory.createTitledBorder("Produtos em transporte"));
        painel.add(Box.createVerticalStrut(10));
        painel.add(observacao);

        dialog.add(painel, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.setBackground(corFundo);

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(ev -> dialog.dispose());

        JButton btnEncerrar = new JButton("Encerrar entrega");
        btnEncerrar.setBackground(new Color(46, 125, 50));
        btnEncerrar.setForeground(Color.WHITE);
        btnEncerrar.addActionListener(ev -> {
            int opcao = JOptionPane.showConfirmDialog(
                    dialog,
                    "Finalizar esta entrega?",
                    "Encerrar entrega",
                    JOptionPane.YES_NO_OPTION
            );
            if (opcao == JOptionPane.YES_OPTION) {
                encerrarEntrega(entrega);
                dialog.dispose();
            }
        });

        botoes.add(btnFechar);
        botoes.add(btnEncerrar);
        dialog.add(botoes, BorderLayout.SOUTH);
        dialog.setResizable(true);
        JanelaUtil.configurarDialog(dialog, this, new Dimension(620, 480), new Dimension(520, 380));
        dialog.setVisible(true);
    }

    private void encerrarEntrega(EntregaMarcada entrega) {
        boolean entregasPendentesAntes = entregasAtivas.stream().anyMatch(item -> item.cliente.getId().equals(entrega.cliente.getId()));
        historicoDesfazer.clear();
        historicoDesfazer.push(new EntregaBackup(entrega.cliente.getId(), Boolean.TRUE.equals(entrega.cliente.getEntregue()), romaneio.getStatus()));

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
        mostrarBarraDesfazer(entrega.cliente.getNome_cliente());
    }

    private void mostrarBarraDesfazer(String nomeCliente) {
        if (timerDesfazer != null) {
            timerDesfazer.stop();
        }
        lblDesfazer.setText("Entrega de " + nomeCliente + " finalizada.");
        painelDesfazer.setVisible(true);
        painelDesfazer.revalidate();
        painelDesfazer.repaint();

        timerDesfazer = new Timer(BACKUP_TIMEOUT_MS, e -> limparBackup());
        timerDesfazer.setRepeats(false);
        timerDesfazer.start();
    }

    private void desfazerUltimaEntrega() {
        if (historicoDesfazer.isEmpty()) {
            limparBackup();
            return;
        }
        EntregaBackup backup = historicoDesfazer.pop();
        ClientesRomaneio cliente = clientesRomaneioRepository.findById(backup.clienteId);
        if (cliente != null) {
            cliente.setEntregue(backup.entregueAnterior);
            clientesRomaneioRepository.update(cliente);
        }
        romaneiosService.atualizarStatus(romaneio, backup.statusRomaneioAnterior != null ? backup.statusRomaneioAnterior : "EM ROTA");
        limparBackup();
        carregarDados();
        JOptionPane.showMessageDialog(this, "Entrega restaurada.");
    }

    private void limparBackup() {
        historicoDesfazer.clear();
        if (timerDesfazer != null) {
            timerDesfazer.stop();
            timerDesfazer = null;
        }
        painelDesfazer.setVisible(false);
        painelDesfazer.revalidate();
        painelDesfazer.repaint();
    }

    @Override
    public void dispose() {
        if (timerAtualizacao != null) {
            timerAtualizacao.stop();
            timerAtualizacao = null;
        }
        if (timerDesfazer != null) {
            timerDesfazer.stop();
            timerDesfazer = null;
        }
        super.dispose();
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

    private String formatarTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return "-";
        }
        String n = telefone.replaceAll("\\D", "");
        if (n.length() == 11) {
            return n.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        }
        if (n.length() == 10) {
            return n.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        }
        return telefone;
    }

    private static class EntregaMarcada {
        private final ClientesRomaneio cliente;
        private final String enderecoTexto;
        private final String produtosTexto;
        private final boolean entregue;
        private final GeoPosition posicao;
        private final double distanciaDoCaminhaoKm;

        private EntregaMarcada(ClientesRomaneio cliente, String enderecoTexto, String produtosTexto,
                               boolean entregue, GeoPosition posicao, double distanciaDoCaminhaoKm) {
            this.cliente = cliente;
            this.enderecoTexto = enderecoTexto;
            this.produtosTexto = produtosTexto;
            this.entregue = entregue;
            this.posicao = posicao;
            this.distanciaDoCaminhaoKm = distanciaDoCaminhaoKm;
        }
    }

    private static class EntregaBackup {
        private final Long clienteId;
        private final boolean entregueAnterior;
        private final String statusRomaneioAnterior;

        private EntregaBackup(Long clienteId, boolean entregueAnterior, String statusRomaneioAnterior) {
            this.clienteId = clienteId;
            this.entregueAnterior = entregueAnterior;
            this.statusRomaneioAnterior = statusRomaneioAnterior;
        }
    }

    private class MarcadorRenderer implements WaypointRenderer<MarcadorEntrega> {
        @Override
        public void paintWaypoint(Graphics2D g, JXMapViewer map, MarcadorEntrega waypoint) {
            int tamanho = waypoint.origem ? 42 : 18;
            int x = -tamanho / 2;
            int y = -tamanho / 2;

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (waypoint.origem && imagemDelivery != null) {
                g.drawImage(imagemDelivery, x, y, tamanho, tamanho, null);
            } else if (waypoint.origem) {
                g.setColor(new Color(46, 125, 50));
                g.fillOval(x, y, tamanho, tamanho);
                g.setColor(Color.WHITE);
                g.drawOval(x, y, tamanho, tamanho);
            } else {
                g.setColor(corLaranja);
                g.fillOval(x, y, tamanho, tamanho);
                g.setColor(Color.WHITE);
                g.drawOval(x, y, tamanho, tamanho);
            }
        }
    }

    private static class MarcadorEntrega extends DefaultWaypoint {
        private final String nome;
        private final boolean origem;
        private final EntregaMarcada entrega;

        private MarcadorEntrega(String nome, GeoPosition posicao, boolean origem, EntregaMarcada entrega, double distancia) {
            super(posicao);
            this.nome = nome;
            this.origem = origem;
            this.entrega = entrega;
        }
    }
}

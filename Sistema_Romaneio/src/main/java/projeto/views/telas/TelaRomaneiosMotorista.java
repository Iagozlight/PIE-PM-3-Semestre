package projeto.views.telas;

import jakarta.persistence.EntityManager;
import projeto.models.Motoristas;
import projeto.models.Romaneios;
import projeto.repositories.*;
import projeto.services.RomaneiosService;
import projeto.views.componentes.DialogDetalhesRomaneio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaRomaneiosMotorista extends JFrame {

    private JTable tabelaRomaneios;
    private DefaultTableModel modeloTabela;
    private RomaneiosService romaneiosService;
    private Motoristas motorista;

    private final Color corFundo = new Color(245, 240, 225);
    private final Color corMarrom = new Color(60, 42, 33);
    private final Color corBege = new Color(220, 198, 150);
    private final Color corBranco = new Color(252, 249, 241);

    public TelaRomaneiosMotorista() {
        configurarJanela();
        iniciarComponentes();
        setVisible(true);
    }

    public TelaRomaneiosMotorista(RomaneiosService romaneiosService, Motoristas motorista) {
        this.romaneiosService = romaneiosService;
        this.motorista = motorista;
        configurarJanela();
        iniciarComponentes();
        carregarRomaneios();
        setVisible(true);
    }

    private void configurarJanela() {
        setTitle("DUTRA MÓVEIS - Motorista");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void iniciarComponentes() {
        // ===== TOPO =====
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBackground(corFundo);
        painelTopo.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        String nomeMotorista = motorista != null ? motorista.getNome() : "Motorista";
        JLabel lblSaudacao = new JLabel("Olá, " + nomeMotorista + "!");
        lblSaudacao.setFont(new Font("Arial", Font.BOLD, 20));
        lblSaudacao.setForeground(corMarrom);

        JLabel lblSubtitulo = new JLabel("Seus Romaneios");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSubtitulo.setForeground(corMarrom);

        JPanel painelTextos = new JPanel();
        painelTextos.setLayout(new BoxLayout(painelTextos, BoxLayout.Y_AXIS));
        painelTextos.setBackground(corFundo);
        painelTextos.add(lblSaudacao);
        painelTextos.add(lblSubtitulo);

        painelTopo.add(painelTextos, BorderLayout.WEST);
        add(painelTopo, BorderLayout.NORTH);

        // ===== TABELA =====
        String[] colunas = {"ID", "Data", "Veículo", "Status"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabelaRomaneios = new JTable(modeloTabela);
        tabelaRomaneios.setRowHeight(30);
        tabelaRomaneios.setFont(new Font("Arial", Font.PLAIN, 14));
        tabelaRomaneios.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tabelaRomaneios.getTableHeader().setBackground(new Color(239, 218, 186));
        tabelaRomaneios.getTableHeader().setForeground(corMarrom);
        tabelaRomaneios.setSelectionBackground(new Color(52, 152, 219));
        tabelaRomaneios.setSelectionForeground(Color.WHITE);
        tabelaRomaneios.setGridColor(new Color(200, 200, 200));
        tabelaRomaneios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tabelaRomaneios);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // ===== RODAPÉ =====
        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelRodape.setBackground(corFundo);
        painelRodape.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));

        JButton btnVerDetalhes = new JButton("Ver Detalhes");
        btnVerDetalhes.setBackground(corBege);
        btnVerDetalhes.setForeground(corMarrom);
        btnVerDetalhes.setFont(new Font("Arial", Font.BOLD, 13));
        btnVerDetalhes.addActionListener(e -> verDetalhes());

        painelRodape.add(btnVerDetalhes);
        add(painelRodape, BorderLayout.SOUTH);
    }

    private void carregarRomaneios() {
        modeloTabela.setRowCount(0);
        List<Romaneios> romaneios = romaneiosService.listarRomaneiosPorMotorista(motorista);
        for (Romaneios r : romaneios) {
            modeloTabela.addRow(new Object[]{
                    r.getId(),
                    r.getData(),
                    r.getVeiculo() != null ? r.getVeiculo().getNomeVeiculo() : "Sem veículo",
                    r.getStatus()
            });
        }
    }

    private void verDetalhes() {
        int linha = tabelaRomaneios.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um romaneio!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = (Long) modeloTabela.getValueAt(linha, 0);
        Romaneios romaneio = romaneiosService.buscarPorId(id);
        new DialogDetalhesRomaneio(this, romaneio, romaneiosService, this::carregarRomaneios);
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception e) {}
        new TelaRomaneiosMotorista();
    }
}
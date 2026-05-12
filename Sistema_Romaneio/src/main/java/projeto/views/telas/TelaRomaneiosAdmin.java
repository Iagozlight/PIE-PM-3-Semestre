package projeto.views.telas;

import projeto.models.Romaneios;
import projeto.services.RomaneiosService;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

public class TelaRomaneiosAdmin extends JFrame {

    private JTable tabela;
    private javax.swing.table.DefaultTableModel modeloTabela;

    public TelaRomaneiosAdmin() {
        setTitle("DUTRA MÓVEIS - Romaneios");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        iniciarComponentes();
        setVisible(true);
    }

    private void iniciarComponentes() {
        setLayout(new BorderLayout());

        Color corFundoCreme = new Color(245, 240, 225); // Fundo geral
        Color corMarromTexto = new Color(60, 42, 33);  // Texto e títulos
        Color corBegeBotoes = new Color(220, 198, 150); // Destaques e botões
        Color corBrancoPapel = new Color(252, 249, 241); // Fundo de campos/tabela

        // ===== PAINEL TOPO =====
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBackground(corFundoCreme);
        painelTopo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titulo = new JLabel("DUTRA MÓVEIS - Romaneios");
        titulo.setForeground(corMarromTexto);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.setBackground(corFundoCreme);

        JButton btnNovoCliente = new JButton("+ Novo Cliente");
        btnNovoCliente.setBackground(corBegeBotoes);
        btnNovoCliente.setForeground(corMarromTexto);

        JButton btnNovoRomaneio = new JButton("+ Novo Romaneio");
        btnNovoRomaneio.setBackground(corBegeBotoes);
        btnNovoRomaneio.setForeground(corMarromTexto);

        painelBotoes.add(btnNovoCliente);
        painelBotoes.add(btnNovoRomaneio);

        painelTopo.add(titulo, BorderLayout.WEST);
        painelTopo.add(painelBotoes, BorderLayout.EAST);

        add(painelTopo, BorderLayout.NORTH);

        // ===== TABELA DE ROMANEIOS =====
        String[] colunas = {"ID", "Data", "Veículo", "Motorista"};
        modeloTabela = new javax.swing.table.DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int row, int column) {
                return false; // impede edição direta na tabela
            }
        };

        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(30);
        tabela.setFont(new Font("Arial", Font.PLAIN, 14));
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tabela.getTableHeader().setBackground(new Color(239, 218, 186, 255));
        tabela.getTableHeader().setForeground(corMarromTexto);
        tabela.setSelectionBackground(new Color(52, 152, 219));
        tabela.setSelectionForeground(Color.WHITE);
        tabela.setGridColor(new Color(200, 200, 200));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.setBackground(corFundoCreme);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        add(scrollPane, BorderLayout.CENTER);

        // ===== PAINEL RODAPÉ =====
        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelRodape.setBackground(corFundoCreme);
        painelRodape.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JButton btnAtribuirVeiculo = new JButton("Atribuir Veículo");
        btnAtribuirVeiculo.setBackground(corBrancoPapel);
        btnAtribuirVeiculo.setForeground(corMarromTexto);

        JButton btnAtribuirMotorista = new JButton("Atribuir Motorista");
        btnAtribuirMotorista.setBackground(corBrancoPapel);
        btnAtribuirMotorista.setForeground(corMarromTexto);

        JButton btnDeletar = new JButton("Deletar");
        btnDeletar.setBackground(new Color(211, 47, 47));
        btnDeletar.setForeground(Color.WHITE);

        painelRodape.add(btnAtribuirVeiculo);
        painelRodape.add(btnAtribuirMotorista);
        painelRodape.add(btnDeletar);

        add(painelRodape, BorderLayout.SOUTH);
    }

    private void carregarRomaneios(RomaneiosService romaneiosService) {
        modeloTabela.setRowCount(0); // limpa a tabela
        List<Romaneios> romaneios = romaneiosService.listarRomaneios();
        for (Romaneios r : romaneios) {
            modeloTabela.addRow(new Object[]{
                    r.getId(),
                    r.getData(),
                    r.getVeiculo() != null ? r.getVeiculo().getNomeVeiculo() : "Sem veículo",
                    r.getMotorista() != null ? r.getMotorista().getNome() : "Sem motorista"
            });
        }
    }

    public static void main(String[] args) {
        // Look and Feel serve para garantir que as cores manuais prevaleçam, sem causar erros
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception e) {}
        new TelaRomaneiosAdmin();
    }
}
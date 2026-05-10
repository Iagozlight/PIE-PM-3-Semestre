package projeto.views.telas;

import javax.swing.*;
import java.awt.*;

public class TelaRomaneiosAdmin extends JFrame {

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
        Object[][] dados = {};

        JTable tabela = new JTable(dados, colunas);
        tabela.setRowHeight(30);
        tabela.setFont(new Font("Arial", Font.PLAIN, 14));
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tabela.getTableHeader().setBackground(Color.WHITE);
        tabela.getTableHeader().setForeground(corMarromTexto);
        tabela.setSelectionBackground(corBegeBotoes);
        tabela.setSelectionForeground(corMarromTexto);
        tabela.setGridColor(corMarromTexto);

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

    public static void main(String[] args) {
        // Look and Feel serve para garantir que as cores manuais prevaleçam, sem causar erros
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception e) {}
        new TelaRomaneiosAdmin();
    }
}
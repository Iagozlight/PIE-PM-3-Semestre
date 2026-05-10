package projeto.views.telas;

import javax.swing.*;
import java.awt.*;

public class TelaRomaneiosAdmin extends JFrame {

    public TelaRomaneiosAdmin() {
        setTitle("DUTRA MÓVEIS - Romaneios");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        iniciarComponentes(); // só chama o método aqui
        setVisible(true);
    }

    private void iniciarComponentes() {
        setLayout(new BorderLayout());

        // ===== PAINEL TOPO =====
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBackground(new Color(44, 62, 80));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titulo = new JLabel("DUTRA MÓVEIS - Romaneios");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.setBackground(new Color(44, 62, 80));

        JButton btnNovoCliente = new JButton("+ Novo Cliente");
        JButton btnNovoRomaneio = new JButton("+ Novo Romaneio");

        painelBotoes.add(btnNovoCliente);
        painelBotoes.add(btnNovoRomaneio);

        painelTopo.add(titulo, BorderLayout.WEST);
        painelTopo.add(painelBotoes, BorderLayout.EAST);

        add(painelTopo, BorderLayout.NORTH);

        // ===== TABELA DE ROMANEIOS =====
        String[] colunas = {"ID", "Data", "Veículo", "Motorista"};
        Object[][] dados = {}; // vazia por enquanto

        JTable tabela = new JTable(dados, colunas);
        tabela.setRowHeight(30);
        tabela.setFont(new Font("Arial", Font.PLAIN, 14));
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tabela.getTableHeader().setBackground(new Color(44, 62, 80));
        tabela.getTableHeader().setForeground(Color.WHITE);
        tabela.setSelectionBackground(new Color(52, 152, 219));
        tabela.setSelectionForeground(Color.WHITE);
        tabela.setGridColor(new Color(200, 200, 200));

        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        add(scrollPane, BorderLayout.CENTER);

        // ===== PAINEL RODAPÉ =====
        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelRodape.setBackground(new Color(236, 240, 241));
        painelRodape.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JButton btnAtribuirVeiculo = new JButton("Atribuir Veículo");
        JButton btnAtribuirMotorista = new JButton("Atribuir Motorista");
        JButton btnDeletar = new JButton("Deletar");

        btnDeletar.setBackground(new Color(231, 76, 60));
        btnDeletar.setForeground(Color.WHITE);

        painelRodape.add(btnAtribuirVeiculo);
        painelRodape.add(btnAtribuirMotorista);
        painelRodape.add(btnDeletar);

        add(painelRodape, BorderLayout.SOUTH);

    }

    public static void main(String[] args) {
        new TelaRomaneiosAdmin();
    }
}
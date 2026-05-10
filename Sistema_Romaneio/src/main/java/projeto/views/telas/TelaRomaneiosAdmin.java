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
    }

    public static void main(String[] args) {
        new TelaRomaneiosAdmin();
    }
}
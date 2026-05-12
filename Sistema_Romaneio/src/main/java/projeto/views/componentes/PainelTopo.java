package projeto.views.componentes;

import javax.swing.*;
import java.awt.*;

public class PainelTopo extends JPanel {

    private JButton btnNovoCliente;
    private JButton btnNovoRomaneio;

    public PainelTopo() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 240, 225));
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        iniciar();
    }

    private void iniciar() {
        JLabel titulo = new JLabel("DUTRA MÓVEIS - Romaneios");
        titulo.setForeground(new Color(60, 42, 33));
        titulo.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.setBackground(new Color(245, 240, 225));

        btnNovoCliente = new JButton("+ Novo Cliente");
        btnNovoRomaneio = new JButton("+ Novo Romaneio");

        painelBotoes.add(btnNovoCliente);
        painelBotoes.add(btnNovoRomaneio);

        add(titulo, BorderLayout.WEST);
        add(painelBotoes, BorderLayout.EAST);
    }

    public JButton getBtnNovoCliente() { return btnNovoCliente; }
    public JButton getBtnNovoRomaneio() { return btnNovoRomaneio; }
}
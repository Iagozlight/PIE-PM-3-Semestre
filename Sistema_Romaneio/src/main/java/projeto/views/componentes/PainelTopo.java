package projeto.views.componentes;

import javax.swing.*;
import java.awt.*;

public class PainelTopo extends JPanel {

    private JButton btnNovoCliente;
    private JButton btnNovoRomaneio;
    private JButton btnVeiculos;

    private final Color corFundo = new Color(245, 240, 225);
    private final Color corMarrom = new Color(60, 42, 33);
    private final Color corBege = new Color(220, 198, 150);

    public PainelTopo() {
        setLayout(new BorderLayout());
        setBackground(corFundo);
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        iniciar();
    }

    private void iniciar() {
        JLabel titulo = new JLabel("DUTRA MÓVEIS - Romaneios");
        titulo.setForeground(corMarrom);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.setBackground(corFundo);

        btnNovoCliente = new JButton("+ Novo Cliente");
        btnNovoCliente.setBackground(corBege);
        btnNovoCliente.setForeground(corMarrom);

        btnNovoRomaneio = new JButton("+ Novo Romaneio");
        btnNovoRomaneio.setBackground(corBege);
        btnNovoRomaneio.setForeground(corMarrom);

        btnVeiculos = new JButton("Veiculos");
        btnVeiculos.setBackground(corBege);
        btnVeiculos.setForeground(corMarrom);

        painelBotoes.add(btnNovoCliente);
        painelBotoes.add(btnNovoRomaneio);
        painelBotoes.add(btnVeiculos);

        add(titulo, BorderLayout.WEST);
        add(painelBotoes, BorderLayout.EAST);
    }

    public JButton getBtnNovoCliente() { return btnNovoCliente; }
    public JButton getBtnNovoRomaneio() { return btnNovoRomaneio; }
    public JButton getBtnVeiculos() { return btnVeiculos; }
}

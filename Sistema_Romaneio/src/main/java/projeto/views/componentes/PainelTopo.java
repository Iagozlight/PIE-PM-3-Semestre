package projeto.views.componentes;

import projeto.util.Cores;
import projeto.util.Fontes;

import javax.swing.*;
import java.awt.*;

public class PainelTopo extends JPanel {

    private JButton btnNovoCliente;
    private JButton btnNovoRomaneio;
    private JButton btnVeiculos;

    public PainelTopo() {
        setLayout(new BorderLayout());
        setBackground(Cores.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        iniciar();
    }

    private void iniciar() {
        JLabel titulo = new JLabel("DUTRA MÃ“VEIS - Romaneios");
        titulo.setForeground(Cores.MARROM);
        titulo.setFont(Fontes.arial(Font.BOLD, 18));

        JPanel painelBotoes = new JPanel(new GridLayout(1, 3, 8, 0));
        painelBotoes.setBackground(Cores.FUNDO);

        btnNovoCliente = new JButton("+ Novo Cliente");
        btnNovoCliente.setBackground(Cores.BEGE);
        btnNovoCliente.setForeground(Cores.MARROM);

        btnNovoRomaneio = new JButton("+ Novo Romaneio");
        btnNovoRomaneio.setBackground(Cores.BEGE);
        btnNovoRomaneio.setForeground(Cores.MARROM);

        btnVeiculos = new JButton("Veiculos");
        btnVeiculos.setBackground(Cores.BEGE);
        btnVeiculos.setForeground(Cores.MARROM);

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

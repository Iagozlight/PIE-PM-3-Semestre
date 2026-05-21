package projeto.views.componentes;

import projeto.util.Cores;
import projeto.util.Fontes;
import projeto.util.BotaoEstilo;

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
        JLabel titulo = new JLabel("DUTRA MÓVEIS - Romaneios");
        titulo.setForeground(Cores.MARROM);
        titulo.setFont(Fontes.arial(Font.BOLD, 18));

        JPanel painelBotoes = new JPanel(new GridLayout(1, 3, 8, 0));
        painelBotoes.setBackground(Cores.FUNDO);

        btnNovoCliente = BotaoEstilo.aviso(new JButton("+ Novo Cliente"));

        btnNovoRomaneio = BotaoEstilo.aviso(new JButton("+ Novo Romaneio"));

        btnVeiculos = BotaoEstilo.aviso(new JButton("Veículos"));

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

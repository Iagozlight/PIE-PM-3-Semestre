package view.telas;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import view.componentes.BotaoNav;
import view.util.CursorUtil;

public class TelaPrincipal {

    private JFrame frame;
    private JPanel painelNavegar;
    private JPanel painelContendo;
    private CardLayout cardLayout;

    public TelaPrincipal() {
        //-----DEFINIÇÕES------

        frame = new JFrame("Tela Principal");
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        setarIcone();
        frame.getContentPane().setBackground(new Color(0xF5F0E0));
        frame.setCursor(CursorUtil.carregar("/view/icons/cursor.png"));

        painelNavegar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        painelNavegar.setBackground(new Color(0xE9DDBF));
        painelNavegar.setPreferredSize(new Dimension(frame.getWidth(), 60));

        JButton btnLogin = new BotaoNav("Login");
        JButton btnRomaneios = new BotaoNav("Romaneios");
        JButton btnCriarRomaneio = new BotaoNav("Criar Romaneio");

        painelNavegar.add(btnLogin);
        painelNavegar.add(btnRomaneios);
        painelNavegar.add(btnCriarRomaneio);

        // -------- CONTEÚDO ------
        cardLayout = new CardLayout();
        painelContendo = new JPanel(cardLayout);

        painelContendo.add(new JLabel("Tela Login"),"LOGIN");
        painelContendo.add(new JLabel("Tela Romaneios"),"ROMANEIOS");
        painelContendo.add(new JLabel("Tela Criar Romaneio"),"CRIAR_ROMANEIO");

        // ------ EVENTOS -------
        btnLogin.addActionListener(e -> cardLayout.show(painelContendo, "LOGIN"));
        btnRomaneios.addActionListener(e -> cardLayout.show(painelContendo, "ROMANEIOS"));
        btnCriarRomaneio.addActionListener(e -> cardLayout.show(painelContendo, "CRIAR_ROMANEIO"));

        // ------- ADICIONADOS -------
        frame.add(painelNavegar,  BorderLayout.NORTH);
        frame.add(painelContendo, BorderLayout.CENTER);


        cardLayout.show(painelContendo, "LOGIN");

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void setarIcone() {
        URL iconUrl = getClass().getResource("/view/icons/supplies.png");

        if (iconUrl != null) {
            Image icon = new ImageIcon(iconUrl).getImage();
            frame.setIconImage(icon);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaPrincipal::new);
    }
}

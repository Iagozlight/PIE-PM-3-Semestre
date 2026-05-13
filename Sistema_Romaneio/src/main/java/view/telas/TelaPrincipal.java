package view.telas;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import view.componentes.BotaoNav;
import view.util.CursorUtil;

public class TelaPrincipal {

    private static final Color NORMAL = new Color(0xF5F0E0);
    private static final Color LOGO = new Color(0xF9F6ED);
    private static final Color ESCURO  = new Color(0x9A6F35);

    private JFrame frame;
    private JPanel painelNavegar;
    private JPanel painelContendo;
    private CardLayout cardLayout;
    private JPanel painelLogo;

    public TelaPrincipal() {
        //-----DEFINIÇÕES------

        frame = new JFrame("Tela Principal");
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        setarIcone();
        frame.getContentPane().setBackground(NORMAL);
        frame.setCursor(CursorUtil.carregar("/view/icons/cursor.png"));

        painelLogo = new JPanel(new BorderLayout());
        painelLogo.setBackground(LOGO);

        painelNavegar = new JPanel(new BorderLayout());
        painelNavegar.setBackground(NORMAL);
        painelNavegar.setPreferredSize(new Dimension(frame.getWidth(), 60));

        JButton btnLogin = new BotaoNav("Login");
        JButton btnRomaneios = new BotaoNav("Romaneios");
        JButton btnCriarRomaneio = new BotaoNav("Criar Romaneio");

        //---- Painel Esquerrdo --------

        JPanel painelEsquerdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        painelEsquerdo.setOpaque(false);
        painelEsquerdo.add(btnLogin);

        //---- Painel Central --------

        JPanel painelCentro = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        painelCentro.setOpaque(false);
        painelCentro.add(btnRomaneios);

        //------ Painel Direito ---------

        JPanel painelDireito = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        painelDireito.setOpaque(false);
        painelDireito.add(btnCriarRomaneio);


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

        painelNavegar.add(painelEsquerdo, BorderLayout.WEST);
        painelNavegar.add(painelCentro, BorderLayout.CENTER);
        painelNavegar.add(painelDireito, BorderLayout.EAST);

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

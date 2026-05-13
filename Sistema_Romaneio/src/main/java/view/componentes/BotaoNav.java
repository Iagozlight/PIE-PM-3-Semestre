package view.componentes;

import javax.swing.*;
import java.awt.*;

public class BotaoNav extends JButton {
    public BotaoNav(String texto) {
        super(texto);
        setFocusPainted(false);
        setBackground(new Color(0xC0934F));
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setBorder(BorderFactory.createEmptyBorder(8,16,8,16));
    }
}

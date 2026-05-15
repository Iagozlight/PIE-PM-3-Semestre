package view.componentes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BotaoNav extends JButton {

    private static final Color NORMAL = new Color(0xC0934F);
    private static final Color HOVER  = new Color(0x9A6F35);

    public BotaoNav(String texto) {
        super(texto);
        setFocusPainted(false);
        setBackground(NORMAL);
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setBorder(BorderFactory.createEmptyBorder(8,16,8,16));

        Hover();
    }

    private void Hover() {
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(HOVER);
            }

            @Override
            public  void mouseExited(MouseEvent e) {
                setBackground(NORMAL);
            }
        });
    }
}

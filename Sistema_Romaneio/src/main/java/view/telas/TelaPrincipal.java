package view.telas;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class TelaPrincipal {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tela Principal");
        frame.setSize(800, 600);
        frame.setLayout(new FlowLayout());
        frame.getContentPane().setBackground(new Color(0xF5F0E0));

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));

        frame.setCursor(loadCursor("/view/cursor/cursor.png"));
        frame.add(panel);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static Cursor loadCursor(String resourcePath) {
        URL cursorUrl = TelaPrincipal.class.getResource(resourcePath);
        if (cursorUrl == null) {
            return Cursor.getDefaultCursor();
        }

        Image image = new ImageIcon(cursorUrl).getImage();
        try {
            return Toolkit.getDefaultToolkit().createCustomCursor(
                    image,
                    new Point(16, 16),
                    "cursor"
            );
        } catch (RuntimeException ex) {
            return Cursor.getDefaultCursor();
        }
    }
}

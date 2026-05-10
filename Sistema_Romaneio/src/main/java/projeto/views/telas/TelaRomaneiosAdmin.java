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

    private void iniciarComponentes() {}

    public static void main(String[] args) {
        new TelaRomaneiosAdmin();
    }
}
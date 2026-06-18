package projeto.views.componentes;

import javax.swing.*;
import java.awt.*;

public final class JanelaUtil {

    private JanelaUtil() {
    }

    public static void configurarJanela(JFrame janela, Dimension tamanhoPreferido, Dimension tamanhoMinimo) {
        if (tamanhoPreferido != null) {
            janela.setPreferredSize(tamanhoPreferido);
        }
        janela.pack();
        aplicarTamanhoMinimo(janela, tamanhoMinimo);
        janela.setLocationRelativeTo(null);
    }

    public static void configurarDialog(JDialog dialog, Component parent, Dimension tamanhoPreferido, Dimension tamanhoMinimo) {
        if (tamanhoPreferido != null) {
            dialog.setPreferredSize(tamanhoPreferido);
        }
        dialog.pack();
        aplicarTamanhoMinimo(dialog, tamanhoMinimo);
        dialog.setLocationRelativeTo(parent);
    }

    private static void aplicarTamanhoMinimo(Window janela, Dimension tamanhoMinimo) {
        if (tamanhoMinimo == null) {
            return;
        }
        janela.setMinimumSize(tamanhoMinimo);
        if (janela.getWidth() < tamanhoMinimo.width || janela.getHeight() < tamanhoMinimo.height) {
            janela.setSize(tamanhoMinimo);
        }
    }
}

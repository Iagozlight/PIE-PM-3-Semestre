package projeto.models.util;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public final class BotaoEstilo {

    private BotaoEstilo() {
    }

    public static JButton primario(JButton botao) {
        configurarBasico(botao);
        botao.setBackground(Cores.AZUL);
        botao.setForeground(Color.WHITE);
        return botao;
    }

    public static JButton sucesso(JButton botao) {
        configurarBasico(botao);
        botao.setBackground(Cores.VERDE);
        botao.setForeground(Color.WHITE);
        return botao;
    }

    public static JButton aviso(JButton botao) {
        configurarBasico(botao);
        botao.setBackground(Cores.BEGE);
        botao.setForeground(Cores.MARROM);
        return botao;
    }

    public static JButton neutro(JButton botao) {
        configurarBasico(botao);
        botao.setBackground(Cores.BRANCO);
        botao.setForeground(Cores.MARROM);
        return botao;
    }

    public static JButton perigo(JButton botao) {
        configurarBasico(botao);
        botao.setBackground(Cores.VERMELHO);
        botao.setForeground(Color.WHITE);
        return botao;
    }

    private static void configurarBasico(JButton botao) {
        botao.setFocusPainted(false);
        botao.setBorder(new EmptyBorder(8, 14, 8, 14));
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        botao.setOpaque(true);
    }
}

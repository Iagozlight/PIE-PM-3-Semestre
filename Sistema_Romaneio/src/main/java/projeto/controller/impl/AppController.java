package projeto.controller.impl;

import projeto.views.telas.TelaPrincipal;

import javax.swing.*;

public class AppController {

    public void iniciar() {
        SwingUtilities.invokeLater(() -> {
            try {
                new TelaPrincipal();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                        null,
                        e.getMessage(),
                        "Erro ao iniciar",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}

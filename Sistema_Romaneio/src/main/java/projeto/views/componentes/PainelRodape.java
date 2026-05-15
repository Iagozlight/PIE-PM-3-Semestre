package projeto.views.componentes;

import javax.swing.*;
import java.awt.*;

public class PainelRodape extends JPanel {

    private JButton btnAtribuirVeiculo;
    private JButton btnAtribuirMotorista;
    private JButton btnDeletar;
    private JButton btnEditar;

    private final Color corFundo = new Color(245, 240, 225);
    private final Color corMarrom = new Color(60, 42, 33);
    private final Color corBranco = new Color(252, 249, 241);

    public PainelRodape() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBackground(corFundo);
        setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        iniciar();
    }

    private void iniciar() {
        btnAtribuirVeiculo = new JButton("Atribuir Veículo");
        btnAtribuirVeiculo.setBackground(corBranco);
        btnAtribuirVeiculo.setForeground(corMarrom);

        btnAtribuirMotorista = new JButton("Atribuir Motorista");
        btnAtribuirMotorista.setBackground(corBranco);
        btnAtribuirMotorista.setForeground(corMarrom);

        btnDeletar = new JButton("Deletar");
        btnDeletar.setBackground(new Color(211, 47, 47));
        btnDeletar.setForeground(Color.WHITE);

        btnEditar = new JButton("Editar");
        btnEditar.setBackground(new Color(255, 193, 7));
        btnEditar.setForeground(corMarrom);

        add(btnAtribuirVeiculo);
        add(btnAtribuirMotorista);
        add(btnDeletar);
        add(btnEditar);
    }

    public JButton getBtnAtribuirVeiculo() { return btnAtribuirVeiculo; }
    public JButton getBtnAtribuirMotorista() { return btnAtribuirMotorista; }
    public JButton getBtnDeletar() { return btnDeletar; }
    public JButton getBtnEditar() { return btnEditar; }
}
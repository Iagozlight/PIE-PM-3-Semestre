package projeto.views.componentes;

import projeto.util.Cores;

import javax.swing.*;
import java.awt.*;

public class PainelRodape extends JPanel {

    private JButton btnAtribuirVeiculo;
    private JButton btnAtribuirMotorista;
    private JButton btnDeletar;
    private JButton btnEditar;
    private JButton btnGps;

    public PainelRodape() {
        setLayout(new GridLayout(0, 2, 8, 8));
        setBackground(Cores.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        iniciar();
    }

    private void iniciar() {
        btnAtribuirVeiculo = new JButton("Atribuir VeÃ­culo");
        btnAtribuirVeiculo.setBackground(Cores.BRANCO);
        btnAtribuirVeiculo.setForeground(Cores.MARROM);

        btnAtribuirMotorista = new JButton("Atribuir Motorista");
        btnAtribuirMotorista.setBackground(Cores.BRANCO);
        btnAtribuirMotorista.setForeground(Cores.MARROM);

        btnDeletar = new JButton("Deletar");
        btnDeletar.setBackground(Cores.VERMELHO);
        btnDeletar.setForeground(Color.WHITE);

        btnEditar = new JButton("Editar");
        btnEditar.setBackground(new Color(255, 193, 7));
        btnEditar.setForeground(Cores.MARROM);

        btnGps = new JButton("GPS");
        btnGps.setBackground(Cores.AZUL);
        btnGps.setForeground(Color.WHITE);

        add(btnAtribuirVeiculo);
        add(btnAtribuirMotorista);
        add(btnDeletar);
        add(btnEditar);
        add(btnGps);
    }

    public JButton getBtnAtribuirVeiculo() { return btnAtribuirVeiculo; }
    public JButton getBtnAtribuirMotorista() { return btnAtribuirMotorista; }
    public JButton getBtnDeletar() { return btnDeletar; }
    public JButton getBtnEditar() { return btnEditar; }
    public JButton getBtnGps() { return btnGps; }
}

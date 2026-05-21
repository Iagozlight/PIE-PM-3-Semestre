package projeto.views.componentes;

import projeto.util.Cores;
import projeto.util.BotaoEstilo;

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
        btnAtribuirVeiculo = BotaoEstilo.neutro(new JButton("Atribuir Veículo"));

        btnAtribuirMotorista = BotaoEstilo.neutro(new JButton("Atribuir Motorista"));

        btnDeletar = BotaoEstilo.perigo(new JButton("Deletar"));

        btnEditar = BotaoEstilo.aviso(new JButton("Editar"));

        btnGps = BotaoEstilo.primario(new JButton("GPS"));

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

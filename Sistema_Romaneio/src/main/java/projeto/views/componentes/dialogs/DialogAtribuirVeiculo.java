package projeto.views.componentes.dialogs;

import projeto.models.Romaneios;
import projeto.models.Veiculos;
import projeto.repositories.VeiculosRepository;
import projeto.services.RomaneiosService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DialogAtribuirVeiculo extends JDialog {

    private JList<String> listaVeiculos;
    private List<Veiculos> veiculos;

    private final Romaneios romaneio;
    private final RomaneiosService romaneiosService;
    private final VeiculosRepository veiculosRepository;
    private Runnable aoSalvar;

    private final Color corFundo = new Color(245, 240, 225);
    private final Color corMarrom = new Color(60, 42, 33);
    private final Color corBranco = new Color(252, 249, 241);

    public DialogAtribuirVeiculo(JFrame parent, Romaneios romaneio,
                                 RomaneiosService romaneiosService,
                                 VeiculosRepository veiculosRepository,
                                 Runnable aoSalvar) {
        super(parent, "Atribuir Veículo", true);
        this.romaneio = romaneio;
        this.romaneiosService = romaneiosService;
        this.veiculosRepository = veiculosRepository;
        this.aoSalvar = aoSalvar;
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setResizable(false);
        iniciarComponentes();
        setVisible(true);
    }

    private void iniciarComponentes() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(corFundo);

        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS));
        painelPrincipal.setBackground(corFundo);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("Selecione um Veículo");
        titulo.setFont(new Font("Arial", Font.BOLD, 14));
        titulo.setForeground(corMarrom);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelPrincipal.add(titulo);
        painelPrincipal.add(Box.createVerticalStrut(10));

        veiculos = veiculosRepository.findAll();
        DefaultListModel<String> modelo = new DefaultListModel<>();
        for (Veiculos v : veiculos) {
            modelo.addElement(v.getNomeVeiculo() + " - " + v.getPlaca());
        }

        listaVeiculos = new JList<>(modelo);
        listaVeiculos.setFont(new Font("Arial", Font.PLAIN, 13));
        listaVeiculos.setBackground(corBranco);
        listaVeiculos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaVeiculos.setSelectionBackground(new Color(52, 152, 219));
        listaVeiculos.setSelectionForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(listaVeiculos);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        painelPrincipal.add(scroll);

        add(painelPrincipal, BorderLayout.CENTER);

        // ===== BOTÕES =====
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.setBackground(corFundo);
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(corBranco);
        btnCancelar.setForeground(corMarrom);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnAtribuir = new JButton("Atribuir");
        btnAtribuir.setBackground(new Color(46, 125, 50));
        btnAtribuir.setForeground(Color.WHITE);
        btnAtribuir.addActionListener(e -> atribuir());

        painelBotoes.add(btnCancelar);
        painelBotoes.add(btnAtribuir);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void atribuir() {
        int index = listaVeiculos.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um veículo!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Veiculos veiculo = veiculos.get(index);
        String mensagem = romaneiosService.atribuirVeiculo(romaneio, veiculo);
        JOptionPane.showMessageDialog(this, mensagem);

        if (mensagem.equals("Veículo atribuído com sucesso!")) {
            aoSalvar.run();
            dispose();
        }
    }
}
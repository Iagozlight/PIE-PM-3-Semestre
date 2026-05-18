package projeto.views.dialogs;

import projeto.models.Motoristas;
import projeto.models.Romaneios;
import projeto.repositories.MotoristasRepository;
import projeto.services.RomaneiosService;
import projeto.views.componentes.JanelaUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DialogAtribuirMotorista extends JDialog {

    private JList<String> listaMotoristas;
    private List<Motoristas> motoristas;

    private final Romaneios romaneio;
    private final RomaneiosService romaneiosService;
    private final MotoristasRepository motoristasRepository;
    private Runnable aoSalvar;

    private final Color corFundo = new Color(245, 240, 225);
    private final Color corMarrom = new Color(60, 42, 33);
    private final Color corBranco = new Color(252, 249, 241);

    public DialogAtribuirMotorista(JFrame parent, Romaneios romaneio,
                                   RomaneiosService romaneiosService,
                                   MotoristasRepository motoristasRepository,
                                   Runnable aoSalvar) {
        super(parent, "Atribuir Motorista", true);
        this.romaneio = romaneio;
        this.romaneiosService = romaneiosService;
        this.motoristasRepository = motoristasRepository;
        this.aoSalvar = aoSalvar;
        iniciarComponentes();
        setResizable(true);
        JanelaUtil.configurarDialog(this, parent, new Dimension(460, 380), new Dimension(360, 300));
        setVisible(true);
    }

    private void iniciarComponentes() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(corFundo);

        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS));
        painelPrincipal.setBackground(corFundo);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("Selecione um Motorista");
        titulo.setFont(new Font("Arial", Font.BOLD, 14));
        titulo.setForeground(corMarrom);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelPrincipal.add(titulo);
        painelPrincipal.add(Box.createVerticalStrut(10));

        motoristas = motoristasRepository.findAll();
        DefaultListModel<String> modelo = new DefaultListModel<>();
        for (Motoristas m : motoristas) {
            modelo.addElement(m.getNome());
        }

        listaMotoristas = new JList<>(modelo);
        listaMotoristas.setFont(new Font("Arial", Font.PLAIN, 13));
        listaMotoristas.setBackground(corBranco);
        listaMotoristas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaMotoristas.setSelectionBackground(new Color(52, 152, 219));
        listaMotoristas.setSelectionForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(listaMotoristas);
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
        int index = listaMotoristas.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um motorista!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Motoristas motorista = motoristas.get(index);
        String mensagem = romaneiosService.atribuirMotorista(romaneio, motorista);
        JOptionPane.showMessageDialog(this, mensagem);

        if (mensagem.equals("Motorista atribuído com sucesso!")) {
            aoSalvar.run();
            dispose();
        }
    }
}

package projeto.views.dialogs;

import projeto.models.ClientesRomaneio;
import projeto.models.Romaneios;
import projeto.repositories.ClientesRomaneioRepository;
import projeto.services.RomaneiosService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;

public class DialogEditarRomaneio extends JDialog {

    private JTextField campoData;
    private JTable tabelaClientes;
    private DefaultTableModel modeloClientes;

    private final Romaneios romaneio;
    private final RomaneiosService romaneiosService;
    private final ClientesRomaneioRepository clientesRomaneioRepository;
    private Runnable aoSalvar;

    private final Color corFundo = new Color(245, 240, 225);
    private final Color corMarrom = new Color(60, 42, 33);
    private final Color corBege = new Color(220, 198, 150);
    private final Color corBranco = new Color(252, 249, 241);

    public DialogEditarRomaneio(JFrame parent, Romaneios romaneio,
                                RomaneiosService romaneiosService,
                                ClientesRomaneioRepository clientesRomaneioRepository,
                                Runnable aoSalvar) {
        super(parent, "Editar Romaneio", true);
        this.romaneio = romaneio;
        this.romaneiosService = romaneiosService;
        this.clientesRomaneioRepository = clientesRomaneioRepository;
        this.aoSalvar = aoSalvar;
        setSize(500, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
        iniciarComponentes();
        carregarDados();
        setVisible(true);
    }

    private void iniciarComponentes() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(corFundo);

        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS));
        painelPrincipal.setBackground(corFundo);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // ===== DATA =====
        painelPrincipal.add(criarTitulo("Data do Romaneio"));
        painelPrincipal.add(Box.createVerticalStrut(5));

        campoData = new JTextField();
        painelPrincipal.add(criarCampo("Data (dd/MM/yyyy):", campoData));
        painelPrincipal.add(Box.createVerticalStrut(15));

        // ===== CLIENTES =====
        painelPrincipal.add(criarTitulo("Clientes do Romaneio"));
        painelPrincipal.add(Box.createVerticalStrut(5));

        JLabel lblDica = new JLabel("Selecione um cliente e clique em Remover para tirá-lo");
        lblDica.setFont(new Font("Arial", Font.ITALIC, 11));
        lblDica.setForeground(corMarrom);
        lblDica.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelPrincipal.add(lblDica);
        painelPrincipal.add(Box.createVerticalStrut(5));

        String[] colunas = {"Nome", "CPF"};
        modeloClientes = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabelaClientes = new JTable(modeloClientes);
        tabelaClientes.setRowHeight(28);
        tabelaClientes.setFont(new Font("Arial", Font.PLAIN, 13));
        tabelaClientes.getTableHeader().setBackground(new Color(239, 218, 186));
        tabelaClientes.getTableHeader().setForeground(corMarrom);
        tabelaClientes.setSelectionBackground(new Color(52, 152, 219));
        tabelaClientes.setSelectionForeground(Color.WHITE);
        tabelaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollClientes = new JScrollPane(tabelaClientes);
        scrollClientes.setPreferredSize(new Dimension(440, 180));
        scrollClientes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        scrollClientes.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelPrincipal.add(scrollClientes);
        painelPrincipal.add(Box.createVerticalStrut(5));

        JButton btnRemoverCliente = new JButton("Remover Cliente Selecionado");
        btnRemoverCliente.setBackground(new Color(211, 47, 47));
        btnRemoverCliente.setForeground(Color.WHITE);
        btnRemoverCliente.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRemoverCliente.addActionListener(e -> removerCliente());
        painelPrincipal.add(btnRemoverCliente);

        add(painelPrincipal, BorderLayout.CENTER);

        // ===== BOTÕES =====
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.setBackground(corFundo);
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(corBranco);
        btnCancelar.setForeground(corMarrom);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setBackground(new Color(46, 125, 50));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.addActionListener(e -> salvar());

        painelBotoes.add(btnCancelar);
        painelBotoes.add(btnSalvar);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void carregarDados() {
        // Carrega a data atual do romaneio
        campoData.setText(romaneio.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        // Carrega os clientes do romaneio
        modeloClientes.setRowCount(0);
        for (ClientesRomaneio c : romaneio.getClientes()) {
            modeloClientes.addRow(new Object[]{c.getNome_cliente(), c.getCpf()});
        }
    }

    private void removerCliente() {
        int linha = tabelaClientes.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um cliente para remover!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (romaneio.getClientes().size() == 1) {
            JOptionPane.showMessageDialog(this,
                    "O romaneio precisa ter pelo menos um cliente!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja remover este cliente do romaneio?\nO histórico do cliente será mantido.",
                "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            ClientesRomaneio cliente = romaneio.getClientes().get(linha);
            cliente.setRomaneio(null); // desvincula do romaneio sem deletar o cliente
            clientesRomaneioRepository.update(cliente);
            romaneio.getClientes().remove(linha);
            modeloClientes.removeRow(linha);
            JOptionPane.showMessageDialog(this, "Cliente removido do romaneio!");
        }
    }

    private void salvar() {
        String dataStr = campoData.getText().trim();

        if (dataStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Informe a data!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate novaData = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            romaneio.setData(novaData);
            romaneiosService.atualizarRomaneio(romaneio);
            JOptionPane.showMessageDialog(this, "Romaneio atualizado com sucesso!");
            aoSalvar.run();
            dispose();
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Data inválida! Use o formato dd/MM/yyyy", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel criarCampo(String label, JTextField campo) {
        JPanel painel = new JPanel(new BorderLayout(10, 0));
        painel.setBackground(corFundo);
        painel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setForeground(corMarrom);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl.setPreferredSize(new Dimension(150, 25));

        campo.setFont(new Font("Arial", Font.PLAIN, 13));
        campo.setBackground(corBranco);

        painel.add(lbl, BorderLayout.WEST);
        painel.add(campo, BorderLayout.CENTER);

        return painel;
    }

    private JLabel criarTitulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(corMarrom);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
}
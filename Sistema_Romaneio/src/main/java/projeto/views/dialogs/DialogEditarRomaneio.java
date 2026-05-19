package projeto.views.dialogs;

import projeto.models.ClientesRomaneio;
import projeto.models.Motoristas;
import projeto.models.Romaneios;
import projeto.models.Veiculos;
import projeto.repositories.ClientesRomaneioRepository;
import projeto.repositories.MotoristasRepository;
import projeto.repositories.VeiculosRepository;
import projeto.services.RomaneiosService;
import projeto.views.componentes.JanelaUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DialogEditarRomaneio extends JDialog {

    private JTextField campoData;
    private JTable tabelaClientes;
    private DefaultTableModel modeloClientes;
    private JComboBox<Motoristas> comboMotoristas;
    private JComboBox<Veiculos> comboVeiculos;

    private final Romaneios romaneio;
    private final RomaneiosService romaneiosService;
    private final ClientesRomaneioRepository clientesRomaneioRepository;
    private final MotoristasRepository motoristasRepository;
    private final VeiculosRepository veiculosRepository;
    private final Runnable aoSalvar;

    private final Color corFundo = new Color(245, 240, 225);
    private final Color corMarrom = new Color(60, 42, 33);
    private final Color corBege = new Color(220, 198, 150);
    private final Color corBranco = new Color(252, 249, 241);

    public DialogEditarRomaneio(JFrame parent,
                                Romaneios romaneio,
                                RomaneiosService romaneiosService,
                                ClientesRomaneioRepository clientesRomaneioRepository,
                                MotoristasRepository motoristasRepository,
                                VeiculosRepository veiculosRepository,
                                Runnable aoSalvar) {
        super(parent, "Editar Romaneio", true);
        this.romaneio = romaneio;
        this.romaneiosService = romaneiosService;
        this.clientesRomaneioRepository = clientesRomaneioRepository;
        this.motoristasRepository = motoristasRepository;
        this.veiculosRepository = veiculosRepository;
        this.aoSalvar = aoSalvar;
        iniciarComponentes();
        carregarDados();
        setResizable(true);
        JanelaUtil.configurarDialog(this, parent, new Dimension(580, 670), new Dimension(520, 520));
        setVisible(true);
    }

    private void iniciarComponentes() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(corFundo);

        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS));
        painelPrincipal.setBackground(corFundo);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        painelPrincipal.add(criarTitulo("Data do Romaneio"));
        painelPrincipal.add(Box.createVerticalStrut(5));
        campoData = new JTextField();
        painelPrincipal.add(criarCampo("Data (dd/MM/yyyy):", campoData));
        painelPrincipal.add(Box.createVerticalStrut(15));

        painelPrincipal.add(criarTitulo("Motorista"));
        painelPrincipal.add(Box.createVerticalStrut(5));
        comboMotoristas = new JComboBox<>();
        comboMotoristas.addItem(null);
        for (Motoristas motorista : motoristasRepository.findAll()) {
            comboMotoristas.addItem(motorista);
        }
        comboMotoristas.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setText(value == null ? "Sem motorista" : value.toString());
                return label;
            }
        });
        painelPrincipal.add(criarCombo("Motorista:", comboMotoristas));
        painelPrincipal.add(Box.createVerticalStrut(15));

        painelPrincipal.add(criarTitulo("Veiculo"));
        painelPrincipal.add(Box.createVerticalStrut(5));
        comboVeiculos = new JComboBox<>();
        comboVeiculos.addItem(null);
        for (Veiculos veiculo : veiculosRepository.findAll()) {
            comboVeiculos.addItem(veiculo);
        }
        comboVeiculos.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setText(value == null ? "Sem veiculo" : value.toString());
                return label;
            }
        });
        painelPrincipal.add(criarCombo("Veiculo:", comboVeiculos));
        painelPrincipal.add(Box.createVerticalStrut(15));

        painelPrincipal.add(criarTitulo("Clientes do Romaneio"));
        painelPrincipal.add(Box.createVerticalStrut(5));
        JLabel lblDica = new JLabel("Selecione um cliente e clique em Remover para tira-lo");
        lblDica.setFont(new Font("Arial", Font.ITALIC, 11));
        lblDica.setForeground(corMarrom);
        lblDica.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelPrincipal.add(lblDica);
        painelPrincipal.add(Box.createVerticalStrut(5));

        modeloClientes = new DefaultTableModel(new Object[]{"Nome", "CPF", "Telefone"}, 0) {
            @Override
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
        campoData.setText(romaneio.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        comboMotoristas.setSelectedItem(romaneio.getMotorista());
        comboVeiculos.setSelectedItem(romaneio.getVeiculo());

        modeloClientes.setRowCount(0);
        for (ClientesRomaneio cliente : romaneio.getClientes()) {
            modeloClientes.addRow(new Object[]{
                    cliente.getNome_cliente(),
                    cliente.getCpf(),
                    formatarTelefone(cliente.getTelefone())
            });
        }
    }

    private void removerCliente() {
        int linha = tabelaClientes.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para remover!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (romaneio.getClientes().size() == 1) {
            JOptionPane.showMessageDialog(this, "O romaneio precisa ter pelo menos um cliente!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja remover este cliente do romaneio?\nO historico do cliente sera mantido.",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacao == JOptionPane.YES_OPTION) {
            ClientesRomaneio cliente = romaneio.getClientes().get(linha);
            cliente.setRomaneio(null);
            clientesRomaneioRepository.update(cliente);
            romaneio.getClientes().remove(linha);
            modeloClientes.removeRow(linha);
            JOptionPane.showMessageDialog(this, "Cliente removido do romaneio!");
        }
    }

    private void salvar() {
        String dataStr = campoData.getText().trim();
        if (dataStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe a data!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate novaData = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            romaneio.setData(novaData);
            romaneio.setMotorista((Motoristas) comboMotoristas.getSelectedItem());
            romaneio.setVeiculo((Veiculos) comboVeiculos.getSelectedItem());
            romaneiosService.atualizarRomaneio(romaneio);
            JOptionPane.showMessageDialog(this, "Romaneio atualizado com sucesso!");
            aoSalvar.run();
            dispose();
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data invalida! Use o formato dd/MM/yyyy", "Erro", JOptionPane.ERROR_MESSAGE);
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

    private JPanel criarCombo(String label, JComboBox<?> combo) {
        JPanel painel = new JPanel(new BorderLayout(10, 0));
        painel.setBackground(corFundo);
        painel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setForeground(corMarrom);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl.setPreferredSize(new Dimension(150, 25));

        combo.setFont(new Font("Arial", Font.PLAIN, 13));
        combo.setBackground(corBranco);

        painel.add(lbl, BorderLayout.WEST);
        painel.add(combo, BorderLayout.CENTER);
        return painel;
    }

    private JLabel criarTitulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(corMarrom);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private String formatarTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return "-";
        }
        String n = telefone.replaceAll("\\D", "");
        if (n.length() == 11) {
            return n.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        }
        if (n.length() == 10) {
            return n.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        }
        return telefone;
    }
}

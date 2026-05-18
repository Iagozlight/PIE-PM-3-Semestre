package projeto.views.dialogs;

import projeto.models.ClientesRomaneio;
import projeto.services.RomaneiosService;
import projeto.views.componentes.JanelaUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class DialogNovoRomaneio extends JDialog {

    private JTextField campoData;
    private JTable tabelaClientes;
    private DefaultTableModel modeloClientes;
    private List<ClientesRomaneio> clientesSemRomaneio;
    private List<ClientesRomaneio> clientesSelecionados = new ArrayList<>();

    private final RomaneiosService romaneiosService;
    private Runnable aoSalvar;

    private final Color corFundo = new Color(245, 240, 225);
    private final Color corMarrom = new Color(60, 42, 33);
    private final Color corBege = new Color(220, 198, 150);
    private final Color corBranco = new Color(252, 249, 241);

    public DialogNovoRomaneio(JFrame parent, RomaneiosService romaneiosService, Runnable aoSalvar) {
        super(parent, "Novo Romaneio", true);
        this.romaneiosService = romaneiosService;
        this.aoSalvar = aoSalvar;
        iniciarComponentes();
        carregarClientes();
        setResizable(true);
        JanelaUtil.configurarDialog(this, parent, new Dimension(560, 560), new Dimension(480, 420));
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
        campoData.setToolTipText("dd/MM/yyyy");
        painelPrincipal.add(criarCampo("Data (dd/MM/yyyy):", campoData));
        painelPrincipal.add(Box.createVerticalStrut(15));

        // ===== CLIENTES =====
        painelPrincipal.add(criarTitulo("Selecione os Clientes"));
        painelPrincipal.add(Box.createVerticalStrut(5));

        JLabel lblDica = new JLabel("Segure Ctrl para selecionar mais de um");
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
        tabelaClientes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JScrollPane scrollClientes = new JScrollPane(tabelaClientes);
        scrollClientes.setPreferredSize(new Dimension(440, 200));
        scrollClientes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        scrollClientes.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelPrincipal.add(scrollClientes);

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

    private void carregarClientes() {
        modeloClientes.setRowCount(0);
        clientesSemRomaneio = romaneiosService.listarClientesSemRomaneio();
        if (clientesSemRomaneio.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nenhum cliente disponível! Cadastre um cliente primeiro.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }
        for (ClientesRomaneio c : clientesSemRomaneio) {
            modeloClientes.addRow(new Object[]{c.getNome_cliente(), c.getCpf()});
        }
    }

    private void salvar() {
        String dataStr = campoData.getText().trim();
        int[] linhasSelecionadas = tabelaClientes.getSelectedRows();

        if (dataStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Informe a data do romaneio!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (linhasSelecionadas.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecione pelo menos um cliente!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate data;
        try {
            data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Data inválida! Use o formato dd/MM/yyyy", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        clientesSelecionados.clear();
        for (int linha : linhasSelecionadas) {
            clientesSelecionados.add(clientesSemRomaneio.get(linha));
        }

        romaneiosService.criarRomaneio(data, clientesSelecionados);
        JOptionPane.showMessageDialog(this, "Romaneio criado com sucesso!");
        aoSalvar.run(); // atualiza a tabela na tela principal
        dispose();
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

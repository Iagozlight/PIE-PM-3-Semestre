package projeto.views.dialogs;

import projeto.models.Endereco;
import projeto.models.Pedidos;
import projeto.services.ClientesService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DialogNovoCliente extends JDialog {

    private JTextField campoNome;
    private JTextField campoCpf;
    private JTextField campoCep;
    private JTextField campoRua;
    private JTextField campoNumero;
    private JTextField campoBairro;
    private JTextField campoComplemento;
    private JTextField campoReferencia;
    private JTextField campoProduto;
    private JTextField campoQuantidade;
    private JTable tabelaPedidos;
    private DefaultTableModel modeloPedidos;
    private List<Pedidos> listaPedidos = new ArrayList<>();

    private final ClientesService clientesService;

    private final Color corFundo = new Color(245, 240, 225);
    private final Color corMarrom = new Color(60, 42, 33);
    private final Color corBege = new Color(220, 198, 150);
    private final Color corBranco = new Color(252, 249, 241);

    public DialogNovoCliente(JFrame parent, ClientesService clientesService) {
        super(parent, "Novo Cliente", true);
        this.clientesService = clientesService;
        setSize(500, 650);
        setLocationRelativeTo(parent);
        setResizable(false);
        iniciarComponentes();
        setVisible(true);
    }

    private void iniciarComponentes() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(corFundo);

        // ===== PAINEL PRINCIPAL =====
        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS));
        painelPrincipal.setBackground(corFundo);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // ===== DADOS DO CLIENTE =====
        painelPrincipal.add(criarTitulo("Dados do Cliente"));
        painelPrincipal.add(Box.createVerticalStrut(5));

        campoNome = new JTextField();
        campoCpf = new JTextField();

        painelPrincipal.add(criarCampo("Nome:", campoNome));
        painelPrincipal.add(criarCampo("CPF:", campoCpf));
        painelPrincipal.add(Box.createVerticalStrut(10));

        // ===== ENDEREÇO =====
        painelPrincipal.add(criarTitulo("Endereço"));
        painelPrincipal.add(Box.createVerticalStrut(5));

        campoCep = new JTextField();
        campoRua = new JTextField();
        campoNumero = new JTextField();
        campoBairro = new JTextField();
        campoComplemento = new JTextField();
        campoReferencia = new JTextField();

        painelPrincipal.add(criarCampo("CEP:", campoCep));
        painelPrincipal.add(criarCampo("Rua:", campoRua));
        painelPrincipal.add(criarCampo("Número:", campoNumero));
        painelPrincipal.add(criarCampo("Bairro:", campoBairro));
        painelPrincipal.add(criarCampo("Complemento:", campoComplemento));
        painelPrincipal.add(criarCampo("Referência:", campoReferencia));
        painelPrincipal.add(Box.createVerticalStrut(10));

        // ===== PEDIDOS =====
        painelPrincipal.add(criarTitulo("Pedidos"));
        painelPrincipal.add(Box.createVerticalStrut(5));

        campoProduto = new JTextField();
        campoQuantidade = new JTextField();

        painelPrincipal.add(criarCampo("Produto:", campoProduto));
        painelPrincipal.add(criarCampo("Quantidade:", campoQuantidade));
        painelPrincipal.add(Box.createVerticalStrut(5));

        JButton btnAdicionarPedido = new JButton("+ Adicionar Pedido");
        btnAdicionarPedido.setBackground(corBege);
        btnAdicionarPedido.setForeground(corMarrom);
        btnAdicionarPedido.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAdicionarPedido.addActionListener(e -> adicionarPedido());
        painelPrincipal.add(btnAdicionarPedido);
        painelPrincipal.add(Box.createVerticalStrut(5));

        // Tabela de pedidos adicionados
        String[] colunas = {"Produto", "Quantidade"};
        modeloPedidos = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaPedidos = new JTable(modeloPedidos);
        tabelaPedidos.setRowHeight(25);
        tabelaPedidos.setFont(new Font("Arial", Font.PLAIN, 13));
        tabelaPedidos.getTableHeader().setBackground(new Color(239, 218, 186));
        tabelaPedidos.getTableHeader().setForeground(corMarrom);

        JScrollPane scrollPedidos = new JScrollPane(tabelaPedidos);
        scrollPedidos.setPreferredSize(new Dimension(440, 100));
        scrollPedidos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        scrollPedidos.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelPrincipal.add(scrollPedidos);

        JScrollPane scrollPrincipal = new JScrollPane(painelPrincipal);
        scrollPrincipal.setBorder(null);
        scrollPrincipal.getViewport().setBackground(corFundo);
        add(scrollPrincipal, BorderLayout.CENTER);

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

    private JPanel criarCampo(String label, JTextField campo) {
        JPanel painel = new JPanel(new BorderLayout(10, 0));
        painel.setBackground(corFundo);
        painel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setForeground(corMarrom);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl.setPreferredSize(new Dimension(100, 25));

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

    private void adicionarPedido() {
        String produto = campoProduto.getText().trim();
        String quantidade = campoQuantidade.getText().trim();

        if (produto.isEmpty() || quantidade.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Preencha o produto e a quantidade!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Pedidos pedido = new Pedidos(null, produto, quantidade);
        listaPedidos.add(pedido);
        modeloPedidos.addRow(new Object[]{produto, quantidade});

        campoProduto.setText("");
        campoQuantidade.setText("");
    }

    private void salvar() {
        String nome = campoNome.getText().trim();
        String cpf = campoCpf.getText().trim();
        String cep = campoCep.getText().trim();
        String rua = campoRua.getText().trim();
        String numero = campoNumero.getText().trim();
        String bairro = campoBairro.getText().trim();
        String complemento = campoComplemento.getText().trim();
        String referencia = campoReferencia.getText().trim();

        if (nome.isEmpty() || cpf.isEmpty() || cep.isEmpty() ||
                rua.isEmpty() || numero.isEmpty() || bairro.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Preencha todos os campos obrigatórios!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (listaPedidos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Adicione pelo menos um pedido!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Latitude e Longitude passam como null para serem definidos via API/Nominatim posteriormente
            Endereco endereco = new Endereco(cep, rua, numero, bairro, complemento, referencia, null, null);
            clientesService.criarCliente(nome, cpf, endereco, listaPedidos);
            JOptionPane.showMessageDialog(this, "Cliente salvo com sucesso!");
            dispose();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
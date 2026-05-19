package projeto.views.dialogs;

import projeto.models.Endereco;
import projeto.models.Pedidos;
import projeto.services.ClientesService;
import projeto.views.componentes.JanelaUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DialogNovoCliente extends JDialog {

    private static final List<String> CIDADES_DISPONIVEIS = List.of(
            "Foz do Iguacu",
            "Santa Terezinha de Itaipu",
            "Medianeira",
            "Matelandia",
            "Itaipulandia",
            "Sao Miguel do Iguacu",
            "Santa Helena"
    );

    private JTextField campoNome;
    private JTextField campoCpf;
    private JTextField campoTelefone;

    private JTextField campoCep;
    private JTextField campoRua;
    private JTextField campoNumero;
    private JTextField campoBairro;
    private JTextField campoComplemento;
    private JTextField campoReferencia;
    private JComboBox<String> comboCidade;
    private JList<String> listaCidades;

    private JTextField campoProduto;
    private JTextField campoQuantidade;
    private JTable tabelaPedidos;
    private DefaultTableModel modeloPedidos;
    private List<Pedidos> listaPedidos = new ArrayList<>();

    private final ClientesService clientesService;

    private final Color corFundo  = new Color(245, 240, 225);
    private final Color corMarrom = new Color(60, 42, 33);
    private final Color corBege   = new Color(220, 198, 150);
    private final Color corBranco = new Color(252, 249, 241);
    private final Color corErro   = new Color(220, 53, 69);
    private final Color corOk     = new Color(40, 167, 69);

    public DialogNovoCliente(JFrame parent, ClientesService clientesService) {
        super(parent, "Novo Cliente", true);
        this.clientesService = clientesService;
        iniciarComponentes();
        setResizable(true);
        JanelaUtil.configurarDialog(this, parent, new Dimension(620, 820), new Dimension(520, 680));
        setVisible(true);
    }

    private static class FiltroSomenteDigitos extends DocumentFilter {

        private final int maxChars;

        FiltroSomenteDigitos(int maxChars) {
            this.maxChars = maxChars;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {

            if (string == null) return;

            String digits = string.replaceAll("\\D", "");

            if (fb.getDocument().getLength() + digits.length() <= maxChars) {
                super.insertString(fb, offset, digits, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {

            if (text == null) return;

            String digits = text.replaceAll("\\D", "");

            int currentLen = fb.getDocument().getLength() - length;
            int allowed = maxChars - currentLen;

            if (allowed <= 0) return;

            if (digits.length() > allowed) {
                digits = digits.substring(0, allowed);
            }

            super.replace(fb, offset, length, digits, attrs);
        }
    }

    private void aplicarFiltroDigitos(JTextField campo, int maxChars) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new FiltroSomenteDigitos(maxChars));
    }

    private void marcarCampo(JTextField campo, boolean valido) {
        campo.setBorder(BorderFactory.createLineBorder(valido ? corOk : corErro, 1));
    }

    private void limparMarcacao(JTextField campo) {
        campo.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
    }

    private void iniciarComponentes() {

        setLayout(new BorderLayout());
        getContentPane().setBackground(corFundo);

        JPanel painelPrincipal = new JPanel();

        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS));
        painelPrincipal.setBackground(corFundo);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        painelPrincipal.add(criarTitulo("Dados do Cliente"));
        painelPrincipal.add(Box.createVerticalStrut(5));

        campoNome = new JTextField();
        campoCpf = new JTextField();
        campoTelefone = new JTextField();

        aplicarFiltroDigitos(campoCpf, 11);
        aplicarFiltroDigitos(campoTelefone, 11);

        painelPrincipal.add(criarCampo("Nome:", campoNome));

        painelPrincipal.add(criarCampoComDica(
                "CPF:",
                campoCpf,
                "Somente números, 11 dígitos"
        ));

        painelPrincipal.add(criarCampoComDica(
                "Telefone:",
                campoTelefone,
                "DDD + número  |  fixo: 10 dígitos  |  celular: 11 dígitos (começa com 9)"
        ));

        painelPrincipal.add(Box.createVerticalStrut(10));

        painelPrincipal.add(criarTitulo("Endereco"));
        painelPrincipal.add(Box.createVerticalStrut(5));

        campoCep = new JTextField();
        campoRua = new JTextField();
        campoNumero = new JTextField();
        campoBairro = new JTextField();
        campoComplemento = new JTextField();
        campoReferencia = new JTextField();

        aplicarFiltroDigitos(campoCep, 8);

        painelPrincipal.add(criarCampoComDica(
                "CEP:",
                campoCep,
                "8 dígitos — oeste do Paraná (85800-000 a 85999-999)"
        ));

        painelPrincipal.add(criarCampoComDica(
                "Rua:",
                campoRua,
                "Letras, números, espaços, hífen e ponto"
        ));

        painelPrincipal.add(criarCampo("Numero:", campoNumero));
        painelPrincipal.add(criarCampo("Bairro:", campoBairro));

        JPanel painelCidade = new JPanel(new BorderLayout(10, 0));

        painelCidade.setBackground(corFundo);
        painelCidade.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        painelCidade.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblCidade = new JLabel("Cidade:");

        lblCidade.setForeground(corMarrom);
        lblCidade.setFont(new Font("Arial", Font.PLAIN, 13));
        lblCidade.setPreferredSize(new Dimension(100, 25));

        comboCidade = new JComboBox<>(CIDADES_DISPONIVEIS.toArray(new String[0]));

        comboCidade.setBackground(corBranco);

        painelCidade.add(lblCidade, BorderLayout.WEST);
        painelCidade.add(comboCidade, BorderLayout.CENTER);

        painelPrincipal.add(painelCidade);

        painelPrincipal.add(criarCampo("Complemento:", campoComplemento));
        painelPrincipal.add(criarCampo("Referencia:", campoReferencia));

        painelPrincipal.add(Box.createVerticalStrut(10));

        painelPrincipal.add(criarTitulo("Cidades atendidas"));
        painelPrincipal.add(Box.createVerticalStrut(5));

        JLabel lblDicaCidade = new JLabel("Selecione uma ou mais cidades para priorizar a rota");

        lblDicaCidade.setFont(new Font("Arial", Font.ITALIC, 11));
        lblDicaCidade.setForeground(corMarrom);
        lblDicaCidade.setAlignmentX(Component.LEFT_ALIGNMENT);

        painelPrincipal.add(lblDicaCidade);

        DefaultListModel<String> modeloCidades = new DefaultListModel<>();

        for (String cidade : CIDADES_DISPONIVEIS) {
            modeloCidades.addElement(cidade);
        }

        listaCidades = new JList<>(modeloCidades);

        listaCidades.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listaCidades.setVisibleRowCount(4);
        listaCidades.setBackground(corBranco);
        listaCidades.setSelectionBackground(new Color(52, 152, 219));
        listaCidades.setSelectionForeground(Color.WHITE);

        JScrollPane scrollCidades = new JScrollPane(listaCidades);

        scrollCidades.setPreferredSize(new Dimension(440, 110));
        scrollCidades.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        scrollCidades.setAlignmentX(Component.LEFT_ALIGNMENT);

        painelPrincipal.add(scrollCidades);
        painelPrincipal.add(Box.createVerticalStrut(10));

        painelPrincipal.add(criarTitulo("Pedidos"));
        painelPrincipal.add(Box.createVerticalStrut(5));

        campoProduto = new JTextField();
        campoQuantidade = new JTextField();

        aplicarFiltroDigitos(campoQuantidade, 6);

        painelPrincipal.add(criarCampo("Produto:", campoProduto));

        painelPrincipal.add(criarCampoComDica(
                "Quantidade:",
                campoQuantidade,
                "Somente números inteiros"
        ));

        painelPrincipal.add(Box.createVerticalStrut(5));

        JButton btnAdicionarPedido = new JButton("+ Adicionar Pedido");

        btnAdicionarPedido.setBackground(corBege);
        btnAdicionarPedido.setForeground(corMarrom);
        btnAdicionarPedido.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnAdicionarPedido.addActionListener(e -> adicionarPedido());

        painelPrincipal.add(btnAdicionarPedido);
        painelPrincipal.add(Box.createVerticalStrut(5));

        modeloPedidos = new DefaultTableModel(new Object[]{"Produto", "Quantidade"}, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
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

    private JPanel criarCampoComDica(String label, JTextField campo, String dica) {

        JPanel wrapper = new JPanel();

        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(corFundo);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        wrapper.add(criarCampo(label, campo));

        JLabel lblDica = new JLabel("  " + dica);

        lblDica.setFont(new Font("Arial", Font.ITALIC, 10));
        lblDica.setForeground(new Color(140, 120, 100));
        lblDica.setAlignmentX(Component.LEFT_ALIGNMENT);

        wrapper.add(lblDica);

        return wrapper;
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

            JOptionPane.showMessageDialog(
                    this,
                    "Preencha o produto e a quantidade!",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (Integer.parseInt(quantidade) == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "A quantidade deve ser maior que zero!",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Pedidos pedido = new Pedidos(null, produto, quantidade);

        listaPedidos.add(pedido);

        modeloPedidos.addRow(new Object[]{produto, quantidade});

        campoProduto.setText("");
        campoQuantidade.setText("");
    }

    private boolean exibirResumoConfirmacao(
            String nome,
            String cpf,
            String telefone,
            String cep,
            String rua,
            String numero,
            String bairro,
            String cidade,
            String complemento,
            String referencia,
            List<String> cidadesSelecionadas
    ) {

        StringBuilder sb = new StringBuilder();

        sb.append("<html><body style='font-family:Arial; font-size:12px; width:360px'>");
        sb.append("<h3 style='color:#3c2a21'>Confirmar cadastro</h3>");

        sb.append("<b>Nome:</b> ").append(nome).append("<br>");

        String cpfFmt = cpf.replaceAll(
                "(\\d{3})(\\d{3})(\\d{3})(\\d{2})",
                "$1.$2.$3-$4"
        );

        sb.append("<b>CPF:</b> ").append(cpfFmt).append("<br>");

        String telFmt = telefone.length() == 11
                ? telefone.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3")
                : telefone.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");

        sb.append("<b>Telefone:</b> ").append(telFmt).append("<br><br>");

        sb.append("<b>Endereço</b><br>");

        String cepFmt = cep.replaceAll("(\\d{5})(\\d{3})", "$1-$2");

        sb.append("&nbsp;&nbsp;CEP: ").append(cepFmt).append("<br>");
        sb.append("&nbsp;&nbsp;Rua: ").append(rua).append(", ").append(numero).append("<br>");
        sb.append("&nbsp;&nbsp;Bairro: ").append(bairro).append("<br>");
        sb.append("&nbsp;&nbsp;Cidade: ").append(cidade).append("<br>");

        if (!complemento.isEmpty()) {
            sb.append("&nbsp;&nbsp;Complemento: ").append(complemento).append("<br>");
        }

        if (!referencia.isEmpty()) {
            sb.append("&nbsp;&nbsp;Referência: ").append(referencia).append("<br>");
        }

        sb.append("<br><b>Cidades atendidas:</b><br>");

        for (String c : cidadesSelecionadas) {
            sb.append("&nbsp;&nbsp;• ").append(c).append("<br>");
        }

        sb.append("<br><b>Pedidos (").append(listaPedidos.size()).append("):</b><br>");

        for (Pedidos p : listaPedidos) {
            sb.append("&nbsp;&nbsp;• ")
                    .append(p.getNome_produto())
                    .append(" — qtd: ")
                    .append(p.getQuantidade())
                    .append("<br>");
        }

        sb.append("</body></html>");

        int opcao = JOptionPane.showConfirmDialog(
                this,
                new JLabel(sb.toString()),
                "Confirmar dados",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        return opcao == JOptionPane.OK_OPTION;
    }

    private void salvar() {

        String nome = campoNome.getText().trim();
        String cpf = campoCpf.getText().trim();
        String telefone = campoTelefone.getText().trim();
        String cep = campoCep.getText().trim();
        String rua = campoRua.getText().trim();
        String numero = campoNumero.getText().trim();
        String bairro = campoBairro.getText().trim();
        String cidade = (String) comboCidade.getSelectedItem();
        String complemento = campoComplemento.getText().trim();
        String referencia = campoReferencia.getText().trim();

        limparMarcacao(campoNome);
        limparMarcacao(campoCpf);
        limparMarcacao(campoTelefone);
        limparMarcacao(campoCep);
        limparMarcacao(campoRua);
        limparMarcacao(campoNumero);
        limparMarcacao(campoBairro);

        boolean valido = true;

        StringBuilder erros = new StringBuilder(
                "<html><body style='font-family:Arial'>"
        );

        erros.append("<b>Corrija os seguintes erros:</b><br><br>");

        if (nome.isEmpty()) {
            marcarCampo(campoNome, false);
            erros.append("• <b>Nome</b> é obrigatório.<br>");
            valido = false;
        } else {
            marcarCampo(campoNome, true);
        }

        if (cpf.isEmpty()) {

            marcarCampo(campoCpf, false);

            erros.append("• <b>CPF</b> é obrigatório.<br>");

            valido = false;

        } else if (cpf.length() != 11) {

            marcarCampo(campoCpf, false);

            erros.append("• <b>CPF</b> deve ter 11 dígitos (atual: ")
                    .append(cpf.length())
                    .append(").<br>");

            valido = false;

        } else if (!clientesService.validarCpf(cpf)) {

            marcarCampo(campoCpf, false);

            erros.append("• <b>CPF</b> inválido (dígitos verificadores incorretos).<br>");

            valido = false;

        } else {
            marcarCampo(campoCpf, true);
        }

        if (telefone.isEmpty()) {

            marcarCampo(campoTelefone, false);

            erros.append("• <b>Telefone</b> é obrigatório.<br>");

            valido = false;

        } else if (!clientesService.validarTelefone(telefone)) {

            marcarCampo(campoTelefone, false);

            String ddd = clientesService.extrairDdd(telefone);

            if (telefone.length() < 10) {

                erros.append("• <b>Telefone</b> incompleto (")
                        .append(telefone.length())
                        .append(" dígitos). Fixo: 10  |  Celular: 11.<br>");

            } else if (ddd != null && !isValidDdd(ddd)) {

                erros.append("• <b>Telefone</b>: DDD ")
                        .append(ddd)
                        .append(" não é válido.<br>");

            } else if (telefone.length() == 11 && telefone.charAt(2) != '9') {

                erros.append("• <b>Telefone</b> celular deve começar com 9 após o DDD.<br>");

            } else {

                erros.append("• <b>Telefone</b> inválido. Verifique DDD e quantidade de dígitos.<br>");
            }

            valido = false;

        } else {
            marcarCampo(campoTelefone, true);
        }

        if (cep.isEmpty()) {

            marcarCampo(campoCep, false);

            erros.append("• <b>CEP</b> é obrigatório.<br>");

            valido = false;

        } else if (cep.length() != 8) {

            marcarCampo(campoCep, false);

            erros.append("• <b>CEP</b> deve ter 8 dígitos (atual: ")
                    .append(cep.length())
                    .append(").<br>");

            valido = false;

        } else if (!clientesService.validarCep(cep)) {

            marcarCampo(campoCep, false);

            erros.append("• <b>CEP</b> fora da faixa do oeste do Paraná (85800-000 a 85999-999).<br>");

            valido = false;

        } else {
            marcarCampo(campoCep, true);
        }

        if (rua.isEmpty()) {

            marcarCampo(campoRua, false);

            erros.append("• <b>Rua</b> é obrigatória.<br>");

            valido = false;

        } else if (!clientesService.validarRua(rua)) {

            marcarCampo(campoRua, false);

            erros.append("• <b>Rua</b> contém caracteres inválidos. Use letras, números, espaços, hífen ou ponto.<br>");

            valido = false;

        } else {
            marcarCampo(campoRua, true);
        }

        if (numero.isEmpty()) {

            marcarCampo(campoNumero, false);

            erros.append("• <b>Número</b> é obrigatório.<br>");

            valido = false;

        } else {
            marcarCampo(campoNumero, true);
        }

        if (bairro.isEmpty()) {

            marcarCampo(campoBairro, false);

            erros.append("• <b>Bairro</b> é obrigatório.<br>");

            valido = false;

        } else {
            marcarCampo(campoBairro, true);
        }

        if (cidade == null || cidade.isBlank()) {

            erros.append("• <b>Cidade</b> é obrigatória.<br>");

            valido = false;
        }

        if (listaPedidos.isEmpty()) {

            erros.append("• Adicione pelo menos <b>um pedido</b>.<br>");

            valido = false;
        }

        erros.append("</body></html>");

        if (!valido) {

            JOptionPane.showMessageDialog(
                    this,
                    new JLabel(erros.toString()),
                    "Dados inválidos",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        List<String> cidadesSelecionadas = listaCidades.getSelectedValuesList();

        if (cidadesSelecionadas.isEmpty()) {

            cidadesSelecionadas = new ArrayList<>();
            cidadesSelecionadas.add(cidade);

        } else if (!cidadesSelecionadas.contains(cidade)) {

            cidadesSelecionadas = new ArrayList<>(cidadesSelecionadas);
            cidadesSelecionadas.add(0, cidade);
        }

        boolean confirmado = exibirResumoConfirmacao(
                nome,
                cpf,
                telefone,
                cep,
                rua,
                numero,
                bairro,
                cidade,
                complemento,
                referencia,
                cidadesSelecionadas
        );

        if (!confirmado) return;

        try {

            Endereco endereco = new Endereco(
                    cep,
                    rua,
                    numero,
                    bairro,
                    cidade,
                    complemento,
                    referencia,
                    null,
                    null
            );

            clientesService.criarCliente(
                    nome,
                    cpf,
                    telefone,
                    endereco,
                    listaPedidos,
                    cidadesSelecionadas
            );

            JOptionPane.showMessageDialog(this, "Cliente salvo com sucesso!");

            dispose();

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private boolean isValidDdd(String ddd) {

        try {

            int n = Integer.parseInt(ddd);

            return n >= 11 && n <= 99;

        } catch (NumberFormatException e) {

            return false;
        }
    }
}

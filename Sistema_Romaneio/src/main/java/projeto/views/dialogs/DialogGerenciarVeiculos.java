package projeto.views.dialogs;

import projeto.models.entity.Veiculos;
import projeto.models.services.VeiculosService;
import projeto.views.componentes.JanelaUtil;
import projeto.models.util.BotaoEstilo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DialogGerenciarVeiculos extends JDialog {

    private final VeiculosService veiculosService;
    private final Runnable aoSalvar;

    private JTextField campoPlaca;
    private JTextField campoModelo;
    private JTable tabelaVeiculos;
    private DefaultTableModel modeloTabela;
    private List<Veiculos> veiculos;

    private final Color corFundo = new Color(245, 240, 225);
    private final Color corMarrom = new Color(60, 42, 33);
    private final Color corBege = new Color(220, 198, 150);
    private final Color corBranco = new Color(252, 249, 241);

    public DialogGerenciarVeiculos(JFrame parent, VeiculosService veiculosService, Runnable aoSalvar) {
        super(parent, "Veículos", true);
        this.veiculosService = veiculosService;
        this.aoSalvar = aoSalvar;
        iniciarComponentes();
        carregarVeiculos();
        setResizable(true);
        JanelaUtil.configurarDialog(this, parent, new Dimension(760, 560), new Dimension(640, 460));
        setVisible(true);
    }

    private void iniciarComponentes() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(corFundo);

        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS));
        painelPrincipal.setBackground(corFundo);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("Cadastro de Veículos");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setForeground(corMarrom);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelPrincipal.add(titulo);
        painelPrincipal.add(Box.createVerticalStrut(10));

        campoPlaca = new JTextField();
        campoModelo = new JTextField();
        painelPrincipal.add(criarCampo("Placa comercial:", campoPlaca));
        painelPrincipal.add(criarCampo("Modelo:", campoModelo));
        painelPrincipal.add(Box.createVerticalStrut(10));

        JButton btnCadastrar = BotaoEstilo.aviso(new JButton("Cadastrar veículo"));
        btnCadastrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCadastrar.addActionListener(e -> cadastrar());
        painelPrincipal.add(btnCadastrar);
        painelPrincipal.add(Box.createVerticalStrut(14));

        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Placa comercial", "Modelo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaVeiculos = new JTable(modeloTabela);
        tabelaVeiculos.setRowHeight(28);
        tabelaVeiculos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaVeiculos.setSelectionBackground(new Color(52, 152, 219));
        tabelaVeiculos.setSelectionForeground(Color.WHITE);
        tabelaVeiculos.getTableHeader().setBackground(new Color(239, 218, 186));
        tabelaVeiculos.getTableHeader().setForeground(corMarrom);

        JScrollPane scroll = new JScrollPane(tabelaVeiculos);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setPreferredSize(new Dimension(640, 260));
        painelPrincipal.add(scroll);

        add(painelPrincipal, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.setBackground(corFundo);
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));

        JButton btnExcluir = BotaoEstilo.perigo(new JButton("Excluir selecionado"));
        btnExcluir.addActionListener(e -> excluirSelecionado());

        JButton btnFechar = BotaoEstilo.neutro(new JButton("Fechar"));
        btnFechar.addActionListener(e -> dispose());

        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnFechar);
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
        lbl.setPreferredSize(new Dimension(140, 25));

        campo.setFont(new Font("Arial", Font.PLAIN, 13));
        campo.setBackground(corBranco);

        painel.add(lbl, BorderLayout.WEST);
        painel.add(campo, BorderLayout.CENTER);
        return painel;
    }

    private void cadastrar() {
        try {
            veiculosService.criarVeiculo(campoModelo.getText().trim(), campoPlaca.getText().trim());
            campoModelo.setText("");
            campoPlaca.setText("");
            carregarVeiculos();
            aoSalvar.run();
            JOptionPane.showMessageDialog(this, "Veículo cadastrado com sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarVeiculos() {
        modeloTabela.setRowCount(0);
        veiculos = veiculosService.listarVeiculos();
        for (Veiculos veiculo : veiculos) {
            modeloTabela.addRow(new Object[]{
                    veiculo.getId(),
                    veiculo.getPlaca(),
                    veiculo.getModelo()
            });
        }
    }

    private void excluirSelecionado() {
        int linha = tabelaVeiculos.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um veículo!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Veiculos veiculo = veiculos.get(linha);
        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja excluir este veículo?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION
        );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            veiculosService.deletarVeiculo(veiculo);
            carregarVeiculos();
            aoSalvar.run();
            JOptionPane.showMessageDialog(this, "Veículo excluído com sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}

package projeto.views.dialogs;

import projeto.models.ClientesRomaneio;
import projeto.models.Pedidos;
import projeto.models.Romaneios;
import projeto.services.RomaneiosService;
import projeto.views.telas.TelaGPS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class DialogDetalhesRomaneio extends JDialog {

    private final Romaneios romaneio;
    private final RomaneiosService romaneiosService;
    private Runnable aoAtualizar;

    private final Color corFundo = new Color(245, 240, 225);
    private final Color corMarrom = new Color(60, 42, 33);
    private final Color corBege = new Color(220, 198, 150);
    private final Color corBranco = new Color(252, 249, 241);

    public DialogDetalhesRomaneio(JFrame parent, Romaneios romaneio,
                                  RomaneiosService romaneiosService,
                                  Runnable aoAtualizar) {
        super(parent, "Detalhes do Romaneio", true);
        this.romaneio = romaneio;
        this.romaneiosService = romaneiosService;
        this.aoAtualizar = aoAtualizar;
        setSize(600, 550);
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

        // ===== INFORMAÇÕES DO ROMANEIO =====
        painelPrincipal.add(criarTitulo("Informações do Romaneio"));
        painelPrincipal.add(Box.createVerticalStrut(10));

        String data = romaneio.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String veiculo = romaneio.getVeiculo() != null ? romaneio.getVeiculo().getNomeVeiculo() : "Sem veículo";
        String status = romaneio.getStatus() != null ? romaneio.getStatus() : "SEM ROTA";

        painelPrincipal.add(criarInfo("Data:", data));
        painelPrincipal.add(Box.createVerticalStrut(5));
        painelPrincipal.add(criarInfo("Veículo:", veiculo));
        painelPrincipal.add(Box.createVerticalStrut(5));
        painelPrincipal.add(criarInfo("Status:", status));
        painelPrincipal.add(Box.createVerticalStrut(15));

        // ===== CLIENTES E PEDIDOS =====
        painelPrincipal.add(criarTitulo("Clientes e Pedidos"));
        painelPrincipal.add(Box.createVerticalStrut(10));

        String[] colunas = {"Cliente", "Endereço", "Produto", "Quantidade"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        for (ClientesRomaneio c : romaneio.getClientes()) {
            String endereco = c.getEndereco() != null ?
                    c.getEndereco().getRua() + ", " + c.getEndereco().getNumero() + " - " + c.getEndereco().getBairro()
                    : "Sem endereço";

            if (c.getPedidos().isEmpty()) {
                modelo.addRow(new Object[]{c.getNome_cliente(), endereco, "-", "-"});
            } else {
                for (Pedidos p : c.getPedidos()) {
                    modelo.addRow(new Object[]{
                            c.getNome_cliente(),
                            endereco,
                            p.getNome_produto(),
                            p.getQuantidade()
                    });
                }
            }
        }

        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(28);
        tabela.setFont(new Font("Arial", Font.PLAIN, 13));
        tabela.getTableHeader().setBackground(new Color(239, 218, 186));
        tabela.getTableHeader().setForeground(corMarrom);
        tabela.setGridColor(new Color(200, 200, 200));

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        scroll.getViewport().setBackground(Color.WHITE);
        painelPrincipal.add(scroll);

        JScrollPane scrollPrincipal = new JScrollPane(painelPrincipal);
        scrollPrincipal.setBorder(null);
        scrollPrincipal.getViewport().setBackground(corFundo);
        add(scrollPrincipal, BorderLayout.CENTER);

        // ===== BOTÕES =====
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotoes.setBackground(corFundo);
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));

        JButton btnFechar = new JButton("Fechar");
        btnFechar.setBackground(corBranco);
        btnFechar.setForeground(corMarrom);
        btnFechar.addActionListener(e -> dispose());

        JButton btnIniciarRota = new JButton("Iniciar Rota");
        btnIniciarRota.setBackground(new Color(46, 125, 50));
        btnIniciarRota.setForeground(Color.WHITE);
        btnIniciarRota.setFont(new Font("Arial", Font.BOLD, 13));
        btnIniciarRota.addActionListener(e -> iniciarRota());

        JButton btnGps = new JButton("Abrir GPS");
        btnGps.setBackground(new Color(33, 150, 243));
        btnGps.setForeground(Color.WHITE);
        btnGps.setFont(new Font("Arial", Font.BOLD, 13));
        btnGps.addActionListener(e -> new TelaGPS(romaneio, romaneiosService));

        // Desabilita o botão se já estiver em rota
        if ("EM ROTA".equals(romaneio.getStatus())) {
            btnIniciarRota.setEnabled(false);
            btnIniciarRota.setText("Em Rota");
        }

        painelBotoes.add(btnFechar);
        painelBotoes.add(btnGps);
        painelBotoes.add(btnIniciarRota);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void iniciarRota() {
        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Confirma o início da rota?",
                "Iniciar Rota", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            romaneiosService.atualizarStatus(romaneio, "EM ROTA");
            JOptionPane.showMessageDialog(this, "Rota iniciada!");
            aoAtualizar.run();
            dispose();
        }
    }

    private JPanel criarInfo(String label, String valor) {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        painel.setBackground(corFundo);
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label + " ");
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(corMarrom);

        JLabel val = new JLabel(valor);
        val.setFont(new Font("Arial", Font.PLAIN, 13));
        val.setForeground(corMarrom);

        painel.add(lbl);
        painel.add(val);
        return painel;
    }

    private JLabel criarTitulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.BOLD, 15));
        label.setForeground(corMarrom);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
}

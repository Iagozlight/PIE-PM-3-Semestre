package projeto.views.telas;

import projeto.models.Romaneios;
import projeto.services.RomaneiosService;
import projeto.views.componentes.PainelRodape;
import projeto.views.componentes.PainelTopo;
import projeto.views.componentes.TabelaRomaneios;

import javax.swing.*;
import java.awt.*;

public class TelaRomaneiosAdmin extends JFrame {

    private TabelaRomaneios tabelaRomaneios;
    private PainelTopo painelTopo;
    private PainelRodape painelRodape;
    private RomaneiosService romaneiosService;

    // Construtor para testes
    public TelaRomaneiosAdmin() {
        configurarJanela();
        iniciarComponentes();
        setVisible(true);
    }

    // Construtor real
    public TelaRomaneiosAdmin(RomaneiosService romaneiosService) {
        this.romaneiosService = romaneiosService;
        configurarJanela();
        iniciarComponentes();
        configurarBotoes();
        carregarRomaneios();
        setVisible(true);
    }

    private void configurarJanela() {
        setTitle("DUTRA MÓVEIS - Romaneios");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void iniciarComponentes() {
        painelTopo = new PainelTopo();
        tabelaRomaneios = new TabelaRomaneios();
        painelRodape = new PainelRodape();

        add(painelTopo, BorderLayout.NORTH);
        add(tabelaRomaneios, BorderLayout.CENTER);
        add(painelRodape, BorderLayout.SOUTH);
    }

    private void configurarBotoes() {

        // Deletar
        painelRodape.getBtnDeletar().addActionListener(e -> {
            int linha = tabelaRomaneios.getLinhaSelecionada();
            if (linha == -1) {
                JOptionPane.showMessageDialog(this,
                        "Selecione um romaneio!", "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirmacao = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja deletar?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirmacao == JOptionPane.YES_OPTION) {
                Long id = (Long) tabelaRomaneios.getValorColuna(linha, 0);
                Romaneios romaneio = romaneiosService.buscarPorId(id);
                romaneiosService.deletarRomaneio(romaneio);
                carregarRomaneios();
                JOptionPane.showMessageDialog(this, "Romaneio deletado!");
            }
        });

        painelTopo.getBtnNovoCliente().addActionListener(e -> {
        });

        painelTopo.getBtnNovoRomaneio().addActionListener(e -> {
        });

        painelRodape.getBtnAtribuirVeiculo().addActionListener(e -> {
        });

        painelRodape.getBtnAtribuirMotorista().addActionListener(e -> {
        });
    }

    private void carregarRomaneios() {
        tabelaRomaneios.carregarDados(romaneiosService.listarRomaneios());
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception e) {}
        new TelaRomaneiosAdmin();
    }
}
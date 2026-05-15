package projeto.views.telas;

import jakarta.persistence.EntityManager;
import projeto.models.Romaneios;
import projeto.repositories.*;
import projeto.services.ClientesService;
import projeto.services.RomaneiosService;
import projeto.services.VeiculosService;
import projeto.views.componentes.*;

import javax.swing.*;
import java.awt.*;

public class TelaRomaneiosAdmin extends JFrame {

    private TabelaRomaneios tabelaRomaneios;
    private PainelTopo painelTopo;
    private PainelRodape painelRodape;
    private RomaneiosService romaneiosService;
    private ClientesService clientesService;
    private VeiculosRepository veiculosRepository;
    private MotoristasRepository motoristasRepository;
    private ClientesRomaneioRepository clientesRomaneioRepository;

    public TelaRomaneiosAdmin() {
        configurarJanela();
        iniciarComponentes();
        setVisible(true);
    }

    public TelaRomaneiosAdmin(RomaneiosService romaneiosService,
                              ClientesService clientesService,
                              VeiculosRepository veiculosRepository,
                              MotoristasRepository motoristasRepository,
                              ClientesRomaneioRepository clientesRomaneioRepository) {
        this.romaneiosService = romaneiosService;
        this.clientesService = clientesService;
        this.veiculosRepository = veiculosRepository;
        this.motoristasRepository = motoristasRepository;
        this.clientesRomaneioRepository = clientesRomaneioRepository;
        configurarJanela();
        iniciarComponentes();
        configurarBotoes(clientesService);
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

    private void configurarBotoes(ClientesService clientesService) {

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
            new DialogNovoCliente(this, clientesService);
        });

        painelTopo.getBtnNovoRomaneio().addActionListener(e -> {
            new DialogNovoRomaneio(this, romaneiosService, this::carregarRomaneios);
        });

        painelRodape.getBtnAtribuirVeiculo().addActionListener(e -> {
            int linha = tabelaRomaneios.getLinhaSelecionada();
            if (linha == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um romaneio!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Long id = (Long) tabelaRomaneios.getValorColuna(linha, 0);
            Romaneios romaneio = romaneiosService.buscarPorId(id);
            new DialogAtribuirVeiculo(this, romaneio, romaneiosService, veiculosRepository, this::carregarRomaneios);
        });

        painelRodape.getBtnAtribuirMotorista().addActionListener(e -> {
            int linha = tabelaRomaneios.getLinhaSelecionada();
            if (linha == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um romaneio!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Long id = (Long) tabelaRomaneios.getValorColuna(linha, 0);
            Romaneios romaneio = romaneiosService.buscarPorId(id);
            new DialogAtribuirMotorista(this, romaneio, romaneiosService, motoristasRepository, this::carregarRomaneios);
        });

        painelRodape.getBtnEditar().addActionListener(e -> {
            int linha = tabelaRomaneios.getLinhaSelecionada();
            if (linha == -1) {
                JOptionPane.showMessageDialog(this,
                        "Selecione um romaneio!", "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            Long id = (Long) tabelaRomaneios.getValorColuna(linha, 0);
            Romaneios romaneio = romaneiosService.buscarPorId(id);
            new DialogEditarRomaneio(this, romaneio, romaneiosService,
                    clientesRomaneioRepository, this::carregarRomaneios);
        });
    }

    private void carregarRomaneios() {
        tabelaRomaneios.carregarDados(romaneiosService.listarRomaneios());
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception e) {}

        EntityManager em = CustomizerFactory.getEntityManager();
        RomaneiosRepository romaneiosRepo = new RomaneiosRepository(em);
        ClientesRomaneioRepository clientesRepo = new ClientesRomaneioRepository(em);
        VeiculosRepository veiculosRepo = new VeiculosRepository(em);
        MotoristasRepository motoristasRepo = new MotoristasRepository(em);
        ClientesRomaneioRepository clientesRomaneioRepo = new ClientesRomaneioRepository(em);


        RomaneiosService romaneiosService = new RomaneiosService(romaneiosRepo, clientesRepo);
        ClientesService clientesService = new ClientesService(clientesRepo);

        new TelaRomaneiosAdmin(romaneiosService, clientesService, veiculosRepo, motoristasRepo, clientesRomaneioRepo);
    }
}
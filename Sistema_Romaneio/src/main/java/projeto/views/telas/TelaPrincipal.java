package projeto.views.telas;

import jakarta.persistence.EntityManager;
import projeto.Main;
import projeto.config.FlyWayconfig;
import projeto.models.ClientesRomaneio;
import projeto.models.Motoristas;
import projeto.models.Romaneios;
import projeto.models.Usuarios;
import projeto.repositories.ClientesRomaneioRepository;
import projeto.repositories.CustomizerFactory;
import projeto.repositories.MotoristasRepository;
import projeto.repositories.RomaneiosRepository;
import projeto.repositories.UsuarioRepository;
import projeto.repositories.VeiculosRepository;
import projeto.services.ClientesService;
import projeto.services.RomaneiosService;
import projeto.services.UsuariosService;
import projeto.views.componentes.PainelRodape;
import projeto.views.componentes.PainelTopo;
import projeto.views.componentes.TabelaRomaneios;
import projeto.views.componentes.CursorUtil;
import projeto.views.dialogs.DialogAtribuirMotorista;
import projeto.views.dialogs.DialogAtribuirVeiculo;
import projeto.views.dialogs.DialogDetalhesRomaneio;
import projeto.views.dialogs.DialogEditarRomaneio;
import projeto.views.dialogs.DialogNovoCliente;
import projeto.views.dialogs.DialogNovoRomaneio;
import projeto.views.telas.TelaGPS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TelaPrincipal extends JFrame {

    private static final Color NORMAL     = new Color(0xF5F0E0);
    private static final Color LOGO       = new Color(0xF9F6ED);
    private static final Color ESCURO     = new Color(0x9A6F35);
    private static final Color TEXTO      = new Color(0x361C12);
    private static final Color FUNDO_CARD = Color.WHITE;

    private final CardLayout cardLayout  = new CardLayout();
    private final JPanel     painelCards = new JPanel(cardLayout);

    // ── Apenas 3 botões visíveis na barra ──
    private final JButton     btnRomaneios  = new JButton("Romaneios");
    private final JButton     btnUsuarios   = new JButton("Usuários ▾");
    private final JButton     btnLogout     = new JButton("Sair");

    // Popup que aparece ao clicar em "Usuários ▾"
    private final JPopupMenu  menuUsuarios  = new JPopupMenu();

    // btnLogin usado internamente (mostrarLogin)
    private final JButton btnLogin = new JButton("Login");

    private final JLabel lblStatus = new JLabel("Nenhuma sessao ativa");

    // Campos de formulários
    private final JTextField    campoLoginUsuario              = new JTextField(18);
    private final JPasswordField campoLoginSenha               = new JPasswordField(18);

    private final JTextField    campoAlterarUsuario            = new JTextField(18);
    private final JPasswordField campoAlterarSenhaAtual        = new JPasswordField(18);
    private final JPasswordField campoAlterarSenhaNova         = new JPasswordField(18);

    private final JTextField    campoNovoUsuario               = new JTextField(18);
    private final JPasswordField campoNovoUsuarioSenha         = new JPasswordField(18);
    private final JPasswordField campoNovoUsuarioSenhaConfirmacao = new JPasswordField(18);

    private final JTextField    campoNovoMotoristaNome         = new JTextField(18);
    private final JTextField    campoNovoMotoristaData         = new JTextField(18);
    private final JTextField    campoNovoMotoristaUsuario      = new JTextField(18);

    private final JTextField    campoRemoverUsuario            = new JTextField(18);
    private final JTextField    campoRemoverConfirmacao        = new JTextField(18);

    private DefaultTableModel modeloUsuarios;
    private JTable            tabelaUsuarios;
    private TabelaRomaneios   tabelaRomaneiosAdmin;
    private JTable            tabelaRomaneiosMotorista;
    private DefaultTableModel modeloRomaneiosMotorista;

    private JPanel painelLogin;
    private JPanel painelRomaneiosAdmin;
    private JPanel painelRomaneiosMotorista;
    private JPanel painelMenuUsuarios;
    private JPanel painelAlterarSenha;
    private JPanel painelExibirUsuarios;
    private JPanel painelNovoUsuario;
    private JPanel painelNovoMotorista;
    private JPanel painelRemoverUsuario;

    private EntityManager              entityManager;
    private UsuarioRepository          usuarioRepository;
    private MotoristasRepository       motoristasRepository;
    private RomaneiosRepository        romaneiosRepository;
    private ClientesRomaneioRepository clientesRomaneioRepository;
    private VeiculosRepository         veiculosRepository;
    private UsuariosService            usuariosService;
    private ClientesService            clientesService;
    private RomaneiosService           romaneiosService;

    private Main.SessaoUsuario sessaoAtual;

    public TelaPrincipal() {
        super("DUTRA MOVEIS");
        inicializarInfraestrutura();
        construirInterface();
        atualizarAcesso(null);
        setVisible(true);
    }

    // =========================================================
    // INFRAESTRUTURA
    // =========================================================

    private void inicializarInfraestrutura() {
        FlyWayconfig.migrate();
        entityManager              = CustomizerFactory.getEntityManager();
        usuarioRepository          = new UsuarioRepository(entityManager);
        motoristasRepository       = new MotoristasRepository(entityManager);
        romaneiosRepository        = new RomaneiosRepository(entityManager);
        clientesRomaneioRepository = new ClientesRomaneioRepository(entityManager);
        veiculosRepository         = new VeiculosRepository(entityManager);
        usuariosService            = new UsuariosService(usuarioRepository, motoristasRepository);
        clientesService            = new ClientesService(clientesRomaneioRepository);
        romaneiosService           = new RomaneiosService(romaneiosRepository, clientesRomaneioRepository);
    }

    // =========================================================
    // CONSTRUÇÃO DA INTERFACE
    // =========================================================

    private void construirInterface() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1180, 760);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(NORMAL);
        setarIcone();
        setCursor(CursorUtil.carregar("/view/icons/cursor.png"));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(construirTopo());
        header.add(construirNavegacao());

        painelCards.setBackground(NORMAL);
        painelCards.add(construirPainelLogin(),              "LOGIN");
        painelCards.add(construirPainelRomaneiosAdmin(),     "ROMANEIOS_ADMIN");
        painelCards.add(construirPainelRomaneiosMotorista(), "ROMANEIOS_MOTORISTA");
        painelCards.add(construirPainelMenuUsuarios(),       "MENU_USUARIOS");
        painelCards.add(construirPainelAlterarSenha(),       "ALTERAR_SENHA");
        painelCards.add(construirPainelExibirUsuarios(),     "EXIBIR_USUARIOS");
        painelCards.add(construirPainelNovoUsuario(),        "NOVO_USUARIO");
        painelCards.add(construirPainelNovoMotorista(),      "NOVO_MOTORISTA");
        painelCards.add(construirPainelRemoverUsuario(),     "REMOVER_USUARIO");

        add(header,      BorderLayout.NORTH);
        add(painelCards, BorderLayout.CENTER);

        configurarAcoes();
        mostrarLogin();
    }

    // ── Topo (logo + status) ──────────────────────────────────
    private JPanel construirTopo() {
        JPanel painelLogo = new JPanel(new BorderLayout());
        painelLogo.setBackground(LOGO);
        painelLogo.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel logoTexto = new JLabel("DUTRA MOVEIS", SwingConstants.CENTER);
        logoTexto.setForeground(TEXTO);
        logoTexto.setFont(new Font("Segoe UI", Font.BOLD, 28));

        lblStatus.setForeground(TEXTO);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblStatus.setHorizontalAlignment(SwingConstants.RIGHT);

        painelLogo.add(logoTexto, BorderLayout.CENTER);
        painelLogo.add(lblStatus, BorderLayout.EAST);
        return painelLogo;
    }

    // ── Barra de navegação: apenas 3 botões ──────────────────
    private JPanel construirNavegacao() {
        JPanel faixa = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        faixa.setBackground(NORMAL);
        faixa.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

        // Itens do popup de Usuários
        menuUsuarios.add(itemMenu("Novo Usuário",    "NOVO_USUARIO"));
        menuUsuarios.add(itemMenu("Novo Motorista",  "NOVO_MOTORISTA"));
        menuUsuarios.add(itemMenu("Exibir Usuários", "EXIBIR_USUARIOS"));
        menuUsuarios.add(itemMenu("Alterar Senha",   "ALTERAR_SENHA"));
        menuUsuarios.add(itemMenu("Remover Usuário", "REMOVER_USUARIO"));

        // Abre o popup abaixo do botão
        btnUsuarios.addActionListener(e ->
                menuUsuarios.show(btnUsuarios, 0, btnUsuarios.getHeight()));

        faixa.add(btnRomaneios);
        faixa.add(btnUsuarios);
        faixa.add(btnLogout);

        return faixa;
    }

    /** Cria um JMenuItem que navega para o card indicado. */
    private JMenuItem itemMenu(String texto, String card) {
        JMenuItem item = new JMenuItem(texto);
        item.addActionListener(e -> abrirCard(card));
        return item;
    }

    // =========================================================
    // PAINÉIS (cards)
    // =========================================================

    private JPanel construirPainelLogin() {
        painelLogin = new JPanel(new GridBagLayout());
        painelLogin.setBackground(FUNDO_CARD);
        painelLogin.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        GridBagConstraints c = baseConstraints();
        c.gridy = 0;
        c.gridwidth = 2;

        JLabel titulo = new JLabel("Acesso ao Sistema");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        painelLogin.add(titulo, c);

        c.gridwidth = 1;
        c.gridy++; c.gridx = 0; painelLogin.add(new JLabel("Usuario"), c);
        c.gridx = 1;            painelLogin.add(campoLoginUsuario, c);

        c.gridy++; c.gridx = 0; painelLogin.add(new JLabel("Senha"), c);
        c.gridx = 1;            painelLogin.add(campoLoginSenha, c);

        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.addActionListener(e -> autenticarUsuario());

        c.gridy++; c.gridx = 0; c.gridwidth = 2;
        painelLogin.add(btnEntrar, c);
        return painelLogin;
    }

    private JPanel construirPainelRomaneiosAdmin() {
        painelRomaneiosAdmin = new JPanel(new BorderLayout(8, 8));
        painelRomaneiosAdmin.setBackground(FUNDO_CARD);
        painelRomaneiosAdmin.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        PainelTopo topo              = new PainelTopo();
        tabelaRomaneiosAdmin         = new TabelaRomaneios();
        PainelRodape rodape          = new PainelRodape();

        painelRomaneiosAdmin.add(topo,                BorderLayout.NORTH);
        painelRomaneiosAdmin.add(tabelaRomaneiosAdmin, BorderLayout.CENTER);
        painelRomaneiosAdmin.add(rodape,              BorderLayout.SOUTH);

        topo.getBtnNovoCliente().addActionListener(e ->
                new DialogNovoCliente(this, clientesService));
        topo.getBtnNovoRomaneio().addActionListener(e ->
                new DialogNovoRomaneio(this, romaneiosService, this::carregarRomaneiosAdmin));

        rodape.getBtnDeletar().addActionListener(e -> {
            Romaneios r = romaneioSelecionadoDaTabelaAdmin();
            if (r == null) return;
            int ok = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deletar?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                romaneiosService.deletarRomaneio(r);
                carregarRomaneiosAdmin();
                JOptionPane.showMessageDialog(this, "Romaneio deletado!");
            }
        });

        rodape.getBtnAtribuirVeiculo().addActionListener(e -> {
            Romaneios r = romaneioSelecionadoDaTabelaAdmin();
            if (r != null)
                new DialogAtribuirVeiculo(this, r, romaneiosService, veiculosRepository, this::carregarRomaneiosAdmin);
        });

        rodape.getBtnAtribuirMotorista().addActionListener(e -> {
            Romaneios r = romaneioSelecionadoDaTabelaAdmin();
            if (r != null)
                new DialogAtribuirMotorista(this, r, romaneiosService, motoristasRepository, this::carregarRomaneiosAdmin);
        });

        rodape.getBtnEditar().addActionListener(e -> {
            Romaneios r = romaneioSelecionadoDaTabelaAdmin();
            if (r != null)
                new DialogEditarRomaneio(this, r, romaneiosService, clientesRomaneioRepository, this::carregarRomaneiosAdmin);
        });

        rodape.getBtnGps().addActionListener(e -> {
            Romaneios r = romaneioSelecionadoDaTabelaAdmin();
            if (r != null) {
                new TelaGPS(r, romaneiosService, sessaoAtual);
            }
        });

        return painelRomaneiosAdmin;
    }

    private JPanel construirPainelRomaneiosMotorista() {
        painelRomaneiosMotorista = new JPanel(new BorderLayout(8, 8));
        painelRomaneiosMotorista.setBackground(FUNDO_CARD);
        painelRomaneiosMotorista.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        JLabel titulo = new JLabel("Romaneios do Motorista");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        topo.add(titulo, BorderLayout.WEST);

        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.addActionListener(e -> carregarRomaneiosMotorista());
        topo.add(btnAtualizar, BorderLayout.EAST);
        painelRomaneiosMotorista.add(topo, BorderLayout.NORTH);

        modeloRomaneiosMotorista = new DefaultTableModel(
                new Object[]{"ID", "Data", "Veiculo", "Status"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabelaRomaneiosMotorista = new JTable(modeloRomaneiosMotorista);
        tabelaRomaneiosMotorista.setRowHeight(28);
        tabelaRomaneiosMotorista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        painelRomaneiosMotorista.add(new JScrollPane(tabelaRomaneiosMotorista), BorderLayout.CENTER);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rodape.setOpaque(false);
        JButton btnDetalhes = new JButton("Ver Detalhes");
        btnDetalhes.addActionListener(e -> {
            Romaneios r = romaneioSelecionadoDaTabelaMotorista();
            if (r != null)
                new DialogDetalhesRomaneio(this, r, romaneiosService, this::carregarRomaneiosMotorista, sessaoAtual);
        });
        JButton btnGps = new JButton("GPS");
        btnGps.setBackground(new Color(33, 150, 243));
        btnGps.setForeground(Color.WHITE);
        btnGps.addActionListener(e -> {
            Romaneios r = romaneioSelecionadoDaTabelaMotorista();
            if (r != null) {
                new TelaGPS(r, romaneiosService, sessaoAtual);
            }
        });
        rodape.add(btnDetalhes);
        rodape.add(btnGps);
        painelRomaneiosMotorista.add(rodape, BorderLayout.SOUTH);
        return painelRomaneiosMotorista;
    }

    private JPanel construirPainelMenuUsuarios() {
        painelMenuUsuarios = new JPanel(new GridLayout(0, 1, 8, 8));
        painelMenuUsuarios.setBackground(FUNDO_CARD);
        painelMenuUsuarios.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel titulo = new JLabel("Menu Usuarios");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        painelMenuUsuarios.add(titulo);
        painelMenuUsuarios.add(botaoMenu("Novo Usuario",      "NOVO_USUARIO"));
        painelMenuUsuarios.add(botaoMenu("Novo Motorista",    "NOVO_MOTORISTA"));
        painelMenuUsuarios.add(botaoMenu("Exibir Usuarios",   "EXIBIR_USUARIOS"));
        painelMenuUsuarios.add(botaoMenu("Alterar Senha",     "ALTERAR_SENHA"));
        painelMenuUsuarios.add(botaoMenu("Remover Usuarios",  "REMOVER_USUARIO"));
        return painelMenuUsuarios;
    }

    private JPanel construirPainelAlterarSenha() {
        return painelAlterarSenha = construirFormularioSimples(
                "Alterar Senha",
                new String[]{"Usuario", "Senha atual", "Nova senha"},
                new JTextField[]{campoAlterarUsuario},
                new JPasswordField[]{campoAlterarSenhaAtual, campoAlterarSenhaNova},
                this::executarAlterarSenha);
    }

    private JPanel construirPainelNovoUsuario() {
        return painelNovoUsuario = construirFormularioSimples(
                "Novo Usuario",
                new String[]{"Usuario", "Senha", "Confirmar senha"},
                new JTextField[]{campoNovoUsuario},
                new JPasswordField[]{campoNovoUsuarioSenha, campoNovoUsuarioSenhaConfirmacao},
                this::executarNovoUsuario);
    }

    private JPanel construirPainelNovoMotorista() {
        return painelNovoMotorista = construirFormularioSimples(
                "Novo Motorista",
                new String[]{"Nome", "Data nascimento (dd/MM/yyyy)", "Usuario"},
                new JTextField[]{campoNovoMotoristaNome, campoNovoMotoristaData, campoNovoMotoristaUsuario},
                new JPasswordField[0],
                this::executarNovoMotorista);
    }

    private JPanel construirPainelRemoverUsuario() {
        return painelRemoverUsuario = construirFormularioSimples(
                "Remover Usuario",
                new String[]{"Usuario", "Repita o usuario"},
                new JTextField[]{campoRemoverUsuario, campoRemoverConfirmacao},
                new JPasswordField[0],
                this::executarRemoverUsuario);
    }

    private JPanel construirPainelExibirUsuarios() {
        painelExibirUsuarios = new JPanel(new BorderLayout(8, 8));
        painelExibirUsuarios.setBackground(FUNDO_CARD);
        painelExibirUsuarios.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        JLabel titulo = new JLabel("Usuarios");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        topo.add(titulo, BorderLayout.WEST);

        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.addActionListener(e -> carregarUsuariosTabela());
        topo.add(btnAtualizar, BorderLayout.EAST);
        painelExibirUsuarios.add(topo, BorderLayout.NORTH);

        modeloUsuarios = new DefaultTableModel(new Object[]{"Usuario", "Perfil"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabelaUsuarios = new JTable(modeloUsuarios);
        tabelaUsuarios.setRowHeight(28);
        painelExibirUsuarios.add(new JScrollPane(tabelaUsuarios), BorderLayout.CENTER);
        return painelExibirUsuarios;
    }

    // UTILITÁRIOS DE CONSTRUÇÃO

    private JPanel construirFormularioSimples(String tituloTexto,
                                              String[] rotulos,
                                              JTextField[] camposTexto,
                                              JPasswordField[] camposSenha,
                                              Runnable acaoSalvar) {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(FUNDO_CARD);
        painel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        GridBagConstraints c = baseConstraints();
        c.gridy = 0; c.gridwidth = 2;

        JLabel titulo = new JLabel(tituloTexto);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        painel.add(titulo, c);

        c.gridwidth = 1;
        int it = 0, is = 0;
        for (int i = 0; i < rotulos.length; i++) {
            c.gridy++; c.gridx = 0; painel.add(new JLabel(rotulos[i]), c);
            c.gridx = 1;
            painel.add(i < camposTexto.length ? camposTexto[it++] : camposSenha[is++], c);
        }

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botoes.setOpaque(false);
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> acaoSalvar.run());
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.addActionListener(e -> mostrarCardPadrao());
        botoes.add(btnSalvar);
        botoes.add(btnVoltar);

        c.gridy++; c.gridx = 0; c.gridwidth = 2;
        painel.add(botoes, c);
        return painel;
    }

    private GridBagConstraints baseConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets  = new Insets(8, 8, 8, 8);
        c.anchor  = GridBagConstraints.WEST;
        c.fill    = GridBagConstraints.HORIZONTAL;
        return c;
    }

    private JButton botaoMenu(String texto, String card) {
        JButton b = new JButton(texto);
        b.addActionListener(e -> abrirCard(card));
        return b;
    }

    // AÇÕES

    private void configurarAcoes() {
        btnRomaneios.addActionListener(e -> mostrarCardPadrao());
        btnLogout.addActionListener(e -> encerrarOuLogout());
        campoLoginSenha.addActionListener(e -> autenticarUsuario());
    }

    private void autenticarUsuario() {
        Main.SessaoUsuario sessao = usuariosService.autenticar(
                campoLoginUsuario.getText().trim(),
                new String(campoLoginSenha.getPassword())
        );
        if (sessao == null) {
            JOptionPane.showMessageDialog(this, "Usuario ou senha incorretos!");
            return;
        }
        atualizarAcesso(sessao);
        if (sessao.isAdmin()) {
            mostrarCard("ROMANEIOS_ADMIN");
            carregarRomaneiosAdmin();
        } else {
            mostrarCard("ROMANEIOS_MOTORISTA");
            carregarRomaneiosMotorista();
        }
    }

    private void atualizarAcesso(Main.SessaoUsuario sessao) {
        sessaoAtual = sessao;

        boolean logado = sessaoAtual != null;
        boolean admin  = logado && sessaoAtual.isAdmin();

        lblStatus.setText(logado
                ? "Sessão ativa: " + sessaoAtual.getUsuario().getUsuario()
                  + (admin ? " [ADMIN]" : " [MOTORISTA]")
                : "Nenhuma sessão ativa");

        btnRomaneios.setEnabled(logado);
        btnUsuarios.setEnabled(admin);        // popup só para admin
        btnLogout.setText(logado ? "Logout" : "Sair");

        campoLoginUsuario.setText("");
        campoLoginSenha.setText("");
    }

    private void mostrarLogin() {
        mostrarCard("LOGIN");
        campoLoginUsuario.requestFocusInWindow();
    }

    private void mostrarCardPadrao() {
        if (sessaoAtual == null) { mostrarLogin(); return; }
        if (sessaoAtual.isAdmin()) {
            mostrarCard("ROMANEIOS_ADMIN");
            carregarRomaneiosAdmin();
        } else {
            mostrarCard("ROMANEIOS_MOTORISTA");
            carregarRomaneiosMotorista();
        }
    }

    private void abrirCard(String card) {
        if (sessaoAtual == null && !"LOGIN".equals(card)) { mostrarLogin(); return; }
        if (!sessaoAtual.isAdmin()
                && !"LOGIN".equals(card)
                && !"ROMANEIOS_MOTORISTA".equals(card)) {
            JOptionPane.showMessageDialog(this, "Acesso negado para este perfil.");
            mostrarCard("ROMANEIOS_MOTORISTA");
            return;
        }
        mostrarCard(card);
        if      ("EXIBIR_USUARIOS".equals(card))      carregarUsuariosTabela();
        else if ("ROMANEIOS_ADMIN".equals(card))       carregarRomaneiosAdmin();
        else if ("ROMANEIOS_MOTORISTA".equals(card))   carregarRomaneiosMotorista();
    }

    private void mostrarCard(String card) {
        cardLayout.show(painelCards, card);
    }

    private void encerrarOuLogout() {
        if (sessaoAtual == null) { dispose(); return; }
        atualizarAcesso(null);
        mostrarLogin();
    }


    // EXECUÇÃO DOS FORMULÁRIOS

    private void executarAlterarSenha() {
        try {
            usuariosService.alterarSenha(
                    campoAlterarUsuario.getText().trim(),
                    new String(campoAlterarSenhaAtual.getPassword()),
                    new String(campoAlterarSenhaNova.getPassword()));
            limparFormularioAlterarSenha();
            JOptionPane.showMessageDialog(this, "Senha alterada com sucesso!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }

    private void executarNovoUsuario() {
        String senha       = new String(campoNovoUsuarioSenha.getPassword());
        String confirmacao = new String(campoNovoUsuarioSenhaConfirmacao.getPassword());
        if (!senha.equals(confirmacao)) {
            JOptionPane.showMessageDialog(this, "As senhas nao conferem.");
            return;
        }
        try {
            usuariosService.criarUsuario(campoNovoUsuario.getText().trim(), senha);
            limparFormularioNovoUsuario();
            carregarUsuariosTabela();
            JOptionPane.showMessageDialog(this, "Usuario cadastrado com sucesso!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }

    private void executarNovoMotorista() {
        try {
            LocalDate data = LocalDate.parse(
                    campoNovoMotoristaData.getText().trim(),
                    DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            usuariosService.criarMotorista(
                    campoNovoMotoristaNome.getText().trim(),
                    data,
                    campoNovoMotoristaUsuario.getText().trim());
            limparFormularioNovoMotorista();
            JOptionPane.showMessageDialog(this, "Motorista cadastrado com sucesso!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }

    private void executarRemoverUsuario() {
        String usuario    = campoRemoverUsuario.getText().trim();
        String confirmacao = campoRemoverConfirmacao.getText().trim();
        if (!usuario.equals(confirmacao)) {
            JOptionPane.showMessageDialog(this, "Os campos nao sao iguais!");
            return;
        }
        try {
            usuariosService.removerUsuario(usuario);
            limparFormularioRemoverUsuario();
            carregarUsuariosTabela();
            JOptionPane.showMessageDialog(this, "Usuario removido com sucesso!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }

    // =========================================================
    // CARREGAMENTO DE DADOS
    // =========================================================

    private void carregarUsuariosTabela() {
        if (modeloUsuarios == null) return;
        modeloUsuarios.setRowCount(0);

        Set<Long> motoristaIds = new HashSet<>();
        for (Motoristas m : motoristasRepository.findAll()) {
            if (m.getUsuarios() != null && m.getUsuarios().getId() != null)
                motoristaIds.add(m.getUsuarios().getId());
        }
        for (Usuarios u : usuariosService.listarUsuarios()) {
            String perfil = u.temPermissao("ADMIN") || "admin".equalsIgnoreCase(u.getUsuario())
                    ? "ADMIN"
                    : (motoristaIds.contains(u.getId()) ? "MOTORISTA" : "USUARIO");
            modeloUsuarios.addRow(new Object[]{u.getUsuario(), perfil});
        }
    }

    private void carregarRomaneiosAdmin() {
        if (tabelaRomaneiosAdmin != null)
            tabelaRomaneiosAdmin.carregarDados(romaneiosService.listarRomaneios());
    }

    private void carregarRomaneiosMotorista() {
        if (modeloRomaneiosMotorista == null || sessaoAtual == null) return;
        modeloRomaneiosMotorista.setRowCount(0);
        Motoristas motorista = sessaoAtual.getMotorista();
        if (motorista == null && sessaoAtual.isAdmin()) return;
        List<Romaneios> romaneios = motorista == null
                ? List.of()
                : romaneiosService.listarRomaneiosPorMotorista(motorista);
        for (Romaneios r : romaneios) {
            modeloRomaneiosMotorista.addRow(new Object[]{
                    r.getId(),
                    r.getData(),
                    r.getVeiculo() != null ? r.getVeiculo().getNomeVeiculo() : "Sem veiculo",
                    r.getStatus()
            });
        }
    }

    // =========================================================
    // SELEÇÃO DE LINHA NAS TABELAS
    // =========================================================

    private Romaneios romaneioSelecionadoDaTabelaAdmin() {
        if (tabelaRomaneiosAdmin == null) return null;
        int linha = tabelaRomaneiosAdmin.getLinhaSelecionada();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um romaneio!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return romaneiosService.buscarPorId((Long) tabelaRomaneiosAdmin.getValorColuna(linha, 0));
    }

    private Romaneios romaneioSelecionadoDaTabelaMotorista() {
        if (tabelaRomaneiosMotorista == null) return null;
        int linha = tabelaRomaneiosMotorista.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um romaneio!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return romaneiosService.buscarPorId((Long) modeloRomaneiosMotorista.getValueAt(linha, 0));
    }

    // =========================================================
    // LIMPEZA DE FORMULÁRIOS
    // =========================================================

    private void limparFormularioAlterarSenha() {
        campoAlterarUsuario.setText("");
        campoAlterarSenhaAtual.setText("");
        campoAlterarSenhaNova.setText("");
    }

    private void limparFormularioNovoUsuario() {
        campoNovoUsuario.setText("");
        campoNovoUsuarioSenha.setText("");
        campoNovoUsuarioSenhaConfirmacao.setText("");
    }

    private void limparFormularioNovoMotorista() {
        campoNovoMotoristaNome.setText("");
        campoNovoMotoristaData.setText("");
        campoNovoMotoristaUsuario.setText("");
    }

    private void limparFormularioRemoverUsuario() {
        campoRemoverUsuario.setText("");
        campoRemoverConfirmacao.setText("");
    }

    // =========================================================
    // ÍCONE
    // =========================================================

    private void setarIcone() {
        URL iconUrl = getClass().getResource("/view/icons/supplies.png");
        if (iconUrl != null) setIconImage(new ImageIcon(iconUrl).getImage());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new TelaPrincipal();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, e.getMessage(),
                        "Erro ao iniciar", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

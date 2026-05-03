package projeto;

import jakarta.persistence.EntityManager;
import projeto.config.FlyWayconfig;
import projeto.models.*;
import projeto.repositories.*;
import projeto.services.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    // ==================== ROMANEIOS ====================

    static void novoRomaneio(RomaneiosService romaneiosService,
                             ClientesService clientesService,
                             ClientesRomaneioRepository clientesRomaneioRepository,
                             RomaneiosRepository romaneiosRepository) {
        Scanner scanner = new Scanner(System.in);
        String escolha;

        do {
            System.out.println("\t====(Submenu)Novo Romaneio====\n");
            System.out.println("(1) - Cadastrar Cliente");
            System.out.println("(2) - Cadastrar Romaneio");
            System.out.println("(0) - Voltar");
            escolha = scanner.nextLine();

            switch (escolha) {
                case "1":
                    cadastrarCliente(clientesService);
                    break;
                case "2":
                    cadastrarRomaneio(romaneiosService, clientesRomaneioRepository, romaneiosRepository, scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Opção Inválida!");
            }
        } while (!escolha.equals("0"));
    }

    static void verRomaneios(RomaneiosService romaneiosService,
                             VeiculosRepository veiculosRepository,
                             MotoristasRepository motoristasRepository) {
        Scanner scanner = new Scanner(System.in);

        List<Romaneios> romaneios = romaneiosService.listarRomaneios();

        if (romaneios.isEmpty()) {
            System.out.println("Nenhum Romaneio encontrado!");
            return;
        }

        for (int i = 0; i < romaneios.size(); i++) {
            System.out.println("(" + i + ") - " + romaneios.get(i).getData());
        }

        System.out.println("Selecione um romaneio (ou Q para voltar): ");
        String opcao = scanner.nextLine();

        while (!opcao.equals("Q") && !opcao.equals("q")) {
            try {
                int index = Integer.parseInt(opcao);

                if (index < 0 || index >= romaneios.size()) {
                    System.out.println("Número inválido! Escolha entre 0 e " + (romaneios.size() - 1));
                } else {
                    Romaneios romaneioSelecionado = romaneios.get(index);
                    System.out.println("\n" + romaneioSelecionado);

                    String escolha;
                    do {
                        System.out.println("\n==Opções==");
                        System.out.println("(1) - Atribuir Veículo");
                        System.out.println("(2) - Atribuir Motorista");
                        System.out.println("(3) - Deletar Romaneio");
                        System.out.println("(0) - Voltar");
                        escolha = scanner.nextLine();

                        switch (escolha) {
                            case "1":
                                List<Veiculos> veiculos = veiculosRepository.findAll();
                                if (veiculos.isEmpty()) {
                                    System.out.println("Nenhum veículo cadastrado!");
                                    break;
                                }
                                for (int i = 0; i < veiculos.size(); i++) {
                                    System.out.println("(" + i + ") - " + veiculos.get(i).getNomeVeiculo());
                                }
                                System.out.println("Selecione um veículo: ");
                                int indexVeiculo = Integer.parseInt(scanner.nextLine());
                                Veiculos veiculoSelecionado = veiculos.get(indexVeiculo);
                                String msgVeiculo = romaneiosService.atribuirVeiculo(romaneioSelecionado, veiculoSelecionado);
                                System.out.println(msgVeiculo);
                                break;

                            case "2":
                                List<Motoristas> motoristas = motoristasRepository.findAll();
                                if (motoristas.isEmpty()) {
                                    System.out.println("Nenhum motorista cadastrado!");
                                    break;
                                }
                                for (int i = 0; i < motoristas.size(); i++) {
                                    System.out.println("(" + i + ") - " + motoristas.get(i).getNome());
                                }
                                System.out.println("Selecione um motorista: ");
                                int indexMotorista = Integer.parseInt(scanner.nextLine());
                                Motoristas motoristaSelecionado = motoristas.get(indexMotorista);
                                String msgMotorista = romaneiosService.atribuirMotorista(romaneioSelecionado, motoristaSelecionado);
                                System.out.println(msgMotorista);
                                break;

                            case "3":
                                romaneiosService.deletarRomaneio(romaneioSelecionado);
                                System.out.println("Romaneio deletado!");
                                return;
                            case "0":
                                break;
                            default:
                                System.out.println("Opção Inválida!");
                        }
                    } while (!escolha.equals("0"));
                }

            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido ou Q para voltar!");
            }

            System.out.println("Selecione um romaneio (ou Q para voltar): ");
            opcao = scanner.nextLine();
        }
    }

    static void cadastrarCliente(ClientesService clientesService) {
        Scanner scanner = new Scanner(System.in);
        String confirmar;

        System.out.println("=====Cadastrar Cliente======");
        System.out.println("Nome do Cliente: ");
        String nomeCliente = scanner.nextLine();
        System.out.println("CPF: ");
        String cpfCliente = scanner.nextLine();
        System.out.println("Dados do Cliente Confirmado!");

        Endereco endereco = Endereco.lerEndereco(scanner);

        List<Pedidos> listaPedidos = new ArrayList<>();

        String opcao;
        do {
            System.out.println("==Pedido==");
            System.out.println("Nome do Produto: ");
            String nomeProduto = scanner.nextLine();
            System.out.println("Quantidade adquirida do Produto: ");
            String quantidadeProduto = scanner.nextLine();

            Pedidos pedido = new Pedidos(null, nomeProduto, quantidadeProduto);

            System.out.println("Confirme os dados abaixo:");
            System.out.println(pedido);
            System.out.println("Confirmar?(s/n): ");
            confirmar = scanner.nextLine();

            if (confirmar.equals("s") || confirmar.equals("S")) {
                System.out.println("Pedido adicionado com Sucesso!");
                listaPedidos.add(pedido);
            } else if (confirmar.equals("n") || confirmar.equals("N")) {
                System.out.println("Pedido cancelado!");
            } else {
                System.out.println("Opção inválida!");
            }

            System.out.println("Adicionar mais um pedido?(s/n): ");
            opcao = scanner.nextLine();

        } while (!opcao.equals("n") && !opcao.equals("N"));

        try {
            clientesService.criarCliente(nomeCliente, cpfCliente, endereco, listaPedidos);
            System.out.println("Cliente salvo com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    static void cadastrarRomaneio(RomaneiosService romaneiosService,
                                  ClientesRomaneioRepository clientesRomaneioRepository,
                                  RomaneiosRepository romaneiosRepository,
                                  Scanner scanner) {
        LocalDate dataRomaneio = null;

        System.out.println("==Cadastrar Romaneio==");
        while (dataRomaneio == null) {
            try {
                System.out.println("Data do Romaneio(dd/MM/yyyy): ");
                String dataString = scanner.nextLine();
                dataRomaneio = LocalDate.parse(dataString, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida! Use o formato correto: dd/MM/yyyy");
            }
        }

        List<ClientesRomaneio> clientesSemRomaneio = romaneiosService.listarClientesSemRomaneio();

        if (clientesSemRomaneio.isEmpty()) {
            System.out.println("Nenhum cliente disponível para adicionar!");
            return;
        }

        for (int i = 0; i < clientesSemRomaneio.size(); i++) {
            System.out.println("(" + i + ") - " + clientesSemRomaneio.get(i).getNome_cliente());
        }

        System.out.println("Digite o número do cliente para adicionar (ou Q para finalizar): ");
        String opcao = scanner.nextLine();

        List<ClientesRomaneio> clientesSelecionados = new ArrayList<>();

        while (!opcao.equals("Q") && !opcao.equals("q")) {
            try {
                int index = Integer.parseInt(opcao);
                if (index < 0 || index >= clientesSemRomaneio.size()) {
                    System.out.println("Número inválido! Escolha entre 0 e " + (clientesSemRomaneio.size() - 1));
                } else {
                    ClientesRomaneio cliente = clientesSemRomaneio.get(index);
                    clientesSelecionados.add(cliente);
                    System.out.println("Cliente " + cliente.getNome_cliente() + " adicionado!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido ou Q para finalizar!");
            }
            System.out.println("Adicionar mais um cliente? (ou Q para finalizar): ");
            opcao = scanner.nextLine();
        }

        romaneiosService.criarRomaneio(dataRomaneio, clientesSelecionados);
        System.out.println("Romaneio criado com sucesso!");
    }

    static void cadastrarVeiculo(VeiculosService veiculosService, Scanner sc) {
        System.out.println("=== Cadastro de Veículo ===");
        System.out.println("Nome do veículo: ");
        String nome = sc.nextLine();
        System.out.println("Placa (ex: ABC1D23): ");
        String placa = sc.nextLine();

        try {
            veiculosService.criarVeiculo(nome, placa);
            System.out.println("Veículo cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    // ==================== ROMANEIOS MENU ====================

    public static void romaneios(Scanner sc, SessaoUsuario sessao) {
        Boolean rodando = true;
        EntityManager em = CustomizerFactory.getEntityManager();

        RomaneiosRepository romaneiosRepository = new RomaneiosRepository(em);
        ClientesRomaneioRepository clientesRomaneioRepository = new ClientesRomaneioRepository(em);
        PedidosRepository pedidosRepository = new PedidosRepository(em);
        VeiculosRepository veiculosRepository = new VeiculosRepository(em);
        MotoristasRepository motoristasRepository = new MotoristasRepository(em);

        RomaneiosService romaneiosService = new RomaneiosService(romaneiosRepository, clientesRomaneioRepository);
        ClientesService clientesService = new ClientesService(clientesRomaneioRepository);
        VeiculosService veiculosService = new VeiculosService(veiculosRepository);

        do {
            System.out.println("======================================\n");
            System.out.println("\t\tDUTRA MOVEIS(romaneios)");
            System.out.println("\n======================================");
            System.out.println("Selecione uma das opções:");
            System.out.println("(1) - Ver Romaneios");
            if (sessao.isAdmin()) {
                System.out.println("(2) - Novo Romaneio");
                System.out.println("(3) - Criar Veículos");
            }
            System.out.println("(0) - Fechar");
            String escolha = sc.nextLine();

            switch (escolha) {
                case "1":
                    verRomaneios(romaneiosService, veiculosRepository, motoristasRepository);
                    break;
                case "2":
                    if (!sessao.isAdmin()) {
                        System.out.println("Acesso negado!");
                        break;
                    }
                    novoRomaneio(romaneiosService, clientesService, clientesRomaneioRepository, romaneiosRepository);
                    break;
                case "3":
                    if (!sessao.isAdmin()) {
                        System.out.println("Acesso negado!");
                        break;
                    }
                    cadastrarVeiculo(veiculosService, sc);
                    break;
                case "0":
                    System.out.println("Fechando...");
                    rodando = false;
                    break;
                default:
                    System.out.println("Opção Inválida!");
            }
        } while (rodando.equals(true));
    }

    // ==================== USUARIOS ====================

    static class SessaoUsuario {
        private final Usuarios usuario;
        private final boolean isAdmin;
        private final Motoristas motorista;

        public SessaoUsuario(Usuarios usuario, boolean isAdmin, Motoristas motorista) {
            this.usuario = usuario;
            this.isAdmin = isAdmin;
            this.motorista = motorista;
        }

        public boolean isAdmin() { return isAdmin; }
        public Usuarios getUsuario() { return usuario; }
        public Motoristas getMotorista() { return motorista; }
    }

    public static SessaoUsuario autenticar(UsuariosService usuariosService, Scanner sc) {
        System.out.println("\n-=-=- TELA DE LOGIN -=-=-");
        System.out.println("Usuário: ");
        String username = sc.nextLine();
        System.out.println("Senha: ");
        String senha = sc.nextLine();

        SessaoUsuario sessao = usuariosService.autenticar(username, senha);
        if (sessao != null) {
            System.out.println("Login realizado com sucesso! Bem-vindo, " + username);
            System.out.println("Perfil: " + (sessao.isAdmin() ? "ADMIN" : "MOTORISTA"));
        } else {
            System.out.println("Usuário ou senha incorretos!");
        }
        return sessao;
    }

    public static void login(UsuariosService usuariosService, Scanner sc) {
        while (true) {
            System.out.println("Selecione uma opção\n" +
                    "\n1: Cadastrar usuario" +
                    "\n2: Novo motorista" +
                    "\n3: Exibir usuarios" +
                    "\n4: Alterar senha" +
                    "\n5: Remover usuario" +
                    "\n0: Sair");
            String opcao = sc.nextLine();

            switch (opcao) {
                case "1":
                    novoUsuario(usuariosService, sc);
                    break;
                case "2":
                    novoMotorista(usuariosService, sc);
                    break;
                case "3":
                    exibirUsuarios(usuariosService);
                    break;
                case "4":
                    alterarSenha(usuariosService, sc);
                    break;
                case "5":
                    removerUsuario(usuariosService, sc);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    static void novoUsuario(UsuariosService usuariosService, Scanner sc) {
        while (true) {
            System.out.println("Digite S para sair ou C para cadastrar");
            String condicao = sc.nextLine();

            if (condicao.equals("S") || condicao.equals("s")) {
                break;
            }
            if (condicao.equals("C") || condicao.equals("c")) {
                System.out.println("\nCADASTRO DE USUARIO");
                System.out.println("Usuario: ");
                String username = sc.nextLine();
                System.out.println("Senha: ");
                String senha = sc.nextLine();

                try {
                    usuariosService.criarUsuario(username, senha);
                    System.out.println("Usuario cadastrado!!");
                    return;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    static void novoMotorista(UsuariosService usuariosService, Scanner sc) {
        System.out.println("Nome do motorista: ");
        String nome = sc.nextLine();

        LocalDate data = null;
        while (data == null) {
            try {
                System.out.println("Data de nascimento no formato DD/MM/AAAA");
                String dataStr = sc.nextLine();
                data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (Exception e) {
                System.out.println("Formato invalido! Use DD/MM/AAAA");
            }
        }

        System.out.println("Nome de Usuario desse motorista: ");
        String username = sc.nextLine();

        try {
            usuariosService.criarMotorista(nome, data, username);
            System.out.println("Motorista cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    static void exibirUsuarios(UsuariosService usuarioService) {
        List<Usuarios> lista = usuarioService.listarUsuarios();
        for (Usuarios u : lista) {
            System.out.println("Usuario: " + u.getUsuario());
        }
    }

    static void alterarSenha(UsuariosService usuariosService, Scanner sc) {
        exibirUsuarios(usuariosService);
        System.out.println("Digite o Usuario que deseja atualizar a senha");
        String user = sc.nextLine();
        System.out.println("Senha antiga: ");
        String senhaAntiga = sc.nextLine();
        System.out.println("Nova Senha: ");
        String novaSenha = sc.nextLine();

        try {
            usuariosService.alterarSenha(user, senhaAntiga, novaSenha);
            System.out.println("Senha atualizada!!");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    static void removerUsuario(UsuariosService usuariosService, Scanner sc) {
        exibirUsuarios(usuariosService);
        System.out.println("Digite o usuario que sera removido: ");
        String user = sc.nextLine();

        try {
            usuariosService.removerUsuario(user);
            System.out.println("Usuario deletado com sucesso!!");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void espaco(String escolha) {
        if (escolha == null || escolha.trim().isEmpty()) {
            System.out.println("Entrada vazia.");
        }
    }

    // ==================== MAIN ====================

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        EntityManager em = CustomizerFactory.getEntityManager();
        FlyWayconfig.migrate();

        UsuarioRepository usuarioRepository = new UsuarioRepository(em);
        MotoristasRepository motoristasRepository = new MotoristasRepository(em);
        UsuariosService usuariosService = new UsuariosService(usuarioRepository, motoristasRepository);

        SessaoUsuario sessao = null;
        String escolha = "";

        System.out.println("=-=-= LOGI-DUTRA =-=-=");
        while (sessao == null) {
            sessao = autenticar(usuariosService, sc);
            if (sessao == null) {
                System.out.println("Deseja tentar novamente? (1-Sim / 0-Sair)");
                if (sc.nextLine().equals("0")) {
                    System.out.println("Encerrando...");
                    return;
                }
            }
        }

        do {
            System.out.println("1 =-=-= RELOGAR         =-=-=");
            System.out.println("2 =-=-= ROMANEIOS       =-=-=");
            if (sessao.isAdmin()) {
                System.out.println("3 =-=-= CADASTRAR       =-=-=");
            }
            System.out.println("0 =-=-= ENCERRAR        =-=-=");

            try {
                escolha = sc.nextLine();
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
            espaco(escolha);

            switch (escolha) {
                case "1":
                    SessaoUsuario novaSessao = autenticar(usuariosService, sc);
                    if (novaSessao != null) {
                        sessao = novaSessao;
                    }
                    break;
                case "2":
                    romaneios(sc, sessao);
                    break;
                case "3":
                    if (!sessao.isAdmin()) {
                        System.out.println("Acesso negado!");
                        break;
                    }
                    login(usuariosService, sc);
                    break;
                case "0":
                    System.out.println("Encerrando...");
                    break;
                default:
                    System.out.println("Indisponível");
            }
        } while (!escolha.equals("0"));

        sc.close();
    }
}
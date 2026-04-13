package projeto;

import jakarta.persistence.EntityManager;
import projeto.config.FlyWayconfig;
import projeto.models.*;
import projeto.repositories.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;


public class Main {

    static void novoRomaneio(RomaneiosRepository romaneiosRepository, ClientesRomaneioRepository clientesRomaneioRepository,
                             PedidosRepository pedidosRepository) { //sem passa os parametros o metodo nao conseguiria acessar as variaveis de instancia
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
                    cadastrarCliente(clientesRomaneioRepository, pedidosRepository);
                    break;
                case "2":
                    cadastrarRomaneio(romaneiosRepository, clientesRomaneioRepository);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Opção Inválida!");
            }
        } while (!escolha.equals("0"));
    }

    static void verRomaneios(RomaneiosRepository romaneiosRepository,
                             ClientesRomaneioRepository clientesRomaneioRepository,
                             VeiculosRepository veiculosRepository,
                             MotoristasRepository motoristasRepository) {
        Scanner scanner = new Scanner(System.in);

        List<Romaneios> romaneios = romaneiosRepository.findAll();

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
                    System.out.println("\n" + romaneioSelecionado); // mostra os detalhes

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
                                if (romaneioSelecionado.getVeiculo() != null) {
                                    System.out.println("Este romaneio já possui o veículo: "
                                            + romaneioSelecionado.getVeiculo().getNomeVeiculo());
                                    break;
                                }
                                List<Veiculos> veiculos = veiculosRepository.findAll();
                                if (veiculos.isEmpty()) {
                                    System.out.println("Nenhum veÃ­culo cadastrado!");
                                    break;
                                }
                                for (int i = 0; i < veiculos.size(); i++) {
                                    System.out.println("(" + i + ") - " + veiculos.get(i).getNomeVeiculo());
                                }
                                System.out.println("Selecione um veículo: ");
                                int indexVeiculo = Integer.parseInt(scanner.nextLine());
                                Veiculos veiculoSelecionado = veiculos.get(indexVeiculo);
                                if (veiculoSelecionado.getDisponibilidade() == null || veiculoSelecionado.getDisponibilidade().equals(false)) {
                                    System.out.println("VeÃ­culo indisponÃ­vel para romaneio!");
                                } else if (romaneiosRepository.veiculoEmUso(veiculoSelecionado)) {
                                    System.out.println("Veículo já está em uso em outro romaneio!");
                                } else {
                                    romaneioSelecionado.setVeiculo(veiculoSelecionado);
                                    romaneiosRepository.update(romaneioSelecionado);
                                    System.out.println("Veículo atribuído com sucesso!");
                                }
                                break;

                            case "2":
                                if (romaneioSelecionado.getMotorista() != null) {
                                    System.out.println("Este romaneio já possui o motorista: "
                                            + romaneioSelecionado.getMotorista().getNome());
                                    break;
                                }
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
                                if (romaneiosRepository.motoristaEmUso(motoristaSelecionado)) {
                                    System.out.println("Motorista já está em uso em outro romaneio!");
                                } else {
                                    romaneioSelecionado.setMotorista(motoristaSelecionado);
                                    romaneiosRepository.update(romaneioSelecionado);
                                    System.out.println("Motorista atribuído com sucesso!");
                                }
                                break;
                            case "3":
                                romaneiosRepository.delete(romaneioSelecionado);
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

    static void cadastrarCliente(ClientesRomaneioRepository clientesRomaneioRepository,
                                 PedidosRepository pedidosRepository) {
        Scanner scanner = new Scanner(System.in);
        String confirmar;

        System.out.println("=====Cadastrar Cliente======");
        System.out.println("Nome do Cliente: ");
        String nomeCliente = scanner.nextLine();
        System.out.println("CPF: ");
        String cpfCliente = scanner.nextLine();
        System.out.println("Dados do Cliente Confirmado!");
        ClientesRomaneio cliente = new ClientesRomaneio(null, nomeCliente, cpfCliente);

        Endereco endereco = Endereco.lerEndereco(scanner); // Le o endereço e cadastra
        cliente.setEndereco(endereco);

        String opcao;
        do { // Loop para escolher se quer cadastrar mais de um pedido
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
                pedido.setClientes(cliente);
                cliente.getPedidos().add(pedido);
            } else if (confirmar.equals("n") || confirmar.equals("N")) {
                System.out.println("Pedido cancelado!");
            } else {
                System.out.println("Opção inválida!");
            }

            System.out.println("Adicionar mais um pedido?(s/n): ");
            opcao = scanner.nextLine();

        } while (!opcao.equals("n") && !opcao.equals("N"));

        clientesRomaneioRepository.create(cliente);
        System.out.println("Cliente salvo com sucesso!");
    }

    static void cadastrarRomaneio(RomaneiosRepository romaneiosRepository,
                                  ClientesRomaneioRepository clientesRomaneioRepository) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("==Cadastrar Romaneio==");
        System.out.println("Data do Romaneio(dd/MM/yyyy): ");
        String dataString = scanner.nextLine();
        LocalDate dataRomaneio = LocalDate.parse(dataString, DateTimeFormatter.ofPattern("dd/MM/yyyy")); // Mudando formato da data

        Romaneios romaneio = new Romaneios(null, dataRomaneio);

        // Lista os clientes sem romaneio
        List<ClientesRomaneio> clientesSemRomaneio = clientesRomaneioRepository.findSemRomaneio();

        if (clientesSemRomaneio.isEmpty()) {
            System.out.println("Nenhum cliente disponível para adicionar!");
            return;
        }

        for (int i = 0; i < clientesSemRomaneio.size(); i++) { // Mostra os clientes disponiveis
            System.out.println("(" + i + ") - " + clientesSemRomaneio.get(i).getNome_cliente());
        }

        System.out.println("Digite o número do cliente para adicionar (ou Q para finalizar): ");
        String opcao = scanner.nextLine(); // inicializa aqui com o primeiro valor

        while (!opcao.equals("Q") && !opcao.equals("q")) {
            try {
                int index = Integer.parseInt(opcao);

                if (index < 0 || index >= clientesSemRomaneio.size()) {
                    System.out.println("Número inválido! Escolha entre 0 e " + (clientesSemRomaneio.size() - 1));
                } else {
                    ClientesRomaneio cliente = clientesSemRomaneio.get(index);
                    cliente.setRomaneio(romaneio);
                    romaneio.getClientes().add(cliente);
                    System.out.println("Cliente " + cliente.getNome_cliente() + " adicionado!");
                }

            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido ou Q para finalizar!");
            }

            System.out.println("Adicionar mais um cliente? (ou Q para finalizar): ");
            opcao = scanner.nextLine();
        }
        romaneiosRepository.create(romaneio);
        System.out.println("Romaneio criado com sucesso!");
    }

    public static void romaneios() {
        Scanner scanner = new Scanner(System.in);
        Boolean rodando = true;
        EntityManager em = CustomizerFactory.getEntityManager(); //EntityManager é a API(JPA) responsavel por interagir com a database
        FlyWayconfig.migrate(); //Flyway é uma ferramenta voltada para o versionamento e migração de banco de dados

        RomaneiosRepository romaneiosRepository = new RomaneiosRepository(em);
        ClientesRomaneioRepository clientesRomaneioRepository = new ClientesRomaneioRepository(em);
        PedidosRepository pedidosRepository = new PedidosRepository(em);
        VeiculosRepository veiculosRepository = new VeiculosRepository(em);
        MotoristasRepository motoristasRepository = new MotoristasRepository(em);

        do { // Aqui será o menu principal de romaneios
            System.out.println("======================================\n");
            System.out.println("\t\tDUTRA MOVEIS(romaneios)");
            System.out.println("\n======================================");
            System.out.println("Selecione uma das opções:");
            System.out.println("(1) - Novo Romaneio");
            System.out.println("(2) - Ver Romaneios");
            System.out.println("(0) - Fechar");
            String escolha = scanner.nextLine();

            switch (escolha) {
                case "1":
                    novoRomaneio(romaneiosRepository, clientesRomaneioRepository, pedidosRepository);
                    break;
                case "2":
                    verRomaneios(romaneiosRepository, clientesRomaneioRepository, veiculosRepository,
                            motoristasRepository);
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

    public static void espaco(String escolha) {
        if (escolha == null || escolha.trim().isEmpty()) {
            System.out.println("Entrada vazia.");
            return;
        }
    }


    // INICIO

    public static void login(Scanner sc) {
        FlyWayconfig.migrate();

        while (true) {
            EntityManager em = CustomizerFactory.getEntityManager();// aqui cria o entitymanager que vai conversar com o banco
            UsuarioRepository usuarioRepository = new UsuarioRepository(em);// o "em" é o EntityManager que será usado pelo repository para manipular o banco de dados
            Motoristasrepository motoristasrepository = new Motoristasrepository(em);
            String condicao;



            System.out.println("Selecione uma opção\n" +
                    "\n1: Cadastrar usuario" +
                    "\n2: Novo motorista" +
                    "\n3: Exibir usuarios" +
                    "\n4: Alterar senha" +
                    "\n5: Remover usuario");
            String opcao = sc.nextLine();

            switch (opcao) {
                case "1":
                    novoUsuario(usuarioRepository);
                    break;
                case "2":
                    novoMotorista(motoristasrepository, usuarioRepository);
                    break;
                case "3":
                    exibirUsuarios(usuarioRepository);
                    break;
                case "4":
                    alterarSenha(usuarioRepository);
                    break;
                case "5":
                    removerUsuario(usuarioRepository);
                    break;
                default:
                    System.out.println("opção invalida");
                    return;
            }
        }
    }

    static void novoUsuario(UsuarioRepository usuarioRepository) {  //sem passa os parametros o metodo nao conseguiria acessar as variaveis de instancia

        while (true) { //depois ajusta isso
            Scanner sc = new Scanner(System.in);

            System.out.println("digite S para sair do loop ou C para cadastrar");
            String condição = sc.nextLine();

            if (condição.equals("S") || condição.equals("s")) {
                break;
            }
            if (condição.equals("C") || condição.equals("c")) {
                Usuarios novo = new Usuarios();
                Motoristas motoristas = new Motoristas();

                System.out.println("\nCADASTRO DE USUARIO");

                System.out.println("Nome: ");
                motoristas.setNome(sc.nextLine());

                System.out.println("Usuario: ");
                novo.setUsuario(sc.nextLine());

                System.out.println("Senha: ");
                novo.setSenha(sc.nextLine());

                try {
                    usuarioRepository.create(novo);
                    System.out.println("Usuario cadastrado!!");
                    return;
                } catch (Exception e) {
                    if (e.getMessage().contains("Usuario ja existente, escolha outro nome de usuario!")) {
                        System.out.println("Nome de usuario ja em uso, escolha outro");
                        novoUsuario(usuarioRepository);
                    }
                }
            }
        }
    }

    static void novoMotorista(Motoristasrepository motoristasrepository, UsuarioRepository usuarioRepository) {
        Scanner sc = new Scanner(System.in);
        Motoristas motoristas = new Motoristas();

        Usuarios cadastrado = null;

        System.out.println("Nome do motorista: ");
        motoristas.setNome(sc.nextLine());

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
        motoristas.setData_nascimento(data);

        List<Usuarios> lista = usuarioRepository.findAll();

        System.out.println("Nome de Usuario desse motorista: ");
        String user = sc.nextLine();

        for (Usuarios u : lista) {
            if (u.getUsuario().equals(user)) {
                cadastrado = u;
            }
        }
        if (cadastrado != null) {
            if (cadastrado.getUsuario().equals(user)) {
                System.out.println("Motorista encontrado!!");
            }

            motoristas.setUsuarios(cadastrado);

            try {
                motoristasrepository.create(motoristas);
                System.out.println("Motorista cadastrado com sucesso!");
                return;
            } catch (Exception e) {
                if (e.getMessage().contains("Motorista deve ter mais de 24 anos")) {
                    System.out.println("Cadastro negado: pela questão do seguro, o motorista deve ter mais de 24 anos!");
                }
                return;
            }

        } else {
            String opcao;
            do {
                System.out.println("Motorista nao encontrado, necessita cadastrar como usuario primeiron\nSelecione uma opção:");
                System.out.println("1- Cadastro de usuarios\n0-Sair");
                opcao = sc.nextLine();
                switch (opcao) {
                    case "1":
                        novoUsuario(usuarioRepository);
                        novoMotorista(motoristasrepository, usuarioRepository);
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Opçao invalida!!");
                }
            }
            while (!opcao.equals("0"));
        }
    }

    static void exibirUsuarios(UsuarioRepository usuarioRepository) {
        List<Usuarios> lista = usuarioRepository.findAll();
        for (Usuarios u : lista) {
            System.out.println("Usuario: " + u.getUsuario());
        }
    }

    static void alterarSenha(UsuarioRepository usuarioRepository) {
        Scanner sc = new Scanner(System.in);

        exibirUsuarios(usuarioRepository);
        System.out.println("Digite o Usuario que deseja atualizar a senha");
        String user = sc.nextLine();

        boolean encontrado = false;
        String senhaDigitada;
        Usuarios usuarioencontrado = null;

        while (encontrado == false) {
            usuarioencontrado = null;//caso o usuario nao seja encontrado, reseta o valor pra null;
            List<Usuarios> lista = usuarioRepository.findAll();
            for (Usuarios u : lista) {
                if (u.getUsuario().equals(user)) {
                    usuarioencontrado = u;
                }
            }
            if (usuarioencontrado != null) {
                if (usuarioencontrado.getUsuario().equals(user)) {// verificação desnecessaria, mas rodou assim entao deixei
                    System.out.println("Senha antiga: ");
                    senhaDigitada = sc.nextLine();

                    if (senhaDigitada.equals(usuarioencontrado.getSenha())) {
                        System.out.println("Nova Senha: ");
                        String novaSenha = sc.nextLine();
                        usuarioencontrado.setSenha(novaSenha);
                        usuarioRepository.update(usuarioencontrado);// atualizando no banco de dados
                        encontrado = true;

                        System.out.println("Senha atualizada!!");
                    }
                }
            } else {
                exibirUsuarios(usuarioRepository);
                System.out.println("Usuario nao encontrado, digite um usuario valido!!");
                user = sc.nextLine();
            }
            //quando as duas forem verdadeiras vai significar que  senha ta errada mas o usuario ta correto, evita de cair direto na senha incorreta
            if (encontrado == false && usuarioencontrado != null) {
                System.out.println("Senha incorreta!!\nDigite novamente");
                senhaDigitada = sc.nextLine();
            }
        }
    }

    static void removerUsuario(UsuarioRepository usuarioRepository) {
        Scanner sc = new Scanner(System.in);

        exibirUsuarios(usuarioRepository);
        System.out.println("Digite o usuario que sera removido: ");
        String user = sc.nextLine();

        Usuarios usuarioencontrado = null;
        boolean encontrado = false;

        while (encontrado == false) {
            usuarioencontrado = null;
            List<Usuarios> lista = usuarioRepository.findAll();

            for (Usuarios u : lista) {
                if (u.getUsuario().equals(user)) {
                    usuarioencontrado = u;
                }
            }
            if (usuarioencontrado != null) {
                usuarioRepository.delete(usuarioencontrado);
                encontrado = true;
                System.out.println("Usuario deletado com sucesso!!");
            } else {
                System.out.println("Usuario não encontrado!!\n" +
                        "Digite um usuario valido: ");
                exibirUsuarios(usuarioRepository);
                user = sc.nextLine();
            }
        }
    }

    //FIM DO MATHEUS

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int logged = 0;
        String escolha = "";
        boolean[] admin = {false}; //A Booleana está por array, porque quando passamos por parametro normalmente ele apenas passa uma "cópia"//

        System.out.println("=-=-= LOGI-DUTRA =-=-=");
        do {
            try {
                System.out.println("1 =-=-= LOGIN =-=-=");
                System.out.println("____________________");
                try {
                    escolha = sc.nextLine();
                } catch (IllegalStateException e) {
                    System.out.println("Erro no Scanner (fechado inesperadamente)");
                } catch (Exception e) {
                    System.out.println("Erro inesperado: " + e.getMessage());

                espaco(escolha);

                switch (escolha) {
                    case "1":
                        login(sc);
                        break;
                    default:
                        System.out.println("Opção inválida!");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Erro de entrada");
                return;
            }
        } while (logged == 0);


        do {
            System.out.println("1 =-=-= RELOGAR         =-=-=");
            System.out.println("2 =-=-= ROMANEIOS       =-=-=");
            System.out.println("0 =-=-= ENCERRAR        =-=-=");
            try {
                escolha = sc.nextLine();
            } catch (IllegalStateException e) {
                System.out.println("Erro na hora de scanear o código");
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());

                espaco(escolha);

                switch (escolha) {
                    case "1":
                        login();
                        break;
                    case "2":
                        romaneios();
                        break;
                    case "0":
                        break;
                    default:
                        System.out.println("Indisponível");
                        break;
                }

            }
            while (!escolha.equals("0")) ;
        }
    }
}

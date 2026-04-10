package projeto;

import jakarta.persistence.EntityManager;
import projeto.config.FlyWayconfig;
import projeto.models.*;
import projeto.repositories.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;


public class Main {

    static void novoRomaneio(RomaneiosRepository romaneiosRepository, ClientesRomaneioRepository clientesRomaneioRepository,
                             PedidosRepository pedidosRepository) { //sem passa os parametros o metodo nao conseguiria acessar as variaveis de instancia
        Scanner scanner = new Scanner(System.in);
        String escolha;

        do{
            System.out.println("\t====(Submenu)Novo Romaneio====\n");
            System.out.println("(1) - Cadastrar Cliente");
            System.out.println("(2) - Cadastrar Romaneio");
            System.out.println("(0) - Voltar");
            escolha = scanner.nextLine();

            switch (escolha) {
                case "1":
                    cadastrarCliente(clientesRomaneioRepository, pedidosRepository); break;
                case "2":
                    cadastrarRomaneio(romaneiosRepository, clientesRomaneioRepository); break;
                case "0":
                    return;
                default:
                    System.out.println("Opção Inválida!");
            }
        } while(!escolha.equals("0"));
    }

    static  void verRomaneios(RomaneiosRepository romaneiosRepository,
                              ClientesRomaneioRepository clientesRomaneioRepository) {
    }

    static void cadastrarCliente(ClientesRomaneioRepository clientesRomaneioRepository,
                                 PedidosRepository pedidosRepository) {
        Scanner scanner = new Scanner(System.in);
        Boolean funcionando = true;
        String confirmar;

        do {
            System.out.println("=====Cadastrar Cliente======");
            System.out.println("Nome do Cliente: ");
            String nomeCliente = scanner.nextLine();
            System.out.println("CPF: ");
            String cpfCliente = scanner.nextLine();
            System.out.println("Dados do Cliente Confirmado!");
            ClientesRomaneio cliente = new ClientesRomaneio(null,  nomeCliente, cpfCliente);

            Endereco endereco = Endereco.lerEndereco(scanner); // Lendo endereço do cliente

            cliente.setEndereco(endereco); // Ligando endereço ao cliente

            String opcao;
            do {
                System.out.println("==Pedido==");
                System.out.println("Nome do Produto: ");
                String nomeProduto = scanner.nextLine();
                System.out.println("Quantidade adquirida do Produto: ");
                String quantidadeProduto = scanner.nextLine();

                Pedidos pedido = new Pedidos(null, nomeProduto, quantidadeProduto);

                System.out.println("Confirme os dados abaixo: \n");
                System.out.println(pedido);

                System.out.println("Confirmar?(s/n): ");
                confirmar = scanner.nextLine();

                if (confirmar.equals("s") || confirmar.equals("S")) {
                    System.out.println("Criação de pedido feito com Sucesso!");

                    pedido.setClientes(cliente);     // lado ManyToOne aponta para o pai
                    cliente.getPedidos().add(pedido);// lado OneToMany adiciona o filho

                } else if (confirmar.equals("n") || confirmar.equals("N")) {
                    System.out.println("Pedido cancelado!");
                } else {
                    System.out.println("Opção inválida!");
                    return;
                }

                System.out.println("Adicionar mais um pedido?(s/n): ");
                opcao = scanner.nextLine();

                if (confirmar.equals("n") && confirmar.equals("N")) {
                    return;
                }
            } while (!opcao.equals("n") && !opcao.equals("N"));

            clientesRomaneioRepository.create(cliente); // Salvando cliente uma única vez
            System.out.println("Fechando criação dos pedidos...");

        } while (funcionando.equals(true));
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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Boolean rodando = true;
        EntityManager em = CustomizerFactory.getEntityManager(); //EntityManager é a API(JPA) responsavel por interagir com a database
        FlyWayconfig.migrate(); //Flyway é uma ferramenta voltada para o versionamento e migração de banco de dados

        RomaneiosRepository romaneiosRepository               = new RomaneiosRepository(em);
        ClientesRomaneioRepository clientesRomaneioRepository = new ClientesRomaneioRepository(em);
        PedidosRepository pedidosRepository                   = new PedidosRepository(em);
        VeiculosRepository veiculosRepository                 = new VeiculosRepository(em);
        MotoristasRepository motoristasRepository             = new MotoristasRepository(em);

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
                    novoRomaneio(romaneiosRepository, clientesRomaneioRepository, pedidosRepository); break;
                case "2":
                    verRomaneios(romaneiosRepository, clientesRomaneioRepository); break;
                case "0":
                    System.out.println("Fechando...");
                    rodando = false;
                    break;
                default:
                    System.out.println("Opção Inválida!");
            }
        }while(rodando.equals(true));
    }
}
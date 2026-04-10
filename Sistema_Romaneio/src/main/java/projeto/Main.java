package projeto;

import jakarta.persistence.EntityManager;
import projeto.config.FlyWayconfig;
import projeto.models.*;
import projeto.repositories.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
            System.out.println("Dados do Cliente confirmado com Sucesso!");
            ClientesRomaneio cliente = new ClientesRomaneio(null, nomeCliente, cpfCliente);

            System.out.println("==Endereço==");
            System.out.println("CEP: ");
            String cepCasa = scanner.nextLine();
            System.out.println("Rua: ");
            String ruaCliente = scanner.nextLine();
            System.out.println("Numero da casa: ");
            String numeroCasa = scanner.nextLine();
            System.out.println("Bairro: ");
            String bairroCliente = scanner.nextLine();
            System.out.println("Complemento(Opcional): ");
            String complementoCasa = scanner.nextLine();
            System.out.println("Referencia(Opcional): ");
            String referenciaCasa = scanner.nextLine();
            System.out.println("Endereço do Cliente confirmado com Sucesso!");
            Endereco endereco = new Endereco(cepCasa, ruaCliente, numeroCasa, bairroCliente,
                    complementoCasa, referenciaCasa);

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
                    pedido.setClientes(cliente);      // lado ManyToOne aponta para o pai

                    cliente.getPedidos().add(pedido); // lado OneToMany adiciona o filho
                } else if (confirmar.equals("n") || confirmar.equals("N")) {
                    return;
                } else {
                    System.out.println("Opção inválida!");
                    return;
                }

                System.out.println("Adicionar mais um pedido?(s/n): ");
                opcao = scanner.nextLine();

                if (confirmar.equals("s") || confirmar.equals("S")) {
                    return;
                }
            } while (!opcao.equals("n") && !opcao.equals("N"));

            clientesRomaneioRepository.create(cliente); // Salvando cliente uma única vez
            System.out.println("Fechando criação dos pedidos...");

        } while (funcionando.equals(true));
    }

    static void cadastrarRomaneio(RomaneiosRepository romaneiosRepository,
                                  ClientesRomaneioRepository clientesRomaneioRepository) {
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
            System.out.println("\n======================================\n");
            System.out.println("\t\tDUTRA MOVEIS(romaneios)");
            System.out.println("\n======================================\n");
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
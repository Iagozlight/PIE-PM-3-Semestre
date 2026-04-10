package projeto;

import jakarta.persistence.EntityManager;
import projeto.config.FlyWayconfig;
import projeto.models.*;
import projeto.repositories.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import static jdk.internal.org.jline.reader.impl.LineReaderImpl.CompletionType.List;

public class Main {
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
            System.out.println("(3) - Fechar");
            String escolha = scanner.nextLine();

            switch (escolha) {
                case "1":
                    novoRomaneio();
                case "2":
                    verRomaneios();
                case "3":
                    System.out.println("Fechando aba DUTRA MOVEIS(romaneios)...";
            }
        }while(rodando.equals(true));
    }
}

static void novoRomaneio(RomaneiosRepository romaneiosRepository, ClientesRomaneioRepository clientesRomaneioRepository) { //sem passa os parametros o metodo nao conseguiria acessar as variaveis de instancia
    Scanner scanner = new Scanner(System.in)
    String escolha;

    do{
        System.out.println("\t====(Submenu)Novo Romaneio====\n");
        System.out.println("(1) - Cadastrar Cliente");
        System.out.println("(2) - Cadastrar Romaneio");
        System.out.println("(3) - Sair");
        escolha = scanner.nextLine();

        switch (escolha) {
            case "1":
                cadastrarCliente(); break;
            case "2":
                cadastrarRomaneio(); break;
            case "3":
                System.out.println("Encerrando...");
                break;
        }
    } while(escolha.equals("3"));


} break;

static  void verRomaneios(RomaneiosRepository romaneiosRepository, ClientesRomaneioRepository clientesRomaneioRepository) {
}

static void cadastrarCliente(ClientesRomaneioRepository clientesRomaneioRepository, PedidosRepository pedidosRepository) {
    Scanner scanner = new Scanner(System.in);
    Boolean funcionando = true;
    String confirmar;

    do{
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
                pedido.setClientes(cliente);

                clientesRomaneioRepository.create(cliente);

                break;
            } else if (confirmar.equals("n") || confirmar.equals("N")) {
                return;
            }

            System.out.println("Adicionar mais um pedido?(s/n): ");
            confirmar = scanner.nextLine();

            if (confirmar.equals("s") || confirmar.equals("S")) {
                return;
            } else if (confirmar.equals("n") || confirmar.equals("N")) {
                System.out.println("Fechando aba criação de pedido...");
                funcionando = false;
            }
        }
    } while(funcionando.equals(true));
}

static void cadastrarRomaneio(RomaneiosRepository romaneiosRepository) {
}
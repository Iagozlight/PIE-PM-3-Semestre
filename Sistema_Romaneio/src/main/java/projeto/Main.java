package projeto;

import jakarta.persistence.EntityManager;
import projeto.config.FlyWayconfig;
import projeto.models.ClientesRomaneio;
import projeto.models.Romaneios;
import projeto.repositories.ClientesRomaneioRepository;
import projeto.repositories.CustomizerFactory;
import projeto.repositories.PedidosRepository;
import projeto.repositories.RomaneiosRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean rodando = true;
        EntityManager em = CustomizerFactory.getEntityManager();
        FlyWayconfig.migrate();
//
//        RomaneiosRepository romaneiosRepository = new RomaneiosRepository(em);
//        ClientesRomaneioRepository clientesRomaneioRepository = new ClientesRomaneioRepository(em);
//        PedidosRepository pedidosRepository = new PedidosRepository(em);
//
//
//        do {
//            System.out.println("\t\tDUTRA MOVEIS(romaneios)");
//            System.out.println("Selecione uma das opções:");
//            System.out.println("(1) - Novo Romaneio");
//            System.out.println("(2) - Ver Romaneios");
//            System.out.println("(3) - Fechar");
//            int escolha = scanner.nextInt();
//
//            switch (escolha){
//                case 1:
//                    scanner.nextLine();
//
//                    System.out.println("\t====Cadastro Cliente====\n");
//                    String confirmar;
//
//                    do {
//                        System.out.println("Data do Romaneio(dd/MM/yyyy): ");
//                        String dataRomaneio = scanner.nextLine();
//                        LocalDate data = LocalDate.parse(dataRomaneio, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
//
//                        Romaneios romaneio = new Romaneios(null, data);
//
//                        System.out.println("\nConfirme os dados:");
//                        System.out.println(romaneio);
//
//                        System.out.println("Confirmar?(s/n): ");
//                        confirmar = scanner.nextLine();
//                    } while(!confirmar.equals("s") || !confirmar.equals("S"));
//
//
//
////                    do {
////                        System.out.println("Nome: \n");
////                        String nomeCliente = scanner.nextLine();
////                        System.out.println("CEP: ");
////                        String cep = scanner.nextLine();
////                        System.out.println("Rua: \n");
////                        String ruaCliente = scanner.nextLine();
////                        System.out.println("Numero da casa: \n");
////                        String numeroCasa = scanner.nextLine();
////                        System.out.println("Bairro: \n");
////                        String bairroCliente = scanner.nextLine();
////                        ClientesRomaneio cliente = new ClientesRomaneio(null, nomeCliente, cep, ruaCliente,
////                                numeroCasa, bairroCliente);
////
////                        System.out.println("\nConfirme os dados:");
////                        System.out.println(cliente);
////                    }
//
//                    System.out.println("\nConfirme os dados:");
//                    System.out.println(romaneio);
//
//                    System.out.println("Confirmar?(s/n): ");
//                    String confirmar = scanner.nextLine();
//
//                    if (confirmar.equals("s") || confirmar.equals("S")) {
//                        System.out.println("Data romaneio cadastrado com sucesso!");
//                    } else {
//                        System.out.println("Romaneio cancelado");
//                    }
//
//
//                    break;
//                case 2:
//
//                    break;
//                case 3:
//                    System.out.println("Encerrando...");
//                    rodando = false;
//                    break;
//            }
//        } while (rodando);
//
//        scanner.close();
    }
}
package projeto;

import jakarta.persistence.EntityManager;
import projeto.config.FlyWayconfig;
import projeto.models.*;
import projeto.repositories.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean rodando = true;
        EntityManager em = CustomizerFactory.getEntityManager(); //EntityManager é a API(JPA) responsavel por interagir com a database
        FlyWayconfig.migrate(); //Flyway é uma ferramenta voltada para o versionamento e migração de banco de dados

        RomaneiosRepository romaneiosRepository = new RomaneiosRepository(em);
        ClientesRomaneioRepository clientesRomaneioRepository = new ClientesRomaneioRepository(em);
        PedidosRepository pedidosRepository = new PedidosRepository(em);
        VeiculosRepository veiculosRepository = new VeiculosRepository(em);
        MotoristasRepository motoristasRepository = new MotoristasRepository(em);

        do { // Aqui será o menu principal de romaneios
            System.out.println("\n======================================\n");
            System.out.println("\t\tDUTRA MOVEIS(romaneios)");
            System.out.println("\n======================================\n");
            System.out.println("Selecione uma das opções:");
            System.out.println("(1) - Novo Romaneio");
            System.out.println("(2) - Ver Romaneios");
            System.out.println("(3) - Fechar");
            String escolha = scanner.nextLine();

            switch (escolha){
                case "1":
                    do {
                        scanner.nextLine();

                        System.out.println("\t====(Submenu)Novo Romaneio====\n");
                        System.out.println("(1) - Cadastrar Cliente");
                        System.out.println("(2) - Cadastrar Romaneio");
                        System.out.println("(3) - Sair");
                        String escolha2 = scanner.nextLine();

                        if(escolha2.equals("1")){
                                scanner.nextLine();

                                System.out.println("=====Cadastrar Cliente======\n");
                                System.out.println("Nome do Cliente: \n");
                                String nomeCliente = scanner.nextLine();
                                System.out.println("CPF: \n");
                                String cpfCliente = scanner.nextLine();
                                ClientesRomaneio cliente = new ClientesRomaneio(null,  nomeCliente, cpfCliente);

                                System.out.println("==Endereço==\n");
                                System.out.println("CEP: ");
                                String cepCasa = scanner.nextLine();
                                System.out.println("Rua: \n");
                                String ruaCliente = scanner.nextLine();
                                System.out.println("Numero da casa: \n");
                                String numeroCasa = scanner.nextLine();
                                System.out.println("Bairro: \n");
                                String bairroCliente = scanner.nextLine();
                                System.out.println("Complemento(Opcional): \n");
                                String complementoCasa = scanner.nextLine();
                                System.out.println("Referencia(Opcional): \n");
                                String referenciaCasa = scanner.nextLine();
                                Endereco endereco = new Endereco(cepCasa, ruaCliente, numeroCasa, bairroCliente,
                                        complementoCasa, referenciaCasa);


                        }
                    } while(true);






                    do {
                        System.out.println("Data do Romaneio(dd/MM/yyyy): ");
                        String dataRomaneio = scanner.nextLine();
                        LocalDate data = LocalDate.parse(dataRomaneio, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                        Romaneios romaneio = new Romaneios(null, data);

                        System.out.println("\nConfirme os dados:");
                        System.out.println(romaneio);

                        System.out.println("Confirmar?(s/n): ");
                        confirmar = scanner.nextLine();
                    } while(!confirmar.equals("s") || !confirmar.equals("S"));



                    do {
                        System.out.println("Nome: \n");
                        String nomeCliente = scanner.nextLine();

                        ClientesRomaneio cliente = new ClientesRomaneio(null, nomeCliente, cep, ruaCliente,
                                numeroCasa, bairroCliente);

                        System.out.println("\nConfirme os dados:");
                        System.out.println(cliente);
                    } while ();

                    System.out.println("\nConfirme os dados:");
                    System.out.println(romaneio);

                    System.out.println("Confirmar?(s/n): ");
                    String confirmar = scanner.nextLine();

                    if (confirmar.equals("s") || confirmar.equals("S")) {
                        System.out.println("Data romaneio cadastrado com sucesso!");
                    } else {
                        System.out.println("Romaneio cancelado");
                    }


                    break;
                case "2":

                    break;
                case "3":
                    System.out.println("Encerrando...");
                    rodando = false;
                    break;
            }
        } while (true);

        scanner.close();
    }
}
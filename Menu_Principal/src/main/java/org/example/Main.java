package org.example;
import java.util.Scanner;

public class Main {

    public static void espaco(String escolha) {
        if (escolha == null || escolha.trim().isEmpty()) {
            System.out.println("Entrada vazia.");
            return;
        }
    }

    public static int login(boolean[] admin,Scanner sc) {
        //Apenas um teste, será aplicado futuramente o Login do Mateus//

        System.out.println("Você é o que : 1 Admin | 2 Funcionario ___");
        String escolha = sc.nextLine();

        if (escolha.equals("1")) {
            admin[0] = true; System.out.println("Você é Admin");
        } else if (escolha.equals("2")) {
            System.out.println("Você é um funcionário");
            admin[0] = false;
        } else {
            System.out.println("Opção invalida");
            admin[0] = false;
        }

        return "1".equals(escolha) || "2".equals(escolha) ? 1 : 0; // Isso aqui utiliza um operador ternário para retornar APENAS se for um numero entre 1 e 2//
    }

    public static void romaneios(){
        //Apenas um teste, será aplicado futuramente os Romaneios do Thales//
    }

    public static void criar_romaneios(){
        //Apenas um teste, será aplicado futuramente o Criar Romaneios do Thales//
    }

    public static void cadastro(Scanner sc) {
        //Apenas um teste, será aplicado futuramente o Cadastro do Mateus//

        String escolha = "";

        do {
            try {
                System.out.println("  =-=-= LOGI-DUTRA =-=-=");
                System.out.println("1 =-=-= MOTORISTA  =-=-=");
                System.out.println("2 =-=-= VEÍCULO    =-=-=");
                System.out.println("0 =-=-= VOLTAR     =-=-=");

                escolha = sc.nextLine();

                switch (escolha) {
                    case "1":

                        break;
                    case "2":

                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Opção Invalida");
                }
            } catch(Exception e){
                System.out.println("Erro de entrada, Voltando");
                return;

            }
        }while(!escolha.equals("0"));
    }

    public static void ver_cadastros(){
        //Apenas um teste, será aplicado futuramente o Login do Mateus//
    }

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
                escolha = sc.nextLine();

                espaco(escolha);

                switch (escolha) {
                    case "1":
                        logged = login(admin, sc);
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


        do{
            try {
                System.out.println("1 =-=-= RELOGAR         =-=-=");
                System.out.println("2 =-=-= ROMANEIOS       =-=-=");

                if (admin[0]) {
                    System.out.println("3 =-=-= CRIAR ROMANEIOS =-=-=");
                    System.out.println("4 =-=-= CADASTRAR       =-=-=");
                    System.out.println("5 =-=-= VER CADASTROS   =-=-=");
                }

                System.out.println("0 =-=-= ENCERRAR        =-=-=");
                escolha = sc.nextLine();

                espaco(escolha);

                switch(escolha) {
                    case "1":
                        login(admin, sc);
                        break;
                    case "2":
                        romaneios();
                        break;
                    case "3":
                        if (admin[0] == true) {
                            criar_romaneios();
                        } else {
                            System.out.println("Indisponível");
                        }
                        break;
                    case "4":
                        if (admin[0] == true) {
                            cadastro(sc);
                        } else {
                            System.out.println("Indisponível");
                        }
                        break;
                    case "5":
                        if (admin[0] == true) {
                            ver_cadastros();
                        } else {
                            System.out.println("Indisponível");
                        }
                        break;
                    case "0":
                        break;
                    default:
                        System.out.println("Indisponível");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Erro de entrada");
            }
        } while(!escolha.equals("0"));
    }
}
package org.example;
import java.util.Scanner;

public class Main {

    public static int login(boolean[] admin) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Você é o que : 1 Admin | 2 Funcionario ___");
        int escolha = sc.nextInt();

        if (escolha == 1) {
            admin[0] = true;
            System.out.println("Você é Admin");
        } else if (escolha == 2) {
            System.out.println("Você é um funcionário");
            admin[0] = false;
        } else {
            System.out.println("Opção inválida");
            admin[0] = false;
        }

        return 1;
    }

    public static void romaneios(){

    }

    public static void criar_romaneios(){

    }

    public static void cadastro(){

    }

    public static void ver_cadastros(){

    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int logged = 0;
        String escolha;
        boolean[] admin = {false};

        System.out.println("=-=-= LOGI-DUTRA =-=-=");

        if (logged != 1) {
            do {
                System.out.println("1 =-=-= LOGIN =-=-=");
                System.out.println("||__||");
                escolha = sc.nextLine();

                switch (escolha) {
                    case "1":
                        logged = login(admin);
                        break;
                    default:
                        System.out.println("Opção inválida!");
                        break;
                }

            } while (logged != 1);
        }

        do{
        System.out.println("1 =-=-= LOGADO          =-=-=");
        System.out.println("2 =-=-= ROMANEIOS       =-=-=");

        if (admin[0]) {
            System.out.println("3 =-=-= CRIAR ROMANEIOS =-=-=");
            System.out.println("4 =-=-= CADASTRAR       =-=-=");
            System.out.println("5 =-=-= VER CADSASTROS  =-=-=");
        }

        System.out.println("0 =-=-= ENCERRAR         =-=-=");
        escolha = sc.nextLine();

        switch(escolha) {
            case "1":
                login(admin);
                break;
            case "2":
                romaneios();
                break;
            case "3":
                if(admin[0] == true) {
                    criar_romaneios();
                } else {
                    System.out.println("Indisponível");
                }
                break;
            case "4":
                if(admin[0] == true) {
                    cadastro();
                } else {
                    System.out.println("Indisponível");
                }
                break;
            case "5":
                if(admin[0] == true) {
                    ver_cadastros();
                } else {
                    System.out.println("Indisponível");
                }
                break;

            default:
                System.out.println("Indisponível");
                break;
        }
        } while(!escolha.equals("0"));
    }
}
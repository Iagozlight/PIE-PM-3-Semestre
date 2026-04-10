package org.example;
import org.example.Repository.Motoristasrepository;
import org.example.config.FlyWayconfig;
import org.example.models.Motoristas;
import org.example.models.Usuarios;
import org.example.repositories.CustomizerFactory;
import org.example.Repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import org.flywaydb.core.Flyway;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        FlyWayconfig.migrate();

        while (true) {
            EntityManager em = CustomizerFactory.getEntityManager();// aqui cria o entitymanager que vai conversar com o banco
            UsuarioRepository usuarioRepository = new UsuarioRepository(em);// o "em" é o EntityManager que será usado pelo repository para manipular o banco de dados
            Motoristasrepository motoristasrepository = new Motoristasrepository(em);
            String condicao;

            Scanner sc = new Scanner(System.in);

            System.out.println("Selecione uma opção\n" +
                    "\n1: Cadastrar usuario" +
                    "\n2: Novo motorista" +
                    "\n3: Exibir usuarios" +
                    "\n4: Alterar senha" +
                    "\n5: Remover usuario");
            String opcao = sc.nextLine();

            switch (opcao) {
                case "1" :
                    novoUsuario(usuarioRepository); break;
                case "2":
                    novoMotorista(motoristasrepository,usuarioRepository);break;
                case "3":
                    exibirUsuarios(usuarioRepository);break;
                case "4":
                    alterarSenha(usuarioRepository);break;
                case"5":
                    removerUsuario(usuarioRepository);break;
                default:
                    System.out.println("opção invalida");
                    return;
            }
        }
    }

    static void novoUsuario(UsuarioRepository usuarioRepository) {//sem passa os parametros o metodo nao conseguiria acessar as variaveis de instancia

        while (true) { //depois ajusta isso
            Scanner sc = new Scanner(System.in);

            System.out.println("digite S para sair do loop ou C para cadastrar");
            String condição = sc.nextLine();

            if (condição.equals("S") || condição.equals("s") ){
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

                usuarioRepository.create(novo);// substitui o lista.add(novo) pelo create do banco de dados

                System.out.println("USUARIO CADASTRADO");

            }
        }
    }

    static  void novoMotorista ( Motoristasrepository motoristasrepository, UsuarioRepository usuarioRepository) {
        Scanner sc = new Scanner(System.in);
        Motoristas motoristas = new Motoristas();

        Usuarios cadastrado = null;

        System.out.println("Nome do motorista: ");
        motoristas.setNome(sc.nextLine());

        System.out.println("Data de nascimento: (ex DD/MM/AAAA)");
        String dataStr = sc.nextLine();
        LocalDate data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        motoristas.setData_nascimento(data);

        List<Usuarios> lista = usuarioRepository.findAll();

        System.out.println("Nome de Usuario desse motorista: ");
        String user = sc.nextLine();

        for (Usuarios u : lista) {
            if (u.getUsuario().equals(user)){
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
            } catch (Exception e) {
                if (e.getMessage().contains("Motorista deve ter mais de 24 anos")) {
                    System.out.println("Cadastro negado: pela questão do seguro, o motorista deve ter mais de 24 anos!");
                }
            }

        }else {
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

    static void alterarSenha (UsuarioRepository usuarioRepository) {
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
                if (u.getUsuario().equals(user)){
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
                }else {
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

    static void removerUsuario (UsuarioRepository usuarioRepository) {
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
            if (usuarioencontrado!= null){
                usuarioRepository.delete(usuarioencontrado);
                encontrado = true;
                System.out.println("Usuario deletado com sucesso!!");
            }else {
                System.out.println("Usuario não encontrado!!\n" +
                        "Digite um usuario valido: ");
                exibirUsuarios(usuarioRepository);
                user = sc.nextLine();
            }
        }
    }
}
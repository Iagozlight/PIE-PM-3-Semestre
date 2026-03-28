import java.util.ArrayList;
import java.util.Scanner;

public class Funcionarios {

    private ArrayList<Usuario> lista = new ArrayList<>();

    void novo_Usuario() {

        while (true) { //depois ajusta isso
            Scanner sc = new Scanner(System.in);

            System.out.println("digite S para sair do loop ou C para cadastrar");
            String condição = sc.nextLine();

            if (condição.equals("S") || condição.equals("s") ){
                break;
            }
            if (condição.equals("C") || condição.equals("c")) {
                Usuario novo = new Usuario();
                System.out.println("\nCADASTRO DE USUARIO");

                System.out.println("Usuario: ");
                novo.setUsuario(sc.nextLine());

                System.out.println("Senha: ");
                novo.setSenha(sc.nextLine());

                lista.add(novo);

                System.out.println("USUARIO CADASTRADO");

            }
        }
    }

    void exibir_Funcionario() {
        System.out.println("\n\tLISTA DE USUARIOS!!");
        for (Usuario u : lista) {
            System.out.println("\nUsuario:" + u.getUsuario());
        }
    }

    void alterar_Senha() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Digite o nome do usuario ");
        String user = sc.nextLine();

        boolean encontrado = false;
        boolean senhaCorreta = false;
        String senha_Digitada;

        while (encontrado == false) {

            for (int i =0; i < lista.size(); i ++) {

                if (lista.get(i).getUsuario().equals(user)) {
                    System.out.println("Senha antiga:");
                    senha_Digitada = sc.nextLine();

                    if (senha_Digitada.equals(lista.get(i).getSenha())) {
                        System.out.println("Nova senha: ");
                        String nova_senha = sc.nextLine();

                        lista.get(i).setSenha(nova_senha);
                        System.out.println("Senha alterada");
                        encontrado = true;
                        senhaCorreta = true;
                    }
                }
            }
            if (encontrado == false) {
                System.out.println("Usuario nao encontrado!\n" +
                        "Digite um usuario valido: ");
                user = sc.nextLine();
            }
            if (senhaCorreta == false) {
                System.out.println("Senha incorreta:\n" +
                        "Digite novamente: ");
                senha_Digitada = sc.nextLine();
            }
        }
    }

    void remover_Usuario() {
        Scanner sc = new Scanner(System.in);
        Usuario usuario = new Usuario();

        System.out.println("Digite o nome do usuario que deseja remover: ");
        usuario.setUsuario(sc.nextLine());

        boolean encontrado = false;

        while (encontrado == false) {
            for (int i = 0; i < lista.size(); i ++) {
                if (lista.get(i).getUsuario().equals(usuario.getUsuario())) {
                    lista.remove(i);
                    encontrado = true;
                    System.out.println("Usuario removido");
                }
            }
            if (encontrado == false) {
                System.out.println("Usuario nao encontrado, digite um usuario valido!" +
                        "\nDigite o nome do usuario que deseja remover:");
                usuario.setUsuario(sc.nextLine());
            }
        }
    }
}
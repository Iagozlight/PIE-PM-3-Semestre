import java.util.ArrayList;
import java.util.Scanner;

public class Funcionarios {

    private ArrayList<Usuario> lista = new ArrayList<>();

    void novo_Usuario() {

        Usuario novo = new Usuario();
        Scanner sc = new Scanner(System.in);

        System.out.println("\nCADASTRO DE USUARIO");

        System.out.println("Usuario: ");
        novo.setUsuario(sc.nextLine());

        System.out.println("Senha: ");
        novo.setSenha(sc.nextLine());

        lista.add(novo);

        System.out.println("USUARIO CADASTRADO");
    }

}
package org.example;
import org.example.models.Usuarios;
import org.example.repositories.CustomizerFactory;
import org.example.Repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        EntityManager em = CustomizerFactory.getEntityManager();// aqui cria o entitymanager que vai conversar com o banco
        UsuarioRepository usuarioRepository = new UsuarioRepository(em);// o "em" é o EntityManager que será usado pelo repository para manipular o banco de dados
        novoUsuario(usuarioRepository);// chamando o metodo
        exibirUsuarios(usuarioRepository);
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
                System.out.println("\nCADASTRO DE USUARIO");

                System.out.println("Usuario: ");
                novo.setUsuario(sc.nextLine());

                System.out.println("Senha: ");
                novo.setSenha(sc.nextLine());

                usuarioRepository.create(novo);// substitui o lista.add(novo) pelo create do banco de dados

                System.out.println("USUARIO CADASTRADO");

            }
        }
    }

    static void exibirUsuarios(UsuarioRepository usuarioRepository) {
        List<Usuarios> lista = usuarioRepository.findAll();//faz a busca de todos os usuarios de todos os usuarios do banco de dados e retorna uma lista
        for (Usuarios u : lista) {
            System.out.println("Usuario: " + u.getUsuario());
        }
    }
}
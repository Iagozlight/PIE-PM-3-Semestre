import java.util.Scanner;

public class main {
    public static void main(String[] args){
        Funcionarios funcionarios = new Funcionarios();

        while(true) {
            int opcao;
            Scanner sc = new Scanner(System.in);


            System.out.println("\t\tSelecione uma opção\n"+
                    "1: Novo usuario\n" +
                    "2: Remover usuario\n" +
                    "3: Alterar senha\n" +
                    "4: Lista de usuarios\n");
            opcao = sc.nextInt();



            switch (opcao) {
                case 1:
                    funcionarios.novo_Usuario(); break;

                case 2:
                    funcionarios.remover_Usuario();break;

                case 3:
                    funcionarios.alterar_Senha();break;

                case 4:
                    funcionarios.exibir_Funcionario();break;

                default:
                    System.out.println("Opção invalida");
            }
        }
    }
}

package projeto.views.componentes;

import projeto.services.UsuariosService;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class NovoUsuarioComponentes {
    private UsuariosService usuarioService;

    public NovoUsuarioComponentes(UsuariosService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public void cadastrar(String usuario,
                          String senha,
                          JFrame frame) {
        try {
            usuarioService.criarUsuario(usuario, senha);
            JOptionPane.showMessageDialog(
                    frame,
                    "Usuário cadastrado com sucesso!"
            );

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    frame,
                    e.getMessage()
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Erro ao cadastrar usuário!"
            );
        }
    }
}
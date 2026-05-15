package projeto.views.componentes;

import projeto.services.UsuariosService;

import javax.swing.*;

public class RemoverUsuariosComponentes {

    private UsuariosService usuarioService;

    public RemoverUsuariosComponentes(UsuariosService usuarioService){
        this.usuarioService = usuarioService;
    }

    public void remover(String usuario,
                        String confirmacao,
                        JFrame frame) {

        if (!usuario.equals(confirmacao)) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Os campos não são iguais!"
            );
            return;
        }
        try {
            usuarioService.removerUsuario(usuario);

            JOptionPane.showMessageDialog(
                    frame,
                    "Usuário removido com sucesso!"
            );
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    frame,
                    e.getMessage()
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Erro ao remover usuário!"
            );
        }
    }
}
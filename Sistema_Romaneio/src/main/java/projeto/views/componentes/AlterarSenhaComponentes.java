package projeto.views.componentes;

import projeto.services.UsuariosService;

import javax.swing.*;

public class AlterarSenhaComponentes {

    private UsuariosService usuarioService;

    public AlterarSenhaComponentes(UsuariosService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public void alterar(String usuario,
                        String senhaAntiga,
                        String novaSenha,
                        JFrame frame) {
        try {
            usuarioService.alterarSenha(
                    usuario,
                    senhaAntiga,
                    novaSenha
            );
            JOptionPane.showMessageDialog(
                    frame,
                    "Senha alterada com sucesso!"
            );
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    frame,
                    e.getMessage()
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Erro ao alterar senha!"
            );
        }
    }
}
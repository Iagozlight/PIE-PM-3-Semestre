package org.example.componentes;

import jakarta.persistence.JoinColumn;
import org.example.service.UsuarioService;

import javax.swing.*;

public class RemoverUsuariosComponentes {

    private UsuarioService usuarioService;

    public RemoverUsuariosComponentes(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    public void remover (String usuario, String confirmacao, JFrame frame) {
        if (!usuario.equals(confirmacao)){
            JOptionPane.showMessageDialog(frame,"Os campos nao sao iguais");
            return;
        }

        String resultado = usuarioService.remover(usuario);

        switch (resultado) {
            case "Sucesso":
                JOptionPane.showMessageDialog(frame, "Usuario removido com sucesso!");break;
            case "Usuario nao encntrado":
                JOptionPane.showMessageDialog(frame, "Usuario não encontrado!");break;
            default:
                JOptionPane.showMessageDialog(frame, "Erro ao remover usuario!");break;
        }
    }
}

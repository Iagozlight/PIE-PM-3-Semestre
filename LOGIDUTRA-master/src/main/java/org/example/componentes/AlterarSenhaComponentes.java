package org.example.componentes;

import org.example.service.UsuarioService;

import javax.swing.*;

public class AlterarSenhaComponentes {

    private UsuarioService usuarioService;

    public AlterarSenhaComponentes(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public void alterar(String usuario, String senhaAntiga, String novaSenha, JFrame frame) {
        String resultado = usuarioService.alterarSenha(usuario, senhaAntiga, novaSenha);

        switch (resultado) {
            case "Sucesso":
                JOptionPane.showMessageDialog(frame, "Senha alterada com sucesso!");break;
            case "Usuario nao encontrado":
                JOptionPane.showMessageDialog(frame, "Usuario nao encontrado!");break;
            case "Senha incorreta":
                JOptionPane.showMessageDialog(frame, "Senha incorreta!");break;
            default:
                JOptionPane.showMessageDialog(frame, "Erro ao alterar a senha!");break;
        }
    }
}

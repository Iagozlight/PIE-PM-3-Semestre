package org.example.componentes;

import org.example.service.UsuarioService;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class NovoUsuarioComponentes {

    private UsuarioService usuarioService;

    public NovoUsuarioComponentes(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public void cadastrar(String usuario, String senha, JFrame frame) {
        String resultado = usuarioService.cadastrar(usuario, senha);
        if (resultado.equals("Sucesso")) {
            JOptionPane.showMessageDialog(frame, "Usuario cadastrado com sucesso!");
        } else {
            JOptionPane.showMessageDialog(frame, "Nome de usuario ja em uso!");
        }
    }
}
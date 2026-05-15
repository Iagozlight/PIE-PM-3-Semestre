package org.example.service;

import org.example.Repository.UsuarioRepository;
import org.example.models.Usuarios;
import java.util.List;

public class UsuarioService {
    private UsuarioRepository usuarioRepository;

    public UsuarioService (UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public String cadastrar (String usuario, String senha) {
        try {
            Usuarios novo = new Usuarios();
            novo.setUsuario(usuario);
            novo.setSenha(senha);
            usuarioRepository.create(novo);
            return "Sucesso";
        } catch (Exception e) {
            return "Erro";
        }
    }

    public List<Usuarios> listar() {
        return usuarioRepository.findAll();
    }
}

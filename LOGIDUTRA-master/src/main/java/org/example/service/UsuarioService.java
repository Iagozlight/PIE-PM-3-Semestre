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

    public String alterarSenha(String usuario, String senhaAntiga, String novaSenha) {
        try {
            List<Usuarios> lista = usuarioRepository.findAll();
            Usuarios encontrado = null;

            for (Usuarios u : lista) {
                if (u.getUsuario().equals(usuario)){
                    encontrado = u;
                }
            }
            if (encontrado == null) return "Usuario nao encontrado";

            if (!encontrado.getSenha().equals(senhaAntiga)) return "Senha incorreta";

            encontrado.setSenha(novaSenha);
            usuarioRepository.update(encontrado);
            return "Sucesso";
        } catch (Exception e) {
            return "Erro";
        }
    }

    public String remover (String usuario) {
        try {
            List<Usuarios> lista = usuarioRepository.findAll();
            Usuarios encontrado = null;

            for (Usuarios u : lista) {
                if (u.getUsuario().equals(usuario)) {
                    encontrado = u;
                }
            }

            if (encontrado == null) return "Usuario nao encontrado";

            usuarioRepository.delete(encontrado);
            return "Sucesso";
        } catch (Exception e) {
            return "Erro";
        }
    }
}

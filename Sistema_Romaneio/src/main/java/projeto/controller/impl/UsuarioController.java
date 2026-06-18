package projeto.controller.impl;

import projeto.controller.dto.SessaoUsuario;
import projeto.models.entity.Usuarios;
import projeto.models.repositories.MotoristasRepository;
import projeto.models.repositories.UsuarioRepository;
import projeto.models.services.UsuariosService;

import java.time.LocalDate;
import java.util.List;

public class UsuarioController {

    private final UsuariosService usuariosService;

    public UsuarioController(UsuarioRepository usuarioRepository, MotoristasRepository motoristasRepository) {
        this.usuariosService = new UsuariosService(usuarioRepository, motoristasRepository);
    }

    public SessaoUsuario autenticar(String username, String senha) {
        return usuariosService.autenticar(username, senha);
    }

    public void criarUsuario(String username, String senha) {
        usuariosService.criarUsuario(username, senha);
    }

    public void alterarSenha(String username, String senhaAntiga, String novaSenha) {
        usuariosService.alterarSenha(username, senhaAntiga, novaSenha);
    }

    public void removerUsuario(String username) {
        usuariosService.removerUsuario(username);
    }

    public void criarMotorista(String nome, LocalDate dataNascimento, String username) {
        usuariosService.criarMotorista(nome, dataNascimento, username);
    }

    public List<Usuarios> listarUsuarios() {
        return usuariosService.listarUsuarios();
    }
}

package projeto.services;

import projeto.Main;
import projeto.models.Motoristas;
import projeto.models.Usuarios;
import projeto.repositories.MotoristasRepository;
import projeto.repositories.UsuarioRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class UsuariosService {

    private UsuarioRepository usuarioRepository;
    private MotoristasRepository motoristasRepository;

    public UsuariosService(UsuarioRepository usuarioRepository,
                          MotoristasRepository motoristasRepository) {
        this.usuarioRepository = usuarioRepository;
        this.motoristasRepository = motoristasRepository;
    }

    public Main.SessaoUsuario autenticar(String username, String senha) {
        List<Usuarios> usuariosDb = usuarioRepository.findAll();
        for (Usuarios u : usuariosDb) {
            if (u.getUsuario().equals(username) && u.getSenha().equals(senha)) {
                boolean isAdmin = u.temPermissao("ADMIN");

                Motoristas motorista = null;
                if (!isAdmin) {
                    List<Motoristas> todos = motoristasRepository.findAll();
                    for (Motoristas m : todos) {
                        if (m.getUsuarios() != null && m.getUsuarios().getId().equals(u.getId())) {
                            motorista = m;
                            break;
                        }
                    }
                }
                return new Main.SessaoUsuario(u, isAdmin, motorista);
            }
        }
        return null;
    }

    public void criarUsuario(String username, String senha) {
        List<Usuarios> existentes = usuarioRepository.findAll();
        for (Usuarios u : existentes) {
            if (u.getUsuario().equals(username)) {
                throw new IllegalArgumentException("Usuário já existente, escolha outro nome!");
            }
        }
        Usuarios novo = new Usuarios();
        novo.setUsuario(username);
        novo.setSenha(senha);
        usuarioRepository.create(novo);
    }

    public void alterarSenha(String username, String senhaAntiga, String novaSenha) {
        Usuarios usuario = usuarioRepository.findByUsuario(username);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado!");
        }
        if (!usuario.getSenha().equals(senhaAntiga)) {
            throw new IllegalArgumentException("Senha incorreta!");
        }
        usuario.setSenha(novaSenha);
        usuarioRepository.update(usuario);
    }

    public void removerUsuario(String username) {
        Usuarios usuario = usuarioRepository.findByUsuario(username);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado!");
        }
        usuarioRepository.delete(usuario);
    }

    public void criarMotorista(String nome, LocalDate dataNascimento, String username) {
        int idade = Period.between(dataNascimento, LocalDate.now()).getYears();
        if (idade < 24) {
            throw new IllegalArgumentException("Motorista deve ter mais de 24 anos!");
        }

        Usuarios usuario = usuarioRepository.findByUsuario(username);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado!");
        }

        Motoristas motorista = new Motoristas();
        motorista.setNome(nome);
        motorista.setData_nascimento(dataNascimento);
        motorista.setUsuarios(usuario);
        motoristasRepository.create(motorista);
    }

    public List<Usuarios> listarUsuarios() {
        return usuarioRepository.findAll();
    }
}
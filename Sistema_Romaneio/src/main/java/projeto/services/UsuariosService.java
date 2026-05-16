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

    private final UsuarioRepository usuarioRepository;
    private final MotoristasRepository motoristasRepository;

    public UsuariosService(UsuarioRepository usuarioRepository,
                           MotoristasRepository motoristasRepository) {
        this.usuarioRepository = usuarioRepository;
        this.motoristasRepository = motoristasRepository;
    }

    public Main.SessaoUsuario autenticar(String username, String senha) {
        if (username == null || senha == null) {
            return null;
        }

        if ("admin".equals(username) && "admin123".equals(senha)) {
            Usuarios admin = new Usuarios();
            admin.setUsuario("admin");
            admin.setSenha("admin123");
            return new Main.SessaoUsuario(admin, true, null);
        }

        Usuarios usuario = usuarioRepository.findByUsuario(username);
        if (usuario == null || !usuario.getSenha().equals(senha)) {
            return null;
        }

        boolean isAdmin = usuario.temPermissao("ADMIN");
        Motoristas motorista = null;
        if (!isAdmin) {
            List<Motoristas> todos = motoristasRepository.findAll();
            for (Motoristas m : todos) {
                if (m.getUsuarios() != null && m.getUsuarios().getId().equals(usuario.getId())) {
                    motorista = m;
                    break;
                }
            }
        }
        return new Main.SessaoUsuario(usuario, isAdmin, motorista);
    }

    public void criarUsuario(String username, String senha) {
        try {
            if ("admin".equalsIgnoreCase(username)) {
                throw new IllegalArgumentException("Usuario admin e reservado para acesso do sistema!");
            }

            Usuarios novo = new Usuarios();
            novo.setUsuario(username);
            novo.setSenha(senha);
            usuarioRepository.create(novo);

        } catch (Exception e) {
            Throwable cause = e;
            while (cause != null) {
                if (cause.getMessage() != null &&
                        cause.getMessage().contains("Usuario ja existente")) {
                    throw new IllegalArgumentException(
                            "Usuario ja existente, escolha outro nome!"
                    );
                }
                cause = cause.getCause();
            }
            if (e instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) e;
            }
            throw e;
        }
    }

    public void alterarSenha(String username, String senhaAntiga, String novaSenha) {
        if ("admin".equalsIgnoreCase(username)) {
            throw new IllegalArgumentException("A senha do usuario admin nao pode ser alterada!");
        }

        Usuarios usuario = usuarioRepository.findByUsuario(username);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario nao encontrado!");
        }
        if (!usuario.getSenha().equals(senhaAntiga)) {
            throw new IllegalArgumentException("Senha incorreta!");
        }
        usuario.setSenha(novaSenha);
        usuarioRepository.update(usuario);
    }

    public void removerUsuario(String username) {
        if ("admin".equalsIgnoreCase(username)) {
            throw new IllegalArgumentException("O usuario admin nao pode ser removido!");
        }

        Usuarios usuario = usuarioRepository.findByUsuario(username);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario nao encontrado!");
        }
        usuarioRepository.delete(usuario);
    }

    public void criarMotorista(String nome, LocalDate dataNascimento, String username) {
        int idade = Period.between(dataNascimento, LocalDate.now()).getYears();
        if (idade < 24) {
            throw new IllegalArgumentException("Motorista deve ter mais de 24 anos!");
        }

        if ("admin".equalsIgnoreCase(username)) {
            throw new IllegalArgumentException("O usuario admin nao pode ser vinculado como motorista!");
        }

        Usuarios usuario = usuarioRepository.findByUsuario(username);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario nao encontrado!");
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

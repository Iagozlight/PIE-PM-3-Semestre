package org.example.service;

import org.example.Repository.Motoristasrepository;
import org.example.Repository.UsuarioRepository;
import org.example.models.Motoristas;
import org.example.models.Usuarios;
import java.time.LocalDate;

public class MotoristaService {
    private Motoristasrepository motoristasrepository;
    private UsuarioRepository usuarioRepository;


    public MotoristaService(Motoristasrepository motoristasrepository, UsuarioRepository usuarioRepository) {
        this.motoristasrepository = motoristasrepository;
        this.usuarioRepository = usuarioRepository;
    }

    public String cadastrar(String nome, LocalDate dataNascimento, String usuario) {
        try {
            Usuarios cadastrado = null;
            for (Usuarios u : usuarioRepository.findAll()) {
                if (u.getUsuario().equals(usuario)) {
                    cadastrado = u;
                }
            }
            if (cadastrado == null) return "Usuario nao encontrado";

            Motoristas motoristas = new Motoristas();
            motoristas.setNome(nome);
            motoristas.setData_nascimento(dataNascimento);
            motoristas.setUsuarios(cadastrado);

            motoristasrepository.create(motoristas);
            return "Sucesso";
        } catch (Exception e) {
            if (e.getMessage().contains("Motorista deve ter mais de 24 anos")) {
                return "Idade_invalida";
            }
            return "erro";
        }
    }
}

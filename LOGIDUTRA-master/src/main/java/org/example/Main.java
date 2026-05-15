package org.example;
import org.example.Repository.Motoristasrepository;
import org.example.View.MenuUsuarios;
import org.example.config.FlyWayconfig;
import org.example.models.Motoristas;
import org.example.models.Usuarios;
import org.example.repositories.CustomizerFactory;
import org.example.Repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import org.example.service.MotoristaService;
import org.example.service.UsuarioService;
import org.flywaydb.core.Flyway;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        FlyWayconfig.migrate();

        EntityManager em = CustomizerFactory.getEntityManager();
        UsuarioRepository usuarioRepository = new UsuarioRepository(em);
        Motoristasrepository motoristasrepository = new Motoristasrepository(em);
        UsuarioService usuarioService = new UsuarioService(usuarioRepository);
        MotoristaService motoristaService = new MotoristaService(motoristasrepository, usuarioRepository);

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new org.example.View.MenuUsuarios(usuarioService, motoristaService).setVisible(true);
            }
        });
    }
}

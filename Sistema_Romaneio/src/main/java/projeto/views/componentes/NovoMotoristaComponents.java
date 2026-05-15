package projeto.views.componentes;

import jakarta.persistence.JoinColumn;
import projeto.services.MotoristaService;
import projeto.services.UsuariosService;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class NovoMotoristaComponents {

    private MotoristaService motoristaService;
    private UsuariosService usuarioService;

    public NovoMotoristaComponents(MotoristaService motoristaService, UsuariosService usuarioService) {
        this.motoristaService = motoristaService;
        this.usuarioService = usuarioService;
    }

    public void cadastrar(String nome, String dataStr, String usuario, JFrame frame) {

        try {
            LocalDate data = LocalDate.parse(
                    dataStr,
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
            );
            usuarioService.criarMotorista(nome, data, usuario);

            JOptionPane.showMessageDialog(
                    frame,
                    "Motorista cadastrado com sucesso!"
            );
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    frame,
                    e.getMessage()
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Formato de data inválido! Use DD/MM/AAAA"
            );
        }

    }
}
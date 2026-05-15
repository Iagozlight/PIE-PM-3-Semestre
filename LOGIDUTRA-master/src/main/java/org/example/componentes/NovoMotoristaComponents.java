package org.example.componentes;

import jakarta.persistence.JoinColumn;
import org.example.service.MotoristaService;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class NovoMotoristaComponents {

    private MotoristaService motoristaService;

    public NovoMotoristaComponents(MotoristaService motoristaService) {
        this.motoristaService = motoristaService;
    }

    public void  cadastrar(String nome, String dataStr, String usuario, JFrame frame) {
        try {
            LocalDate data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String resultado = motoristaService.cadastrar(nome, data, usuario);

            switch (resultado) {
                case "Sucesso":
                    JOptionPane.showMessageDialog(frame, "Motorista cadastrado com sucesso!");break;
                case "idade_invalida":
                    JOptionPane.showMessageDialog(frame, "Motorista deve ter mais de 24 anos!");break;
                case "Usuario nao encontrado":
                    JOptionPane.showMessageDialog(frame, "Usuario nao encontrado!");break;
                default:
                    JOptionPane.showMessageDialog(frame, "Erro ao cadastrar motorista");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Formato de data invalido! use DD/MM/AAAA");
        }
    }
}

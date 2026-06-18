package projeto.controller.dto;

import projeto.models.entity.Motoristas;
import projeto.models.entity.Usuarios;

public class SessaoUsuario {

    private final Usuarios usuario;
    private final boolean admin;
    private final Motoristas motorista;

    public SessaoUsuario(Usuarios usuario, boolean admin, Motoristas motorista) {
        this.usuario = usuario;
        this.admin = admin;
        this.motorista = motorista;
    }

    public boolean isAdmin() {
        return admin;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public Motoristas getMotorista() {
        return motorista;
    }
}

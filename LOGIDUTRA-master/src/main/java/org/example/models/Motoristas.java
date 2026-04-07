    package org.example.models;

    import jakarta.persistence.*;
    import org.hibernate.sql.results.internal.StandardRowReader;

    @Entity
    @Table (name = "motorista")

    public class Motoristas {

        @OneToOne//relacionamento um pra um
        @JoinColumn (name = "usuario_id")//cria uma coluna na tabela motoristas que guarda o id do usuario relacionado
        private Usuarios usuarios;

        public Usuarios getUsuarios() {
            return usuarios;
        }

        public void setUsuarios(Usuarios usuarios) {
            this.usuarios = usuarios;
        }

        @Id
        @GeneratedValue (strategy = GenerationType.IDENTITY)
        long id;
        private String nome;
        private String senha;

        public Motoristas () {};

        public Motoristas(long id, String nome, String senha) {
            this.id = id;
            this.nome = nome;
            this.senha = senha;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getSenha() {
            return senha;
        }

        public void setSenha(String senha) {
            this.senha = senha;
        }
    }

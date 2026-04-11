    package org.example.models;

    import jakarta.persistence.*;
    import org.hibernate.sql.results.internal.StandardRowReader;

    import java.time.LocalDate;

    @Entity
    @Table (name = "motorista")

    public class Motoristas {

        @OneToOne//relacionamento um pra um
        @JoinColumn (name = "usuario_id", nullable = false)//cria uma coluna na tabela motoristas que guarda o id do usuario relacionado
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
        private LocalDate data_nascimento;

        public Motoristas () {};

        public Motoristas(long id, String nome, String senha, LocalDate data_nascimento) {
            this.id = id;
            this.nome = nome;
            this.data_nascimento = data_nascimento;
        }

        public LocalDate getData_nascimento() {
            return data_nascimento;
        }

        public void setData_nascimento(LocalDate data_nascimento) {
            this.data_nascimento = data_nascimento;
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

    }

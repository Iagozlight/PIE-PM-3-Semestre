package org.example.Repository;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.example.models.Usuarios;

public class UsuarioRepository {
    private EntityManager em;//EntityManager serve pra interagir com banco de dados, o "em" é um atributo que eu defini do EntityManager

    public UsuarioRepository(EntityManager em) {this.em = em;} //construtor da classe, o atributo "em" da classe vai receber o valor do "em" que veio no construtor

    public Usuarios findbyId(Long id) { return (Usuarios) this.em.find(Usuarios.class, id);}//Busca de usuario pelo ID, o "find" é o metodo de busca

    public void create (Usuarios usuarios) {
        this.em.getTransaction().begin();//começa a operação
        this.em.persist(usuarios);// faz a operação
        this.em.getTransaction().commit();// vai salva caso de certo, se der errado cancela tudo, basicamente nao fica em um meio termo
    }

    public void update (Usuarios usuarios) {
        em.getTransaction().begin();
        em.persist(usuarios);
        em.getTransaction().commit();
    }

    public void delete (Usuarios usuarios) {
        em.getTransaction().begin();
        em.remove(em.contains(usuarios) ? usuarios : em.merge(usuarios));/* o "em.contains" vai verifica se o objeto é gerenciado pelo EntityManager
                                                                         usa o operador ternary pra verificar, se ja esta gerenciado usa usuarios e remove
                                                                         se não esta sendo gerenciado faz o merge com o usuarios e ai remove*/
        em.getTransaction().commit();
    }

    public List<Usuarios> findAll() { return em.createQuery("select u from Usuarios u", Usuarios.class).getResultList();}/* busca todos os usuarios do banco de dados
                                                                                                                            e retorna uma lista*/
    public List<Usuarios> findByName(String prefixo) {//esse prefixo é so o nome da variavel, passamos ela como parametro
        return  em.createQuery("select u from Usuarios u where u.nome like :prefixo", Usuarios.class)// o like é um metodo de busca flexivel

                .setParameter("prefixo", prefixo + "%")//o prefixo antes do "%" define que "começa com" ex: digitou "ma" vai exibir mateus, maria, madalena etc..

                .getResultList();
    }
}

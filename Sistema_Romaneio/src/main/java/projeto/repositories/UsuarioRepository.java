package projeto.repositories;

import jakarta.persistence.EntityManager;
import java.util.List;
import projeto.models.Usuarios;

public class UsuarioRepository {
    private EntityManager em;//EntityManager serve pra interagir com banco de dados, o "em" Ã© um atributo que eu defini do EntityManager

    public UsuarioRepository(EntityManager em) {this.em = em;} //construtor da classe, o atributo "em" da classe vai receber o valor do "em" que veio no construtor

    public Usuarios findbyId(Long id) { return (Usuarios) this.em.find(Usuarios.class, id);}//Busca de usuario pelo ID, o "find" Ã© o metodo de busca

    public void create (Usuarios usuarios) {
        List<Usuarios> existentes = em.createQuery("select u from Usuarios u where u.usuario = :usuario", Usuarios.class)
                .setParameter("usuario", usuarios.getUsuario())
                .getResultList();
        if (!existentes.isEmpty()) {
            throw new IllegalArgumentException("Usuario ja existente, escolha outro nome de usuario!");
        }
        this.em.getTransaction().begin();//comeÃ§a a operaÃ§Ã£o
        this.em.persist(usuarios);// faz a operaÃ§Ã£o
        this.em.getTransaction().commit();// vai salva caso de certo, se der errado cancela tudo, basicamente nao fica em um meio termo
    }

    public void update (Usuarios usuarios) {
        em.getTransaction().begin();
        em.merge(usuarios);
        em.getTransaction().commit();
    }

    public void delete (Usuarios usuarios) {
        em.getTransaction().begin();
        em.remove(em.contains(usuarios) ? usuarios : em.merge(usuarios));/* o "em.contains" vai verifica se o objeto Ã© gerenciado pelo EntityManager
                                                                         usa o operador ternary pra verificar, se ja esta gerenciado usa usuarios e remove
                                                                         se nÃ£o esta sendo gerenciado faz o merge com o usuarios e ai remove*/
        em.getTransaction().commit();
    }

    public List<Usuarios> findAll() { return em.createQuery("select u from Usuarios u", Usuarios.class).getResultList();}/* busca todos os usuarios do banco de dados
                                                                                                                            e retorna uma lista*/
    public List<Usuarios> findByName(String prefixo) {//esse prefixo Ã© so o nome da variavel, passamos ela como parametro
        return  em.createQuery("select u from Usuarios u where u.usuario like :prefixo", Usuarios.class)// o like Ã© um metodo de busca flexivel

                .setParameter("prefixo", prefixo + "%")//o prefixo antes do "%" define que "comeÃ§a com" ex: digitou "ma" vai exibir mateus, maria, madalena etc..

                .getResultList();
    }

    public Usuarios findByUsuario(String usuario) {
        List<Usuarios> usuarios = em.createQuery("select u from Usuarios u where u.usuario = :usuario", Usuarios.class)
                .setParameter("usuario", usuario)
                .getResultList();
        if (usuarios.isEmpty()) {
            return null;
        }
        return usuarios.get(0);
    }
}

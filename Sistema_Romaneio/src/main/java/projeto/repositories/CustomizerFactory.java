package projeto.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class CustomizerFactory {

    private static final EntityManagerFactory emf;/*emf Ã© a variavel de EntityManagerFactory, o static Ã© por que vai ser uma so pra toda a aplicaÃ§Ã£o
                                                  o final serÃ¡ inicializadao uma Ãºnica vez e nÃ£o pode ser alterada depois*/

    static {
        SessionFactory sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();

        emf = sessionFactory.unwrap(EntityManagerFactory.class);
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();// aqui ele vai cria um entitymanager novo toda vez que for chamado
    }

    public static void fechar() {
        emf.close();// aqui ele vai fecha o entitymanager, sempre deve fechar pra evitar vazamento de dados etc
    }


}

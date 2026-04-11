
package org.example.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class CustomizerFactory {
    private static final EntityManagerFactory emf;/*emf é a variavel de EntityManagerFactory, o static é por que vai ser uma so pra toda a aplicação
                                                  o final será inicializadao uma única vez e não pode ser alterada depois*/
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();// aqui ele vai cria um entitymanager novo toda vez que for chamado
    }

    public static void fechar() {
        emf.close();// aqui ele vai fecha o entitymanager, sempre deve fechar pra evitar vazamento de dados etc
    }

    static {
        SessionFactory sessionFactory = (new Configuration()).configure("hibernate.cfg.xml").buildSessionFactory();//crias as conexoe com o hibernate
        emf = (EntityManagerFactory)sessionFactory.unwrap(EntityManagerFactory.class);/*Converte o SessionFactory do Hibernate em um EntityManagerFactory (padrão JPA).
                                                                                      pega do SessionFactory a parte que funciona como EntityManagerFactory*/
    }
}

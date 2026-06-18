package org.example;

import org.hibernate.SessionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EntityScan(basePackages = {"org.example"})
public class StartRestServer {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(StartRestServer.class);
        app.setWebApplicationType(WebApplicationType.SERVLET);
        app.run(args);
        System.out.println("Server pornit!");
    }

    @Bean
    public SessionFactory sessionFactory() {
        return HibernateUtils.getSessionFactory();
    }

    @Bean
    public IService service(SessionFactory sessionFactory) {
        IRepositoryJucator jucatorRepo = new RepositoryJucator(sessionFactory);
        IRepositoryJoc jocRepo = new RepositoryJoc(sessionFactory);
        IRepositoryConfiguratie configRepo = new RepositoryConfiguratie(sessionFactory);
        IRepositoryIncercare incercareRepo = new RepositoryIncercare(sessionFactory);
        return new Service(jucatorRepo, jocRepo, configRepo, incercareRepo);
    }
}

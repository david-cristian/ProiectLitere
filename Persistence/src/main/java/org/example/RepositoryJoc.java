package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class RepositoryJoc extends AbstractRepository<Long, Joc> implements IRepositoryJoc {

    public RepositoryJoc(SessionFactory sessionFactory) {
        super(sessionFactory, Joc.class);
    }

    @Override
    public List<Joc> findByAlias(String alias) {
        logger.traceEntry("Finding by alias {}", alias);
        try (Session session = sessionFactory.openSession()) {
            Transaction tx =  session.beginTransaction();
            try {
                tx = session.beginTransaction();
                Query<Joc> query = session.createQuery("FROM Joc j where j.jucator.alias = :alias", Joc.class);
                query.setParameter("alias", alias);

                List<Joc> jocuri = query.getResultList();
                tx.commit();
                return jocuri;
            } catch (Exception ex) {
                if (tx != null) {
                    tx.rollback();
                }
                logger.error(ex.getMessage());
                return List.of();
            }
        }
    }
}

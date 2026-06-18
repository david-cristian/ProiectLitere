package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Optional;

public class RepositoryJucator extends AbstractRepository<Long, Jucator> implements IRepositoryJucator{

    public RepositoryJucator(SessionFactory sessionFactory) {
        super(sessionFactory, Jucator.class);
    }

    @Override
    public Optional<Jucator> findByAlias(String alias) {
        logger.traceEntry("finding by alias {}", alias);
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                Query<Jucator> query = session.createQuery("from Jucator where alias = :alias", Jucator.class);
                query.setParameter("alias", alias);

                Jucator jucator = query.uniqueResult();

                tx.commit();
                return Optional.ofNullable(jucator);
            } catch (RuntimeException e) {
                if (tx != null) {
                    tx.rollback();
                }
                logger.error(e.getMessage());
                return Optional.empty();
            }
        }
    }
}

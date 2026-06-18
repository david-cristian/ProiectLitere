package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class RepositoryIncercare extends AbstractRepository<Long, Incercare> implements IRepositoryIncercare {

    public RepositoryIncercare(SessionFactory sessionFactory) {
        super(sessionFactory, Incercare.class);
    }

    @Override
    public List<Incercare> getAllIncercariByJoc(Long idJoc) {
        logger.traceEntry("finding all Incercari with Joc {}", idJoc);
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                Query<Incercare> query = session.createQuery("from Incercare m where m.joc.id = :idJoc", Incercare.class);
                query.setParameter("idJoc", idJoc);

                List<Incercare> incercari = query.list();
                tx.commit();
                return incercari;
            } catch (Exception ex) {
                if (tx != null) {
                    tx.rollback();
                }
                logger.error(ex.getMessage(), ex);
                return List.of();
            }
        }
    }
}


package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class RepositoryConfiguratie extends AbstractRepository<Long, Configuratie> implements IRepositoryConfiguratie {
    public RepositoryConfiguratie(SessionFactory sessionFactory) {
        super(sessionFactory, Configuratie.class);
    }

    @Override
    public Configuratie creazaConfiguratie() {
        logger.traceEntry("Creaza configuratie");
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                List<Numere> numere = session.createNativeQuery("select * from Numere order by random() limit 4", Numere.class).list();
                List<Litere> litere = session.createNativeQuery("select * from Litere order by random() limit 4", Litere.class).list();
                List<String> perechi = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    String pereche = litere.get(i).getLitera() + "," + numere.get(i).getNumar();
                    perechi.add(pereche);
                }
                String confFinal = String.join(";", perechi);
                Configuratie configuratie = new Configuratie(confFinal);
                tx.commit();
                return configuratie;
            } catch (Exception e) {
                if (tx != null) {
                    tx.rollback();
                }
                logger.error(e);
                throw e;
            }
        }
    }
}

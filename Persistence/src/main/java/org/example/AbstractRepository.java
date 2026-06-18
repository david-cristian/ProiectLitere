package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public abstract class AbstractRepository<ID extends Number, T extends Entity<ID>> implements IRepository<ID, T>{
    protected final SessionFactory sessionFactory;
    protected final Class<T> entityClass;
    protected static final Logger logger = LogManager.getLogger();

    public AbstractRepository(SessionFactory sessionFactory, Class<T> entityClass) {
        this.sessionFactory = sessionFactory;
        this.entityClass = entityClass;
        logger.info("Initializing AbstractRepository");
    }

    @Override
    public T save(T entity) {
        logger.traceEntry("saving entity {}", entity);
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.persist(entity);
                tx.commit();
                return entity;
            } catch (RuntimeException e) {
                if (tx != null) {
                    tx.rollback();
                }
                logger.error("Hibernate error: " + e.getMessage());
                throw e;
            }
        }
    }

    @Override
    public T update(T entity) {
        logger.traceEntry("updating entity {}", entity);
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                T updatedEntity = session.merge(entity);
                tx.commit();
                return updatedEntity;
            }catch (RuntimeException e) {
                if (tx != null) {
                    tx.rollback();
                }
                logger.error("Hibernate error: " + e.getMessage());
                throw e;
            }
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        logger.traceEntry("finding entity with id {}", id);
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                T entity = session.get(entityClass, id);
                tx.commit();
                return Optional.ofNullable(entity);
            } catch (RuntimeException e) {
                logger.error("Hibernate error: " + e.getMessage());
                return Optional.empty();
            }
        }
    }

    @Override
    public List<T> findAll() {
        logger.traceEntry("finding all entities");
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                List<T> entities = session.createQuery("from " + entityClass.getName(), entityClass).list();
                tx.commit();
                logger.traceExit("found {} entities", entities.size());
                return entities;
            } catch (Exception e) {
                logger.error("Hibernate error: " + e.getMessage());
                return List.of();
            }
        }
    }
}

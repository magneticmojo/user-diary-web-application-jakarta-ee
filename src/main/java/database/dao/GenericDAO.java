package database.dao;

import database.DBUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

/**
 * A generic Data Access Object (DAO) class that provides common CRUD operations for entities within the application.
 * This class serves as a base for specific entity DAO classes, {@link ImageDAO} and {@link UserDAO}.
 * It abstracts the common database operations such as insert, update, and retrieve by ID, ensuring a consistent and
 * reusable implementation.
 * <p>
 * Specific DAO classes that extend this class must provide the entity type they manage, enabling tailored operations
 * for various entity classes within the application.
 *
 * @author Björn Forsberg
 * @param <T> The type of the entity that this DAO manages
 */
public abstract class GenericDAO<T> {

    private Class<T> entityType;

    public GenericDAO(Class<T> entityType) {
        this.entityType = entityType;
    }

    /**
     * Inserts a given entity into the database.
     * @param entity The entity to be inserted
     * @throws RuntimeException if an error occurs during insertion
     */
    public void insert(T entity) {
        EntityManager em = DBUtil.getEMFInstance().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error while inserting entity", e);
        } finally {
            em.close();
        }
    }

    /**
     * Updates a given entity in the database.
     * @param entity The entity to be updated
     * @throws RuntimeException if an error occurs during the update
     */
    public void update(T entity) {
        EntityManager em = DBUtil.getEMFInstance().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error while updating entity", e);
        } finally {
            em.close();
        }
    }

    /**
     * Retrieves an entity by its ID from the database.
     * @param id The ID of the entity to be retrieved
     * @return The entity if found, null otherwise
     */
    public T getById(Long id) {
        try (EntityManager em = DBUtil.getEMFInstance().createEntityManager()) {
            return em.find(entityType, id);
        }
    }

    /**
     * Checks if an entity with the given ID exists in the database. If the ID is -1, which represents an invalid id
     * produced by {@link services.ImageService}, false is returned.
     * @param id The ID of the entity to be checked
     * @return True if the entity exists, false otherwise
     */
    public boolean existsById(Long id) {
        if (id == -1L) {
            return false;
        }
        try (EntityManager em = DBUtil.getEMFInstance().createEntityManager()) {
            return em.find(entityType, id) != null;
        }
    }
}

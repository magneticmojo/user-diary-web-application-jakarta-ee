package database.dao;

import database.DBUtil;
import database.dao.GenericDAO;
import entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

/**
 * Specific Data Access Object (DAO) class for managing {@link User} entities within the application.
 * Extends the generic {@link GenericDAO} class, inheriting common CRUD operations, and provides specialized
 * handling for User entities, such as retrieving users by username or email and fetching user data along
 * with associated diary posts.
 *
 * @author Björn Forsberg
 */
public class UserDAO extends GenericDAO<User> {

    public UserDAO() {
        super(User.class);
    }

    /**
     * Retrieves a user from the database by username.
     *
     * @param username the username to search for
     * @return the User object if found, null otherwise
     * @throws RuntimeException if an error occurs during the search operation
     */
    public User findByUsername(String username) {
        EntityManager em = DBUtil.getEMFInstance().createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> rootEntry = cq.from(User.class);
        cq.select(rootEntry).where(cb.equal(rootEntry.get("username"), username));
        TypedQuery<User> userQuery = em.createQuery(cq);
        try {
            return userQuery.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving user", e);
        } finally {
            em.close();
        }
    }

    /**
     * Retrieves a user from the database by email.
     *
     * @param email the email to search for
     * @return the User object if found, null otherwise
     * @throws RuntimeException if an error occurs during the search operation
     */
    public User findByEmail(String email) {
        EntityManager em = DBUtil.getEMFInstance().createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> rootEntry = cq.from(User.class);
        cq.select(rootEntry).where(cb.equal(rootEntry.get("email"), email));
        TypedQuery<User> userQuery = em.createQuery(cq);
        try {
            return userQuery.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving user", e);
        } finally {
            em.close();
        }
    }

    /**
     * Retrieves a user, and it's associated diary posts from the database by username.
     * @param username the username to search for
     * @return the User object along with associated diary posts if found, null otherwise
     */
    public User getUserWithDiaryPosts(String username) {
        EntityManager em = DBUtil.getEMFInstance().createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> rootEntry = cq.from(User.class);
        rootEntry.fetch("diaryPosts", JoinType.LEFT);
        cq.select(rootEntry).where(cb.equal(rootEntry.get("username"), username));
        TypedQuery<User> userQuery = em.createQuery(cq);
        try {
            return userQuery.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } finally {
            em.close();
        }
    }
}

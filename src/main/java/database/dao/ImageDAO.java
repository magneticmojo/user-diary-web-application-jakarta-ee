package database.dao;

import entities.Image;

/**
 * Specific Data Access Object (DAO) class for managing {@link Image} entities within the application.
 * Extends the generic {@link GenericDAO} class, inheriting common CRUD operations and providing specialized
 * handling for Image entities.
 * <p>
 *
 * @author Björn Forsberg
 */
public class ImageDAO extends GenericDAO<Image> {

    public ImageDAO() {
        super(Image.class);
    }
}

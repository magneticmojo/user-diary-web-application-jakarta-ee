package services;

import database.dao.ImageDAO;
import entities.Image;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.io.InputStream;

/**
 * Provides functionalities for managing and retrieving images, used by {@link usermanagement.userpage.ImageServlet}.
 * This includes obtaining an image from the database by its ID, handling invalid IDs, and providing a default image.
 *
 * @author Björn Forsberg
 */
public class ImageService {

    private static final Long INVALID_IMAGE_ID = -1L;

    private final ImageDAO imageDAO;

    /**
     * Constructs an instance of the ImageService with an injected ImageDAO.
     *
     * @param imageDAO The ImageDAO instance to handle database operations related to images.
     */
    public ImageService(ImageDAO imageDAO) {
        this.imageDAO = imageDAO;
    }

    /**
     * Retrieves an image by its ID, returning a default image if the ID is invalid or not found.
     *
     * @param imageId The ID of the image as a string.
     * @return Image  The retrieved Image object.
     * @throws IOException if an I/O error occurs while reading the default image.
     */
    public Image retreiveImage(String imageId) throws IOException {
        Long id = formatRequestParamValue(imageId);
        if (id.equals(INVALID_IMAGE_ID)) {
            return getDefaultImage();
        }
        return retrieveImageFromDB(id);
    }


    /**
     * Formats the request parameter value for the image ID, returning a specific invalid ID constant if the format is incorrect.
     *
     * @param imageIdString The image ID as a string.
     * @return              The formatted image ID as a Long, or the invalid ID constant if the format is incorrect.
     */
    private Long formatRequestParamValue(String imageIdString) {
        try {
            return Long.valueOf(imageIdString);
        } catch (NumberFormatException e) {
            return INVALID_IMAGE_ID;
        }
    }

    /**
     * Retrieves an image from the database by its ID, or returns the default image if the ID does not exist.
     *
     * @param id The ID of the image to retrieve.
     * @return   The retrieved Image object, or the default image if the ID does not exist.
     * @throws IOException if an I/O error occurs while reading the default image.
     */
    private Image retrieveImageFromDB(Long id) throws IOException {
        if (imageDAO.existsById(id)) {
            return imageDAO.getById(id);
        } else {
            return getDefaultImage();
        }
    }

    /**
     * Constructs and returns the default image, reading it from a pre-defined resource path.
     *
     * @return   The default Image object.
     * @throws IOException if an I/O error occurs while reading the default image.
     */
    private Image getDefaultImage() throws IOException {
        InputStream in = getClass().getResourceAsStream("/images/default.jpeg");
        byte[] imageData = IOUtils.toByteArray(in);
        String mimeType = "image/jpeg";

        Image defaultImage = new Image();
        defaultImage.setImageData(imageData);
        defaultImage.setMimeType(mimeType);
        return defaultImage;
    }
}

package usermanagement.userpage;

import constants.ServletURIs;
import database.dao.ImageDAO;
import entities.Image;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import services.ImageService;

import java.io.IOException;
import java.io.InputStream;

/**
 * Responsible for handling image requests. The GET method is called when an image is requested by the rendered HTML of a user page.
 * Each post in the user page has an image tag with a source attribute that points to this servlet. And the image ID is passed as a query parameter.
 * <p>
 * This servlet retrieves and responds with image data based on an image ID provided in the request.
 * The image data is retrieved from the MySQL database using the {@link ImageService}.
 *
 * @author Björn Forsberg
 */
@WebServlet(name = "ImageServlet", urlPatterns = ServletURIs.IMAGE)
public class ImageServlet extends HttpServlet {

    /**
     * Handles the GET request for retrieving an image, processing the image request and responding with the image data.
     *
     * @param request  The HttpServletRequest object containing client request information, including the image ID.
     * @param response The HttpServletResponse object to send the image data back to the client.
     * @throws IOException If an input or output error occurs.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processImageRequest(request, response);
    }

    /**
     * Processes the image request by retrieving the image based on the provided ID and responds with the image data.
     *
     * @param request  The HttpServletRequest object containing client request information, including the image ID.
     * @param response The HttpServletResponse object to send the image data back to the client.
     * @throws IOException If an input or output error occurs.
     */
    private void processImageRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String imageId = request.getParameter("id");
        ImageService imageService = new ImageService(new ImageDAO());
        Image image = imageService.retreiveImage(imageId);
        respondWithImageData(response, image);
    }

    /**
     * Responds with the image data by setting the appropriate content type and writing the image data to the response.
     *
     * @param response The HttpServletResponse object to send the image data back to the client.
     * @param image    The Image object containing the image data and MIME type.
     * @throws IOException If an input or output error occurs.
     */
    private void respondWithImageData(HttpServletResponse response, Image image) throws IOException {
        response.setContentType(image.getMimeType());
        response.getOutputStream().write(image.getImageData());
    }
}
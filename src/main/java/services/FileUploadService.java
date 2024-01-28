package services;

import constants.UserInteractionMessages;
import entities.Image;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Provides functionalities for handling file uploads, specifically for images,
 * used by {@link DiaryPostService} and {@link usermanagement.userpage.UserPageServlet}.
 * The class includes validations for content type, file extension, file size, and other related aspects.
 * Supported image formats are JPEG, PNG, and GIF.
 *
 * @author Björn Forsberg
 */
public class FileUploadService {

    private static final long MAX_FILE_SIZE = 1024 * 1024 * 5;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/gif");

    /**
     * Validates the uploaded image file based on its content type, extension, and size.
     *
     * @param image The image part to validate.
     * @return      A user interaction message if the validation fails, otherwise null.
     */
    public String validateFile(Part image) {
        String contentType = image.getContentType();
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            return UserInteractionMessages.FILE_INVALID_CONTENT_TYPE;
        }

        String fileName = Paths.get(image.getSubmittedFileName()).getFileName().toString();
        String fileExtension = getFileExtension(fileName);
        if (!isAllowedFileExtension(fileExtension)) {
            return UserInteractionMessages.FILE_INVALID_EXTENSION;
        }

        if (image.getSize() > MAX_FILE_SIZE) {
            return UserInteractionMessages.FILE_TOO_LARGE;
        }

        return null;
    }

    /**
     * Extracts the file extension from the given file name.
     *
     * @param fileName The file name to extract the extension from.
     * @return         The file extension or an empty string if no extension is found.
     */
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return fileName.substring(dotIndex + 1);
        } else {
            return "";
        }
    }

    /**
     * Checks if the provided file extension is one of the allowed image file extensions.
     *
     * @param fileExtension The file extension to check.
     * @return              True if the extension is allowed, false otherwise.
     */
    private boolean isAllowedFileExtension(String fileExtension) {
        return fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("jpeg")
                || fileExtension.equalsIgnoreCase("png") || fileExtension.equalsIgnoreCase("gif");
    }

    /**
     * Determines if an image file has been uploaded based on the presence and size of the image part.
     *
     * @param imagePart The image part to check.
     * @return          True if an image has been uploaded, false otherwise.
     */
    public boolean isImageUploaded(Part imagePart) {
        return imagePart != null && imagePart.getSize() > 0;
    }

    /**
     * Constructs an Image entity from the provided image part, reading the image data and mime type.
     *
     * @param imagePart The image part to construct the Image entity from.
     * @return          The constructed Image entity.
     * @throws IOException if an I/O error occurs while reading the image data.
     */
    public Image getImage(Part imagePart) throws IOException {
        byte[] imageData = imagePart.getInputStream().readAllBytes();
        String mimeType = imagePart.getContentType();
        Image image = new Image();
        image.setImageData(imageData);
        image.setMimeType(mimeType);
        return image;
    }
}

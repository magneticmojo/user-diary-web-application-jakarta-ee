package usermanagement.userpage;

import constants.ServletURIs;
import constants.UserInteractionMessages;
import services.DiaryPostService;
import database.dao.UserDAO;
import entities.DiaryPost;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import services.FileUploadService;
import services.UserInputService;
import services.UserManagementService;
import routingstrategies.PostRedirectGetRoutingStrategy;
import viewbuilder.ViewBuilder;

import java.io.IOException;
import java.util.List;

/**
 * Servlet responsible for handling user diary page requests. This servlet is used to serve
 * the user's diary page view, allowing users to view their diary posts, submit new diary posts,
 * and handles validations for the post submissions.
 * <p>
 * A GET request renders the user diary page view. A POST request handles the submission of a new diary post.
 * The submission process is handled internally by {@link UserManagementService}, {@link UserInputService},
 * {@link FileUploadService}, and {@link DiaryPostService}.
 * <p>
 * Routing is handled by the {@link PostRedirectGetRoutingStrategy} class.
 *
 * @author Björn Forsberg
 */
@WebServlet(name = "UserPageServlet", urlPatterns = ServletURIs.USER_PAGE)
@MultipartConfig
public class UserPageServlet extends HttpServlet {

    private static ViewBuilder viewBuilder = null;

    /**
     * Initializes this servlet with a viewBuilder if not already initialized.
     */
    @Override
    public void init() {
        if (viewBuilder == null) {
            viewBuilder = new ViewBuilder(getServletContext().getContextPath(), getServletContext().getRealPath("/"));
        }
    }

    /**
     * Handles the GET request to provide the user diary page view.
     *
     * @param request  The HttpServletRequest object containing client request information.
     * @param response The HttpServletResponse object to send the rendered user page view back to the client.
     * @throws IOException If an input or output error occurs while processing the request or rendering the view.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        respondWithUserPageView(request, response);
    }

    /**
     * Handles the POST request for submitting a new diary post.
     *
     * @param request  The HttpServletRequest object containing client request information, including the post data.
     * @param response The HttpServletResponse object to respond to the client.
     * @throws IOException      If an input or output error occurs while processing the request or handling file upload.
     * @throws ServletException If a servlet error occurs during the request handling.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleNewDiaryPostSubmission(request, response);
    }

    /**
     * Responds with the rendered HTML for the user's diary page.
     *
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object to send the rendered HTML to the client.
     * @throws IOException If an input or output error occurs while rendering the HTML.
     */
    private void respondWithUserPageView(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        String username = (String) session.getAttribute("username");
        String validationUserMessage = (String) session.getAttribute("validationUserMessage");
        session.removeAttribute("validationUserMessage");
        String renderedHTML = viewBuilder.getRenderedUserPageView(validationUserMessage, username, getDiaryPosts(username));
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().print(renderedHTML);
    }

    /**
     * Retrieves a list of diary posts for a given username.
     *
     * @param username The username for which to retrieve the diary posts.
     * @return A list of DiaryPost objects representing the user's diary posts.
     */
    private List<DiaryPost> getDiaryPosts(String username) {
        UserManagementService userService = new UserManagementService(new UserDAO());
        return userService.getUserAndPosts(username).getDiaryPosts();
    }

    /**
     * Handles the submission of a new diary post, performing validations and persisting the post.
     *
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws IOException      If an input or output error occurs.
     * @throws ServletException If a servlet error occurs during the request handling.
     */
    private void handleNewDiaryPostSubmission(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String title = request.getParameter("title");
        String post = request.getParameter("diary-post");
        String imageCaption = request.getParameter("image-caption");
        Part imagePart = request.getPart("image");

        UserInputService userInputService = new UserInputService();
        HttpSession session = request.getSession();

        if (userInputService.exceedsMaxLength(title, 200)) {
            redirectWithUserMessage(request, response, session, UserInteractionMessages.TITLE_MAX_LENGTH_EXCEEDED);
            return;
        }

        if (userInputService.exceedsMaxLength(imageCaption, 200)) {
            redirectWithUserMessage(request, response, session, UserInteractionMessages.IMAGE_CAP_MAX_LENGTH_EXCEEDED);
            return;
        }

        if (userInputService.exceedsMaxLength(post, 20000)) {
            redirectWithUserMessage(request, response, session, UserInteractionMessages.POST_MAX_LENGTH_EXCEEDED);
            return;
        }

        FileUploadService fileUploadService = new FileUploadService();
        if (fileUploadService.isImageUploaded(imagePart)) {
            String errorMessage = fileUploadService.validateFile(imagePart);
            if (errorMessage != null) {
                redirectWithUserMessage(request, response, session, errorMessage);
                return;
            }
        }

        String username = (String) session.getAttribute("username");
        persistPost(username, title, post, imagePart, imageCaption);
        executeRoutingStrategy(request, response);
    }

    /**
     * Redirects the user with a validation message.
     *
     * @param request                The HttpServletRequest object.
     * @param response               The HttpServletResponse object.
     * @param session                The HttpSession object.
     * @param validationUserMessage  The validation message to be sent to the user.
     * @throws IOException If an input or output error occurs while redirecting.
     */
    private void redirectWithUserMessage(HttpServletRequest request, HttpServletResponse response, HttpSession session, String validationUserMessage) throws IOException {
        session.setAttribute("validationUserMessage", validationUserMessage);
        executeRoutingStrategy(request, response);
    }

    /**
     * Persists a new diary post for the user.
     *
     * @param username     The username of the user.
     * @param title        The title of the post.
     * @param post         The content of the post.
     * @param imagePart    The image part of the post, if provided.
     * @param imageCaption The caption for the image, if provided.
     * @throws IOException If an input or output error occurs while persisting the post.
     */
    private void persistPost(String username, String title, String post, Part imagePart, String imageCaption) throws IOException {
        DiaryPostService diaryPostService = new DiaryPostService(new UserDAO());
        diaryPostService.persistNewPostForUser(username, title, post, imagePart, imageCaption);
    }

    /**
     * Executes the routing strategy to redirect the user after the post submission. Follows the Post-Redirect-Get pattern.
     *
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws IOException If an input or output error occurs while executing the routing strategy.
     */
    private void executeRoutingStrategy(HttpServletRequest request, HttpServletResponse response) throws IOException {
        new PostRedirectGetRoutingStrategy().handleRouting(request, response, getServletContext().getContextPath());
    }
}


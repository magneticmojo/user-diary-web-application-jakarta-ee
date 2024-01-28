package usermanagement.userpage;

import constants.ServletURIs;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import viewbuilder.ViewBuilder;

import java.io.IOException;

/**
 * Responsible for handling user settings page requests. This servlet is used to serve the user settings view,
 * allowing users to either continue to account deletion or returning to the user page.
 *
 * @author Björn Forsberg
 */
@WebServlet(name = "SettingsServlet", urlPatterns = ServletURIs.SETTINGS)
public class SettingsServlet extends HttpServlet {

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
     * Handles the GET request to provide the user settings view.
     *
     * @param request  The HttpServletRequest object containing client request information.
     * @param response The HttpServletResponse object to send the rendered settings view back to the client.
     * @throws IOException If an input or output error occurs while processing the request or rendering the view.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        respondWithSettingsView(response);
    }

    /**
     * Responds with the user settings view by rendering the HTML and writing it to the response.
     *
     * @param response The HttpServletResponse object to send the rendered settings view back to the client.
     * @throws IOException If an input or output error occurs while rendering the view.
     */
    private void respondWithSettingsView(HttpServletResponse response) throws IOException {
        String renderedHTML = viewBuilder.getRenderedSettingsView();
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().print(renderedHTML);
    }
}

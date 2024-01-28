package viewbuilder;

import app.journeyman.Mixer;
import constants.ServletURIs;
import constants.StaticResourcePaths;
import constants.UserInteractionMessages;
import entities.DiaryPost;

import java.io.File;
import java.util.List;

/**
 * ViewBuilder is responsible for rendering various views of the web application by utilizing String templates and {@link Mixer}.
 * It reads the static HTML templates, and based on the parameters provided, it populates these templates
 * with dynamic content. The class encapsulates the logic for handling different views such as login,
 * registration, user page, settings, and account deletion. It provides methods to render each view with
 * the appropriate data.
 * <p>
 * The Mixer class used in this servlet was authored by Pierre Wijkman and Björn Nilsson. See {@link Mixer}.
 * Link to Mixer source code <a href="https://people.dsv.su.se/~pierre/os/mixer/">https://people.dsv.su.se/~pierre/os/mixer/</a>}
 *
 * @author Björn Forsberg
 */
public class ViewBuilder {

    private final String contextPath;
    private final String realPath;
    private final String loginView;
    private final String registrationView;
    private final String verificationView;
    private final String userPageView;
    private final String settingsView;
    private final String accountDeletionView;

    /**
     * Initializes a ViewBuilder instance with the given context path and real path, and loads the HTML templates
     * required for the application's views. The context path is used to construct URLs, and the real path is used to locate
     * the HTML files within the file system.
     *
     * @param contextPath The context path of the web application, used for constructing URLs.
     * @param realPath    The real path of the HTML templates within the file system.
     */
    public ViewBuilder(String contextPath, String realPath) {
        this.contextPath = contextPath;
        this.realPath = realPath;
        this.loginView = loadTemplate(StaticResourcePaths.LOGIN_VIEW);
        this.registrationView = loadTemplate(StaticResourcePaths.REGISTRATION_VIEW);
        this.verificationView = loadTemplate(StaticResourcePaths.VERIFICATION_VIEW);
        this.userPageView = loadTemplate(StaticResourcePaths.USER_PAGE_VIEW);
        this.settingsView = loadTemplate(StaticResourcePaths.SETTINGS_VIEW);
        this.accountDeletionView = loadTemplate(StaticResourcePaths.ACCOUNT_DELETION_VIEW);
    }

    /**
     * Loads the HTML template file from the given file path.
     *
     * @param filePath The relative path to the HTML file.
     * @return The content of the HTML file as a String.
     */
    private String loadTemplate(String filePath) {
        return Mixer.getContent(new File(realPath + filePath));
    }

    /**
     * Renders the login view by populating the template with appropriate URI and user messages.
     *
     * @param userMessage A message to display to the user.
     * @return The rendered HTML content as a String.
     */
    public String getRenderedLoginView(String userMessage) {
        Mixer mixer = new Mixer(loginView);
        handleUserMessageRendering(userMessage, mixer);

        mixer.add("---login-uri---", contextPath + ServletURIs.LOGIN);
        mixer.add("---registration-uri---", contextPath + ServletURIs.REGISTRATION);

        return mixer.getMix();
    }

    /**
     * Renders the registration view by populating the template with appropriate URI and user messages.
     *
     * @param userMessage A message to display to the user.
     * @return The rendered HTML content as a String.
     */
    public String getRenderedRegistrationView(String userMessage) {
        Mixer mixer = new Mixer(registrationView);
        handleUserMessageRendering(userMessage, mixer);

        mixer.add("---login-uri---", contextPath + ServletURIs.LOGIN);
        mixer.add("---registration-authentication-uri---", contextPath + ServletURIs.REGISTRATION);

        return mixer.getMix();
    }

    /**
     * Renders the verification view by populating the template with appropriate URI and user messages.
     *
     * @param userMessage A message to display to the user.
     * @return The rendered HTML content as a String.
     */
    public String getRenderedVerificationView(String userMessage) {
        Mixer mixer = new Mixer(verificationView);

        mixer.add("---login-uri---", contextPath + ServletURIs.LOGIN);
        mixer.add("---user-message---", userMessage);
        mixer.add("---verification-uri---", contextPath + ServletURIs.VERIFICATION);
        mixer.add("---email-sender-uri---", contextPath + ServletURIs.EMAIL_SENDER);

        return mixer.getMix();
    }

    /**
     * Renders the user page view by populating the template with user details, diary posts, and other information.
     *
     * @param userMessage  A message to display to the user.
     * @param username     The username of the user.
     * @param diaryPosts   The list of diary posts to render.
     * @return The rendered HTML content as a String.
     */
    public String getRenderedUserPageView(String userMessage, String username, List<DiaryPost> diaryPosts) {
        Mixer mixer = new Mixer(userPageView);
        handleUserMessageRendering(userMessage, mixer);

        if (diaryPosts.isEmpty()) {
            mixer.removeContext("<!--===entries===-->");
        } else {
            renderDiaryPosts(diaryPosts, mixer);
        }

        mixer.add("---username---", username);
        mixer.add("---usernamegreeting---", "The Diary Stories of " + username);

        mixer.add("---log-out-uri---", contextPath + ServletURIs.LOG_OUT);
        mixer.add("---settings-uri---", contextPath + ServletURIs.SETTINGS);
        mixer.add("---user-page-uri---", contextPath + ServletURIs.USER_PAGE);

        mixer.add("---user-page-css---", contextPath + StaticResourcePaths.USER_PAGE_CSS);
        return mixer.getMix();
    }

    /**
     * Populates the user message in the view template, or removes the placeholder if the message is null.
     *
     * @param userMessage A message to display to the user.
     * @param mixer       The Mixer object used to manipulate the HTML content.
     */
    private static void handleUserMessageRendering(String userMessage, Mixer mixer) {
        if (userMessage != null) {
            mixer.add("---user-message---", userMessage);
        } else {
            mixer.removeContext("<!--===user-message===-->");
        }
    }

    /**
     * Renders the diary posts into the view template.
     *
     * @param diaryPosts The list of diary posts to render.
     * @param mixer      The Mixer object used to manipulate the HTML content.
     */
    private void renderDiaryPosts(List<DiaryPost> diaryPosts, Mixer mixer) {
        int postCount = 1;
        for (DiaryPost post : diaryPosts) {
            mixer.add("<!--===entries===-->", "---no---", Integer.toString(postCount));
            mixer.add("<!--===entries===-->", "---time---", post.getTimestamp().toString());
            mixer.add("<!--===entries===-->", "---title---", post.getTitle());
            mixer.add("<!--===entries===-->", "---post---", post.getPost());

            if (post.getImage() != null) {
                mixer.add("<!--===entries===-->", "---image-uri---", contextPath + ServletURIs.IMAGE);
                mixer.add("<!--===entries===-->", "---imageId---", Long.toString(post.getImage().getId()));
                mixer.add("<!--===entries===-->", "---altTag---", post.getImageCaption());
            } else {
                mixer.add("<!--===entries===-->", "---image-uri---", "");
                mixer.add("<!--===entries===-->", "---imageId---", "");
                mixer.add("<!--===entries===-->", "---altTag---", "");
            }
            postCount++;
        }
    }

    /**
     * Renders the settings view by populating the template with appropriate URI.
     *
     * @return The rendered HTML content as a String.
     */
    public String getRenderedSettingsView() {
        Mixer mixer = new Mixer(settingsView);
        mixer.add("---user-page-uri---", contextPath + ServletURIs.USER_PAGE);
        mixer.add("---account-deletion-uri---", contextPath + ServletURIs.DELETE_ACCOUNT);
        return mixer.getMix();
    }

    /**
     * Renders the account deletion view by populating the template with appropriate URI.
     *
     * @return The rendered HTML content as a String.
     */
    public String getRenderedAccountDeletionView() {
        Mixer mixer = new Mixer(accountDeletionView);
        mixer.add("---account-deletion-uri---", contextPath + ServletURIs.DELETE_ACCOUNT);
        mixer.add("---settings-uri---", contextPath + ServletURIs.SETTINGS);
        return mixer.getMix();
    }
}
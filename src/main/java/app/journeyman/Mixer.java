package app.journeyman;

import java.util.*;
import java.io.*;

/**
 * <p>Title: Mixer</p>
 * <p>Description: Mixer is a simple helper-class (that is easy to use) for Servlet programmers that enable a complete separation of Servlet- and HTML-code.</p>
 * <p>Copyright: GNU GPL, <a href="http://www.gnu.org/licenses/licenses.html#TOCGPL">...</a></p>
 * @author Pierre Wijkman, pierre@dsv.su.se (generator) and Björn Nilsson, bjorn-ni@dsv.su.se (tester), created September-November 2002
 * @version 0.70
 */
public class Mixer {
    private Hashtable mappings = new Hashtable();
    private String html = "";
    private final String contextNull = "---nullun---";

    /**
     * <p>Constructor for the Mixer object.</p>
     * <p>Example: Mixer mixer = new Mixer(html);</p>
     *
     * @param html The string containing the template HTML-file.
     */
    public Mixer(String html) {
        this.html = html;
    }

    /**
     * <p>Adds a marker that should be substituted by a value. Context of replacement in the HTML-document: no specific.
     * <p>Example: mixer.add("---email---", "pierre@dsv.su.se");</p>
     *
     * @param marker The marker string that will be replaced by the value string in the HTML-document.
     * @param value The value string that will replace the marker string in the HTML-document.
     */
    public void add(String marker, String value) {
        add(contextNull, marker, value);
    }

    /**
     * <p>Adds a marker that should be substituted by a value. Context of replacement in the HTML-document: specific. This context can be used repeatedly and is marked in the HTML-document by two identical markers.</p>
     * <p>Example: mixer.add("---context---", "---email---", "pierre@dsv.su.se");</p>
     *
     * @param context The context string that marks a specific area in the HTML-document.
     * @param marker The marker string that will be replaced by the value string in the HTML-document.
     * @param value The value string that will replace the marker string in the HTML-document.
     */
    public void add(String context, String marker, String value) {
        if(value == null)  { value = "null"; }

        if(mappings.containsKey(context)) {
            Vector mapping = (Vector)mappings.get(context);
            mapping.add(marker);
            mapping.add(value);
        } else {
            Vector mapping = new Vector();
            mapping.add(marker);
            mapping.add(value);
            mappings.put(context, mapping);
        }
    }

    /**
     * <p>Removes a marker-value pair associated with no specific context of the HTML-document.</p>
     * <p>Example: mixer.remove("---email---", "pierre@dsv.su.se");</p>
     *
     * @param marker The marker string (associated with the value string parameter) that will be removed.
     * @param value The value string (associated with the marker string parameter) that will be removed.
     */
    public void remove(String marker, String value) {
        remove(contextNull, marker, value);
    }

    /**
     * <p>Removes a marker-value pair associated with a specific context of the HTML-document.</p>
     * <p>Example: mixer.remove("---context---", "---email---", "pierre@dsv.su.se");</p>
     *
     * @param context The context string that marks a specific area in the HTML-document.
     * @param marker The marker string (accociated with the value string parameter) that will be removed.
     * @param value The value string (accociated with the marker string parameter) that will be removed.
     */
    public void remove(String context, String marker, String value) {
        if(value == null)  { value = "null"; }

        Vector v = (Vector)mappings.get(context);
        int place = -1;
        for(int i = 0; i < v.size(); i = i + 2) {
            String marketTmp = (String)v.elementAt(i);
            String valueTmp = (String)v.elementAt(i + 1);
            if(marketTmp.equals(marker) && valueTmp.equals(value)) place = i;
        }
        if(place != -1) {
            v.removeElementAt(place);
            v.removeElementAt(place);
        }
    }

    /**
     * <p>Clears all entries associated with no specific context of the HTML-document.</p>
     * <p>Example: mixer.clear();</p>
     */
    public void clear() {
        clearContext(contextNull);
    }

    /**
     * <p>Clears all entries associated with a specific context of the HTML-document.</p>
     * <p>Example: mixer.clear("---context---");</p>
     *
     * @param context The context that will be cleared.
     */
    public void clearContext(String context) {
        mappings.remove(context);
    }

    /**
     * <p>Clears all enries associated with no and all HTML-document contexts</p>
     * <p>Example: mixer.clearAll();</p>
     */
    public void clearAll() {
        mappings.clear();
    }

    /**
     * <p>Removes all HTML-code from a context including the context markers.</p>
     * <p>Example: mixer.removeContext("---context---");</p>
     *
     * @param context The HTML-document context that will be removed.
     */
    public void removeContext(String context) {
        String htmls[] = splitIn3(html, context);
        html = htmls[0] + htmls[2];
    }

    /**
     * <p>Adds HTML-code to a context.</p>
     * <p>Example: mixer.addHTML(htmlPart, "---context---");</p>
     * <p>Note! Experimental.</p>
     *
     * @param htmlPart The string that contains the subpart HTML-code that will be included.
     * @param context The HTML-document context where the subpart HTML-code will be inserted.
     */
    public void addHTML(String htmlPart, String context) {
        String htmls[] = splitIn3(html, context);
        html = htmls[0] + context + htmlPart + context + htmls[2];
    }

    /**
     * <p>Removes all HTML-code from a context excluding the context markers.</p>
     * <p>Example: mixer.removeHTML("---context---");</p>
     * <p>Note! Experimental.</p>
     *
     * @param context The HTML-document context where the subpart HTML-code will be removed.
     */
    public void removeHTML(String context) {
        String htmls[] = splitIn3(html, context);
        html = htmls[0] + context + "\n" + context + htmls[2];
    }

    /**
     * <p>Mixes the template HTML-document with the added marker-value pairs and returns the result.</p>
     * <p>Example: String html = mixer.getMix();</p>
     *
     * @return A string with the result of mixing the values from the Java- and the HTML-code.
     */
    public String getMix() {
        String htmlResult = html;
        Enumeration e1 = mappings.keys();
        while(e1.hasMoreElements()) {
            String context = (String)e1.nextElement();
            Vector mapping = (Vector)mappings.get(context);

            if(!context.equals(contextNull)) {
                String[] htmls = splitIn3(htmlResult, context);
                String htmlTmp = htmls[1];
                for(int i = 0; i < mapping.size(); i = i + 2) {
                    String marker = (String)mapping.elementAt(i);
                    String value = (String)mapping.elementAt(i + 1);

                    if(marker.equals(mapping.elementAt(0)) && i > 0) {
                        htmlTmp = htmlTmp + replaceAll(htmls[1], marker, value);
                    } else {
                        htmlTmp = replaceAll(htmlTmp, marker, value);
                    }
                }
                htmlResult = htmls[0] + htmlTmp + htmls[2];
            }
        }

        // Deal with context null last so that users can use any order of adding
        if(mappings.containsKey(contextNull)) {
            Vector mapping = (Vector)mappings.get(contextNull);
            for(int i = 0; i < mapping.size(); i = i + 2) {
                String marker = (String)mapping.elementAt(i);
                String value = (String)mapping.elementAt(i + 1);

                htmlResult = replaceAll(htmlResult, marker, value);
            }
        }

        return htmlResult;
    }

    /**
     * <p>Loads a HTML-file and returns the content as a string.</p>
     * <p>Example: String html = Mixer.getContent(new File("x.html"));</p>
     * <p>Example: String html = Mixer.getContent(new File(getServletContext().getRealPath("x.html")));</p>
     *
     * @param file The template HTML-file to load.
     * @return A string containing the loaded HTML-code.
     */
    public synchronized static String getContent(File file) {
        StringBuffer content = new StringBuffer("");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String row = "";
            while((row = br.readLine()) != null) {
                content = content.append("\n");
                content = content.append(row);
            }
            br.close();
        } catch(IOException ioe) {
            content = new StringBuffer(ioe.getStackTrace().toString());
        }
        return content.toString();
    }

    /**
     * <p>Loads a HTML-file and returns a specific context of this HTML-file as a string.</p>
     * <p>Example: String html = Mixer.getContent(new File("x.html"), "---context---");</p>
     * <p>Example: String html = Mixer.getContent(new File(getServletContext().getRealPath("x.html")), "---context---");</p>
     *
     * @param file The template HTML-file to load.
     * @param context The context string that marks a specific area in the HTML-document.
     * @return A string containing the specific context of the loaded HTML-code.
     */
    public synchronized static String getContent(File file, String context) {
        String html = getContent(file);
        String htmls[] = splitIn3(html, context);
        return htmls[1];
    }

    // Helper method, for effeciency we do not use JDK 1.4 / split
    private static String[] splitIn3(String string, String delimiter) {
        StringBuffer sb = new StringBuffer(string);
        int i1 = sb.indexOf(delimiter);
        int i2 = sb.indexOf(delimiter, i1 + 1);

        String string1 = sb.substring(0, i1);
        String string2 = sb.substring(i1 + delimiter.length(), i2);
        String string3 = sb.substring(i2 + delimiter.length());

        String[] strings = {string1, string2, string3};
        return strings;
    }

    // Helper method, for effeciency we do not use JDK 1.4 / replaceAll
    private String replaceAll(String string, String marker, String value) {
        StringBuffer sb = new StringBuffer(string);

        int i, j = 0;
        while (j < sb.length()) {
            i = sb.indexOf(marker, j);
            if (i == -1) {
                break;
            } else {
                sb = sb.replace(i, i + marker.length(), value);
                j = i + value.length();
            }
        }
        return sb.toString();
    }
}
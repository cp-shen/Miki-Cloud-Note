package App.Utility;

import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

/**
 * The type Editor util.
 */
public class EditorUtil{
    /**
     * Generate editor page html string.
     *
     * @param contentHtml the content html
     * @param isEditable  the is editable
     * @return the string
     */
    public static String generateEditorPageHtml(String contentHtml, boolean isEditable){
        try{
            File editorFile = new File("editor/editor.html");
            Document editorDoc = Jsoup.parse(editorFile,"UTF-8");

            //set the content
            editorDoc.getElementById("div2").html(contentHtml);

            //set the js reference by absolute path
            File jsFile = new File("editor/wangEditor-3.0.15/release/wangEditor.min.js");
            editorDoc.getElementsByTag("script").first().attr("src", jsFile.toURI().toURL().toString());

            //set if editable
            if(!isEditable){
                editorDoc.getElementsByTag("script").last().append("editor1.$textElem.attr('contenteditable', false)");

                //remove the tool bar
                editorDoc.getElementById("div1").remove();
            }
            //test
            System.out.println(editorDoc.outerHtml());
            return editorDoc.outerHtml();
        }catch(IOException ex){
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieve content html string.
     *
     * @param WebW3cDoc the web w 3 c doc
     * @return the string
     */
    public static String retrieveContentHtml(org.w3c.dom.Document WebW3cDoc){
        Document jSoupDoc = Jsoup.parse(new W3CDom().asString(WebW3cDoc));

        return jSoupDoc.getElementById("div2").getElementsByAttribute("contenteditable").first().html();
    }
}

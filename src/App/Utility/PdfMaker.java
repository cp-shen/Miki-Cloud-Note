package App.Utility;


import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf;
import com.sun.istack.internal.NotNull;

import java.io.IOException;


/**
 * The type Pdf maker.
 */
public class PdfMaker{

    /**
     * Make pdf.
     *
     * @param pageHtml   the page html
     * @param targetPath the target path
     * @throws InterruptedException the interrupted exception
     * @throws IOException          the io exception
     */
    public static void makePdf(@NotNull String pageHtml, @NotNull String targetPath)
        throws InterruptedException, IOException{
            Pdf pdf = new Pdf();
            pdf.addPageFromString(pageHtml);
            pdf.saveAs(targetPath);
    }
}

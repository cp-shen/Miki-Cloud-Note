package App.Utility;


import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf;
import com.sun.istack.internal.NotNull;

import java.io.IOException;


public class PdfMaker{

    public static void makePdf(@NotNull String pageHtml, @NotNull String targetPath){
        try{
                Pdf pdf = new Pdf();
                pdf.addPageFromString(pageHtml);
                pdf.saveAs(targetPath);
        }catch(InterruptedException | IOException ex){
            ex.printStackTrace();
        }
    }
}
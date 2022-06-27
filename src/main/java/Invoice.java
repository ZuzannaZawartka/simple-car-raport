import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class Invoice {
    private UUID uuid;
    private Document document;
    private Font font;
    private Font font2;
    private String path;


    public Invoice(String pathx) throws FileNotFoundException, DocumentException {

        document = new Document();

        String path = "katalog/"+pathx+".pdf";
        this.font2 = FontFactory.getFont(FontFactory.COURIER, 20, BaseColor.BLACK);
        this.font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.RED);
        PdfWriter.getInstance(document, new FileOutputStream(path));
        document.open();
    }


    void write(String text) throws DocumentException {
        document.add(new Paragraph(text));
    }

    void addImage(String path) throws DocumentException, IOException {
        Image img = Image.getInstance("images/"+path);
        document.add(img);
    }

    void writeBold(String text) throws DocumentException {
        document.add(new Paragraph(text));
    }

    public Document getDocument() {
        return document;
    }

    void close(){
        document.close();
    }

    Font getFont(){
        return font;
    }

    Font getFont2(){
        return font2;
    }

}

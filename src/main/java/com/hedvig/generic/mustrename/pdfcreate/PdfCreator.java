package com.hedvig.generic.mustrename.pdfcreate;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.annotations.WrapToTest;
 
import java.io.File;
import java.io.IOException;
 
/**
 * Simple Hello World example.
 */
@WrapToTest
public class PdfCreator {
 
    public static final String DEST = "output/hello_world.pdf";
 
    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new PdfCreator().createPdf(DEST);
    }
 
    public void createPdf(String dest) throws IOException {
        //Initialize PDF writer
        PdfWriter writer = new PdfWriter(dest);
 
        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(writer);
 
        // Initialize document
        Document document = new Document(pdf);
 
        //Add paragraph to the document
        document.add(new Paragraph("Hej Hedvig!"));
 
        //Close document
        document.close();
    }
}
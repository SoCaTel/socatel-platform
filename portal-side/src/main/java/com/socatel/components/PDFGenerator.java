package com.socatel.components;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.socatel.models.User;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class PDFGenerator {

    // PDF Fonts
    private Font titleFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 18, BaseColor.BLACK);
    private Font regularFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK);

    /**
     * Creates a PDF with the user's data when the user clicks on View/Download my data
     * @return PDF
     */
    public ByteArrayOutputStream generatePDF(User user, String filename) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);

        document.open();

        addTitle(document, user.getUsername());
        addContent(document, user);

        document.close();

        return outputStream;
    }

    private void addTitle(Document document, String username) throws DocumentException {
        document.add(new Paragraph("Information about " + username, titleFont));

        addNewLines(document, 1);

        document.add(new Paragraph("This document has been generated by SoCaTel... The information related to this user is:", regularFont));
    }

    private void addContent(Document document, User user) throws DocumentException {
        List list = new List(false, false, 10);

        list.add(new ListItem("Username" + ": " + user.getUsername(), regularFont));
        list.add(new ListItem("Email" + ": " + user.getEmail(), regularFont));
        list.add(new ListItem("Age range" + ": " + user.getAgeRange(), regularFont));
        if (user.getGender() != null) list.add(new ListItem("Gender" + ": " + user.getGender(), regularFont));
        list.add(new ListItem("Locality" + ": " + user.getLocality().getName(), regularFont));
        list.add(new ListItem("First lang" + ": " + user.getFirstLang().getName(), regularFont));
        if (user.getSecondLang() != null) list.add(new ListItem("Second lang" + ": " + user.getSecondLang().getName(), regularFont));

        document.add(list);
    }

    /**
     * Add new line
     * @param d document
     * @param i number of new lines
     */
    private void addNewLines(Document d, Integer i) throws DocumentException {
        while (i > 0) {
            d.add(Chunk.NEWLINE);
            i--;
        }
    }
}

package main;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.styledxmlparser.IXmlParser;
import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupHtmlParser;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.datatypes.AttachmentChunks;
import org.apache.poi.hsmf.exceptions.ChunkNotFoundException;
import org.w3c.tidy.Tidy;
import org.w3c.tidy.ant.JTidyTask;
import org.xhtmlrenderer.css.parser.property.PrimitivePropertyBuilders;
import org.xhtmlrenderer.extend.NamespaceHandler;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

public class Message {

    private String fileNameStem;

    /**
     * The Outlook MSG file being processed.
     */
    private MAPIMessage msg;

    public Message(String fileName) throws IOException {
        fileNameStem = fileName;
        if (fileNameStem.endsWith(".msg") || fileNameStem.endsWith(".MSG")) {
            fileNameStem = fileNameStem.substring(0, fileNameStem.length() - 4);
        }
        msg = new MAPIMessage(fileName);
    }

    /**
     * Processes the message.
     *
     * @throws IOException if an exception occurs while writing the message out
     */
    public void processMessage() throws IOException {
        String txtFileName = fileNameStem + ".html";
        String attDirName = fileNameStem + "-att";
        PrintWriter txtOut = null;
        try {
            txtOut = new PrintWriter(txtFileName);
            try {
                String displayFrom = msg.getDisplayFrom();
                txtOut.println("<p style =\"font-size:11px;font-family: Calibri, Verdana, Geneva, sans-serif;\"> <span style = \"font-weight: bold\";> Od:</span> " + displayFrom + "<br>");
            } catch (ChunkNotFoundException e) {
                // ignore
            }
            try {
                String displayTo = msg.getDisplayTo();
                txtOut.println(" <span style = \"font-weight: bold\";> Do:</span> " + displayTo + "<br>");
            } catch (ChunkNotFoundException e) {
                // ignore
            }
            try {
                Calendar date = msg.getMessageDate();
                txtOut.println(" <span style = \"font-weight: bold\";> Data:</span> " + convertDate(date) + "<br>");
            } catch (ChunkNotFoundException e) {
                // ignore
            }
            try {
                String displayCC = msg.getDisplayCC();
                txtOut.println(" <span style = \"font-weight: bold\";> DW:</span> " + displayCC + "<br>");
            } catch (ChunkNotFoundException e) {
                // ignore
            }
            try {
                String displayBCC = msg.getDisplayBCC();
                txtOut.println("<span style = \"font-weight: bold\";> UDW: </span>" + displayBCC + "<br>");
            } catch (ChunkNotFoundException e) {
                // ignore
            }
            try {
                String subject = msg.getSubject();
                txtOut.println("<span style = \"font-weight: bold\";> Temat: </span> " + subject + "</p>");
            } catch (ChunkNotFoundException e) {
                // ignore
            }
            try {
                String body = msg.getHtmlBody();
                txtOut.println(body);
            } catch (ChunkNotFoundException e) {
                System.err.println("No message body");
            }

            AttachmentChunks[] attachments = msg.getAttachmentFiles();
            if (attachments.length > 0) {
                File d = new File(attDirName);
                if (d.mkdir()) {
                    for (AttachmentChunks attachment : attachments) {
                        processAttachment(attachment, d);
                    }
                } else {
                    System.err.println("Can't create directory " + attDirName);
                }
            }
        } finally {
            if (txtOut != null) {
                txtOut.close();
            }
        }
        /*Tidy tidy = new Tidy();
        tidy.setXHTML(true);
        tidy.setInputEncoding("UTF-8");
        tidy.setOutputEncoding("UTF-8");
        tidy.parse(new FileInputStream(txtFileName), new FileOutputStream(txtFileName += ".xhtml"));
*/

/*
        String url = new File(txtFileName).toURI().toURL().toString();
        OutputStream os = new FileOutputStream(txtFileName+".pdf");


        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocument(url);
        renderer.layout();

        renderer.createPDF(os);

        os.close();*/
        /*Document document = new Document(PageSize.A4);
        PdfWriter writer = null;
        try {
            writer = PdfWriter.getInstance(document,
                    new FileOutputStream(txtFileName+".pdf"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        document.open();
        //FontFactory.registerDirectories();

        for (String f : FontFactory.getRegisteredFonts()) {
            try {
                document.add(new Paragraph(f,
                        FontFactory.getFont(f, "UTF-8", BaseFont.EMBEDDED)));
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }


        XMLWorkerHelper.getInstance().parseXHtml(writer, document,
                new FileInputStream(txtFileName), StandardCharsets.UTF_8);
        document.close();*/
        File htmlSource = new File(txtFileName);
        File pdfDest = new File(txtFileName + ".pdf");
        // pdfHTML specific code
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setCharset("UTF-8");

       /* BaseFont bf = null;
        try {
            bf = BaseFont.createFont("c:/windows/fonts/times.ttf",
            BaseFont.CP1250, BaseFont.EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        Font font = new Font(bf, 12);*/
        FontProvider fp = new FontProvider();
        //fp.addFont("c:/windows/fonts/times.ttf", BaseFont.CP1250);
        fp.addSystemFonts();
        fp.addStandardPdfFonts();
        fp.addDirectory("C:\\Windows\\Fonts");
        converterProperties.setFontProvider(fp);
        converterProperties.getFontProvider().addSystemFonts();
        HtmlConverter.convertToPdf(new FileInputStream(htmlSource),
                new FileOutputStream(pdfDest), converterProperties);
    }

    /**
     * Processes a single attachment: reads it from the Outlook MSG file and
     * writes it to disk as an individual file.
     *
     * @param attachment the chunk group describing the attachment
     * @param dir        the directory in which to write the attachment file
     * @throws IOException when any of the file operations fails
     */
    public void processAttachment(AttachmentChunks attachment,
                                  File dir) throws IOException {
        String fileName;
        try {
            fileName = attachment.getAttachFileName().toString();
        } catch (NullPointerException e) {
            System.out.println(e + "Invalid Name");
            fileName = "Unknown-att";
        }
        if (attachment.getAttachLongFileName() != null) {
            fileName = attachment.getAttachLongFileName().toString();
        }

        File f = new File(dir, fileName);
        OutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(f);
            fileOut.write(attachment.getAttachData().getValue());
        } catch (FileNotFoundException e) {
            System.out.println(e + " Error occurred");
        } finally {
            if (fileOut != null) {
                fileOut.close();
            }
        }
    }

    private String convertDate(Calendar calendar) {


        TimeZone timeZone = calendar.getTimeZone();
        ZoneId zoneId;
        if (timeZone == null) {
            zoneId = ZoneId.systemDefault();
        } else {
            zoneId = timeZone.toZoneId();
        }
        LocalDateTime dateTime = LocalDateTime.ofInstant(calendar.toInstant(), zoneId);

        return dateTime.toString().replace('T', ' ');
    }

    /**
     * Processes the list of arguments as a list of names of Outlook MSG files.
     *
     * @param args the list of MSG files to process
     */

}

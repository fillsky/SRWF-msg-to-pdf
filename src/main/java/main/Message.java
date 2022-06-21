package main;


import com.zeonpad.pdfcovertor.OutlookToPdf;
import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.datatypes.AttachmentChunks;
import org.apache.poi.hsmf.exceptions.ChunkNotFoundException;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

public class Message {

    private String fileNameStem;
    private final String inputDirectory;


    /**
     * The Outlook MSG file being processed.
     */
    private final MAPIMessage msg;

    public Message(String inputDirectory, String fileName) throws IOException {
        this.inputDirectory = inputDirectory;
        fileNameStem = fileName;
        if (fileNameStem.endsWith(".msg") || fileNameStem.endsWith(".MSG")) {
            fileNameStem = fileNameStem.substring(0, fileNameStem.length() - 4);
        }
        msg = new MAPIMessage(inputDirectory + fileName);
    }

    /**
     * Processes the message.
     *
     * @throws IOException if an exception occurs while writing the message out
     */
    public void processMessage() throws IOException {


        String htmlDirName = inputDirectory + "html\\";
        String txtFileName = htmlDirName + fileNameStem + ".html";
        String msgFileName = inputDirectory + fileNameStem + ".msg";
        String pdfDirName = inputDirectory + "PDF\\" + fileNameStem + "\\";
        String pdfFileName = pdfDirName + fileNameStem + ".pdf";
        String attDirName = pdfDirName + "attachments";

        File htmlDir = new File(htmlDirName);

        if (!htmlDir.exists()) {
            App.logger.info("Creating HTML Dir: " + htmlDir);
            if (!htmlDir.mkdirs()) {
                App.logger.warning("Error creating HTML directory");
                System.err.println("Error creating HTML directory");
            }
        }
        try (PrintWriter txtOut = new PrintWriter(txtFileName)) {
            try {
                String displayFrom = msg.getDisplayFrom();
                txtOut.println("<p style =\"font-size:11px;font-family: Calibri, Verdana, Geneva, sans-serif;\"> <span style = \"font-weight: bold\";> Od:</span> " + displayFrom + "<br>");
            } catch (ChunkNotFoundException e) {
                // ignore
                App.logger.info("Error: " + e.getMessage());
            }
            try {
                String displayTo = msg.getDisplayTo();
                txtOut.println(" <span style = \"font-weight: bold\";> Do:</span> " + displayTo + "<br>");
            } catch (ChunkNotFoundException e) {
                // ignore
                App.logger.info("Error: " + e.getMessage());
            }
            try {
                Calendar date = msg.getMessageDate();
                txtOut.println(" <span style = \"font-weight: bold\";> Data:</span> " + convertDate(date) + "<br>");
            } catch (ChunkNotFoundException e) {
                // ignore
                App.logger.info("Error: " + e.getMessage());
            }
            try {
                String displayCC = msg.getDisplayCC();
                txtOut.println(" <span style = \"font-weight: bold\";> DW:</span> " + displayCC + "<br>");
            } catch (ChunkNotFoundException e) {
                // ignore
                App.logger.info("Error: " + e.getMessage());
            }
            try {
                String displayBCC = msg.getDisplayBCC();
                txtOut.println("<span style = \"font-weight: bold\";> UDW: </span>" + displayBCC + "<br>");
            } catch (ChunkNotFoundException e) {
                // ignore
                App.logger.info("Error: " + e.getMessage());
            }
            try {
                String subject = msg.getSubject();

                txtOut.println("<span style = \"font-weight: bold\";> Temat: </span> " + subject.replace("/", "_") + "</p>");
            } catch (ChunkNotFoundException e) {
                // ignore
                App.logger.info("Error: " + e.getMessage());
            }
            try {
                String body = msg.getHtmlBody();
                txtOut.println(body);

            } catch (ChunkNotFoundException e) {
                App.logger.info("Error: " + e.getMessage());
                System.err.println("No message body");
            }

            AttachmentChunks[] attachments = msg.getAttachmentFiles();
            if (attachments.length > 0) {
                File d = new File(attDirName);
                if (d.mkdirs()) {
                    if (d.exists()) {
                        for (AttachmentChunks attachment : attachments) {
                            processAttachment(attachment, d);
                            System.out.print("Attachment dir: " + d.getAbsolutePath() + "\n");
                        }
                    } else {
                        App.logger.info("Can't create directory: " + attDirName);
                        System.err.println("Can't create directory: " + attDirName);
                    }
                }
            }
        }

        File dir = new File(pdfDirName);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.err.println("Błąd przy tworzeniu folderu wyjsciowego pdf");
            }
        }

        OutlookToPdf outlookToPdf = new OutlookToPdf();
        outlookToPdf.convert(msgFileName, pdfFileName);


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
            fileName = attachment.getAttachFileName().toString().replace("/", "-");
        } catch (NullPointerException e) {
            App.logger.info("Error: Invalid name or empty name. Renaming to Unknown-att. " +e.getMessage());
            System.out.println(e + " Invalid Name or no name at all.");
            fileName = "Unknown-att";
        }
        if (attachment.getAttachLongFileName() != null) {
            fileName = attachment.getAttachLongFileName().toString();
      //      System.out.println( "\n" + fileName);
        }

        File f = new File(dir, fileName);
        OutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(f);
            fileOut.write(attachment.getAttachData().getValue());
        } catch (FileNotFoundException e) {
            App.logger.info("Error: " +e.getMessage());
            System.err.println(e + " Error occurred");
        } finally {
            if (fileOut != null) {
                fileOut.close();
                App.logger.info("Closing file successful");
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

}

package main;

import java.io.*;
import java.time.LocalDateTime;

import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.datatypes.AttachmentChunks;

public class Attachment {

    public void extract(String dir, String outDir, String fileName) throws IOException {


        Message message = new Message(dir+ "\\"+fileName);
        message.processMessage();
        /*String msgfile = dir + "\\" + fileName;
        MAPIMessage msg = new MAPIMessage(msgfile);
        String attachmentName;

        AttachmentChunks[] attachments = msg.getAttachmentFiles();

        if (attachments.length > 0) {
            for (AttachmentChunks a : attachments) {
                if (a.getAttachFileName() == null) {
                    System.out.println("Brak nazwy pliku");
                    attachmentName = "unknown_attachment" + LocalDateTime.now().getSecond();

                } else {
                    System.out.println(a.getAttachFileName());
                    attachmentName = a.getAttachFileName().toString().replace("/", "_");
                }
                // extract attachment
                ByteArrayInputStream fileIn = new ByteArrayInputStream(a.getAttachData().getValue());

                    File f = new File(outDir, attachmentName);// output


                    OutputStream fileOut = null;
                    try {
                        fileOut = new FileOutputStream(f);
                        byte[] buffer = new byte[2048];
                        int bNum = fileIn.read(buffer);
                        while (bNum > 0) {
                            fileOut.write(buffer);
                            bNum = fileIn.read(buffer);
                        }
                    } catch (FileNotFoundException e){
                        System.out.println(e + " Error occurred");
                    }

                    finally {
                        try {
                            if (fileIn != null) {
                                fileIn.close();
                            }
                        } finally {
                            if (fileOut != null) {
                                fileOut.close();
                            }
                        }
                    }

            }
        } else {

            System.out.println("No attachment");
        }*/
    }
}
package main;

import com.itextpdf.text.FontFactory;
import com.zeonpad.pdfcovertor.OutlookToPdf;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;


public class App {
    public static void main(String[] args) throws IOException {

        //FontFactory.getRegisteredFonts().forEach(System.out::println);
        JFileChooser f = new JFileChooser();
        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        f.setDialogTitle("Choose folder with MSG files.");
        f.showOpenDialog(null);


        File file = f.getSelectedFile();

        String inDir = file.getAbsolutePath()+"\\";
        String outDir = file.getAbsolutePath() + "\\PDF\\";
        System.out.println(inDir);
        System.out.println(outDir);
        System.out.println();

        OutlookToPdf outlookToPdf = new OutlookToPdf();
        Attachment attachment = new Attachment();
        ArrayList<String> msgFiles = Arrays.stream(Objects.requireNonNull(file.list()))
                .filter(v -> v.endsWith(".msg"))
                .collect(Collectors.toCollection(ArrayList::new));

        for (String msgFile : msgFiles) {


            System.out.println("====== Extracting: " + msgFile + " ======");
            outDir = file.getAbsolutePath() + "\\PDF\\" + msgFile.replace(".msg", "");
            File dir = new File(outDir);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    System.out.println("Błąd przy tworzeniu folderu wyjsciowego");
                }
            }
            outlookToPdf.convert(inDir  + msgFile, outDir + msgFile.replace(".msg", ".pdf"));

            Message message = new Message(dir + msgFile);
            message.processMessage();

            attachment.extract(inDir, outDir, msgFile);
            System.out.println(outDir);
            System.out.println("====== Done! ======");
        }
        System.out.println("Extraction Complete.");
       /* try {
            Message processor = new Message(inDir+"\\20180922_133204_1fa3e2d2-8adc-4526-a8b8-5d6acd22fc38.msg");
            processor.processMessage();
        } catch (IOException e) {
            System.err.println("Could not process " + inDir + ": " + e);
        }*/
    }
}

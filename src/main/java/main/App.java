package main;


import com.zeonpad.pdfcovertor.PDFException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;


public class App {


    static {
        try {
            if (System.getProperty("os.arch").equals("amd64")) {
                NativeUtils.loadLibraryFromJar("/jacob-1.18-x64.dll"); // during runtime. .DLL within .JAR
            } else {
                NativeUtils.loadLibraryFromJar("/jacob-1.18-x86.dll");
            }

        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }

    }
    public static Logger logger = Logger.getLogger("MyLog");
    public static FileHandler fh;

    public static void main(String[] args) {

     try {
         fh = new FileHandler("C:/temp/msg.log");
         logger.addHandler(fh);
         SimpleFormatter formatter = new SimpleFormatter();
         fh.setFormatter(formatter);

         logger.info("Start");
         DirectoryChoose.main(args);
     } catch (SecurityException | IOException e) {
         e.printStackTrace();
         logger.info("Error: " + e);
     }

        //executeOld();

    }

    public static void executeOld() {
        ArrayList<String> msgFiles;
        File selectedDirectory;

        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jFileChooser.showOpenDialog(null);
        System.out.println(jFileChooser.getSelectedFile());
        selectedDirectory = jFileChooser.getSelectedFile();
        String inDir = selectedDirectory.getAbsolutePath() + "\\";
        String outDir = selectedDirectory.getAbsolutePath() + "\\PDF\\";
        msgFiles = Arrays.stream(Objects.requireNonNull(selectedDirectory.list()))
                .filter(v -> v.endsWith(".msg"))
                .collect(Collectors.toCollection(ArrayList::new));

        for (String msgFile : msgFiles) {
            System.out.println("\nExtracting: " + msgFile + "\nto: " + outDir);

            Message message;
            try {
                message = new Message(inDir, msgFile);
                message.processMessage();

            } catch (PDFException | IOException ex) {
                System.out.println("\nerr:" + ex);
            }
        }

    }
}

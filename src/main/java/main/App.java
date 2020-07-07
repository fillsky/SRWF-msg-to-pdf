package main;


import com.zeonpad.pdfcovertor.PDFException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
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

    public static void main(String[] args) throws IOException {


        DirectoryChoose.main(args);
        //executeOld();

    }

    public static void executeOld() {
        ArrayList<String> msgFiles = new ArrayList<>();
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

            Message message = null;
            try {
                message = new Message(inDir, msgFile);
                message.processMessage();

            } catch (PDFException | IOException ex) {
                System.out.println("\nerr:" + ex.toString());
            }
        }

    }
}

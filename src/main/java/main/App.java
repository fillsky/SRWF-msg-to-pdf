package main;


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

        ArrayList<String> msgFiles = Arrays.stream(Objects.requireNonNull(file.list()))
                .filter(v -> v.endsWith(".msg"))
                .collect(Collectors.toCollection(ArrayList::new));

        for (String msgFile : msgFiles) {

            System.out.println("====== Extracting: " + msgFile + " ======");

            Message message = new Message(inDir, msgFile);
            message.processMessage();

            System.out.println(outDir);
            System.out.println("====== Done! ======");
        }
        System.out.println("Extraction Complete.");

    }
}

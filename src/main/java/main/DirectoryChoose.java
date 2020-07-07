package main;

import com.zeonpad.pdfcovertor.PDFException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class DirectoryChoose extends Application {

    private File selectedDirectory;
    private DirectoryChooser directoryChooser = new DirectoryChooser();
    private Label pathLabel = new Label();
    private TextArea textArea = new TextArea();
    private Image image = new Image(DirectoryChoose.class.getResourceAsStream("/icon.png"));
    private ImageView imageView = new ImageView(image);
    private Button button = new Button("Select Directory");
    private Button convertButton = new Button("Start", imageView);
    private ArrayList<String> msgFiles = new ArrayList<>();
    private ProgressBar progressBar = new ProgressBar();
    private Label dirLabel = new Label();

    private static int BUTTON_HEIGHT = 28;
    private static int BUTTON_WIDTH = 80;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("MTP Converter");
        primaryStage.getIcons().add(image);

        imageView.setFitHeight(15);
        imageView.setPreserveRatio(true);
        button.setMinSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        convertButton.setMinSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        pathLabel.setText("Please choose directory");
        textArea.setMinHeight(80);
        textArea.setEditable(false);
        directoryChooser.setInitialDirectory(new File("."));


        progressBar.setPrefWidth(350);
        Button cancelButton = new Button("Close");
        cancelButton.setOnAction(event -> System.exit(1));
        cancelButton.setMinSize(BUTTON_WIDTH, BUTTON_HEIGHT);

        button.setOnAction(event -> {
            selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory != null) {
                pathLabel.setText(selectedDirectory.getAbsolutePath());
                selectedDirectory.setExecutable(true, false);
                msgFiles = Arrays.stream(Objects.requireNonNull(selectedDirectory.list()))
                        .filter(v -> v.endsWith(".msg"))
                        .collect(Collectors.toCollection(ArrayList::new));

                if (msgFiles.isEmpty()) {
                    textArea.setText("No msg files in given location!\n");
                    selectedDirectory = null;
                } else {
                    textArea.setText("Found " + msgFiles.size() + " files.\n");
                    textArea.appendText("Program may ask for permission from Outlook. Give Access or it won't work:) ");
                }
            }
        });
        convertButton.setOnAction(e -> {
            if (selectedDirectory == null) {
                textArea.setText("No directory chosen!\n");
            } else {
                executeFiles();

            }
        });


        HBox hBox = new HBox(button, convertButton);
        hBox.setSpacing(5);
        hBox.setPadding(new Insets(10));
        hBox.setAlignment(Pos.CENTER);
        VBox vBox = new VBox(pathLabel, hBox, progressBar, textArea, dirLabel, cancelButton);
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(10));
        vBox.setAlignment(Pos.CENTER);
        //HBox hBox = new HBox(button1, button2);
        Scene scene = new Scene(vBox, 600, 400);
        primaryStage.setMaxWidth(600);
        primaryStage.setMinWidth(400);
        primaryStage.setMaxHeight(400);
        primaryStage.setMinHeight(300);

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void executeFiles(){
        Thread thread = new Thread(()->{
            button.setDisable(true);
            convertButton.setDisable(true);
            String inDir = selectedDirectory.getAbsolutePath() + "\\";
            String outDir = selectedDirectory.getAbsolutePath() + "\\PDF\\";
            Platform.runLater(() -> progressBar.setProgress(0d));
            int counter = 1;

            for (String msgFile : msgFiles) {
                textArea.appendText("\nExtracting: " + msgFile + "\nto: " + outDir);

                Message message = null;
                try {
                    File file = new File(inDir+msgFile);
                    file.setWritable(true);
                    message = new Message(inDir, msgFile);
                    message.processMessage();

                } catch (PDFException | IOException ex) {
                    textArea.appendText("\nerr:" + ex.toString()+ "Check if folder or files are write-only!");

                }

                textArea.appendText("\nDone!\n");
                progressBar.setProgress(counter/(double)msgFiles.size());
                counter++;

            }
            textArea.appendText("Extraction Complete!");
            Platform.runLater(()->dirLabel.setText("Attachments Extracted to:\n " + outDir + "<msg_name>\\attachments\n"));
            button.setDisable(false);
            convertButton.setDisable(false);

        });
        thread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }

}


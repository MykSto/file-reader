package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class FileChooserSample extends Application {

    private Desktop desktop = Desktop.getDesktop();

    @Override
    public void start(final Stage stage) {
        stage.setTitle("File Chooser Sample");

        final FileChooser fileChooser = new FileChooser();

        final Button openButton = new Button("Open a Picture...");
        final Button openMultipleButton = new Button("Open Multiple...");

        openButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        File file = fileChooser.showOpenDialog(stage);
                        if (file != null) {
                            openFile(file);
                        }
                    }
                });

        openMultipleButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        List<File> list = fileChooser.showOpenMultipleDialog(stage);
                        if (list != null) {
                            String fileFolder = null;
                            Map<String, Integer> map = new HashMap<>();
                            try {
                                for (File file : list) {
                                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getAbsolutePath()));
                                    bufferedReader.lines().filter(Objects::nonNull).forEach(line -> {
                                        for (String word : line.trim().split("[\\s@&.?$+-,/:;\"!#%]+")) {
                                            String upWord = word.toUpperCase();
                                            if (word.length() != 0 &&
                                                    'A' <= upWord.charAt(0) && upWord.charAt(0) <= 'Z') {
                                                map.put(word, 1 + (map.get(word) != null ? map.get(word) : 0));
                                            }
                                        }
                                    });
                                    if (fileFolder == null) {
                                        fileFolder = file.getAbsolutePath().substring(0,
                                                file.getAbsolutePath().length() - file.getName().length());
                                    }
                                }
                                writeFiles(map, fileFolder);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });


        final GridPane inputGridPane = new GridPane();

        GridPane.setConstraints(openButton, 0, 0);
        GridPane.setConstraints(openMultipleButton, 1, 0);
        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        inputGridPane.getChildren().addAll(openButton, openMultipleButton);

        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputGridPane);
        rootGroup.setPadding(new Insets(12, 12, 12, 12));

        stage.setScene(new Scene(rootGroup));
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException ex) {
            Logger.getLogger(
                    FileChooserSample.class.getName()).log(
                    Level.SEVERE, null, ex
            );
        }
    }

    private static void writeFiles(Map<String, Integer> map, String fileFolder) {
        EnumSet.allOf(Letters.class).forEach(e -> {
            try (FileWriter fw = new FileWriter(fileFolder.concat(e.fileName))) {
                fw.write(map.entrySet().stream().filter(key -> {
                    char firstChar = key.getKey().toUpperCase().charAt(0);
                    return firstChar >= e.startChar && firstChar <= e.finishChar;
                }).sorted(Comparator.comparing(Map.Entry::getKey))
                        .map(Object::toString).collect(Collectors.joining("\n")));
            } catch (Exception y) {
                y.printStackTrace();
            }
        });
    }
}
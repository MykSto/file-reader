package org.filereader.task;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public final class FileChooserApp extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(final Stage stage) {
        stage.setTitle("File Reader");

        final FileChooser fileChooser = new FileChooser();
        final Button openMultipleButton = new Button("Upload Files");

        //Graphical Interface
        final GridPane inputGridPane = new GridPane();
        GridPane.setConstraints(openMultipleButton, 2,1);
        inputGridPane.setHgap(60);
        inputGridPane.setVgap(10);
        inputGridPane.getChildren().addAll(openMultipleButton);

        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputGridPane);
        stage.setScene(new Scene(rootGroup));

        Canvas canvas = new Canvas(300,50);
        rootGroup.getChildren().add(canvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.fillText( "Welcome to the File Reader v1.0", 70, 10 );
        gc.strokeText( "New files will appear in the same file directory", 35, 40 );

        stage.show();

        //File upload functionality
        openMultipleButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        //List of uploaded files
                        List<File> list = fileChooser.showOpenMultipleDialog(stage);
                        if (list != null) {
                            String fileFolder = null;
                            Map<String, Integer> map = new HashMap<>();
                            try {
                                for (File file : list) {
                                    //Read the files
                                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getAbsolutePath()));
                                    bufferedReader.lines().filter(Objects::nonNull).forEach(line -> {
                                        for (String word : line.trim().split("[\\s@&.?$+-,/:;\"!#%=~<>{}()\\-*\\[\\]\\d]+")) {
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

    }

    //Writes new files with the results
    private static void writeFiles(Map<String, Integer> map, String fileFolder) {
        EnumSet.allOf(Letters.class).forEach(e -> {
            Thread thread = new Thread(){
                @Override
                public synchronized void start() {
                    try (FileWriter fw = new FileWriter(fileFolder.concat(e.fileName))) {
                        fw.write(map.entrySet().stream().filter(key -> {
                            char firstChar = key.getKey().toUpperCase().charAt(0);
                            return firstChar >= e.startChar && firstChar <= e.finishChar;
                        }).sorted(Comparator.comparing(Map.Entry::getKey))
                                .map(Object::toString).collect(Collectors.joining("\r\n")));
                    } catch (Exception y) {
                        y.printStackTrace();
                    }
                    System.out.println(e.name() + " has " + Thread.activeCount() + " threads");
                }
            };
            thread.start();
        });
    }

}
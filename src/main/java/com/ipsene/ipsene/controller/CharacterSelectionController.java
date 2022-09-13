package com.ipsene.ipsene.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.database.annotations.Nullable;
import com.ipsene.ipsene.Globals;
import com.ipsene.ipsene.SceneController;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CharacterSelectionController {

    SceneController sceneController = new SceneController();
    @FXML private RadioButton plum, peacock, green, white, mustard, scarlet;
    @FXML private ImageView characterView;
    @FXML private Pane select;
    @FXML private Pane characterPane;
    private String chosenPlayer = null;
    private static boolean initialized = false;

    public CharacterSelectionController() {
        if (!initialized) {
                listenerPlayersInit("Professor_Plum");
                listenerPlayersInit("Mrs_Peacock");
                listenerPlayersInit("Mr_Green");
                listenerPlayersInit("Mrs_White");
                listenerPlayersInit("Colonel_Mustard");
                listenerPlayersInit("Ms_Scarlet");
        }
        initialized = true;
    }

    public void getCharacter(ActionEvent event) throws FileNotFoundException {
        if (plum.isSelected()) {
            chosenPlayer = "Professor_Plum";
            fadeInCharacterImage(chosenPlayer);
        }
        else if (peacock.isSelected()) {
            chosenPlayer = "Mrs_Peacock";
            fadeInCharacterImage(chosenPlayer);
        }
        else if (green.isSelected()) {
            chosenPlayer = "Mr_Green";
            fadeInCharacterImage(chosenPlayer);
        }
        else if (white.isSelected()) {
            chosenPlayer = "Mrs_White";
            fadeInCharacterImage(chosenPlayer);
        }
        else if (mustard.isSelected()) {
            chosenPlayer = "Colonel_Mustard";
            fadeInCharacterImage(chosenPlayer);
        }
        else if (scarlet.isSelected()) {
            chosenPlayer = "Ms_Scarlet";
            fadeInCharacterImage(chosenPlayer);
        }
    }

    public void confirmPlayer(ActionEvent event) throws ExecutionException, InterruptedException, IOException {
        if (chosenPlayer == null) {
            Alert mustChoosePlayer = new Alert(Alert.AlertType.WARNING);
            mustChoosePlayer.setTitle("Warning");
            mustChoosePlayer.setContentText("You must choose a character to join the lobby!");
            mustChoosePlayer.show();
        } else {
            DocumentReference docRef = Globals.get_instance().getCollectionReference().document(chosenPlayer);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            initPlayerFB(chosenPlayer);
            Globals.get_instance().chosenPlayer = chosenPlayer;
            sceneController.switchToLobby(event);
        }
    }

    public void fadeInCharacterImage(String characterChoice) throws FileNotFoundException {
        FadeTransition ft = new FadeTransition(Duration.millis(3000));

        switch(characterChoice) {
            case "Professor_Plum":
                setImageAndBackgroundColor("src/main/resources/com/ipsene/ipsene/Images/Characters/plum.png", "purple");
                break;
            case "Mrs_Peacock":
                setImageAndBackgroundColor("src/main/resources/com/ipsene/ipsene/Images/Characters/peacock.png", "#2D62FF");
                break;
            case "Mr_Green":
                setImageAndBackgroundColor("src/main/resources/com/ipsene/ipsene/Images/Characters/green.png", "green");
                break;
            case "Colonel_Mustard":
                setImageAndBackgroundColor("src/main/resources/com/ipsene/ipsene/Images/Characters/mustard.png", "yellow");
                break;
            case "Ms_Scarlet":
                setImageAndBackgroundColor("src/main/resources/com/ipsene/ipsene/Images/Characters/scarlet.png", "red");
                break;
            case "Mrs_White" :
                setImageAndBackgroundColor("src/main/resources/com/ipsene/ipsene/Images/Characters/white.png", "#bdbdbd");
                break;
            default:
                setImageAndBackgroundColor("src/main/resources/com/ipsene/ipsene/Images/Characters/none.png", "none");
        }
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.setNode(characterPane);
    }

    public void setImageAndBackgroundColor(String imagePath, String backgroundColor) throws FileNotFoundException{
        FileInputStream inputstream = new FileInputStream(imagePath);
        Image image = new Image(inputstream);
        select.setStyle("-fx-background-color: " + backgroundColor);
        characterView.setImage(image);
    }

    public void listenerPlayersInit(String checkPlayer) {
        DocumentReference docRef = Globals.get_instance().getCollectionReference().document(checkPlayer);
        docRef.addSnapshotListener(
                new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirestoreException e) {
                        if (e != null) {
                            System.err.println("Listen failed: " + e);
                            return;
                        }
                        if (snapshot != null && snapshot.exists()) {
                            switch (checkPlayer) {
                                case "Professor_Plum" -> plum.setDisable(true);
                                case "Mrs_Peacock" -> peacock.setDisable(true);
                                case "Mr_Green" -> green.setDisable(true);
                                case "Mrs_White" -> white.setDisable(true);
                                case "Colonel_Mustard" -> mustard.setDisable(true);
                                case "Ms_Scarlet" -> scarlet.setDisable(true);
                            }
                        }
                    }
                });
    }
    public void initPlayerFB(String chosenPlayer) throws ExecutionException, InterruptedException {
        switch (chosenPlayer) {
            case "Professor_Plum" -> {
                writeDataFB("Professor_Plum", 25, 22);
            }
            case "Mrs_Peacock" -> {
                writeDataFB("Mrs_Peacock", 25, 8);
            }
            case "Mr_Green" -> {
                writeDataFB("Mr_Green", 19, 3);
            }
            case "Mrs_White" -> {
                writeDataFB("Mrs_White", 9, 3);
            }
            case "Colonel_Mustard" -> {
                writeDataFB("Colonel_Mustard", 3, 22);
            }
            case "Ms_Scarlet" -> {
                writeDataFB("Ms_Scarlet", 9, 27);
            }
        }
    }

    public void writeDataFB(String chosenPlayer, int xCoord, int yCoord) throws ExecutionException, InterruptedException {
        Map<String, Object> docData = new HashMap<>();
        docData.put("xCoord", xCoord);
        docData.put("yCoord", yCoord);
        docData.put("location", "Corridor");
        ApiFuture<WriteResult> future = Globals.get_instance().getCollectionReference().document(chosenPlayer).set(docData);
        future.get();
    }

    @FXML
    public void initialize() {
        try {
            fadeInCharacterImage("none");
            }
        catch (FileNotFoundException e)
            {
            throw new RuntimeException(e);
            }
    }
}

package com.ipsene.ipsene.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.annotations.Nullable;
import com.ipsene.ipsene.Globals;
import com.ipsene.ipsene.SceneController;
import com.ipsene.ipsene.model.cards.Deck;
import com.ipsene.ipsene.model.cards.Solution;
import com.ipsene.ipsene.service.Firebase;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class LobbyController {
    @FXML
    private HBox characterBox;
    @FXML
    private Pane joinPane, readyPane;
    @FXML
    private Label plum, peacock, green, white, mustard, scarlet;
    @FXML
    public ImageView one, two, three, four, five, six;

    @FXML
    private Button startButton, readyButton;
    private boolean toggleJoinButton = true;
    private boolean gameState = false;
    SceneController sceneController = new SceneController();
    private boolean initialized = false;
    Deck deck = new Deck();

    public LobbyController() {
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



    public void startGameHost(ActionEvent event) throws IOException, ExecutionException, InterruptedException {
        DocumentReference docRef = Globals.get_instance().getCollectionReference().document("System");
        ApiFuture<WriteResult> future =
                docRef.update("gameState", true);
        docRef.update("currentPlayer", Firebase.getInstance().getPlayers().get(0));
        docRef.update("index", 5);
        ArrayList<String> players = Firebase.getInstance().getPlayers();
        Solution solution = deck.createCards();
        deck.dealCards(players.size(), players);
        docRef.update("currentPlayer", players.get(0));
        docRef.update("index", 5);
        docRef.update("solPerson", solution.getPerson());
        docRef.update("solWeapon", solution.getWeapon());
        docRef.update("solRoom", solution.getRoom());
        WriteResult result = future.get();
        sceneController.switchToGameBoard(event);
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
                            try {
                                switch (checkPlayer) {
                                    case "Professor_Plum"-> setCharacterImage("src/main/resources/com/ipsene/ipsene/Images/Characters/plum.png",one,plum,1, 0, 1);
                                    case "Mrs_Peacock" -> setCharacterImage("src/main/resources/com/ipsene/ipsene/Images/Characters/peacock.png",two,peacock,0, 0, 1);
                                    case "Mr_Green"-> setCharacterImage("src/main/resources/com/ipsene/ipsene/Images/Characters/green.png",three,green,0, 1, 0);
                                    case "Mrs_White"-> setCharacterImage("src/main/resources/com/ipsene/ipsene/Images/Characters/white.png",four,white,0, 0, 0);
                                    case "Colonel_Mustard"-> setCharacterImage("src/main/resources/com/ipsene/ipsene/Images/Characters/mustard.png",five,mustard,1, 1, 0);
                                    case "Ms_Scarlet"-> setCharacterImage("src/main/resources/com/ipsene/ipsene/Images/Characters/scarlet.png",six,scarlet,1, 0, 0);
                                }
                                updateArray(checkPlayer);
                            } catch (ExecutionException | InterruptedException | FileNotFoundException ex) {
                                ex.printStackTrace();
                            }
                            if (toggleJoinButton && !gameState) {
                                //joinButton.setDisable(false);
                            }
                        }
                    }
                });
    }

    @FXML
    public void disableButton() throws IOException, ExecutionException, InterruptedException {
        readyButton.setDisable(true);
        startGameClient();
    }

    public void startGameClient() throws IOException, ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(Globals.get_instance().lobbyPin).document("System");
        while (true) {
            ApiFuture<DocumentSnapshot> future = docRef.get();
            if (future.get().getData().get("gameState").equals(true)) {
                Stage stage2 = (Stage) plum.getParent().getScene().getWindow();
                sceneController.switchSceneNoEvent("Screen/clueBoard.fxml", plum);
                Platform.runLater(() -> {
                    stage2.requestFocus();
                });
                break;
            } else {
                Thread.sleep(2500);
                continue;
            }
        }
    }

//    public void startGameHost(ActionEvent event) throws IOException, ExecutionException, InterruptedException {
//        DocumentReference docRef = Globals.get_instance().getCollectionReference().document("System");
//        ApiFuture<WriteResult> future =
//                docRef.update("gameState", true);
//        docRef.update("currentPlayer", Firebase.getInstance().getPlayers().get(0));
//        docRef.update("index", 5);
//        ArrayList<String> players = Firebase.getInstance().getPlayers();
//        Solution solution = deck.createCards();
//        deck.dealCards(players.size(), players);
//        docRef.update("currentPlayer", players.get(0));
//        docRef.update("index", 5);
//        docRef.update("solPerson", solution.getPerson());
//        docRef.update("solWeapon", solution.getWeapon());
//        docRef.update("solRoom", solution.getRoom());
//        WriteResult result = future.get();
//        sceneController.switchToGameBoard(event);
//    }



    public void setCharacterImage(String imagePath, ImageView imageview, Label label, Integer a, Integer b, Integer c) throws FileNotFoundException {
        FileInputStream inputstream = new FileInputStream(imagePath);
        Image image = new Image(inputstream);
        imageview.setImage(image);
        label.setOpacity(1);
        label.setTextFill(Color.color(a, b, c));
    }


    @FXML
    public void initialize(){
        plum.setOpacity(0);
        white.setOpacity(0);
        green.setOpacity(0);
        mustard.setOpacity(0);
        peacock.setOpacity(0);
        scarlet.setOpacity(0);
        if(!Globals.get_instance().isHost){
            plum.setOpacity(0);
            white.setOpacity(0);
            green.setOpacity(0);
            mustard.setOpacity(0);
            peacock.setOpacity(0);
            scarlet.setOpacity(0);
            startButton.setDisable(true);
            startButton.setOpacity(0);
            readyPane.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        } else {
            readyButton.setDisable(true);
            readyButton.setOpacity(0);
        }
    }

    public void updateArray(String character) throws ExecutionException, InterruptedException {
        DocumentReference docRef = Globals.get_instance().getCollectionReference().document("System");
        ApiFuture<WriteResult> arrayUnion =
                docRef.update("playersArray", FieldValue.arrayUnion(character));
    }

}
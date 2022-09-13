package com.ipsene.ipsene.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.ipsene.ipsene.Globals;
import com.ipsene.ipsene.SceneController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class JoinLobbyController {

    @FXML private ListView joinListView;
    @FXML private VBox joinLobbyListVBox;
    @FXML private Button refreshButton;
    @FXML private TextField lobbyName;
    SceneController sceneController = new SceneController();

    public void pushLobbiesToVBox() throws ExecutionException, InterruptedException {
        ObservableList lobbies = FXCollections.observableArrayList(getLobbiesFromFirebase());
        ObservableList<Label> lobbylabels = FXCollections.observableArrayList();
        for(int i=0; i<lobbies.size(); i++) {
            Label label = new Label(lobbies.get(i).toString());
            label.setStyle("-fx-background-color: transparant");
            label.setOpacity(1);
            lobbylabels.add(label);}
        joinListView.setItems(lobbies);
    }

    @FXML
    public void refreshList() throws ExecutionException, InterruptedException {
        pushLobbiesToVBox();
        Platform.runLater(()->{
            joinListView.requestFocus();
        });
    }
    public int getPlayerCount() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("ActiveLobbies").document(Globals.get_instance().lobbyPin);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        return Integer.parseInt(future.get().getData().get("Player count").toString());
    }

    public void updatePlayerCount() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("ActiveLobbies").document(Globals.get_instance().lobbyPin);
        ApiFuture<WriteResult> writeResult = docRef.update("Player count", Integer.valueOf(getPlayerCount())+1);
    }
    public void getClick() throws IOException, ExecutionException, InterruptedException {
        Globals.get_instance().lobbyPin = ((String) joinListView.getSelectionModel().getSelectedItem()).split(" Players: ")[0];
        updatePlayerCount();
        sceneController.switchSceneNoEvent("Screen/characterSelection.fxml",refreshButton);
    }

    public ArrayList getLobbiesFromFirebase() throws ExecutionException, InterruptedException {
        ArrayList<String> lobbyArrayList = new ArrayList<>();
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> lobbies = db.collection("ActiveLobbies").get();
        List<QueryDocumentSnapshot> documents = lobbies.get().getDocuments();
        LocalDateTime localDateTime = LocalDateTime.now();
        for (QueryDocumentSnapshot document : documents) {
            LocalDateTime timestamp = LocalDateTime.parse((CharSequence) document.getData().get("Time"));
            if(Duration.between(timestamp, localDateTime).toMinutes() <= 30){
                lobbyArrayList.add(document.getId() + " Players: " + document.getData().get("Player count"));
            }}
        return lobbyArrayList;
    }

    @FXML
    public void initialize() throws ExecutionException, InterruptedException {
        pushLobbiesToVBox();
        Globals.get_instance().isHost = false;
    }
}
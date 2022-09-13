package com.ipsene.ipsene.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.ipsene.ipsene.Globals;
import com.ipsene.ipsene.SceneController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AccusationController {

    List<String> solutionList = new ArrayList<>();
    @FXML
    private ChoiceBox<String> whoChoice;
    @FXML
    private ChoiceBox<String> whatChoice;
    @FXML
    private ChoiceBox<String> whereChoice;
    @FXML
    private Button accusationButton;

    public static final String lobbyPin = Globals.get_instance().lobbyPin;
    Firestore db = FirestoreClient.getFirestore();
    SceneController sceneController = new SceneController();
    BoardController boardController = new BoardController();

    public AccusationController() throws ExecutionException, InterruptedException{

    }
    public void initialize() {
        fillWhatChoice();
        fillWhoChoice();
        filWhereChoice();
        accusationButton.disableProperty().bind(
                whoChoice.getSelectionModel().selectedItemProperty().isNull()
                        .or(whereChoice.getSelectionModel().selectedItemProperty().isNull())
                        .or(whatChoice.getSelectionModel().selectedItemProperty().isNull()));

    }
    public void fillWhoChoice(){
        List<String> whoList = Arrays.asList("Miss Scarlet", "Colonel Mustard", "Mrs. White", "Mr. Green", "Mrs. Peacock", "Professor Plum");
        whoChoice.getItems().addAll(whoList);
    }

    public void fillWhoChoiceEx() throws ExecutionException, InterruptedException {
        List<String> whoList = new ArrayList<>();

        CollectionReference docRef = db.collection(lobbyPin);
        ApiFuture<QuerySnapshot> query = docRef.get();
        QuerySnapshot snapshot = query.get();
        snapshot.forEach((doc)-> {
            whoList.add(doc.getId());
        });

        if(whoList.contains("System")){whoList.remove("System");}
        whoChoice.getItems().addAll(whoList);
    }

    public void fillWhatChoice(){
        List<String> whatList = Arrays.asList("Candlestick", "Dagger", "Lead pipe", "Revolver", "Rope", "Wrench");
        whatChoice.getItems().addAll(whatList);
    }

    public void filWhereChoice(){
        List<String> whereList = Arrays.asList("Kitchen", "Ballroom", "Conservatory", "Dining room", "Billiard room", "Library", "Lounge", "Hall", "Study");
        whereChoice.getItems().addAll(whereList);
    }

    public void closeButtonAction(ActionEvent event) {
        sceneController.closeWindow(event);
    }

    public List<String> getSolution() throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection(lobbyPin).document("System");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        solutionList.add(Objects.requireNonNull(document.getData()).get("solPerson").toString());
        solutionList.add(Objects.requireNonNull(document.getData()).get("solWeapon").toString());
        solutionList.add(Objects.requireNonNull(document.getData()).get("solRoom").toString());
        return solutionList;
    }


    public void accusationButton(ActionEvent event) throws IOException {
        try{
            String currentPlayer = Globals.get_instance().chosenPlayer;
            //haalt de informatie op uit de keuzes
            Object who = whoChoice.getSelectionModel().getSelectedItem();
            Object what = whatChoice.getSelectionModel().getSelectedItem();
            Object where = whereChoice.getSelectionModel().getSelectedItem();
            //kijkt wat de solution is
            solutionList = getSolution();
            //vergelijkt de solution met het antwoord
            if(solutionList.get(0).contains(who.toString()) && solutionList.get(1).contains(what.toString()) &&
            solutionList.get(2).contains(where.toString())){

                DocumentReference docRef = db.collection(lobbyPin).document("System");
                Map<String, Object> updates = new HashMap<>();
                updates.put("win", currentPlayer);
                docRef.set(updates, SetOptions.merge());
                boardController.switchPlayer(event);
                sceneController.closeWindow(event);
            } else {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setContentText("You got it wrong \n Your turn has ended") ;
                a.show();
                boardController.switchPlayer(event);
                sceneController.closeWindow(event);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
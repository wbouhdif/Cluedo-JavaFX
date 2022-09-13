package com.ipsene.ipsene.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import com.ipsene.ipsene.Globals;
import com.ipsene.ipsene.SceneController;
import com.ipsene.ipsene.model.board.Dice;
import com.ipsene.ipsene.service.Firebase;
import com.ipsene.ipsene.view.DiceView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class BoardController {
    @FXML private ListView logListView;
    private @FXML
    Button gridButtonUp, gridButtonDown, gridButtonRight, gridButtonLeft;
    private @FXML
    Button suggestionButton, accusationButton, endTurn;
    private @FXML
    Label Professor_Plum, Mrs_Peacock, Mr_Green, Mrs_White, Colonel_Mustard, Ms_Scarlet;
    private @FXML
    Label enter_kitchen, enter_ballroom, enter_conservatory, enter_billiardRoom, enter_library, enter_study, enter_hall, enter_lounge, enter_diningRoom;
    private @FXML
    Label leave_kitchen, leave_ballroom, leave_conservatory, leave_billiardRoom, leave_library, leave_study, leave_hall, leave_lounge, leave_diningRoom;
    private @FXML
    GridPane gridPane;
    private static final Firebase firebase = Firebase.getInstance();
    private int xCoordinate;
    private int yCoordinate;
    private String currentPlayer;
    private @FXML
    String[] cardsToShow;
    private @FXML
    Button submitCardButton;
    private final @FXML
    DiceView diceView = new DiceView();
    private @FXML
    Pane chooseCardToShowPane;
    private @FXML
    BorderPane rollDicePane;
    private @FXML
    Pane notebookPane;
    private @FXML
    ImageView diceImg;
    private @FXML
    ImageView diceImg2;
    private @FXML
    Button rollButton;
    private @FXML
    Label showSteps;
    public @FXML Label label;
    private @FXML TextFlow logDisplay;
    private @FXML Button testButton;
    private int numberOfSteps;
    private ArrayList<String> players;
    private final ArrayList<String> roomsVisitedByPlayer = new ArrayList<>();
    private final SceneController sceneController = new SceneController();
    private String room;

    public BoardController() throws ExecutionException, InterruptedException {
        players = firebase.getPlayers();
        listenerForCurrentPlayer();
        listenerForMessages();
        listenerForCoordinate("Professor_Plum");
        listenerForCoordinate("Mrs_Peacock");
        listenerForCoordinate("Mr_Green");
        listenerForCoordinate("Mrs_White");
        listenerForCoordinate("Colonel_Mustard");
        listenerForCoordinate("Ms_Scarlet");
        listenerForSuggestion();

    }

    public void initialize() throws ExecutionException, InterruptedException {
        firebase.getCards();
        setCards();
        disableMovementButtons();
        disableUIButtons();
        diceView.init(diceImg, diceImg2, rollButton, rollDicePane);
        diceView.rollPaneIsVisible(false);
        if (Objects.equals(currentPlayer, Globals.get_instance().chosenPlayer)) {
            roll();
            enableUIButtons();
        }
    }

    public void listenerForCurrentPlayer() {
        DocumentReference docRef = Globals.get_instance().getDb().collection(Globals.get_instance().lobbyPin).document("System");
        docRef.addSnapshotListener(
                (snapshot, e) -> {
                    if (e != null) {
                        System.err.println("Listen failed: " + e);
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        currentPlayer = snapshot.getString("currentPlayer");
                        try {
                            room = firebase.getRoomSpecific(Globals.get_instance().chosenPlayer);
                        } catch (ExecutionException | InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        if (Objects.equals(currentPlayer, Globals.get_instance().chosenPlayer)) {
                            writePlayerMessageToFirebase("'s turn!");
                            enableUIButtons();
                            roll();
                        } else {
                            disableMovementButtons();
                            disableUIButtons();
                        }
                    }
                });
    }

    public void listenerForCoordinate(String player) {
        DocumentReference docRef = Globals.get_instance().getDb().collection(Globals.get_instance().lobbyPin).document(player);
        docRef.addSnapshotListener(
                (snapshot, e) -> {
                    if (snapshot != null && snapshot.exists()) {
                        try {
                            double doubleXCoordinate = Objects.requireNonNull(snapshot.getDouble("xCoord"));
                            xCoordinate = (int) doubleXCoordinate;
                            double doubleYCoordinate = Objects.requireNonNull(snapshot.getDouble("yCoord"));
                            yCoordinate = (int) doubleYCoordinate;
                            movePlayer();

                            suggestionButton.setDisable(Objects.equals(room, "corridor"));

                            if (Objects.equals(room, "corridor")) {
                                suggestionButton.setDisable(true);
                            } else {
                                suggestionButton.setDisable(false);
                            }

                            roomControl(enter_kitchen);
                            roomControl(enter_ballroom);
                            roomControl(enter_conservatory);
                            roomControl(enter_billiardRoom);
                            roomControl(enter_library);
                            roomControl(enter_study);
                            roomControl(enter_hall);
                            roomControl(enter_lounge);
                            roomControl(enter_diningRoom);

                            roomControl(leave_kitchen);
                            roomControl(leave_ballroom);
                            roomControl(leave_conservatory);
                            roomControl(leave_billiardRoom);
                            roomControl(leave_library);
                            roomControl(leave_study);
                            roomControl(leave_hall);
                            roomControl(leave_lounge);
                            roomControl(leave_diningRoom);
                        } catch (Exception ignored) {
                        }

                    }
                });
    }

    public void switchPlayer(ActionEvent event) throws ExecutionException, InterruptedException, IOException {
        listenerWinner(event);
        writePlayerMessageToFirebase("has ended their turn");
        ArrayList<String> players = firebase.getPlayers();
        int indexNextPlayer;
        int indexCurrentPlayer = players.indexOf(currentPlayer);
        if (indexCurrentPlayer == players.size()-1) {
            indexNextPlayer = 0;
        } else {
            indexNextPlayer = indexCurrentPlayer + 1;
        }
        firebase.setCurrentPlayer(players.get(indexNextPlayer));
        diceView.rollPaneIsVisible(false);
    }

    public void movePlayer() {
        try {
            gridPane.add(findPlayerLabel(currentPlayer), xCoordinate, yCoordinate);
        } catch (Exception ignored) {
        }
    }

    public void updatePlayerUp() throws ExecutionException, InterruptedException {
        int previousCoordinate = firebase.getYCoordinate();
        int newCoordinate = previousCoordinate - 1;
        firebase.updatePlayer(firebase.getXCoordinate(), newCoordinate);
        stepsHandler();
    }

    public void updatePlayerDown() throws ExecutionException, InterruptedException {
        int previousCoordinate = firebase.getYCoordinate();
        int newCoordinate = previousCoordinate + 1;
        firebase.updatePlayer(firebase.getXCoordinate(), newCoordinate);
        stepsHandler();
    }

    public void updatePlayerRight() throws ExecutionException, InterruptedException {
        int previousCoordinate = firebase.getXCoordinate();
        int newCoordinate = previousCoordinate + 1;
        firebase.updatePlayer(newCoordinate, firebase.getYCoordinate());
        stepsHandler();
    }

    public void updatePlayerLeft() throws ExecutionException, InterruptedException {
        int previousCoordinate = firebase.getXCoordinate();
        int newCoordinate = previousCoordinate - 1;
        firebase.updatePlayer(newCoordinate, firebase.getYCoordinate());
        stepsHandler();
    }

    public Label findPlayerLabel(String player) {
        if (Objects.equals(player, Professor_Plum.getId())) {
            return Professor_Plum;
        } else if (Objects.equals(player, Mrs_Peacock.getId())) {
            return Mrs_Peacock;
        } else if (Objects.equals(player, Mr_Green.getId())) {
            return Mr_Green;
        } else if (Objects.equals(player, Mrs_White.getId())) {
            return Mrs_White;
        } else if (Objects.equals(player, Colonel_Mustard.getId())) {
            return Colonel_Mustard;
        } else if (Objects.equals(player, Ms_Scarlet.getId())) {
            return Ms_Scarlet;
        } else return null;
    }

    public void disableMovementButtons() {
        gridButtonUp.setDisable(true);
        gridButtonDown.setDisable(true);
        gridButtonRight.setDisable(true);
        gridButtonLeft.setDisable(true);

    }

    public void disableUIButtons() {
        endTurn.setDisable(true);
        accusationButton.setDisable(true);
        suggestionButton.setDisable(true);
    }

    public void enableMovementButtons() {
        gridButtonUp.setDisable(false);
        gridButtonDown.setDisable(false);
        gridButtonRight.setDisable(false);
        gridButtonLeft.setDisable(false);
    }

    public void enableUIButtons() {
        endTurn.setDisable(false);
        accusationButton.setDisable(false);
    }

    // ROOM LOGICS
    public void roomControl(Label roomID) throws ExecutionException, InterruptedException {
        if (xCoordinate == GridPane.getColumnIndex(roomID) && yCoordinate == GridPane.getRowIndex(roomID)) {
            if (roomID.getId().contains("enter_")) {
                String strippedEnterRoomID = roomID.getId().replace("enter_", "");
                updateRoom(strippedEnterRoomID);
                movePlayerInsideRoom(strippedEnterRoomID);
            } else if (roomID.getId().contains("leave_")){
                String strippedLeaveRoomID = roomID.getId().replace("leave_", "");
                firebase.updateRoomStatus("Corridor");
                movePlayerOutsideRoom(strippedLeaveRoomID);
            }
        }
    }

    public void updateRoom(String room) throws ExecutionException, InterruptedException {
        switch (room) {
            case "kitchen" -> firebase.updateRoomStatus("Kitchen");
            case "ballroom" -> firebase.updateRoomStatus("Ballroom");
            case "conservatory" -> firebase.updateRoomStatus("Conservatory");
            case "billiardRoom" -> firebase.updateRoomStatus("Billiard room");
            case "library" -> firebase.updateRoomStatus("Library");
            case "study" -> firebase.updateRoomStatus("Study");
            case "hall" -> firebase.updateRoomStatus("Hall");
            case "lounge" -> firebase.updateRoomStatus("Lounge");
            case "diningRoom" -> firebase.updateRoomStatus("Dining room");
        }
    }

    public void movePlayerInsideRoom(String room) throws ExecutionException, InterruptedException {
        switch (room) {
            case "kitchen", "ballroom", "conservatory" -> {
                updatePlayerUp();
                updatePlayerUp();
            }
            case "billiardRoom", "library" -> {
                updatePlayerRight();
                updatePlayerRight();
            }
            case "study", "hall", "lounge" -> {
                updatePlayerDown();
                updatePlayerDown();
            }
            case "diningRoom" -> {
                updatePlayerLeft();
                updatePlayerLeft();
            }
        }
    }

    public void movePlayerOutsideRoom(String room) throws ExecutionException, InterruptedException {
        switch (room) {
            case "ballroom" -> {
                updatePlayerDown();
                updatePlayerDown();
            }
            case "conservatory", "billiardRoom", "library", "study" -> {
                updatePlayerLeft();
                updatePlayerLeft();
            }
            case "hall" -> {
                updatePlayerUp();
                updatePlayerUp();
            }
            case "diningRoom", "lounge", "kitchen" -> {
                updatePlayerRight();
                updatePlayerRight();
            }
        }
    }

    public void stepsHandler() {
        int movementLimiter = 1;
        showSteps.setText(String.valueOf(numberOfSteps - movementLimiter));
        numberOfSteps -= 1;

        if (Objects.equals(showSteps.getText(), "0")) {
            disableMovementButtons();
        }
    }



    public void suggestion() throws IOException {
        writePlayerMessageToFirebase("is making a suggestion..");
        suggestionButton.setDisable(true);
        sceneController.suggestionScreen();
    }

    public void accusation() throws IOException {
        writePlayerMessageToFirebase("is making an accusation!!");
        sceneController.switchToAccusation();
    }

    public void listenerForMessages(){
        DocumentReference docRef = Globals.get_instance().getDb().collection(Globals.get_instance().lobbyPin).document("Messages");
        docRef.addSnapshotListener(
                (snapshot, e) -> {
                    if (snapshot != null && snapshot.exists()) {
                        Platform.runLater(()->{
                            addMessagesToListView(getPlayerMessagesFromFirebase(),logListView);
                            logListView.requestFocus();
                        });
                    }
                });
    }

    public void writePlayerMessageToFirebase(String message){
        DocumentReference docRef = Globals.get_instance().getDb().collection(Globals.get_instance().lobbyPin).document("Messages");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        HashMap<String, Object> messageData = new HashMap<String, Object>();
        HashMap<String, String> messageAndSender = new HashMap<String,String>();
        messageAndSender.put(Globals.get_instance().chosenPlayer, message);
        try {
            if(!future.get().exists()){
                messageData.put("0", messageAndSender);
                ApiFuture<WriteResult> writeResult = Globals.get_instance().getDb().collection(Globals.get_instance().lobbyPin).document("Messages").set(messageData);
            }

            else{
                messageData.put(String.valueOf(future.get().getData().size()), messageAndSender);
                ApiFuture<WriteResult> writeResult = Globals.get_instance().getDb().collection(Globals.get_instance().lobbyPin).document("Messages").update(messageData);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Map getPlayerMessagesFromFirebase(){
        DocumentReference docRef = Globals.get_instance().getDb().collection(Globals.get_instance().lobbyPin).document("Messages");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        try {
            return future.get().getData();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void addMessagesToListView(Map messagesMap, ListView listView){
        ObservableList<Label> lobbylabels = FXCollections.observableArrayList();
        for(int i = 0; i < messagesMap.size(); i++){
            Label label = new Label(messagesMap.get(String.valueOf(i)).toString());
            try {
                stringCleanup(decideColor(label));
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            lobbylabels.add(label);

        }
        listView.setItems(lobbylabels);
    }

    public Label decideColor(Label label) throws ExecutionException, InterruptedException {
//        currentPlayer = firebase.getCurrentPlayer();
        String labeltext = label.getText();
        if (labeltext.contains("Professor_Plum")) {
            label.setTextFill(Color.color(1, 0, 1));
            return label;
        }
        else if (labeltext.contains("Mrs_Peacock")) {
            label.setTextFill(Color.color(0, 0, 1));
            return label;
        }
        else if (labeltext.contains("Mr_Green")) {
            label.setTextFill(Color.color(0, 1, 0));
            return label;
        }
        else if (labeltext.contains("Mrs_White")) {
            label.setTextFill(Color.color(0, 0, 0));
            return label;
        }
        else if (labeltext.contains("Colonel_Mustard")) {
            label.setTextFill(Color.color(1, 1, 0));
            return label;
        }
        else if (labeltext.contains("Ms_Scarlet")) {
            label.setTextFill(Color.color(1, 0, 0));
            return label;
        }
        else{return label;}
    }
    public Label stringCleanup(Label label){
        String cleanString = label.getText();
        cleanString = cleanString.replace("{", "").replace("}", "").replace("=", " ").replace("_", " ");
        label.setText(cleanString);
        return label;
    }

    public void listenerWinner(ActionEvent event) throws IOException, ExecutionException, InterruptedException {
        DocumentReference docRef = Globals.get_instance().getDb().collection(Globals.get_instance().lobbyPin).document("System");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        if (future.get().getData().get("win") == null) {
        } else {
            winOrLoseScreen(event);
        }
    }

    public void winOrLoseScreen(ActionEvent event) throws ExecutionException, InterruptedException, IOException {
        String MyPlayer = Globals.get_instance().chosenPlayer;
        DocumentReference docRef = Globals.get_instance().getDb().collection(Globals.get_instance().lobbyPin).document("System");
        ApiFuture<DocumentSnapshot> future = docRef.get();

        String winner = Objects.requireNonNull(future.get().getData()).get("win").toString();

        if (winner.equals(MyPlayer)) {
            sceneController.switchToWinScreen();
            sceneController.closeWindow(event);
        } else {
            sceneController.switchToLostScreen();
        }
    }

    @FXML
    public void roll(){
        Dice dice = new Dice();
        diceView.rollPaneIsVisible(true);
        rollButton.setDisable(false);
        rollButton.setOnAction(event -> {
            writePlayerMessageToFirebase("has rolled the dice");
            Thread thread = new Thread(() -> {
                for (int i = 0; i < 15; i++) {
                    diceView.diceAnimation(dice.DiceRoll(), dice.DiceRoll());
                }
                int playerDice1 = dice.DiceRoll();
                int playerDice2 = dice.DiceRoll();
                diceView.diceAnimation(playerDice1, playerDice2);
                diceView.rollPaneIsVisible(false);
                Platform.runLater(() -> setStepsInit(playerDice1, playerDice2));
            }
            );thread.start();
        });
    }

    public void setStepsInit(Integer playerDice1, Integer playerDice2) {
        showSteps.setText(String.valueOf(playerDice1 + playerDice2));
        enableMovementButtons();
        numberOfSteps = playerDice1 + playerDice2;
    }

    @FXML
    void setCards() {
        for (Node node : notebookPane.getChildren()) {
            if (node instanceof CheckBox checkBox) {
                for (Map card : Globals.get_instance().playerCards) {
                    if (card.get("naam").equals(checkBox.getText())){
                        checkBox.setTextFill(Color.BLUE);
                        checkBox.setSelected(true);
                        checkBox.setDisable(true);
                    }
                }
            }
        }
    }
    @FXML
    void setSuggestedCard(String suggestedCard) {
        for (Node node : this.notebookPane.getChildren()) {
            if (node instanceof CheckBox checkBox) {
                if (checkBox.getText().equals(suggestedCard)) {
                    checkBox.setTextFill(Color.rgb(255, 0, 0));
                    checkBox.setSelected(true);
                    checkBox.setDisable(true);
                }
            }
        }
    }

    public void listenerForSuggestion() {
        DocumentReference docRef = Globals.get_instance().getDb().collection(Globals.get_instance().lobbyPin).document("System");
        docRef.addSnapshotListener(
                (snapshot, e) -> {
                    if (e != null) {
                        System.err.println("Listen failed: " + e);
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        if (Objects.equals(currentPlayer, Globals.get_instance().chosenPlayer)) {
                            if (snapshot.getString("suggestedCharacter") != null &&
                                    snapshot.getString("suggestedWeapon") != null &&
                                    snapshot.getString("suggestedRoom") != null) {
                                int indexCurrentPlayer = players.indexOf(currentPlayer);
                                for (int i = 0; i < players.size(); i++) {
                                    if (i != indexCurrentPlayer) {
                                        try {
                                            for(String suggestedCard : firebase.getSuggestionCards()) {
                                                for (Map card : firebase.getCardsSpecific(players.get(i))) {
                                                    String cardname = (String) card.get("naam");
                                                    if (Objects.equals(cardname, suggestedCard)){
                                                        setSuggestedCard(suggestedCard);
                                                        return;
                                                    }
                                                }
                                            }
                                        } catch (ExecutionException | InterruptedException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
    };





}

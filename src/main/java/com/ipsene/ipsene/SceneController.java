package com.ipsene.ipsene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class SceneController {
    @FXML public AnchorPane menu;
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void switchScene(ActionEvent event, String location) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(location)));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void switchNewWindow(String location) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(location)));
        Stage stage = new Stage();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void closeWindow(ActionEvent event){
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
    public void switchSceneNoEvent(String location, Node node) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(location)));
        stage = (Stage) node.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    //TO:   MAIN MENU
    public void switchToMainMenu(ActionEvent event) throws IOException {
        switchScene(event,"Screen/mainMenu.fxml");
    }
    //FROM: EXIT GAME BUTTON
    //TO:   EXIT GAME SCREEN
    public void switchToExitGame(ActionEvent event) throws IOException {
        switchScene(event,"Screen/exitGame.fxml");
    }
    //FROM: HOST LOBBY BUTTON
    //TO:   HOST LOBBY (ENTER LOBBY NAME)
    public void switchToHostLobby(ActionEvent event) throws IOException {
        switchScene(event, "Screen/hostLobby.fxml");
    }
    //FROM: JOIN LOBBY BUTTON
    //TO:   JOIN LOBBY (ENTER LOBBY NAME)
    public void switchToJoinLobby(ActionEvent event) throws IOException {
        switchScene(event, "Screen/joinLobby2.fxml");
    }
    //FROM: JOIN + HOST LOBBY
    //TO:   CHARACTER SELECT
    public void switchToCharacterSelection(ActionEvent event) throws IOException {
        switchScene(event, "Screen/characterSelection.fxml");
    }
    //FROM: CHARACTER SELECT
    //TO:   LOBBY
    public void switchToLobby(ActionEvent event) throws IOException {
        switchScene(event, "Screen/lobbyRoom.fxml");
    }
    //FROM ACCUSATION
    //TO: WIN SCREEN
    public void switchToWinScreen() throws IOException {
        switchNewWindow("Screen/win_screen.fxml");
    }
    //TO: LOSE SCREEN
    public void switchToLostScreen() throws IOException {
        switchNewWindow("Screen/lost_screen.fxml");
    }
    //FROM: BOARD
    //TO: ACCUSATION
    public void switchToAccusation() throws IOException {
        switchNewWindow("Screen/accusation_screen.fxml");
    }
    //FROM: START GAME BUTTON
    //TO:   GAME
    public void switchToGameBoard(ActionEvent event) throws IOException {
        switchScene(event, "Screen/clueBoard.fxml");
    }
    //FROM: LOBBY
    //TO: CHARACTER SELECT

    public void switchToCharacterSelect() throws IOException {
        switchNewWindow("Screen/characterSelection.fxml");
    }
    //FROM: LOBBY
    //TO:   SUGGESTION SCREEN
    public void suggestionScreen() throws IOException{
        switchNewWindow("Screen/beschuldiging.fxml");
    }

    public void endApplication() {
        System.exit(1);
    }
}

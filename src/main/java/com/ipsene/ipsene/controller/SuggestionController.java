package com.ipsene.ipsene.controller;

import com.ipsene.ipsene.SceneController;
import com.ipsene.ipsene.service.Firebase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.util.concurrent.ExecutionException;

public class SuggestionController {

    private Stage stage;
    private Scene scene;
    private Parent root;
    private ArrayList<RadioButton> suspects = new ArrayList<RadioButton>();
    private ArrayList<RadioButton> weapons = new ArrayList<RadioButton>();
    private static final Firebase firebase = Firebase.getInstance();
    private ArrayList<String> suggestionCards = new ArrayList<>();
    private SceneController sceneController = new SceneController();
    @FXML AnchorPane suggestionPane;
    @FXML private ToggleGroup Susp, Weapons;
    @FXML private RadioButton suspectButton1, suspectButton2, suspectButton3, suspectButton4, suspectButton5, suspectButton6;
    @FXML private RadioButton weaponButton1, weaponButton2, weaponButton3, weaponButton4, weaponButton5, weaponButton6;
    @FXML private Button aproveBtn, aproveBtn2, next, nextBoard;
    @FXML private Label suspectLabel, weaponLabel;

//
    public void suggestSuspect(ActionEvent event){
        suspects.add(suspectButton1);
        suspects.add(suspectButton2);
        suspects.add(suspectButton3);
        suspects.add(suspectButton4);
        suspects.add(suspectButton5);
        suspects.add(suspectButton6);

        for (Node node : suspects) {
            if (node instanceof RadioButton) {
                if (((RadioButton) node).isSelected()) {
                    suspectButton1.setDisable(true);
                    suspectButton2.setDisable(true);
                    suspectButton3.setDisable(true);
                    suspectButton4.setDisable(true);
                    suspectButton5.setDisable(true);
                    suspectButton6.setDisable(true);
                }
            }
        }
        for (Node node : suggestionPane.getChildren()) {
            if (node instanceof RadioButton) {
                if (((RadioButton) node).isSelected()) {
                    suspectLabel.setText("You have selected: " + ((RadioButton) node).getText());
                }
            }
            next.setDisable(false);
            aproveBtn.setDisable(true);
        }
    }

     public void weaponSuggestionScreen(ActionEvent event) throws IOException {
         for (Node node : suggestionPane.getChildren()){
             if (node instanceof RadioButton){
                 if(((RadioButton) node).isSelected()) {
                     Alert suspectMessage = new Alert(Alert.AlertType.INFORMATION);
                     suspectMessage.setContentText("Player suggest "  + ((RadioButton) node).getText() + " committed the murder");
                     this.suggestionCards.add(((RadioButton) node).getText());
                     suspectMessage.show();
                 }
             }
         }
         loadSecondSuggestScreen(event);
     }

    public void suggestWeapon(ActionEvent event) {
        weapons.add(weaponButton1);
        weapons.add(weaponButton2);
        weapons.add(weaponButton3);
        weapons.add(weaponButton4);
        weapons.add(weaponButton5);
        weapons.add(weaponButton6);
        for (Node node : weapons) {
            if (node instanceof RadioButton) {
                if (((RadioButton) node).isSelected()) {
                    weaponButton1.setDisable(true);
                    weaponButton2.setDisable(true);
                    weaponButton3.setDisable(true);
                    weaponButton4.setDisable(true);
                    weaponButton5.setDisable(true);
                    weaponButton6.setDisable(true);
                }
            }
        }

        for (Node node : suggestionPane.getChildren()) {
            if (node instanceof RadioButton) {
                if (((RadioButton) node).isSelected()) {
                    weaponLabel.setText("You have selected: " + ((RadioButton) node).getText());
                }
            }
        }
        nextBoard.setDisable(false);
        aproveBtn2.setDisable(true);
    }

     public void suggestionToBoard(ActionEvent event) throws IOException, ExecutionException, InterruptedException {
         for (Node node : suggestionPane.getChildren()) {
             if (node instanceof RadioButton) {
                 if (((RadioButton) node).isSelected()) {
                     Alert weaponMessege = new Alert(Alert.AlertType.INFORMATION);
                     weaponMessege.setContentText("Player suggest " + ((RadioButton) node).getText() + " is the used weapon");
                     this.suggestionCards.add(((RadioButton) node).getText());
                     weaponMessege.show();
                 }
             }
         }
         firebase.updateSuggestionCards(this.suggestionCards.get(0), this.suggestionCards.get(1), firebase.getRoom());
         firebase.updateSuggestingPlayer(firebase.getCurrentPlayer());
         sceneController.closeWindow(event);
     }

    public void loadSecondSuggestScreen(ActionEvent event) throws IOException {
         FXMLLoader fxmlLoader = new FXMLLoader();
         fxmlLoader.setLocation(getClass().getResource("/com/ipsene/ipsene/Screen/beschuldiging2.fxml"));
         fxmlLoader.setController(this);
         Scene scene = new Scene(fxmlLoader.load());
         stage = (Stage)((Node)event.getSource()).getScene().getWindow();
         stage.setScene(scene);
         stage.show();
     }
}
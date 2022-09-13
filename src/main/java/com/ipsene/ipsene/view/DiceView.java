package com.ipsene.ipsene.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import java.io.File;

public class DiceView {
    @FXML
    private BorderPane rollDicePane;
    @FXML
    private ImageView diceImg;
    @FXML
    private ImageView diceImg2;
    @FXML
    private Button rollButton;
    public void init(ImageView diceImg, ImageView diceImg2, Button rollButton, BorderPane rollDicePane) {
        this.diceImg = diceImg;
        this.diceImg2 = diceImg2;
        this.rollButton = rollButton;
        this.rollDicePane = rollDicePane;
    }

    public void diceAnimation(int dice1, int dice2) {
        rollButton.setDisable(true);
        File file = new File("src/main/resources/com/ipsene/ipsene/Images/Dice/dice" + (dice1)+".png");
        File file2 = new File("src/main/resources/com/ipsene/ipsene/Images/Dice/dice" + (dice2)+".png");
        diceImg.setImage(new Image(file.toURI().toString()));
        diceImg2.setImage(new Image(file2.toURI().toString()));
        try {
            Thread.sleep(70);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void rollPaneIsVisible(boolean isVisible){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.rollDicePane.setVisible(isVisible);
    }
}

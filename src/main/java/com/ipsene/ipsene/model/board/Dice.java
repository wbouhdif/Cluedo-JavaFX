package com.ipsene.ipsene.model.board;

import java.util.Random;

public class Dice {
    public int DiceRoll(){
       //Deze functie zorgt ervoor dat er een random getal uitkomt die 1 tot en met 6 kan zijn
        Random rand = new Random();
        return rand.nextInt(6) + 1;
    }
}

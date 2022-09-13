package com.ipsene.ipsene.controller;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;


public class AudioController {
    public void backgroundMusic(){
        Media media = new Media(new File("src/main/resources/com/ipsene/ipsene/Audio/pinkpanthertheme.mp3").toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        mediaPlayer.setAutoPlay(true);
    }

}


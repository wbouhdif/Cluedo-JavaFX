package com.ipsene.ipsene.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;
import com.ipsene.ipsene.Globals;
import com.ipsene.ipsene.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HostLobbyController {

    @FXML private TextField lobbyName;
    @FXML private Button makeLobbyButton;
    SceneController sceneController = new SceneController();


    public String generateUUID() {
        UUID uuid = UUID.randomUUID();
        Globals.get_instance().hostUUID = uuid.toString();
        return uuid.toString();
    }

    public void createLobby(ActionEvent event) throws IOException {
        Globals.get_instance().lobbyPin = lobbyName.getText();
        Globals.get_instance().isHost = true;
        Map<String, Object> docData = new HashMap<>();
        docData.put("UUID", generateUUID());
        docData.put("gameState", false);
        docData.put("playersArray", null);
        docData.put("currentPlayer", null);
        ApiFuture<WriteResult> future = Globals.get_instance().getCollectionReference().document("System").set(docData);
        Map<String, Object> lobbyLogList = new HashMap<>();
        lobbyLogList.put("Time", LocalDateTime.now().toString());
        lobbyLogList.put("Player count", 1);
        ApiFuture<WriteResult> logListWrite = Globals.get_instance().getDb().collection("ActiveLobbies").document(Globals.get_instance().lobbyPin).set(lobbyLogList);
        sceneController.switchToCharacterSelection(event);
    }
}
package com.ipsene.ipsene.service;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.ipsene.ipsene.Globals;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public final class Firebase {
    String currentPlayer = null;
    ArrayList<String> players = null;
    ArrayList<Map> playersSpecific = null;
    DocumentReference docRef = null;

    private Firebase(){
    }
    private static final Firebase firebase_instance = new Firebase();
    public static Firebase getInstance(){
        return firebase_instance;
    }

    public DocumentReference getDocRefSystem() {
        if (docRef == null) {
            docRef = Globals.get_instance().getCollectionReference().document("System");
        }
        return docRef;
    }
    public static void initialize(InputStream serviceAccount) throws IOException {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://iipsene-10-default-rtdb.europe-west1.firebasedatabase.app/")
                .build();
        FirebaseApp.initializeApp(options);

        Globals.get_instance().setDb(FirestoreClient.getFirestore());
    }
    public void getCards() throws ExecutionException, InterruptedException {
        String player = Globals.get_instance().chosenPlayer;
        DocumentReference docRefPlayer = Globals.get_instance().getCollectionReference().document(player);
        ApiFuture<DocumentSnapshot> future = docRefPlayer.get();
        DocumentSnapshot document = future.get();
        Globals.get_instance().playerCards = (ArrayList<Map>) document.get("cards");
    }

    public ArrayList<Map> getCardsSpecific(String player) throws ExecutionException, InterruptedException {
            DocumentReference docRef = Globals.get_instance().getDb().collection(Globals.get_instance().lobbyPin).document(player);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            playersSpecific = (ArrayList<Map>) document.get("cards");
        return playersSpecific;
    }



    public ArrayList<String> getPlayers() throws ExecutionException, InterruptedException {
        if (players == null) {
            ApiFuture<DocumentSnapshot> future = getDocRefSystem().get();
            DocumentSnapshot document = future.get();
            players = (ArrayList<String>) document.get("playersArray");
        }
        return players;
    }
    public String getCurrentPlayer() throws ExecutionException, InterruptedException {
        DocumentReference docRef = Globals.get_instance().getDb().collection(Globals.get_instance().lobbyPin).document("System");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        return currentPlayer = Objects.requireNonNull(document.getString("currentPlayer"));
    }
    public void setCurrentPlayer(String currentPlayer) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = getDocRefSystem().update("currentPlayer", currentPlayer);
        WriteResult result = future.get();
        this.currentPlayer = currentPlayer;
    }
    public int getXCoordinate() throws ExecutionException, InterruptedException {
        DocumentReference docRef = Globals.get_instance().getDb().collection(Globals.get_instance().lobbyPin).document(getCurrentPlayer());
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        double newLocation = Objects.requireNonNull(document.getDouble("xCoord"));
        return ((int) newLocation);
    }
    public int getYCoordinate() throws ExecutionException, InterruptedException {
        DocumentReference docRef = Globals.get_instance().getDb().collection(Globals.get_instance().lobbyPin).document(getCurrentPlayer());
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        double newLocation = Objects.requireNonNull(document.getDouble("yCoord"));
        return ((int) newLocation);
    }
    public void updatePlayer(int xCoord, int yCoord) throws ExecutionException, InterruptedException {
        Map<String, Object> update = new HashMap<>();
        update.put("xCoord", xCoord);
        update.put("yCoord", yCoord);
        ApiFuture<WriteResult> writeResult = Globals.get_instance().getDb().collection(Globals.get_instance().lobbyPin).document(getCurrentPlayer()).set(update, SetOptions.merge());
    }
    public void updateRoomStatus(String roomID) throws ExecutionException, InterruptedException {
        Map<String, Object> update = new HashMap<>();
        update.put("location", roomID);
        ApiFuture<WriteResult> writeResult = Globals.get_instance().getDb().collection(Globals.get_instance().lobbyPin).document(getCurrentPlayer()).set(update, SetOptions.merge());
    }

    public void updateSuggestionCards(String suggestedCharacter, String suggestedWeapon, String suggestedRoom) throws ExecutionException, InterruptedException {
        DocumentReference docRef = Globals.get_instance().getCollectionReference().document("System");
        ApiFuture<WriteResult> future =
            docRef.update("suggestedCharacter", suggestedCharacter);
            docRef.update("suggestedWeapon", suggestedWeapon);
            docRef.update("suggestedRoom", suggestedRoom);
    }

    public void resetSuggestion(){
        DocumentReference docRef = Globals.get_instance().getCollectionReference().document("System");
        ApiFuture<WriteResult> future =
            docRef.update("suggestedCharacter", null);
            docRef.update("suggestedWeapon", null);
            docRef.update("suggestedRoom", null);
            docRef.update("suggestingPlayer", null);


    }

    public void updateSuggestingPlayerTarget(String suggestingPlayerTarget) throws ExecutionException, InterruptedException {
        DocumentReference docRef = Globals.get_instance().getCollectionReference().document("System");
        ApiFuture<WriteResult> future =
                docRef.update("suggestingPlayerTarget", suggestingPlayerTarget);
    }

    public String[] getSuggestionCards() throws ExecutionException, InterruptedException {
        DocumentReference docRef = Globals.get_instance().getDb().collection(Globals.get_instance().lobbyPin).document("System");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        String [] suggestedCards = new String[3];
        suggestedCards[1] = (document.getString("suggestedWeapon"));
        suggestedCards[2] = (document.getString("suggestedRoom"));
        suggestedCards[0] = (document.getString("suggestedCharacter"));
        return suggestedCards;
    }

    public String getSuggestingPlayer() throws ExecutionException, InterruptedException {
        DocumentReference docRef = Globals.get_instance().getDb().collection(Globals.get_instance().lobbyPin).document("System");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();;
        return (document.getString("suggestingPlayer"));
    }

    public String getSuggestingPlayerTarget() throws ExecutionException, InterruptedException {
        DocumentReference docRef = Globals.get_instance().getDb().collection(Globals.get_instance().lobbyPin).document("System");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();;
        return (document.getString("suggestingPlayerTarget"));
    }

    public void updateSuggestingPlayer(String suggestingPlayer) throws ExecutionException, InterruptedException {
        DocumentReference docRef = Globals.get_instance().getCollectionReference().document("System");
        ApiFuture<WriteResult> future =
                docRef.update("suggestingPlayer", suggestingPlayer);
    }

    public String getRoomSpecific(String player) throws ExecutionException, InterruptedException {
        DocumentReference docRef = Globals.get_instance().getDb().collection(Globals.get_instance().lobbyPin).document(player);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        return Objects.requireNonNull(document.getString("location"));
    }

    public String getRoom() throws ExecutionException, InterruptedException {
        DocumentReference docRef = Globals.get_instance().getDb().collection(Globals.get_instance().lobbyPin).document(getCurrentPlayer());
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        return Objects.requireNonNull(document.getString("location"));
    }
}

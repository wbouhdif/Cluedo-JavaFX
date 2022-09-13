package com.ipsene.ipsene;


import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.ipsene.ipsene.service.Firebase;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
            InputStream serviceAccount = getClass().getResourceAsStream("/credentials.json");
            Firebase.initialize(serviceAccount);
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Screen/mainMenu.fxml")));
            Scene scene = new Scene(root);
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                if(!(Globals.get_instance().lobbyPin == null)){
                Firestore db = FirestoreClient.getFirestore();
                DocumentReference docRef = db.collection("ActiveLobbies").document(Globals.get_instance().lobbyPin);
                ApiFuture<DocumentSnapshot> future = docRef.get();
                try {
                    ApiFuture<WriteResult> writeResult = docRef.update("Player count", Integer.valueOf(Integer.parseInt(future.get().getData().get("Player count").toString())
                )-1);
                } catch (InterruptedException | ExecutionException ex) {
                    throw new RuntimeException(ex);
            }}
            }});
            stage.setScene(scene);
            stage.show();
            }

    public static void main(String[] args) {
        launch(args);
    }
}


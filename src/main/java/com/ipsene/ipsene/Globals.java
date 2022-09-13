package com.ipsene.ipsene;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import java.util.ArrayList;
import java.util.Map;

public final class Globals {

    public boolean isHost;
    public String chosenPlayer;
    public String lobbyPin;
    public String hostUUID;
    public ArrayList<Map> playerCards;
    private Firestore db;
    private CollectionReference collectionReference = null;

    public Firestore getDb() {
        return db;
    }
    public void setDb(Firestore db) {
        this.db = db;
    }

    private static final Globals _instance = new Globals();
    private Globals() {
    }
    public static Globals get_instance() {
        return _instance;
    }

    public CollectionReference getCollectionReference() {
        if (collectionReference == null) {
            collectionReference = db.collection(Globals.get_instance().lobbyPin);
        }
        return collectionReference;
    }

}

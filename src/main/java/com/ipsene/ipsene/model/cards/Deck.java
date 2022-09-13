package com.ipsene.ipsene.model.cards;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.SetOptions;
import com.ipsene.ipsene.Globals;
import java.util.*;


public class Deck {

    Solution solution;
    ArrayList<Card> deck = new ArrayList<>();
    ArrayList<Card> cardsPlayer0 = new ArrayList<>();
    ArrayList<Card> cardsPlayer1 = new ArrayList<>();
    ArrayList<Card> cardsPlayer2 = new ArrayList<>();
    ArrayList<Card> cardsPlayer3 = new ArrayList<>();
    ArrayList<Card> cardsPlayer4 = new ArrayList<>();
    ArrayList<Card> cardsPlayer5 = new ArrayList<>();

    public Deck() {
    }

    public Deck(boolean storeData) {
        this.storeData = storeData;
    }

    public boolean isStoreData() {
        return storeData;
    }

    boolean storeData = true;

    private void fillPersonCards(ArrayList<CardPerson> persons, String ... args){
        for (String arg : args){
            persons.add(new CardPerson(arg));
        }
    }

    private void fillWeaponCards(ArrayList<CardWeapon> weapons, String ... args){
        for (String arg : args){
            weapons.add(new CardWeapon(arg));
        }
    }

    private void fillRoomCards(ArrayList<CardRoom> rooms, String ... args){
        for (String arg : args){
            rooms.add(new CardRoom(arg));
        }
    }

    public Solution createCards() {
        // create decks for types of cards
        ArrayList<CardPerson> persons = new ArrayList<>();
        ArrayList<CardWeapon> weapons = new ArrayList<>();
        ArrayList<CardRoom> rooms = new ArrayList<>();

        /*
        6 suspects
            Miss Scarlet (red), Colonel Mustard (yellow), Mrs. White (white),
            Mr. Green (green), Mrs. Peacock (blue), Professor Plum (purple)

        6 weapons
            Candlestick, Dagger, Lead pipe,
            Revolver, Rope, Wrench

        9 rooms
            Kitchen, Ballroom, Conservatory, Dining room,
            Billiard room, Library, Lounge, Hall, Study
        */


        fillPersonCards(persons, "Miss Scarlet", "Colonel Mustard", "Mrs. White",
                                        "Mr. Green", "Mrs. Peacock", "Professor Plum");
        fillWeaponCards(weapons, "Candlestick", "Dagger", "Lead pipe", "Revolver", "Rope", "Wrench");
        fillRoomCards(rooms, "Kitchen", "Ballroom", "Conservatory", "Dining room",
                                    "Billiard room", "Library", "Lounge", "Hall", "Study");

        // create solution
        Random random = new Random();
        CardPerson solPerson = persons.remove(random.nextInt(persons.size()));
        CardRoom solRoom = rooms.remove(random.nextInt(rooms.size()));
        CardWeapon solWeapon = weapons.remove(random.nextInt(weapons.size()));

        solution = new Solution(solPerson, solWeapon, solRoom);

        // create deck to deal
        deck.addAll(persons);
        deck.addAll(weapons);
        deck.addAll(rooms);

        return solution;
    }

    public void dealCards(int numberOfPlayers, ArrayList<String> players) {
        // shuffle deck
        Collections.shuffle(deck);

        // deal deck
        int dealToPlayer = 0;

        for(Card card: deck) {
            switch (dealToPlayer) {
                case 0 -> cardsPlayer0.add(card);
                case 1 -> cardsPlayer1.add(card);
                case 2 -> cardsPlayer2.add(card);
                case 3 -> cardsPlayer3.add(card);
                case 4 -> cardsPlayer4.add(card);
                case 5 -> cardsPlayer5.add(card);
            }
            dealToPlayer++;
            if (dealToPlayer >= numberOfPlayers) {
                dealToPlayer = 0;
            }
        }

        if (isStoreData()) {
            cardsFirebase(numberOfPlayers, cardsPlayer0, cardsPlayer1, cardsPlayer2, cardsPlayer3, cardsPlayer4, cardsPlayer5, players);
        }
    }

    public void cardsFirebase(int numberOfPlayers,
                              ArrayList<Card> cardsPlayer0, ArrayList<Card> cardsPlayer1, ArrayList<Card> cardsPlayer2,
                              ArrayList<Card> cardsPlayer3, ArrayList<Card> cardsPlayer4, ArrayList<Card> cardsPlayer5, ArrayList<String> players) {

        CollectionReference docRef = Globals.get_instance().getCollectionReference();

        Map<String, Object> updates = new HashMap<>();

        for (int i = 0; i < numberOfPlayers; i++){
            switch (i) {
                case 0 -> {
                    updates.put("cards", cardsPlayer0);
                    docRef.document(players.get(0)).set(updates, SetOptions.merge());
                }
                case 1 -> {
                    updates.put("cards", cardsPlayer1);
                    docRef.document(players.get(1)).set(updates, SetOptions.merge());

                }
                case 2 -> {
                    updates.put("cards", cardsPlayer2);
                    docRef.document(players.get(2)).set(updates, SetOptions.merge());
                }
                case 3 -> {
                    updates.put("cards", cardsPlayer3);
                    docRef.document(players.get(3)).set(updates, SetOptions.merge());
                }
                case 4 -> {
                    updates.put("cards", cardsPlayer4);
                    docRef.document(players.get(4)).set(updates, SetOptions.merge());
                }
                case 5 -> {
                    updates.put("cards", cardsPlayer5);
                    docRef.document(players.get(5)).set(updates, SetOptions.merge());
                }
            }
        }

    }
}

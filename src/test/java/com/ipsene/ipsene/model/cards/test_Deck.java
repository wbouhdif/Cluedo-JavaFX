package com.ipsene.ipsene.model.cards;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class test_Deck {

    @Test
    void test_createCards_deck() {
        Deck deck = new Deck(false);
        deck.createCards();

        ArrayList<Card> result = deck.deck;
        final int expected = 18;

        assertEquals(expected, result.size());
    }


    @Test
    void test_createCards_solution() {
        Deck deck = new Deck(false);
        deck.createCards();

        ArrayList<Card> result = deck.deck;

        assertFalse(result.contains(deck.solution.getPerson()));
        assertFalse(result.contains(deck.solution.getWeapon()));
        assertFalse(result.contains(deck.solution.getRoom()));
    }


    @Test
    void test_dealCards_2player() {
        Deck deck = new Deck(false);
        deck.createCards();
        ArrayList<String> players = new ArrayList<>();
        players.add("Professor_Plum");
        players.add("Mrs_Peacock");
        deck.dealCards(players.size(), players);

        for (Card card : deck.cardsPlayer0) {
            assertFalse(deck.cardsPlayer1.contains(card));
        }

        assertEquals(9, deck.cardsPlayer0.size());
        assertEquals(9, deck.cardsPlayer1.size());
        assertEquals(0, deck.cardsPlayer2.size());
        assertEquals(0, deck.cardsPlayer3.size());
        assertEquals(0, deck.cardsPlayer4.size());
        assertEquals(0, deck.cardsPlayer5.size());
    }


    @Test
    void test_dealCards_6player() {
        Deck deck = new Deck(false);
        deck.createCards();
        ArrayList<String> players = new ArrayList<>();
        players.add("Professor_Plum");
        players.add("Mrs_Peacock");
        players.add("Mr_Green");
        players.add("Mrs_White");
        players.add("Colonel_Mustard");
        players.add("Ms_Scarlet");
        deck.dealCards(players.size(), players);

        for (Card card : deck.cardsPlayer0) {
            assertFalse(deck.cardsPlayer1.contains(card));
            assertFalse(deck.cardsPlayer2.contains(card));
            assertFalse(deck.cardsPlayer3.contains(card));
            assertFalse(deck.cardsPlayer4.contains(card));
            assertFalse(deck.cardsPlayer5.contains(card));
        }

        for (Card card : deck.cardsPlayer1) {
            assertFalse(deck.cardsPlayer2.contains(card));
            assertFalse(deck.cardsPlayer3.contains(card));
            assertFalse(deck.cardsPlayer4.contains(card));
            assertFalse(deck.cardsPlayer5.contains(card));
        }

        for (Card card : deck.cardsPlayer2) {
            assertFalse(deck.cardsPlayer3.contains(card));
            assertFalse(deck.cardsPlayer4.contains(card));
            assertFalse(deck.cardsPlayer5.contains(card));
        }

        for (Card card : deck.cardsPlayer3) {
            assertFalse(deck.cardsPlayer4.contains(card));
            assertFalse(deck.cardsPlayer5.contains(card));
        }

        for (Card card : deck.cardsPlayer4) {
            assertFalse(deck.cardsPlayer5.contains(card));
        }

        assertEquals(3, deck.cardsPlayer0.size());
        assertEquals(3, deck.cardsPlayer1.size());
        assertEquals(3, deck.cardsPlayer2.size());
        assertEquals(3, deck.cardsPlayer3.size());
        assertEquals(3, deck.cardsPlayer4.size());
        assertEquals(3, deck.cardsPlayer5.size());
    }
}
package com.ipsene.ipsene.model.cards;

public class Card {

    @Override
    public String toString() {
        return getNaam();
    }

    private String naam;

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public Card(String naam) {
        this.naam = naam;
    }
}

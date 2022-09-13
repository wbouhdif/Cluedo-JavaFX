package com.ipsene.ipsene.model.cards;

public class Solution {
    CardPerson person;
    CardWeapon weapon;
    CardRoom room;

    @Override
    public String toString() {
        return person + ", " + weapon + ", " + room;
    }

    public Solution(CardPerson person, CardWeapon weapon, CardRoom room) {
        this.person = person;
        this.weapon = weapon;
        this.room = room;
    }

    public CardPerson getPerson() {
        return person;
    }

    public void setPerson(CardPerson person) {
        this.person = person;
    }

    public CardWeapon getWeapon() {
        return weapon;
    }

    public void setWeapon(CardWeapon weapon) {
        this.weapon = weapon;
    }

    public CardRoom getRoom() {
        return room;
    }

    public void setRoom(CardRoom room) {
        this.room = room;
    }
}

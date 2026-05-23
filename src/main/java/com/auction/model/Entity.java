package com.auction.model;

import java.util.UUID;

public abstract class Entity {
    protected int id;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

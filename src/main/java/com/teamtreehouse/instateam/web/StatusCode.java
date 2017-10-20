package com.teamtreehouse.instateam.web;

public enum StatusCode {
    ACTIVE("Active"),
    ARCHIVED("Archived"),
    NOT_STARTED("Not Started");

    private final String name;

    StatusCode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

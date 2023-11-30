package com.eventfull.scraper.model;

public enum SourceNames {

    EVENTS_MADEIRA("Events Madeira");

    private final String sourceName;

    SourceNames(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceName() {
        return sourceName;
    }
}

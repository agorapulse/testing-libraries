package com.agorapulse.officeunit;

class SimpleDocumentDifference implements DocumentDifference {
    private final String path;

    SimpleDocumentDifference(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Difference found: " + path;
    }
}
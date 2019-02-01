package com.agorapulse.testing.officeunit;

import org.xmlunit.diff.Difference;

class XmlDocumentDifference implements DocumentDifference {

    private final String path;
    private final Difference difference;

    XmlDocumentDifference(String path, Difference difference) {
        this.path = path;
        this.difference = difference;
    }

    @Override
    public String toString() {
        return "Path: " + path + "\nDifferences:\n" + difference.toString();
    }
}

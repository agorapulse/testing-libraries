package com.agorapulse.officeunit;

import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;
import org.xmlunit.diff.DifferenceEvaluators;

import javax.xml.transform.Source;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public enum XmlDifferenceCollector implements DifferenceCollector {

    INSTANCE;

    public List<DocumentDifference> computeDifferences(String path, InputStream actualStream, InputStream expectedStream, Set<String> ignoredPaths) {
        List<Difference> ret = new ArrayList<>();
        Source input = Input.fromStream(actualStream).build();
        Source output = Input.fromStream(expectedStream).build();

        Diff diff = DiffBuilder
                .compare(input)
                .withTest(output)
                .ignoreComments()
                .ignoreWhitespace()
                .checkForSimilar()
                .withDifferenceEvaluator(DifferenceEvaluators.chain(
                        DifferenceEvaluators.Default,
                        (comparison, outcome) -> {
                            if (ComparisonResult.DIFFERENT.equals(outcome)) {
                                for (String ignoredPath : ignoredPaths) {
                                    if (comparison.getControlDetails().getXPath() != null && comparison.getControlDetails().getXPath().endsWith(ignoredPath)) {
                                        return ComparisonResult.EQUAL;
                                    }
                                }
                            }
                            return outcome;
                        }
                ))
                .build();

        for (Difference difference : diff.getDifferences()) {
            ret.add(difference);
        }

        return ret.stream().map(difference -> new XmlDocumentDifference(path, difference)).collect(Collectors.toList());
    }
}

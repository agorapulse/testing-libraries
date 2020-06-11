/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2020 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agorapulse.testing.officeunit;

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

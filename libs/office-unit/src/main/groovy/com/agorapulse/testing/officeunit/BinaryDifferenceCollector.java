/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2025 Agorapulse.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public enum BinaryDifferenceCollector implements DifferenceCollector {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(BinaryDifferenceCollector.class);

    public List<DocumentDifference> computeDifferences(String path, InputStream actualStream, InputStream expectedStream, Set<String> ignoredPaths) {
        try {
            boolean contentEquals = contentEquals(actualStream, expectedStream);
            if (!contentEquals) {
                return Collections.singletonList(new SimpleDocumentDifference(path));
            }
        } catch (IOException e){
            LOGGER.error("Exception comparing two binary files", e);
        }
        return Collections.emptyList();
    }

    private static boolean contentEquals(InputStream input1, InputStream input2) throws IOException {
        if (input1 == input2) {
            return true;
        } else {
            if (!(input1 instanceof BufferedInputStream)) {
                input1 = new BufferedInputStream(input1);
            }

            if (!(input2 instanceof BufferedInputStream)) {
                input2 = new BufferedInputStream(input2);
            }

            int ch2;
            for(int ch = input1.read(); -1 != ch; ch = input1.read()) {
                ch2 = input2.read();
                if (ch != ch2) {
                    return false;
                }
            }

            ch2 = input2.read();
            return ch2 == -1;
        }
    }

    @Override
    public String toString() {
        return "Binary difference";
    }
}

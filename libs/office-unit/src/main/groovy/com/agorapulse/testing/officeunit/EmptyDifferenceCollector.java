/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2024 Agorapulse.
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

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public enum EmptyDifferenceCollector implements DifferenceCollector {

    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(DifferenceCollector.class);

    @Override
    public List<DocumentDifference> computeDifferences(String path, InputStream actualStream, InputStream expectedStream, Set<String> ignored) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Skipping {}", path);
        }
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "No difference";
    }
}

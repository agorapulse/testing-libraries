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
package com.agorapulse.testing.spock;

import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension;
import org.spockframework.runtime.model.FeatureInfo;
import org.spockframework.runtime.model.SpecInfo;

public class OverrideParentFeaturesExtension extends AbstractAnnotationDrivenExtension<OverrideParentFeatures> {

    @Override
    public void visitSpecAnnotation(OverrideParentFeatures ignore, SpecInfo spec) {
        if (spec.getSuperSpec() == null) {
            return;
        }
        for (FeatureInfo feature : spec.getFeatures()) {
            ignoreFeatureWithSameName(feature, spec.getSuperSpec());
        }
    }

    @Override
    public void visitFeatureAnnotation(OverrideParentFeatures ignore, FeatureInfo feature) {
        if (feature.getParent().getSuperSpec() == null) {
            return;
        }
        ignoreFeatureWithSameName(feature, feature.getParent().getSuperSpec());
    }

    private void ignoreFeatureWithSameName(FeatureInfo feature, SpecInfo superSpec) {
        for (FeatureInfo parentFeature : superSpec.getAllFeatures()) {
            if (parentFeature.getName().equals(feature.getName())) {
                parentFeature.setExcluded(true);
            }
        }
    }

}

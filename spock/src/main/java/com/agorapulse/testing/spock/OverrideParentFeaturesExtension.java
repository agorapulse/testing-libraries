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

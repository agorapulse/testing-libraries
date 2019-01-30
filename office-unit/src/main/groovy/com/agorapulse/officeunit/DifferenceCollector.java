package com.agorapulse.officeunit;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

public interface DifferenceCollector {

    List<DocumentDifference> computeDifferences(String path, InputStream actualStream, InputStream expectedStream, Set<String> ignored);

}
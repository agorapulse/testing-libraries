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

import org.spockframework.mock.MockUtil;
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension;
import org.spockframework.runtime.extension.IMethodInterceptor;
import org.spockframework.runtime.extension.IMethodInvocation;
import org.spockframework.runtime.model.FieldInfo;
import spock.lang.Specification;

public class DetachedExtension extends AbstractAnnotationDrivenExtension<Detached> {

    private static final MockUtil MOCK_UTIL = new MockUtil();

    @Override
    public void visitFieldAnnotation(Detached annotation, FieldInfo field) {
        field.getParent().addSetupInterceptor(new IMethodInterceptor() {
            @Override
            public void intercept(IMethodInvocation invocation) throws Throwable {
                MOCK_UTIL.attachMock(field.readValue(invocation.getInstance()), (Specification) invocation.getInstance());
                invocation.proceed();
            }
        });
        field.getParent().addCleanupInterceptor(new IMethodInterceptor() {
            @Override
            public void intercept(IMethodInvocation invocation) throws Throwable {
                MOCK_UTIL.detachMock(field.readValue(invocation.getInstance()));
                invocation.proceed();
            }
        });
    }
}

/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.search.runtime;

import org.elasticsearch.script.Script;
import org.elasticsearch.test.ESTestCase;

import static org.hamcrest.Matchers.equalTo;

public class DoubleScriptFieldRangeQueryTests extends AbstractDoubleScriptFieldQueryTestCase<DoubleScriptFieldRangeQuery> {
    @Override
    protected DoubleScriptFieldRangeQuery createTestInstance() {
        double lower = randomDouble();
        double upper = randomValueOtherThan(lower, ESTestCase::randomDouble);
        if (lower > upper) {
            double tmp = lower;
            lower = upper;
            upper = tmp;
        }
        return new DoubleScriptFieldRangeQuery(randomScript(), leafFactory, randomAlphaOfLength(5), lower, upper);
    }

    @Override
    protected DoubleScriptFieldRangeQuery copy(DoubleScriptFieldRangeQuery orig) {
        return new DoubleScriptFieldRangeQuery(orig.script(), leafFactory, orig.fieldName(), orig.lowerValue(), orig.upperValue());
    }

    @Override
    protected DoubleScriptFieldRangeQuery mutate(DoubleScriptFieldRangeQuery orig) {
        Script script = orig.script();
        String fieldName = orig.fieldName();
        double lower = orig.lowerValue();
        double upper = orig.upperValue();
        switch (randomInt(3)) {
            case 0:
                script = randomValueOtherThan(script, this::randomScript);
                break;
            case 1:
                fieldName += "modified";
                break;
            case 2:
                lower -= 1;
                break;
            case 3:
                upper += 1;
                break;
            default:
                fail();
        }
        return new DoubleScriptFieldRangeQuery(script, leafFactory, fieldName, lower, upper);
    }

    @Override
    public void testMatches() {
        DoubleScriptFieldRangeQuery query = new DoubleScriptFieldRangeQuery(randomScript(), leafFactory, "test", 1.2, 3.14);
        assertTrue(query.matches(new double[] { 1.2 }, 1));
        assertTrue(query.matches(new double[] { 3.14 }, 1));
        assertTrue(query.matches(new double[] { 2 }, 1));
        assertFalse(query.matches(new double[] { 0 }, 0));
        assertFalse(query.matches(new double[] { 5 }, 1));
        assertTrue(query.matches(new double[] { 2, 5 }, 2));
        assertTrue(query.matches(new double[] { 5, 2 }, 2));
        assertFalse(query.matches(new double[] { 5, 2 }, 1));
    }

    @Override
    protected void assertToString(DoubleScriptFieldRangeQuery query) {
        assertThat(query.toString(query.fieldName()), equalTo("[" + query.lowerValue() + " TO " + query.upperValue() + "]"));
    }
}

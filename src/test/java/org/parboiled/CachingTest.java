/*
 * Copyright (C) 2009-2011 Mathias Doenitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.parboiled;

import org.parboiled.annotations.Label;
import org.parboiled.matchers.CharMatcher;
import org.parboiled.matchers.CharRangeMatcher;
import org.parboiled.matchers.FirstOfMatcher;
import org.parboiled.matchers.Matcher;
import org.parboiled.matchers.SequenceMatcher;
import org.parboiled.util.StatsAssert;
import org.testng.annotations.Test;

import static org.parboiled.trees.GraphUtils.countAllDistinct;
import static org.testng.Assert.assertEquals;

public class CachingTest {

    public static class CachingParser extends BaseParser<Object> {

        public Rule Rule1() {
            return Sequence(
                    FirstOf('+', '-'),
                    Digit(),
                    FirstOf('+', '-'),
                    Digit()
            );
        }

        public Rule Rule2() {
            return Sequence(
                    FirstOf_uncached('+', '-'),
                    Digit(),
                    FirstOf_uncached('+', '-'),
                    Digit()
            );
        }

        public Rule Digit() {
            return CharRange('0', '9');
        }

        @Label("FirstOf")
        public Rule FirstOf_uncached(Object... rules) {
            return new FirstOfMatcher(toRules(rules));
        }

    }

    @Test
    public void testLabellingParser() {
        CachingParser parser = Parboiled.createParser(CachingParser.class);

        Matcher matcher1 = (Matcher) parser.Rule1();
        Matcher matcher2 = (Matcher) parser.Rule2();

        assertEquals(countAllDistinct(matcher1), 5);
        assertEquals(countAllDistinct(matcher2), 6);

        StatsAssert.assertStatsForRule(parser.Rule1())
            .hasRecordedTotalOf(5)
            .hasRecorded(CharMatcher.class).withCount(2)
            .hasRecorded(CharRangeMatcher.class).withCount(1)
            .hasRecorded(FirstOfMatcher.class).withCount(1)
            .hasRecorded(SequenceMatcher.class).withCount(1)
            .hasNotRecordedAnyActions().hasNotCountedAnyActionClasses()
            .hasRecordedNoOtherMatchers();

        StatsAssert.assertStatsForRule(parser.Rule2())
            .hasRecordedTotalOf(6)
            .hasRecorded(CharMatcher.class).withCount(2)
            .hasRecorded(CharRangeMatcher.class).withCount(1)
            .hasRecorded(FirstOfMatcher.class).withCount(2)
            .hasRecorded(SequenceMatcher.class).withCount(1)
            .hasNotCountedAnyActionClasses().hasNotRecordedAnyActions()
            .hasRecordedNoOtherMatchers();
    }

}
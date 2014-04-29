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

package org.parboiled.matchers;

import com.google.common.base.Preconditions;
import org.parboiled.MatcherContext;
import org.parboiled.matchervisitors.MatcherVisitor;
import org.parboiled.support.Characters;

/**
 * A {@link Matcher} matching a single character out of a given {@link org.parboiled.support.Characters} set.
 */
public class AnyOfMatcher extends AbstractMatcher {
    public final Characters characters;

    public AnyOfMatcher(Characters characters) {
        super(Preconditions.checkNotNull(characters, "characters").toString());
        Preconditions.checkArgument(!characters.equals(Characters.NONE));
        this.characters = characters;
    }

    @Override
    public <V> boolean match(MatcherContext<V> context) {
        if (!characters.contains(context.getCurrentChar())) return false;
        context.advanceIndex(1);
        context.createNode();
        return true;
    }

    @Override
    public <R> R accept(MatcherVisitor<R> visitor) {
        Preconditions.checkNotNull(visitor, "visitor");
        return visitor.visit(this);
    }

}
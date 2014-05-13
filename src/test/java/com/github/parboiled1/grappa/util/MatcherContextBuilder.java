/*
 * Copyright (C) 2014 Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.parboiled1.grappa.util;

import com.github.parboiled1.grappa.stack.DefaultValueStack;
import com.google.common.base.Preconditions;
import org.parboiled.MatcherContext;
import org.parboiled.buffers.CharSequenceInputBuffer;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.errors.ParseError;
import org.parboiled.matchers.Matcher;
import org.parboiled.support.ValueStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class MatcherContextBuilder
{
    private static final ValueStack<Object> STACK
        = new DefaultValueStack<Object>();
    private static final List<ParseError> ERRORS
        = new ArrayList<ParseError>();

    private InputBuffer buffer = null;
    private boolean fastStringMatching = true;
    private Matcher matcher = null;

    public MatcherContextBuilder withInput(@Nonnull final String input)
    {
        buffer = new CharSequenceInputBuffer(input);
        return this;
    }

    public MatcherContextBuilder withFastStringMatching(final boolean fsm)
    {
        fastStringMatching = fsm;
        return this;
    }

    public MatcherContextBuilder withMatcher(@Nonnull final Matcher matcher)
    {
        this.matcher = Preconditions.checkNotNull(matcher);
        return this;
    }

    public MatcherContext<Object> build()
    {
        return new MatcherContext<Object>(buffer, STACK, ERRORS,
            SimpleMatchHandler.INSTANCE, matcher, fastStringMatching);
    }
}
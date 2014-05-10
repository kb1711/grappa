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

package org.parboiled.errors;

import com.github.parboiled1.grappa.cleanup.WillBePrivate;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.support.Chars;
import org.parboiled.support.Position;

/**
 * Exception thrown by the IndentDedentInputbuffer upon detection of an illegal indentation.
 */
@Deprecated
public class IllegalIndentationException
    extends RuntimeException
{
    @WillBePrivate(version = "1.1")
    public final InputBuffer buffer;
    @WillBePrivate(version = "1.1")
    public final Position position;

    public IllegalIndentationException(final InputBuffer buffer,
        final Position position)
    {
        this.buffer = buffer;
        this.position = position;
    }

    @Override
    public String getMessage()
    {
        return "Illegal indentation in line " + position.line + ":\n" +
            buffer.extractLine(position.line) + '\n' +
            Chars.repeat('^', position.column - 1) + '\n';
    }
}
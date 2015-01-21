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

package org.parboiled.buffers;

import com.google.common.io.Closer;
import org.parboiled.errors.IllegalIndentationException;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.reporters.Files;

import java.io.IOException;
import java.io.InputStream;

import static org.parboiled.buffers.InputBufferUtils.collectContent;
import static org.testng.Assert.assertEquals;

public class IndentDedentInputBufferTest {
    
    @Test
    public void testIndentDedentInputBuffer0() {
        final InputBuffer buf = new IndentDedentInputBuffer(("" +
                "###\n" +
                "\n" +
                "L1#X\n" +
                "  \n" +
                "  L2\n" +
                "L12").toCharArray(), 2, "#", false);
        
        final String bufContent = collectContent(buf);
        assertEquals(bufContent, "" +
                "L1\n" +
                "»L2\n" +
                "«L12");
        
        assertEquals(buf.extract(0, 2), "L1");
        assertEquals(buf.extract(4, 7), "L2\n");
        assertEquals(buf.extract(7, 11), "L12");
        assertEquals(buf.getPosition(5).toString(), "Position{line=5, column=4}");
        assertEquals(buf.extractLine(5), "  L2");        
    }

    @Test
    public void testIndentDedentInputBufferEmptyLines() {
        final InputBuffer buf = new IndentDedentInputBuffer(("" +
                "###\n" +
                "\n" +
                "A#X\n" +
                "  \n" +
                "  B\n" +
                "\n" +
                "  C\n" +
                "DEF").toCharArray(), 2, "#", true, false);

        final String bufContent = collectContent(buf);
        assertEquals(bufContent, "" +
                "\n" +
                "\n" +
                "A\n" +
                "\n" +
                "»B\n" +
                "\n" +
                "C\n" +
                "«DEF");

        assertEquals(buf.extract(2, 4), "A#X\n  ");
        assertEquals(buf.extract(6, 10), "B\n\n  C");
        assertEquals(buf.extract(11, 15), "DEF");
        assertEquals(buf.getPosition(5).toString(), "Position{line=5, column=3}");
        assertEquals(buf.extractLine(5), "  B");
    }

    @Test
    public void testIndentDedentInputBuffer1() {
        final InputBuffer buf = new IndentDedentInputBuffer(("" +
                "level 1\n" +
                "  \tlevel 2\n" +
                "    still level 2\n" +
                "\t    level 3\n" +
                "      also 3\n" +
                "    2 again\n" +
                "        another 3\n" +
                "and back to 1\n" +
                "  another level 2 again").toCharArray(), 2, null, false);
        
        final String bufContent = collectContent(buf);
        assertEquals(bufContent, "" +
                "level 1\n" +
                "»level 2\n" +
                "still level 2\n" +
                "»level 3\n" +
                "also 3\n" +
                "«2 again\n" +
                "»another 3\n" +
                "««and back to 1\n" +
                "»another level 2 again\n" +
                "«");
        
        String text = "another 3";
        int start = bufContent.indexOf(text);
        assertEquals(buf.extract(start, start + text.length()), text);  

        text = "back to 1";
        start = bufContent.indexOf(text);
        assertEquals(buf.extract(start, start + text.length()), text);
    }
    
    @Test
    public void testIndentDedentInputBuffer2()
        throws IOException
    {
        final String input = fromResource("IndentDedentBuffer2.test");
        final InputBuffer buf = new IndentDedentInputBuffer(input.toCharArray(), 4, "#", false);
        
        final String bufContent = collectContent(buf);
        assertEquals(bufContent, fromResource("IndentDedentBuffer2.converted.test"));
        
        final String text = "go deep";
        final int start = bufContent.indexOf(text);
        assertEquals(buf.extract(start, start + text.length()), text);        
    }

    @Test
    public void testIndentDedentInputBuffer3()
        throws IOException
    {
        final String input = fromResource("IndentDedentBuffer3.test");
        final InputBuffer buf = new IndentDedentInputBuffer(input.toCharArray(), 4, "//", false);
        final String bufContent = collectContent(buf);
        assertEquals(bufContent, fromResource(
            "IndentDedentBuffer3.converted.test"));
        assertEquals(buf.extract(0, bufContent.length()), input);
    }
    
    @Test
    public void testIndentDedentInputBuffer4() {
        final InputBuffer buf = new IndentDedentInputBuffer(("" +
                "level 1\n" +
                "  level 2 # comment").toCharArray(), 2, "#", false);
        
        final String bufContent = collectContent(buf);
        assertEquals(bufContent, "" +
                "level 1\n" +
                "»level 2 \n" +
                "«");
    }

    @Test
    public void testIndentDedentInputBufferIllegalIndent1() {
        final InputBuffer buf = new IndentDedentInputBuffer(("" +
                "level 1\n" +
                "  \tlevel 2\n" +
                "    still level 2\n" +
                "\t    level 3\n" +
                "     also 3\n" + // illegal dedentation
                "    2 again\n" +
                "        another 3\n" +
                "and back to 1\n" +
                "  another level 2 again").toCharArray(), 2, null, false);

        final String bufContent = collectContent(buf);
        assertEquals(bufContent, "" +
                "level 1\n" +
                "»level 2\n" +
                "still level 2\n" +
                "»level 3\n" +
                "also 3\n" +
                "«2 again\n" +
                "»another 3\n" +
                "««and back to 1\n" +
                "»another level 2 again\n" +
                "«");
    }
    
    @Test
    public void testIndentDedentInputBufferIllegalIndent2() {
        try {
            new IndentDedentInputBuffer(("" +
                    "level 1\n" +
                    "  \tlevel 2\n" +
                    "    still level 2\n" +
                    "\t    level 3\n" +
                    "     illegal!!\n" +
                    "    2 again\n" +
                    "        another 3\n" +
                    "and back to 1\n" +
                    "  another level 2 again").toCharArray(), 2, null, true);
        } catch(IllegalIndentationException e) {
            assertEquals(e.getMessage(), "Illegal indentation in line 5:\n" +
            "     illegal!!\n" +
            "^^^^^\n");
            return;
        }
        Assert.fail("Incorrect or no IllegalIndentationException thrown");
    }
    
    @Test
    public void testEmptyIndentDedentInputBuffer() {
        final InputBuffer buf = new IndentDedentInputBuffer(new char[0], 2, "#", false);
        assertEquals(buf.extract(0, 1), "");
    }

    private static String fromResource(final String resourcePath)
        throws IOException
    {
        final Closer closer = Closer.create();

        final InputStream in;

        try {
            in = closer.register(IndentDedentInputBufferTest.class
                .getClassLoader()
                .getResourceAsStream(resourcePath));
            if (in == null)
                throw new IOException(resourcePath + " not found");
            return Files.streamToString(in);
        } finally {
            closer.close();
        }
    }
}
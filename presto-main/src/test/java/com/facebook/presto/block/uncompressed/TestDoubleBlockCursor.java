/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.block.uncompressed;

import com.facebook.presto.block.AbstractTestBlockCursor;
import com.facebook.presto.spi.block.Block;
import org.testng.annotations.Test;

import static com.facebook.presto.block.BlockAssertions.createDoublesBlock;
import static com.facebook.presto.spi.type.DoubleType.DOUBLE;
import static org.testng.Assert.assertEquals;

public class TestDoubleBlockCursor
        extends AbstractTestBlockCursor
{
    @Override
    protected Block createExpectedValues()
    {
        return createDoublesBlock(11.11, 11.11, 11.11, 22.22, 22.22, 22.22, 22.22, 22.22, 33.33, 33.33, 44.44);
    }

    @Test
    public void testCursorType()
    {
        assertEquals(createExpectedValues().cursor().getType(), DOUBLE);
    }
}

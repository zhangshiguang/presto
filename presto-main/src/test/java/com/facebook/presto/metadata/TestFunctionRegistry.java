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
package com.facebook.presto.metadata;

import com.facebook.presto.metadata.OperatorInfo.OperatorType;
import com.facebook.presto.operator.scalar.CustomAdd;
import com.facebook.presto.operator.scalar.ScalarFunction;
import com.facebook.presto.sql.tree.QualifiedName;
import com.facebook.presto.type.TypeRegistry;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.List;

import static com.facebook.presto.metadata.FunctionRegistry.getMagicLiteralFunctionSignature;
import static com.facebook.presto.spi.type.BigintType.BIGINT;
import static com.facebook.presto.spi.type.HyperLogLogType.HYPER_LOG_LOG;
import static com.facebook.presto.spi.type.TimestampWithTimeZoneType.TIMESTAMP_WITH_TIME_ZONE;
import static org.testng.Assert.assertEquals;

public class TestFunctionRegistry
{
    @Test
    public void testIdentityCast()
    {
        FunctionRegistry registry = new FunctionRegistry(new TypeRegistry(), true);
        OperatorInfo exactOperator = registry.getExactOperator(OperatorType.CAST, ImmutableList.of(HYPER_LOG_LOG), HYPER_LOG_LOG);
        assertEquals(exactOperator.getOperatorType(), OperatorType.CAST);
        assertEquals(exactOperator.getArgumentTypes(), ImmutableList.of(HYPER_LOG_LOG));
        assertEquals(exactOperator.getReturnType(), HYPER_LOG_LOG);
    }

    @Test
    public void testMagicLiteralFunction()
    {
        Signature signature = getMagicLiteralFunctionSignature(TIMESTAMP_WITH_TIME_ZONE);
        assertEquals(signature.getName(), "$literal$timestamp with time zone");
        assertEquals(signature.getArgumentTypes(), ImmutableList.of(BIGINT));
        assertEquals(signature.getReturnType(), TIMESTAMP_WITH_TIME_ZONE);
        assertEquals(signature.isApproximate(), false);

        FunctionRegistry registry = new FunctionRegistry(new TypeRegistry(), true);
        FunctionInfo function = registry.resolveFunction(new QualifiedName(signature.getName()), signature.getArgumentTypes(), signature.isApproximate());
        assertEquals(function.getArgumentTypes(), ImmutableList.of(BIGINT));
        assertEquals(function.getReturnType(), TIMESTAMP_WITH_TIME_ZONE);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "\\QFunction already registered: custom_add(bigint,bigint):bigint\\E")
    public void testDuplicateFunctions()
    {
        List<FunctionInfo> functions = new FunctionRegistry.FunctionListBuilder()
                .scalar(CustomAdd.class)
                .getFunctions();

        FunctionRegistry registry = new FunctionRegistry(new TypeRegistry(), true);
        registry.addFunctions(functions, ImmutableList.<OperatorInfo>of());
        registry.addFunctions(functions, ImmutableList.<OperatorInfo>of());
    }

    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "'sum' is both an aggregation and a scalar function")
    public void testConflictingScalarAggregation()
            throws Exception
    {
        List<FunctionInfo> functions = new FunctionRegistry.FunctionListBuilder()
                .scalar(ScalarSum.class)
                .getFunctions();

        FunctionRegistry registry = new FunctionRegistry(new TypeRegistry(), true);
        registry.addFunctions(functions, ImmutableList.<OperatorInfo>of());
    }

    public static final class ScalarSum
    {
        private ScalarSum() {}

        @ScalarFunction
        public static long sum(long a, long b)
        {
            return a + b;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.dfire.platform.alchemy.api.function;

/**
 * Base class for User-Defined Aggregates.
 *
 * <p>
 * The behavior of an [[StreamAggregateFunction]] can be defined by implementing a series of custom methods. An
 * [[StreamAggregateFunction]] needs at least three methods: - createAccumulator, - accumulate, and - getValue.
 * </p>
 *
 * <p>
 * All these methods muse be declared publicly, not static and named exactly as the names mentioned above. The methods
 * createAccumulator and getValue are defined in the [[StreamAggregateFunction]] functions.
 * </p>
 *
 * <p>
 * Example:
 * </p>
 *
 * <pre>
 * public class SimpleAverageAccum extends Tuple2&lt;Long, Integer&gt; {
 *     public long sum = 0;
 *     public int count = 0;
 * }
 *
 * public class SimpleAverage extends StreamAggregateFunction&lt;Long, SimpleAverageAccum&gt; {
 *     public SimpleAverageAccum createAccumulator() {
 *         return new SimpleAverageAccum();
 *     }
 *
 *     public Long getValue(SimpleAverageAccum accumulator) {
 *         if (accumulator.count == 0) {
 *             return null;
 *         } else {
 *             return accumulator.sum / accumulator.count;
 *         }
 *     }
 *
 *     // overloaded accumulate method
 *     public void accumulate(SimpleAverageAccum accumulator, long iValue) {
 *         accumulator.sum += iValue;
 *         accumulator.count += 1;
 *     }
 *
 *     // Overloaded accumulate method
 *     public void accumulate(SimpleAverageAccum accumulator, int iValue) {
 *         accumulator.sum += iValue;
 *         accumulator.count += 1;
 *     }
 * }
 *
 * </pre>
 *
 * @tparam T the type of the aggregation result
 * @tparam ACC base class for aggregate Accumulator. The accumulator is used to keep the aggregated values which are
 *         needed to compute an aggregation result. AggregateFunction represents its state using accumulator, thereby
 *         the state of the AggregateFunction must be put into the accumulator.
 */
public interface StreamAggregateFunction<IN, ACC, OUT> extends Function {

    ACC createAccumulator();

    void accumulate(ACC accumulator, IN value);

    OUT getValue(ACC accumulator);

}

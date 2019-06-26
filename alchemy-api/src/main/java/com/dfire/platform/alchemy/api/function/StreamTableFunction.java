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


import com.dfire.platform.alchemy.api.function.table.Collect;

/**
 * Base class for a user-defined table function (UDTF) in Stream. A user-defined table functions works on zero, one, or
 * multiple scalar values as input and returns multiple rows as output.
 *
 * <p>
 * The behavior of a [[StreamTableFunction]] can be defined by implementing a custom evaluation method. An evaluation
 * method must be declared publicly, not static and named "eval". Evaluation methods can also be overloaded by
 * implementing multiple methods named "eval".
 * </p>
 *
 * <p>
 * User-defined functions must have a default constructor and must be instantiable during runtime.
 * </p>
 *
 * <p>
 * Example:
 * </p>
 *
 * <pre>
 * public class Split extends TableFunction&lt;String&gt; {
 *
 *     // implement an "eval" method with as many parameters as you want
 *     public void eval(String str) {
 *         for (String s : str.split(" ")) {
 *             collect(s); // use collect(...) to emit an output row
 *         }
 *     }
 *
 *     // you can overload the eval method here ...
 * }
 * </pre>
 *
 * @tparam T The type of the output row
 */
public abstract class StreamTableFunction<T> implements Collect<T>, Function {

    private Collect<T> collect;

    public abstract void invoke(Object... args);

    @Override
    public void collect(T row) {
        this.collect.collect(row);
    }

    public Collect<T> getCollect() {
        return collect;
    }

    public void setCollect(Collect<T> collect) {
        this.collect = collect;
    }
}

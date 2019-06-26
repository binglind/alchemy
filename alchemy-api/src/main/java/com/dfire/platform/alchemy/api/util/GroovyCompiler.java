/*
 * Copyright 2013 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.dfire.platform.alchemy.api.util;

import java.io.File;
import java.io.IOException;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

/**
 * @author congbai
 */
public class GroovyCompiler {

    /**
     * Compiles Groovy code and returns the Class of the compiles code.
     *
     * @param sCode
     * @param sName
     * @return
     */
    public static Class compile(String sCode, String sName) {
        GroovyClassLoader loader = getGroovyClassLoader();
        Class groovyClass = loader.parseClass(sCode, sName);
        return groovyClass;
    }

    public static  <T> T create(String sCode, String sName){
        Class clazz = compile(sCode, sName);
        try {
            return (T)clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return a new GroovyClassLoader
     */
    static GroovyClassLoader getGroovyClassLoader() {
        return new GroovyClassLoader();
    }

    /**
     * Compiles groovy class from a file
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static Class compile(File file) throws IOException {
        GroovyClassLoader loader = getGroovyClassLoader();
        Class groovyClass = loader.parseClass(file);
        return groovyClass;
    }

}

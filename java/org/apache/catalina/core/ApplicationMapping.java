/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.catalina.core;

import javax.servlet.http.Mapping;
import javax.servlet.http.MappingMatch;

import org.apache.catalina.mapper.MappingData;

public class ApplicationMapping {

    private final MappingData mappingData;

    private volatile Mapping mapping = null;

    public ApplicationMapping(MappingData mappingData) {
        this.mappingData = mappingData;
    }

    public Mapping getMapping() {
        if (mapping == null) {
            switch (mappingData.matchType) {
                case CONTEXT_ROOT:
                    mapping = new MappingImpl("", "", mappingData.matchType);
                    break;
                case DEFAULT:
                    mapping = new MappingImpl("/", "/", mappingData.matchType);
                    break;
                case EXACT:
                    mapping = new MappingImpl(mappingData.wrapperPath.toString(),
                            mappingData.wrapperPath.toString(), mappingData.matchType);
                    break;
                case EXTENSION:
                    String path = mappingData.wrapperPath.toString();
                    int extIndex = path.lastIndexOf('.');
                    mapping = new MappingImpl(path.substring(0, extIndex),
                            "*" + path.substring(extIndex), mappingData.matchType);
                    break;
                case PATH:
                    mapping = new MappingImpl(mappingData.pathInfo.toString(),
                            mappingData.wrapperPath.toString() + "/*",
                            mappingData.matchType);
                    break;
                case IMPLICIT:
                    // Treat IMPLICIT as UNKNOWN since Tomcat doesn't use
                    // implicit mappings
                case UNKNOWN:
                    mapping = new MappingImpl("", "", mappingData.matchType);
                    break;
            }
        }

        return mapping;
    }

    public void recycle() {
        mapping = null;
    }

    private static class MappingImpl implements Mapping {

        private final String matchValue;
        private final String pattern;
        private final MappingMatch mappingType;

        public MappingImpl(String matchValue, String pattern, MappingMatch mappingType) {
            this.matchValue = matchValue;
            this.pattern = pattern;
            this.mappingType = mappingType;
        }

        @Override
        public String getMatchValue() {
            return matchValue;
        }

        @Override
        public String getPattern() {
            return pattern;
        }

        @Override
        public MappingMatch getMatchType() {
            return mappingType;
        }
    }
}

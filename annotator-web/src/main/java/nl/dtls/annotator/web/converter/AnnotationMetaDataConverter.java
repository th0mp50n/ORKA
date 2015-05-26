/*
 * Copyright (C) 2015 DTL (${email})
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.dtls.annotator.web.converter;

import java.io.IOException;

import nl.dtls.annotator.model.AnnotationMetaData;

import org.springframework.core.convert.converter.Converter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AnnotationMetaDataConverter implements Converter<String, AnnotationMetaData> {
    private ObjectMapper objectMapper;
    
    public AnnotationMetaDataConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Override
    public AnnotationMetaData convert(String source) {
        try {
            return objectMapper.readValue(source, AnnotationMetaData.class);
        } catch(IOException e) {
            throw new RuntimeException("Failed to parse " + source, e);
        }
    }
}

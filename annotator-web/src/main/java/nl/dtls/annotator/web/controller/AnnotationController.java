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
package nl.dtls.annotator.web.controller;

import java.net.URI;

import nl.dtls.annotator.model.AnnotationMetaData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/annotation")
public class AnnotationController {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationController.class);
    
    @RequestMapping(value = "/triple", method = RequestMethod.GET)
    public AnnotationMetaData annotateTriple(@RequestParam AnnotationMetaData metaData) {
        logger.info("annotating {}", metaData);
        return metaData;
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public URI submitNanopub() {
        return URI.create("http://nanopubstore.org/nanopub/123");
    }
}

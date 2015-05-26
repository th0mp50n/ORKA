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
import java.util.List;

import nl.dtls.annotator.model.LabeledResource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableList;

@RestController
@RequestMapping("/predicate")
public class PredicateController {
    @RequestMapping(method = RequestMethod.GET)
    public List<LabeledResource> listPredicates() {
        LabeledResource predicate1 = new LabeledResource();
        predicate1.setLabel("treats");
        predicate1.setUri(URI.create("http://www.example.com/treats"));
        LabeledResource predicate2 = new LabeledResource();
        predicate2.setLabel("interacts with");
        predicate2.setUri(URI.create("http://www.example.com/interactswith"));
        LabeledResource predicate3 = new LabeledResource();
        predicate3.setLabel("does not affect");
        predicate3.setUri(URI.create("http://www.example.com/doesnotaffect"));
        LabeledResource predicate4 = new LabeledResource();
        predicate4.setLabel("coexists with");
        predicate4.setUri(URI.create("http://www.example.com/coexistswith"));
        
        return ImmutableList.of(predicate1, predicate2, predicate3, predicate4);
    }
}

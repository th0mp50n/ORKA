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
package nl.dtls.annotator.service.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import nl.dtls.annotator.model.LabeledResource;
import nl.dtls.annotator.service.PredicateService;

import org.apache.http.HttpEntity;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;

@Component
public class BrainPredicateService implements PredicateService, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(BrainPredicateService.class);
    
    private static final String TOKEN_HEADER = "x-token";
    
    @Value("${brain.authUrl}")
    private String authUrl;
    @Value("${brain.logoutUrl}")
    private String logoutUrl;
    @Value("${brain.predicateUrl}")
    private String predicateUrl;
    @Value("${brain.conceptPrefix}")
    private String conceptPrefix;
    @Value("${brain.username}")
    private String username;
    @Value("${brain.password}")
    private String password;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private List<LabeledResource> cachedPredicates;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        cachedPredicates = new ArrayList<>();

        Map<String, String> body = ImmutableMap.of("username", username, "password", password);
        String json = objectMapper.writeValueAsString(body);
        
        String token = Request.Post(authUrl)
                .bodyString(json, ContentType.APPLICATION_JSON)
                .execute()
                .handleResponse(response -> {
                    HttpEntity entity = response.getEntity();
                    return objectMapper.readTree(entity.getContent()).get("token").asText();
                });
        
        Content content = Request.Get(predicateUrl)
                .addHeader(TOKEN_HEADER, token)
                .addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                .execute()
                .returnContent();
        
        PredicatesResponse predResponse = objectMapper.readValue(content.asStream(), PredicatesResponse.class);
        predResponse.conceptMeasures.forEach(p -> {
            LabeledResource resource = new LabeledResource(URI.create(conceptPrefix + p.id), p.name);
            cachedPredicates.add(resource);
        });
        
        Request.Get(logoutUrl)
                .addHeader(TOKEN_HEADER, token)
                .execute()
                .discardContent();
    }
    
    @Override
    public List<LabeledResource> getPredicates() {
        return cachedPredicates;
    }
    
    private static class PredicatesResponse {
        public List<Predicate> conceptMeasures;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Predicate {
        public UUID id;
        public String name;
    }
}

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
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

@Component
public class BrainPredicateService implements PredicateService, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(BrainPredicateService.class);
    
    @Value("${brain.predicateUrl}")
    private String predicateUrl;
    @Value("${brain.authUrl}")
    private String authUrl;
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
        
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost loginAction = new HttpPost(authUrl);
            
            Map<String, String> body = ImmutableMap.of("username", username, "password", password);
            String json = objectMapper.writeValueAsString(body);
            StringEntity bodyEntity = new StringEntity(json);
            bodyEntity.setContentType(MediaType.JSON_UTF_8.toString());
            loginAction.setEntity(bodyEntity);
            
            String token;
            try (CloseableHttpResponse response = client.execute(loginAction)) {
                HttpEntity entity = response.getEntity();
                
                MapType type = objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class);
                
                Map<String, String> authenticationResponse = objectMapper.readValue(entity.getContent(), type);
                token = authenticationResponse.get("token");
            }
            
            HttpGet getPredicatesAction = new HttpGet(predicateUrl);
            getPredicatesAction.addHeader("x-token", token);
            getPredicatesAction.addHeader(HttpHeaders.ACCEPT, MediaType.JSON_UTF_8.toString());
            
            PredicatesResponse predicatesResponse;
            try (CloseableHttpResponse response = client.execute(getPredicatesAction)) {
                HttpEntity entity = response.getEntity();
                predicatesResponse = objectMapper.readValue(entity.getContent(), PredicatesResponse.class);
            }
            
            for (Predicate p : predicatesResponse.conceptMeasures) {
                LabeledResource labeledResource = new LabeledResource();
                labeledResource.setLabel(p.name);
                labeledResource.setUri(URI.create(conceptPrefix + p.id));
                cachedPredicates.add(labeledResource);
            }
        }
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

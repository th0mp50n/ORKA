package nl.dtls.annotator.redux.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;

import nl.dtls.annotator.redux.service.PredicateService;

@Component
public class BrainPredicateService implements PredicateService, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(BrainPredicateService.class);
    
    @Value("${predicates.brain.username}") private String brainUsername;
    @Value("${predicates.brain.password}") private String brainPassword;
    @Value("${predicates.brain.endpoint}") private String brainEndpoint;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private List<String> cachedPredicates;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        cachedPredicates = new ArrayList<>();

        Map<String, String> body = ImmutableMap.of("username", brainUsername,
                "password", brainPassword);
        String json = objectMapper.writeValueAsString(body);
        
        logger.info("logging into Brain...");
        String token = Request.Post(brainEndpoint + "/login/authenticate")
                .bodyString(json, ContentType.APPLICATION_JSON)
                .execute()
                .handleResponse(response -> {
                    HttpEntity entity = response.getEntity();
                    return objectMapper.readTree(entity.getContent()).get("token").asText();
                });
        logger.info("logged in, retrieving predicates...");
        
        Content content = Request.Get(brainEndpoint + "/concept/predicates")
                .addHeader("x-token", token)
                .addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                .execute()
                .returnContent();
        
        PredicatesResponse predResponse = objectMapper.readValue(content.asStream(), PredicatesResponse.class);
        predResponse.conceptMeasures.forEach(p -> cachedPredicates.add(p.name));
        logger.info("predicates retrieved, logging out...");
        
        Request.Get(brainEndpoint + "/logout")
                .addHeader("x-token", token)
                .execute()
                .discardContent();
        logger.info("logged out of Brain");
    }
    
    @Override
    public List<String> getPredicates() {
        return cachedPredicates;
    }
    
    private static class PredicatesResponse {
        public List<Predicate> conceptMeasures;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Predicate {
        public String id; // no longer UUID formatted identifiers as of 07/Sep/2015
        public String name;
    }
}

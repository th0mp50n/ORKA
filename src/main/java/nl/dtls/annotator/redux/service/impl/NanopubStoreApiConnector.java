package nl.dtls.annotator.redux.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.io.Files;

import nl.dtls.annotator.redux.model.AnnotationMetadata;
import nl.dtls.annotator.redux.model.AnnotationSubmission;
import nl.dtls.annotator.redux.security.model.UserInformation;
import nl.dtls.annotator.redux.service.NanopubStoreConnector;

@Component
public class NanopubStoreApiConnector implements NanopubStoreConnector {
    private static final Logger logger = LoggerFactory.getLogger(NanopubStoreApiConnector.class);
    
    @Value("${nanopub.template}")
    private File template;
    
    @Value("${nanopub.endpoint}")
    private String endpoint;
    
    private MustacheFactory mustacheFactory = new DefaultMustacheFactory();
    
    @Override
    public String storeNanopub(AnnotationMetadata metadata, AnnotationSubmission submission,
            UserInformation userInfo) {
        // translate
        TemplateContainer tc = new TemplateContainer();
        tc.subject = metadata.getSubject();
        tc.subjectUri = metadata.getSubjectUri();
        tc.predicate = metadata.getPredicate();
        tc.predicateUri = metadata.getPredicateUri();
        tc.object = metadata.getObject();
        tc.objectUri = metadata.getObjectUri();
        
        tc.narrative = submission.getNarrative();
        tc.newPredicate = submission.getPredicate();
        tc.newPredicateUri = "http://example.com/" + submission.getPredicate().replace(' ', '_');
        tc.orcid = userInfo.getUrl();
        
        StringWriter writer = new StringWriter();
        try {
            Mustache mustache = mustacheFactory.compile(Files.newReader(template, StandardCharsets.UTF_8), "np-template");
            mustache.execute(writer, tc);
        } catch (IOException e) {
            logger.error("Failed to fill nanopub template", e);
        }
        
        logger.info("wrote nanopub");
        logger.info("{}", writer.toString());
        
        // actual submission
        try {
            HttpResponse response = Request.Post(endpoint)
                    .bodyString(writer.toString(), ContentType.create("application/x-trig"))
                    .execute()
                    .returnResponse();
            
            StatusLine status = response.getStatusLine();
            logger.info("status: {}, {}", status.getStatusCode(), status.getReasonPhrase());
            
            if (status.getStatusCode() == HttpStatus.SC_CREATED) {
                Header location = response.getFirstHeader(HttpHeaders.LOCATION);
                return location.getValue();
            }
        } catch (IOException e) {
            logger.error("Failed to submit nanopub", e);
            throw new RuntimeException(e.getMessage(), e);
        }
        
        return null;
    }
    
    @SuppressWarnings("unused")
    private static class TemplateContainer {
        public String subject;
        public String subjectUri;
        public String predicate;
        public String predicateUri;
        public String object;
        public String objectUri;
        
        public String orcid;
        public String narrative;
        public String newPredicate;
        public String newPredicateUri;
    }
}

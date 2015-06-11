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
package nl.dtls.annotator.service.nanopub.impl;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import nl.dtls.annotator.service.nanopub.NanopubStoreConnector;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.nanopub.Nanopub;
import org.nanopub.NanopubUtils;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * {@link NanopubStoreConnector} implementation for nanopub-store-api.
 * @see <a href="https://github.com/Nanopublication/nanopub-store-api">nanopub-store-api</a>
 */
public class NanopubStoreApiConnector implements NanopubStoreConnector {
    private static final Logger logger = LoggerFactory.getLogger(NanopubStoreApiConnector.class);
    private static final ContentType XTRIG = ContentType.create("application/x-trig");
    
    @Value("${nanopub.connector.nanopubstoreapi.endpoint}")
    private String endpoint;
    
    @Override
    public URI storeNanopub(Nanopub nanopub) {
        try {
            String nanopubSerialized = NanopubUtils.writeToString(nanopub, RDFFormat.TRIG);
            
            HttpResponse response = Request.Post(endpoint)
                    .bodyString(nanopubSerialized, XTRIG)
                    .execute()
                    .returnResponse();
            
            StatusLine status = response.getStatusLine();
            
            // handle 406 as an error call
            if (status.getStatusCode() == HttpStatus.SC_NOT_ACCEPTABLE) {
                logger.warn("server returned error: {}", status.getReasonPhrase());
                // TODO handle this
            }
            
            // handle 201 as a successful call
            if (status.getStatusCode() == HttpStatus.SC_CREATED) {
                Optional<Header> locationHeader = Optional.ofNullable(response.getFirstHeader(HttpHeaders.LOCATION));
                if (!locationHeader.isPresent()) {
                    logger.warn("header 'location' not present");
                    // TODO handle this
                }
            
                String npUri = locationHeader.get().getValue();
                return URI.create(npUri);
            }
        } catch (RDFHandlerException | IOException e) {
            logger.error("", e);
            throw new RuntimeException("Failed to store nanopub", e);
        }
        
        // TODO error handling
        return null;
    }
}

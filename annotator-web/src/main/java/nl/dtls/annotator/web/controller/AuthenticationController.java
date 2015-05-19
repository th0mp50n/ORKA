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

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/auth", method = RequestMethod.GET)
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    @Value("${orcid.client_id}")
    private String clientId;
    @Value("${orcid.client_secret}")
    private String clientSecret;
    @Value("${orcid.authorize_url}")
    private String authorizeUrl;
    @Value("${orcid.token_url}")
    private String tokenUrl;
    @Value("${orcid.scope}")
    private String scope;
    @Value("${orcid.response_type}")
    private String responseType;
    
    @RequestMapping
    public String selectProvider(@RequestParam Optional<String> error, Model model) {
        if (error.isPresent()) {
            model.addAttribute("error", error.get());
        }
        return "provider-selection";
    }
    
    @RequestMapping("/{provider}")
    public void authenticateByProvider(@PathVariable String provider, HttpServletResponse response)
            throws Exception {
        logger.info("in authentication controller, redirecting for {}", provider);
        
        OAuthClientRequest request = OAuthClientRequest
                .authorizationLocation(authorizeUrl)
                .setClientId(clientId)
                .setRedirectURI("http://127.0.0.1:8080/annotator-web/auth/" + provider + "/success")
                .setScope(scope)
                .setResponseType(responseType)
                .buildQueryMessage();
        
        response.sendRedirect(request.getLocationUri());
    }
}

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
package nl.dtls.annotator.web.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class OAuth2ResponseFilter extends AbstractAuthenticationProcessingFilter {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2ResponseFilter.class);
    
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
    
    protected OAuth2ResponseFilter() {
        super("/auth/**/success");
        
        // workaround for the custom authentication; the AuthenticationManager is
        // a lambda that passes the given Authentication object as given. The
        // responsibility of authentication lies with the OAuth library.
        setAuthenticationManager(a -> a);
    }
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException, IOException,
            ServletException {
        
        OAuthAuthzResponse oauthAuthenticationResponse;
        try {
            oauthAuthenticationResponse = OAuthAuthzResponse.oauthCodeAuthzResponse(request);
        } catch (OAuthProblemException e) {
            logger.info("error: {}", e.getError());
            logger.info("description: {}", e.getDescription());
            logger.info("response status: {}", e.getResponseStatus());
            logger.info("uri: {}", e.getUri());
            logger.info("redirect uri: {}", e.getRedirectUri());
            
            response.sendRedirect(request.getContextPath() + "/auth?error=" + e.getDescription());
            // TODO handle this more gracefully
            return null;
        }
        
        try {
            OAuthClientRequest oauthClientRequest = OAuthClientRequest
                    .tokenLocation(tokenUrl)
                    .setGrantType(GrantType.AUTHORIZATION_CODE)
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setCode(oauthAuthenticationResponse.getCode())
                    .buildQueryMessage();
            
            OAuthClient oauthClient = new OAuthClient(new URLConnectionClient());
            OAuthJSONAccessTokenResponse oauthAccessTokenResponse = oauthClient.accessToken(oauthClientRequest);
            
            PreAuthenticatedAuthenticationToken token =
                    new PreAuthenticatedAuthenticationToken(
                            oauthAccessTokenResponse.getParam("name"),
                            oauthAccessTokenResponse.getParam("orcid"));
            token.setAuthenticated(true);
            
            return token;
        } catch (OAuthProblemException e) {
            // TODO handle more gracefully
            logger.error("oauth problem occured", e);
            throw new AuthenticationServiceException(e.getMessage(), e);
        } catch (OAuthSystemException e) {
            // TODO handle more gracefully
            logger.error("oauth exception occurred", e);
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }
}

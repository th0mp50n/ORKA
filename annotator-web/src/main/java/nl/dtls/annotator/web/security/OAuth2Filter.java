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

import javax.servlet.FilterChain;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class OAuth2Filter extends OncePerRequestFilter {
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
    
    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest,
            HttpServletResponse servletResponse, FilterChain filterChain)
                    throws ServletException, IOException {
        String code = servletRequest.getParameter("code");

        if (code == null) {
            try {
                OAuthClientRequest request = OAuthClientRequest
                        .authorizationLocation(authorizeUrl)
                        .setClientId(clientId)
                        .setRedirectURI("http://127.0.0.1:8080/annotator-web/annotation")
                        .setScope(scope)
                        .setResponseType(responseType)
                        .buildQueryMessage();

                System.out.println("going to redirect to " + request.getLocationUri());

                servletResponse.sendRedirect(request.getLocationUri());
            } catch (OAuthSystemException e) {
                e.printStackTrace();
            }
        } else {
            try {
                OAuthAuthzResponse response = OAuthAuthzResponse.oauthCodeAuthzResponse(servletRequest);
                String oauthCode = response.getCode();

                OAuthClientRequest request = OAuthClientRequest
                        .tokenLocation(tokenUrl)
                        .setGrantType(GrantType.AUTHORIZATION_CODE)
                        .setClientId(clientId)
                        .setClientSecret(clientSecret)
                        .setRedirectURI("http://127.0.0.1:8080/annotator-web/annotation")
                        .setCode(oauthCode)
                        .buildQueryMessage();

                OAuthClient client = new OAuthClient(new URLConnectionClient());
                OAuthJSONAccessTokenResponse orcidResponse = client.accessToken(request);

                String orcid = orcidResponse.getParam("orcid");
                System.out.println("ORCID is: " + orcid);
            } catch (OAuthProblemException | OAuthSystemException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}

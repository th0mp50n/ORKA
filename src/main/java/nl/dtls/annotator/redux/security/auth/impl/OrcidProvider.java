package nl.dtls.annotator.redux.security.auth.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import nl.dtls.annotator.redux.security.auth.AbstractProvider;
import nl.dtls.annotator.redux.security.model.UserInformation;

@Component
public class OrcidProvider extends AbstractProvider {
    private static final Logger logger = LoggerFactory.getLogger(OrcidProvider.class);
    
    @Value("${orcid.auth_url:}") private String orcidAuthUrl;
    @Value("${orcid.token_url:}") private String orcidTokenUrl;
    @Value("${orcid.icon:}") private String orcidIcon;
    @Value("${orcid.client_id:}") private String orcidClientId;
    @Value("${orcid.client_secret:}") private String orcidClientSecret;
    
    @Override
    public String getAuthorizeUrl(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder()
                .append(req.getScheme())
                .append("://")
                .append(req.getServerName())
                // TODO make port append optional if it's 80
                .append(":")
                .append(req.getServerPort())
                .append(req.getContextPath());
        
        String redirect = UriComponentsBuilder.fromHttpUrl(sb.toString())
                .path("/authentication")
                .queryParam("provider", getName())
                .queryParam("return=") // workaround
                .build()
                .toUriString();
        try {
            redirect = URLEncoder.encode(redirect, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // Should not happen
            logger.warn(e.getMessage(), e);
        }
        
        return UriComponentsBuilder.fromHttpUrl(orcidAuthUrl)
                .queryParam("client_id", orcidClientId)
                .queryParam("scope", "/authenticate")
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", redirect + "{{current}}")
                .build()
                .toUriString();
    }
    
    @Override
    public String getName() {
        return "orcid";
    }
    
    @Override
    public String getDisplayName() {
        return "ORCID";
    }
    
    @Override
    public String getIcon() {
        return orcidIcon;
    }
    
    @Override
    public boolean isConfigured() {
        return StringUtils.hasText(orcidAuthUrl) &&
                StringUtils.hasText(orcidTokenUrl) &&
                StringUtils.hasText(orcidClientId) &&
                StringUtils.hasText(orcidClientSecret);
    }
    
    @Override
    protected PreAuthenticatedAuthenticationToken _authenticate(OAuthAuthzResponse response)
            throws OAuthProblemException, OAuthSystemException {
        OAuthClientRequest request = OAuthClientRequest
                .tokenLocation(orcidTokenUrl)
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setClientId(orcidClientId)
                .setClientSecret(orcidClientSecret)
                .setCode(response.getCode())
                .buildQueryMessage();
        
        OAuthClient client = new OAuthClient(new URLConnectionClient());
        OAuthJSONAccessTokenResponse token = client.accessToken(request);
        
        String orcid = token.getParam("orcid");
        
        UserInformation userInfo = new UserInformation(
                token.getParam("name"), orcid,
                "https://orcid.org/" + orcid, getDisplayName());
        
        return new PreAuthenticatedAuthenticationToken(userInfo, null); 
    }
}

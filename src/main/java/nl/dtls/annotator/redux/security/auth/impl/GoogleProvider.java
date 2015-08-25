package nl.dtls.annotator.redux.security.auth.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import nl.dtls.annotator.redux.security.auth.AbstractProvider;

@Component
public class GoogleProvider extends AbstractProvider {
    @Override
    public String getAuthorizeUrl(HttpServletRequest req) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getName() {
        return "google";
    }
    
    @Override
    public String getDisplayName() {
        return "Google";
    }
    
    @Override
    public String getIcon() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public boolean isConfigured() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected PreAuthenticatedAuthenticationToken _authenticate(OAuthAuthzResponse response)
            throws OAuthProblemException, OAuthSystemException {
        // TODO Auto-generated method stub
        return null;
    }
}

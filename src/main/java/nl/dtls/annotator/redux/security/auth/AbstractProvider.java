package nl.dtls.annotator.redux.security.auth;

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public abstract class AbstractProvider implements Provider {
    private static final Logger logger = LoggerFactory.getLogger(AbstractProvider.class);
    
    @Override
    public String authenticate(HttpServletRequest httpRequest, String returnLocation) {
        try {
            OAuthAuthzResponse response = OAuthAuthzResponse.oauthCodeAuthzResponse(httpRequest);
            
            PreAuthenticatedAuthenticationToken token = _authenticate(response);
            token.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(token);
        } catch (OAuthProblemException e) {
            logger.warn("", e.getResponseStatus(), e.getDescription());
            returnLocation += "?error=" + e.getDescription();
        } catch (OAuthSystemException e) {
            logger.warn("", e);
            returnLocation += "?error=" + e.getMessage();
        }
        
        return returnLocation;
    }
    
    // TODO rename this to a more descriptive name
    protected abstract PreAuthenticatedAuthenticationToken _authenticate(OAuthAuthzResponse response)
            throws OAuthProblemException, OAuthSystemException;
}

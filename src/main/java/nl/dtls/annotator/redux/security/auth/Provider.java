package nl.dtls.annotator.redux.security.auth;

import javax.servlet.http.HttpServletRequest;

public interface Provider {
    String getAuthorizeUrl(HttpServletRequest req);
    
    String getName();
    
    String getDisplayName();
    
    // absolute url or relative to the root
    String getIcon();
    
    boolean isConfigured();
    
    String authenticate(HttpServletRequest httpRequest, String returnLocation);
}

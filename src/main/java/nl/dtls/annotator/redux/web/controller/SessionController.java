package nl.dtls.annotator.redux.web.controller;

import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import nl.dtls.annotator.redux.model.AnnotationMetadata;

@RestController
@RequestMapping(value = "/session", method = RequestMethod.GET)
public class SessionController {
    private static final Logger logger = LoggerFactory.getLogger(SessionController.class);
    //
    static final String SESSION_METADATA_ATTR = "metadata";
    
    @RequestMapping("/metadata")
    public AnnotationMetadata getMetadata(HttpSession session) {
        return (AnnotationMetadata)session.getAttribute(SESSION_METADATA_ATTR);
    }
    
    @RequestMapping("/user")
    public Object getUser(Principal principal) {
        Authentication authentication = (Authentication)principal;
        return authentication.getPrincipal();
    }
}

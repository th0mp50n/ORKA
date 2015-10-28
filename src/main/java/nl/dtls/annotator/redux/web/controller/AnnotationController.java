package nl.dtls.annotator.redux.web.controller;

import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import nl.dtls.annotator.redux.model.AnnotationMetadata;
import nl.dtls.annotator.redux.model.AnnotationSubmission;
import nl.dtls.annotator.redux.security.model.UserInformation;
import nl.dtls.annotator.redux.service.NanopubStoreConnector;

@RestController
@RequestMapping("/annotation")
public class AnnotationController {
    @Autowired
    private NanopubStoreConnector connector;
    
    @RequestMapping(method = RequestMethod.POST)
    public NanopubResponse submit(@RequestBody AnnotationSubmission submission, HttpSession session, Principal principal) {
        String location = connector.storeNanopub((AnnotationMetadata)session.getAttribute(SessionController.SESSION_METADATA_ATTR),
                submission,
                (UserInformation)((Authentication)principal).getPrincipal());
        
        NanopubResponse res = new NanopubResponse();
        res.setNplocation(location);
        return res;
    }
    
    public static class NanopubResponse {
        private String nplocation;
        
        public String getNplocation() {
            return nplocation;
        }
        
        public void setNplocation(String nplocation) {
            this.nplocation = nplocation;
        }
    }
}

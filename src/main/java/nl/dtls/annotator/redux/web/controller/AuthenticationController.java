package nl.dtls.annotator.redux.web.controller;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import nl.dtls.annotator.redux.security.auth.Provider;
import nl.dtls.annotator.redux.security.auth.ProviderAccessor;

@Controller
@RequestMapping("/authentication")
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    
    @Autowired
    private ProviderAccessor accessor;
    
    @RequestMapping(method = RequestMethod.GET)
    public void authenticateProvider(@RequestParam String provider, @RequestParam("return") String returnLocation,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("authentication from {}, returning to {}", provider, returnLocation);
        
        Optional<Provider> p = accessor.get(provider);
        if (!p.isPresent()) {
            logger.warn("unknown provider {}", provider);
            // TODO return error to user
        } else {
            Provider authProvider = p.get();
            String redirect = authProvider.authenticate(request, returnLocation);
            
            response.sendRedirect(request.getContextPath() + "/#" + redirect);
        }
    }
    
    @RequestMapping(value = "/providers.html", method = RequestMethod.GET)
    public String loginModalContent(Model model) {
        model.addAttribute("providers", accessor.list());
        return "login-modal-content";
    }
}

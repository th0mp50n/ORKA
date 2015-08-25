package nl.dtls.annotator.redux.security.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProviderAccessor implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(ProviderAccessor.class);
    
    @Autowired
    private List<Provider> providers;
    
    private Map<String, Provider> mapping;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        mapping = new HashMap<>();
        
        for (Provider p : providers) {
            if (!p.isConfigured()) {
                logger.warn("Provider {} is not properly configured and therefor skipped", p.getName());
                continue;
            }
            mapping.put(p.getName(), p);
        }
    }
    
    public List<Provider> list() {
        return providers;
    }
    
    public Optional<Provider> get(String provider) {
        return Optional.ofNullable(mapping.get(provider));
    }
}

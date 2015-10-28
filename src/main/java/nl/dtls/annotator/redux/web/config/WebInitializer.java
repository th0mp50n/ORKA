package nl.dtls.annotator.redux.web.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import nl.dtls.annotator.redux.RootConfig;
import nl.dtls.annotator.redux.security.config.SecurityConfig;

public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return array(RootConfig.class, SecurityConfig.class, MvcConfig.class);
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return null;
    }

    @Override
    protected String[] getServletMappings() {
        return array("/");
    }
    
    @SafeVarargs
    private static <T> T[] array(T... array) {
        return array;
    }
}

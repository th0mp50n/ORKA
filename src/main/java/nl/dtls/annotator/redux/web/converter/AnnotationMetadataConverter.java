package nl.dtls.annotator.redux.web.converter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.dtls.annotator.redux.model.AnnotationMetadata;

@Component
public class AnnotationMetadataConverter extends AbstractHttpMessageConverter<AnnotationMetadata> {
    @Autowired
    private ObjectMapper objectMapper;
    
    public AnnotationMetadataConverter() {
        super(MediaType.APPLICATION_FORM_URLENCODED);
    }
    
    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz.equals(AnnotationMetadata.class);
    }
    
    @Override
    protected AnnotationMetadata readInternal(Class<? extends AnnotationMetadata> clazz,
            HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        FormHttpMessageConverter formConverter = new FormHttpMessageConverter();
        MultiValueMap<String, String> form = formConverter.read(null, inputMessage);
        
        return objectMapper.readValue(form.getFirst("metadata"), AnnotationMetadata.class);
    }

    @Override
    protected void writeInternal(AnnotationMetadata t, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        throw new UnsupportedOperationException("Writing metadata not supported");
    }
}

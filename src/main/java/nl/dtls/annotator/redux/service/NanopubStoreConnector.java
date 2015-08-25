package nl.dtls.annotator.redux.service;

import nl.dtls.annotator.redux.model.AnnotationMetadata;
import nl.dtls.annotator.redux.model.AnnotationSubmission;
import nl.dtls.annotator.redux.security.model.UserInformation;

public interface NanopubStoreConnector {
    String storeNanopub(AnnotationMetadata metadata, AnnotationSubmission submission, UserInformation userInfo);
}

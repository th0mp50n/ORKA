package nl.dtls.annotator.redux.model;

public class AnnotationMetadata {
    private String subject;
    private String subjectUri;
    private String predicate;
    private String predicateUri;
    private String object;
    private String objectUri;
    private String returnLabel;
    private String returnUri;
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getSubjectUri() {
        return subjectUri;
    }
    
    public void setSubjectUri(String subjectUri) {
        this.subjectUri = subjectUri;
    }
    
    public String getPredicate() {
        return predicate;
    }
    
    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }
    
    public String getPredicateUri() {
        return predicateUri;
    }
    
    public void setPredicateUri(String predicateUri) {
        this.predicateUri = predicateUri;
    }
    
    public String getObject() {
        return object;
    }
    
    public void setObject(String object) {
        this.object = object;
    }
    
    public String getObjectUri() {
        return objectUri;
    }
    
    public void setObjectUri(String objectUri) {
        this.objectUri = objectUri;
    }
    
    public String getReturnLabel() {
        return returnLabel;
    }
    
    public void setReturnLabel(String returnLabel) {
        this.returnLabel = returnLabel;
    }
    
    public String getReturnUri() {
        return returnUri;
    }
    
    public void setReturnUri(String returnUri) {
        this.returnUri = returnUri;
    }
}

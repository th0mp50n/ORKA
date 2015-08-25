package nl.dtls.annotator.redux.model;

public class AnnotationSubmission {
    private String predicate;
    private String narrative;
    
    public String getPredicate() {
        return predicate;
    }
    
    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }
    
    public String getNarrative() {
        return narrative;
    }
    
    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }
}

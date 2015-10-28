package nl.dtls.annotator.redux.security.model;

import java.io.Serializable;

public class UserInformation implements Serializable {
    private static final long serialVersionUID = 8555190018615291957L;
    
    private String name;
    private String id;
    private String url;
    private String provider;
    
    public UserInformation(String name, String id, String url, String provider) {
        this.name = name;
        this.id = id;
        this.url = url;
        this.provider = provider;
    }
    
    public String getName() {
        return name;
    }
    
    public String getId() {
        return id;
    }
    
    public String getUrl() {
        return url;
    }
    
    public String getProvider() {
        return provider;
    }
}

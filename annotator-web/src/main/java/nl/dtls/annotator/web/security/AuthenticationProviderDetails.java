/*
 * Copyright (C) 2015 DTL (${email})
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.dtls.annotator.web.security;

/**
 * Holds provider specific information and provided information about the
 * authenticated identity. Can be used to display information about the
 * authentication context and the authenticated identity.
 */
// TODO decide on the name
public class AuthenticationProviderDetails {
    private String providerName;
    private String authenticatedId;
    
    public String getProviderName() {
        return providerName;
    }
    
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
    
    public String getAuthenticatedId() {
        return authenticatedId;
    }
    
    public void setAuthenticatedId(String authenticatedId) {
        this.authenticatedId = authenticatedId;
    }
}

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
package nl.dtls.annotator.model;

import java.net.URL;

import com.google.common.base.MoreObjects;

public class Referrer {
    private String returnLabel;
    private URL returnUrl;
    private String version;
    
    public String getReturnLabel() {
        return returnLabel;
    }
    
    public void setReturnLabel(String returnLabel) {
        this.returnLabel = returnLabel;
    }
    
    public URL getReturnUrl() {
        return returnUrl;
    }
    
    public void setReturnUrl(URL returnUrl) {
        this.returnUrl = returnUrl;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("returnLabel", returnLabel)
                .add("returnUrl", returnUrl)
                .add("version", version)
                .toString();
    }
}

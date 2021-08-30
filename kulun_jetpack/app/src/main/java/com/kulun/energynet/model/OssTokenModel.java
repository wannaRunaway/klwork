package com.kulun.energynet.model;

import java.io.Serializable;

public class OssTokenModel implements Serializable {
    // String ak = json.get("AccessKeyId").getAsString();
    //                String sk = json.get("AccessKeySecret").getAsString();
    //                String token = json.get("SecurityToken").getAsString();
    //                String expiration = json.get("Expiration").getAsString();
    private String AccessKeyId, AccessKeySecret, SecurityToken, Expiration;

    public String getAccessKeyId() {
        return AccessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        AccessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return AccessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        AccessKeySecret = accessKeySecret;
    }

    public String getSecurityToken() {
        return SecurityToken;
    }

    public void setSecurityToken(String securityToken) {
        SecurityToken = securityToken;
    }

    public String getExpiration() {
        return Expiration;
    }

    public void setExpiration(String expiration) {
        Expiration = expiration;
    }
}

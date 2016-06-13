package br.edu.ifsc.mello.dummyuafclient;

import java.util.Date;

/**
 * Created by mello on 10/06/16.
 */
public class AuthenticatorInfo {
    private String icon;
    private String appId;
    private String name;
    private Date created;
    private String keyId;
    private String pubKey;
    private boolean secureHw;

    public AuthenticatorInfo(String icon, String appId, String name, Date created, String keyId, String pubKey, boolean secureHw) {
        this.icon = icon;
        this.name = name;
        this.appId = appId;
        this.created = created;
        this.keyId = keyId;
        this.pubKey = pubKey;
        this.secureHw = secureHw;
    }

    public AuthenticatorInfo() {
        this.secureHw = false;
    }

    public boolean isSecureHw() {
        return secureHw;
    }

    public void setSecureHw(boolean secureHw) {
        this.secureHw = secureHw;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }
}

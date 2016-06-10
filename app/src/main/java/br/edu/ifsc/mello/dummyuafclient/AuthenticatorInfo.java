package br.edu.ifsc.mello.dummyuafclient;

import java.util.Date;

/**
 * Created by mello on 10/06/16.
 */
public class AuthenticatorInfo {
    private String icon;
    private String name;
    private Date created;
    private String keyId;

    public AuthenticatorInfo(String icon, String name, Date created, String keyId) {
        this.icon = icon;
        this.name = name;
        this.created = created;
        this.keyId = keyId;
    }

    public AuthenticatorInfo() {
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

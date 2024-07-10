package com.example.application.data;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;

@Entity
public class ProxyAccount extends AbstractEntity {

    private String aUsername;
    private String aPassword;
    private String pUsername;
    private String pPassword;
    private String ip;
    private Integer port;
    @Email
    private String email;
    private String ePassword;
    private boolean active;

    public String getAUsername() {
        return aUsername;
    }
    public void setAUsername(String aUsername) {
        this.aUsername = aUsername;
    }
    public String getAPassword() {
        return aPassword;
    }
    public void setAPassword(String aPassword) {
        this.aPassword = aPassword;
    }
    public String getPUsername() {
        return pUsername;
    }
    public void setPUsername(String pUsername) {
        this.pUsername = pUsername;
    }
    public String getPPassword() {
        return pPassword;
    }
    public void setPPassword(String pPassword) {
        this.pPassword = pPassword;
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public Integer getPort() {
        return port;
    }
    public void setPort(Integer port) {
        this.port = port;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getEPassword() {
        return ePassword;
    }
    public void setEPassword(String ePassword) {
        this.ePassword = ePassword;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

}

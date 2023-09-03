package com.qboxus.tictic.activitesfragments.spaces.models;

import java.io.Serializable;

public class GroupModel implements Serializable {
    public String id, privacyType, name,riseHandRule, created;


    public GroupModel() {
        riseHandRule="1";
    }

    public String getRiseHandRule() {
        return riseHandRule;
    }

    public void setRiseHandRule(String riseHandRule) {
        this.riseHandRule = riseHandRule;
    }

    public String getId() {
        return id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrivacyType() {
        return privacyType;
    }

    public void setPrivacyType(String privacyType) {
        this.privacyType = privacyType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package com.qboxus.tictic.activitesfragments.spaces.utils.RoomManager;

import java.io.Serializable;

public class StreamModel implements Serializable {
    String id,adminId,title,privacyType,created,riseHandRule,riseHandCount;

    public StreamModel() {
        riseHandRule="1";
        riseHandCount="0";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrivacyType() {
        return privacyType;
    }

    public void setPrivacyType(String privacyType) {
        this.privacyType = privacyType;
    }


    public String getRiseHandRule() {
        return riseHandRule;
    }

    public void setRiseHandRule(String riseHandRule) {
        this.riseHandRule = riseHandRule;
    }

    public String getRiseHandCount() {
        return riseHandCount;
    }

    public void setRiseHandCount(String riseHandCount) {
        this.riseHandCount = riseHandCount;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}

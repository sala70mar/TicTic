package com.qboxus.tictic.models;

import java.io.Serializable;

public class PromotionModel implements Serializable {
    private String id;
    private String user_id;
    private String website_url;
    private String start_datetime;
    private String end_datetime;
    private String coin;
    private String active;
    private String destination;
    private String action_button;
    private String destination_tap;
    private String followers;
    private String reach;
    private String total_reach;
    private String clicks;
    private String audience_id;
    private String payment_card_id;
    private String created;
    private String video_id;

    public PromotionModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getWebsite_url() {
        return website_url;
    }

    public void setWebsite_url(String website_url) {
        this.website_url = website_url;
    }

    public String getStart_datetime() {
        return start_datetime;
    }

    public void setStart_datetime(String start_datetime) {
        this.start_datetime = start_datetime;
    }

    public String getEnd_datetime() {
        return end_datetime;
    }

    public void setEnd_datetime(String end_datetime) {
        this.end_datetime = end_datetime;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getAction_button() {
        return action_button;
    }

    public void setAction_button(String action_button) {
        this.action_button = action_button;
    }

    public String getDestination_tap() {
        return destination_tap;
    }

    public void setDestination_tap(String destination_tap) {
        this.destination_tap = destination_tap;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getReach() {
        return reach;
    }

    public void setReach(String reach) {
        this.reach = reach;
    }

    public String getTotal_reach() {
        return total_reach;
    }

    public void setTotal_reach(String total_reach) {
        this.total_reach = total_reach;
    }

    public String getClicks() {
        return clicks;
    }

    public void setClicks(String clicks) {
        this.clicks = clicks;
    }

    public String getAudience_id() {
        return audience_id;
    }

    public void setAudience_id(String audience_id) {
        this.audience_id = audience_id;
    }

    public String getPayment_card_id() {
        return payment_card_id;
    }

    public void setPayment_card_id(String payment_card_id) {
        this.payment_card_id = payment_card_id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }
}

package com.qboxus.tictic.models;

import java.io.Serializable;

public class RequestPromotionModel implements Serializable {
    //views=>1, website=>2, followers=>3
    int promoteGoal;
    //auto=>1, custom=>2
    int audienceType;
    //all=>1, female=>2, male=>3
    int gender;
    //all=>1, 13X17=>2, 18X24=>3, 25X34=>4, 35X44=>5, 45X54=>6, 55+=>7
    int age;
    String audienceId;
    String websiteULR;
    //learnmore=>1, shop now=>2, signup=>3, contact us=>4, apply now=>5, book now=>6
    int websiteLandingPage;
    //per coin visitor
    long videoViewsStat,websiteStat,followerStat;
    //selected budget
    int selectedBudget;
    //selected duration
    int selectedDuration;
    //selectedVideo
    HomeModel selectedVideo;
    PromotionAudiencesModel selectAudience;

    public RequestPromotionModel() {
    }

    public PromotionAudiencesModel getSelectAudience() {
        return selectAudience;
    }

    public void setSelectAudience(PromotionAudiencesModel selectAudience) {
        this.selectAudience = selectAudience;
    }

    public String getAudienceId() {
        return audienceId;
    }

    public void setAudienceId(String audienceId) {
        this.audienceId = audienceId;
    }

    public HomeModel getSelectedVideo() {
        return selectedVideo;
    }

    public void setSelectedVideo(HomeModel selectedVideo) {
        this.selectedVideo = selectedVideo;
    }

    public int getSelectedBudget() {
        return selectedBudget;
    }

    public void setSelectedBudget(int selectedBudget) {
        this.selectedBudget = selectedBudget;
    }

    public int getSelectedDuration() {
        return selectedDuration;
    }

    public void setSelectedDuration(int selectedDuration) {
        this.selectedDuration = selectedDuration;
    }

    public long getVideoViewsStat() {
        return videoViewsStat;
    }

    public void setVideoViewsStat(long videoViewsStat) {
        this.videoViewsStat = videoViewsStat;
    }

    public long getWebsiteStat() {
        return websiteStat;
    }

    public void setWebsiteStat(long websiteStat) {
        this.websiteStat = websiteStat;
    }

    public long getFollowerStat() {
        return followerStat;
    }

    public void setFollowerStat(long followerStat) {
        this.followerStat = followerStat;
    }

    public int getPromoteGoal() {
        return promoteGoal;
    }

    public void setPromoteGoal(int promoteGoal) {
        this.promoteGoal = promoteGoal;
    }

    public int getAudienceType() {
        return audienceType;
    }

    public void setAudienceType(int audienceType) {
        this.audienceType = audienceType;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getWebsiteULR() {
        return websiteULR;
    }

    public void setWebsiteULR(String websiteULR) {
        this.websiteULR = websiteULR;
    }

    public int getWebsiteLandingPage() {
        return websiteLandingPage;
    }

    public void setWebsiteLandingPage(int websiteLandingPage) {
        this.websiteLandingPage = websiteLandingPage;
    }
}

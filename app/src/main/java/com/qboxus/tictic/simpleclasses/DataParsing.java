package com.qboxus.tictic.simpleclasses;

import android.text.TextUtils;
import android.util.Log;

import com.danikula.videocache.HttpProxyCacheServer;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.activitesfragments.spaces.models.TopicModel;
import com.qboxus.tictic.models.PromotionHistoryModel;
import com.qboxus.tictic.models.PromotionModel;
import com.qboxus.tictic.models.StoryVideoModel;
import com.qboxus.tictic.models.UserModel;

import org.json.JSONObject;

public class DataParsing {


    public static UserModel getUserDataModel(JSONObject user)
    {

        UserModel model=new UserModel();
        try {

            model.setId(user.optString("id"));
            model.setFirstName(user.optString("first_name"));
            model.setLastName(user.optString("last_name"));
            model.setGender(user.optString("gender"));
            model.setBio(user.optString("bio"));
            model.setWebsite(user.optString("website"));
            model.setDob(user.optString("dob"));
            model.setSocial_id(user.optString("social_id"));
            model.setEmail(user.optString("email"));
            model.setPhone(user.optString("phone"));
            model.setPassword(user.optString("password"));
            if (TextUtils.isEmpty(user.optString("profile_pic_small")))
            {
                model.setProfilePic(user.optString("profile_pic"));
            }
            else
            {
                model.setProfilePic(user.optString("profile_pic_small"));
            }
            model.setProfileGif(user.optString("profile_gif"));
            model.setProfileView(user.optString("profile_view"));
            model.setRole(user.optString("role"));
            model.setUsername(user.optString("username"));
            model.setSocialType(user.optString("social"));
            model.setDeviceToken(user.optString("device_token"));
            model.setToken(user.optString("token"));
            model.setActive(user.optString("active"));
            model.setLat(user.optDouble("lat",0));
            model.setLng(user.optDouble("long",0));
            model.setOnline(user.optString("online"));
            model.setVerified(user.optString("verified"));
            model.setApplyVerification(user.optString("verification_applied"));
            model.setAuthToken(user.optString("auth_token"));
            model.setVersion(user.optString("version"));
            model.setDevice(user.optString("device"));
            model.setIp(user.optString("ip"));
            model.setCity(user.optString("city"));
            model.setCountry(user.optString("country"));
            model.setCityId(user.optString("city_id"));
            model.setStateId(user.optString("state_id"));
            model.setCountryId(user.optString("country_id"));
            model.setWallet(user.optLong("wallet",0));
            model.setVisitorCount(user.optLong("profile_visit_count",0));
            model.setPaypal(user.optString("paypal"));
            model.setReferalCode(user.optString("referral_code"));
            model.setResetWalletDatetime(user.optString("reset_wallet_datetime"));
            model.setFbId(user.optString("fb_id"));
            model.setCreated(user.optString("created"));
            model.setFollowersCount(user.optString("followers_count","0"));
            model.setFollowingCount(user.optString("following_count","0"));
            model.setLikesCount(user.optString("likes_count","0"));
            model.setVideoCount(user.optString("video_count","0"));
            model.setNotification(user.optString("notification","1"));
            model.setButton(user.optString("button"));
            model.setBlock(user.optString("block","0"));
            //you are only blocked by yourself
            try {
                JSONObject blockObj=user.getJSONObject("BlockUser");
                model.setBlockByUser(blockObj.optString("user_id","0"));
            }catch (Exception e){
                model.setBlockByUser(user.optString("id"));
            }

        }
        catch (Exception e)
        {
            Log.d(Constants.tag,"Exception : "+e);
        }

        return model;
    }

    public static StoryVideoModel getVideoDataModel(JSONObject video)
    {
        StoryVideoModel item=null;
        if (video != null) {
            item=new StoryVideoModel();

            item.setId(video.optString("id"));
            item.setUser_id(video.optString("user_id"));
            item.setDescription(video.optString("description"));
            item.setVideo(video.optString("video"));

            try {
                if (TicTic.appLevelContext!=null)
                {
                    HttpProxyCacheServer proxy = TicTic.getProxy(TicTic.appLevelContext);
                    String proxyUrl = proxy.getProxyUrl(item.getVideo());
                    if (Functions.isWebUrl(proxyUrl))
                    {
                        item.setVideo(proxyUrl);
                    }
                }
            }
            catch (Exception e){}

            item.setThum(video.optString("thum"));
            item.setGif(video.optString("gif"));
            item.setView(video.optString("view"));
            item.setSection(video.optString("section"));
            item.setSound_id(video.optString("sound_id"));
            item.setPrivacy_type(video.optString("privacy_type"));
            item.setAllow_comments(video.optString("allow_comments"));
            item.setAllow_duet(video.optString("allow_duet"));
            item.setBlock(video.optString("block"));
            item.setDuet_video_id(video.optString("duet_video_id"));
            item.setOld_video_id(video.optString("old_video_id"));
            item.setDuration(video.optString("duration"));
            item.setPromote(video.optString("promote"));
            item.setPin_comment_id(video.optString("pin_comment_id"));
            item.setPin(video.optString("pin"));
            item.setRepost_user_id(video.optString("repost_user_id"));
            item.setRepost_video_id(video.optString("repost_video_id"));
            item.setQuality_check(video.optString("quality_check"));
            item.setAws_job_id(video.optString("aws_job_id"));
            item.setAws_label(video.optString("aws_label"));
            item.setStory(video.optString("story"));
            item.setCreated(video.optString("created"));

        }

        return item;
    }

    public static PromotionHistoryModel parsePromotionHistory(JSONObject promotionObj)
    {
        PromotionHistoryModel promotionModel=new PromotionHistoryModel();
        promotionModel.setId(promotionObj.optString("id"));
        promotionModel.setUser_id(promotionObj.optString("user_id"));
        promotionModel.setWebsite_url(promotionObj.optString("website_url"));
        promotionModel.setStart_datetime(promotionObj.optString("start_datetime"));
        promotionModel.setEnd_datetime(promotionObj.optString("end_datetime"));
        promotionModel.setActive(promotionObj.optString("active"));
        promotionModel.setCoin(promotionObj.optString("coin"));
        promotionModel.setDestination(promotionObj.optString("destination"));
        promotionModel.setAction_button(promotionObj.optString("action_button"));
        promotionModel.setDestination_tap(promotionObj.optString("destination_tap"));
        promotionModel.setFollowers(promotionObj.optString("followers"));
        promotionModel.setReach(promotionObj.optString("reach"));
        promotionModel.setTotal_reach(promotionObj.optString("total_reach"));
        promotionModel.setClicks(promotionObj.optString("clicks"));
        promotionModel.setAudience_id(promotionObj.optString("audience_id"));
        promotionModel.setPayment_card_id(promotionObj.optString("payment_card_id"));
        promotionModel.setVideo_id(promotionObj.optString("video_id"));
        promotionModel.setCreated(promotionObj.optString("created"));
        return promotionModel;
    }


    public static TopicModel getTopicDataModel(JSONObject topic)
    {

        TopicModel model=new TopicModel();
        try {

            model.setId(topic.optString("id"));
            model.setTitle(topic.optString("title"));
            model.setImage(topic.optString("image"));
            model.setCreated(topic.optString("created"));
            model.setFollow(topic.optString("follow"));
        }
        catch (Exception e)
        {
            Log.d(Constants.tag,"Exception : getTopicDataModel"+e);
        }

        return model;
    }


}

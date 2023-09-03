package com.qboxus.tictic.apiclasses;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface InterfaceFileUpload {

    @Multipart
    @POST(ApiLinks.postVideo)
    Call<Object> UploadFile(@Part MultipartBody.Part file,
                                    @Part("privacy_type") RequestBody PrivacyType,
                                    @Part("user_id") RequestBody UserId,
                                    @Part("sound_id") RequestBody SoundId,
                                    @Part("allow_comments") RequestBody AllowComments,
                                    @Part("description") RequestBody Description,
                                    @Part("allow_duet") RequestBody AllowDuet,
                                    @Part("users_json") RequestBody UsersJson,
                                    @Part("hashtags_json") RequestBody HashtagsJson,
                                    @Part("story") RequestBody story ,
                                    @Part("video_id") RequestBody videoId);

    @Multipart
    @POST(ApiLinks.postVideo)
    Call<Object> UploadFile(@Part MultipartBody.Part file,
                                    @Part("privacy_type") RequestBody PrivacyType,
                                    @Part("user_id") RequestBody UserId,
                                    @Part("sound_id") RequestBody SoundId,
                                    @Part("allow_comments") RequestBody AllowComments,
                                    @Part("description") RequestBody Description,
                                    @Part("allow_duet") RequestBody AllowDuet,
                                    @Part("users_json") RequestBody UsersJson,
                                    @Part("hashtags_json") RequestBody HashtagsJson,
                                    @Part("video_id") RequestBody videoId,
                                    @Part("story") RequestBody story ,
                                    @Part("duet") RequestBody duet);

    @Multipart
    @POST(ApiLinks.addUserProfileVideo)
    Call<Object> UploadProfileVideo(@Part MultipartBody.Part file,
                                    @Part("user_id") RequestBody UserId);


}

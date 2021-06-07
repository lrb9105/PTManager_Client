package com.teamnova.ptmanager.network.friend;

import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.model.userInfo.IdPwDto;
import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.model.userInfo.UserInfoDtoWithUserId;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface FriendService {
    @FormUrlEncoded
    @POST("friend/addToFriend.php")
    Call<String> addToFriend(@Field("ownerId") String ownerId, @Field("ownerType") String ownerType, @Field("friendId") String friendId, @Field("friendType") String friendType);

    @GET("friend/getFriendsList.php")
    Call<ArrayList<FriendInfoDto>> getFriendsList(@Query("ownerId") String ownerId);

    @GET("friend/getMemberInfo.php")
    Call<FriendInfoDto> getFriendInfo(@Query("friendId") String friendId);
}

package com.teamnova.ptmanager.network.chatting;

import com.teamnova.ptmanager.model.chatting.ChatRoomInfoDto;
import com.teamnova.ptmanager.model.chatting.ChattingMemberDto;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ChattingRoomService {
    /** 채팅방 정보 가져오기*/
    @GET("chattingRoom/getChatRoomInfo.php")
    Call<ChatRoomInfoDto> getChatRoomInfo(@Query("chattingRoomId") String chattingRoomId);

    /** 채팅방 정보 저장하기*/
    @POST("chattingRoom/insertChatRoomInfo.php")
    Call<String> insertChatRoomInfo(@Body ArrayList<ChattingMemberDto> chatMemberList);


    @FormUrlEncoded
    @POST("friend/addToFriend.php")
    Call<String> addToFriend(@Field("ownerId") String ownerId, @Field("ownerType") String ownerType, @Field("friendId") String friendId, @Field("friendType") String friendType);

    @GET("friend/getMemberInfo.php")
    Call<FriendInfoDto> getFriendInfo(@Query("friendId") String friendId);

    @PUT("friend/completeConnect.php")
    Call<String> completeConnect(@Body String memberId);

}

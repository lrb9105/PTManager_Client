package com.teamnova.ptmanager.network.chatting;

import com.teamnova.ptmanager.model.chatting.ChatMsgInfo;
import com.teamnova.ptmanager.model.chatting.ChatRoomInfoDto;
import com.teamnova.ptmanager.model.chatting.ChatRoomInfoForListDto;
import com.teamnova.ptmanager.model.chatting.ChattingMemberDto;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ChattingService {
    /** 채팅방 정보 가져오기*/
    @GET("chattingroom/getChatRoomInfo.php")
    //Call<String> getChatRoomInfo(@Query("chattingRoomId") String chattingRoomId);
    Call<ChatRoomInfoDto> getChatRoomInfo(@Query("chattingRoomId") String chattingRoomId);

    /** 채팅방 리스트 정보 가져오기*/
    @GET("chattingroom/getChattingRoomList.php")
    //Call<String> getChatRoomInfo(@Query("chattingRoomId") String chattingRoomId);
    Call<ArrayList<ChatRoomInfoForListDto>> getChattingRoomList(@Query("userId") String userId);

    /** 특정 채팅방의 메시지 리스트가져오기*/
    @GET("chatting/getMsgListInfo.php")
    //Call<String> getChatRoomInfo(@Query("chattingRoomId") String chattingRoomId);
    Call<ArrayList<ChatMsgInfo>> getMsgListInfo(@Query("chattingRoomId") String roomId, @Query("userId") String userId);


    /** 채팅방 정보 저장하기*/
    @POST("chattingroom/insertChatRoomInfo.php")
    Call<String> insertChatRoomInfo(@Body ChatRoomInfoDto chatRoomInfoDto);

    /** 새로 초대한 인원 저장하기*/
    @POST("chattingroom/insertMemberList.php")
    Call<String> insertMemberList(@Body HashMap hashMap);


    /** 채팅참여자가 속해있는 채팅방 아이디가져오기*/
    @POST("chattingroom/getExistedChatRoomId.php")
    Call<String> getExistedChatRoomId(@Body ArrayList<ChattingMemberDto> chatMemberList);

    /** 채팅방의 멤버리스트 가져오기*/
    @GET("chattingroom/getChatMemberList.php")
    //Call<String> getChatRoomInfo(@Query("chattingRoomId") String chattingRoomId);
    Call<ArrayList<ChattingMemberDto>> getChatMemberList(@Query("chattingRoomId") String roomId);

    /** 채팅방을 나간 회원 삭제요청*/
    @POST("chattingroom/deleteMemberFromChatRoom.php")
    //Call<String> getChatRoomInfo(@Query("chattingRoomId") String chattingRoomId);
    Call<String> deleteMemberFromChatRoom(@Body HashMap hashMap);


    @FormUrlEncoded
    @POST("friend/addToFriend.php")
    Call<String> addToFriend(@Field("ownerId") String ownerId, @Field("ownerType") String ownerType, @Field("friendId") String friendId, @Field("friendType") String friendType);

    @GET("friend/getMemberInfo.php")
    Call<FriendInfoDto> getFriendInfo(@Query("friendId") String friendId);

    @PUT("friend/completeConnect.php")
    Call<String> completeConnect(@Body String memberId);

}

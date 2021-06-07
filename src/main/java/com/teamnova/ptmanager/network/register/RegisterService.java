package com.teamnova.ptmanager.network.register;

import com.teamnova.ptmanager.model.userInfo.UserInfoDto;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RegisterService {
    /**
     *  1) 회원정보를 서버에 전달하는 메소드
     *  2) 객체를 전달하기 위해선 @Body태그 사용
     * */
    @POST("register/registerUserInfo.php")
    Call<String> registerUserInfo(@Body UserInfoDto userInfoDto);

    @Multipart
    @POST("register/saveProfileImg.php")
    Call<String> transferImgToServer(@Part MultipartBody.Part profileImgFile);
}

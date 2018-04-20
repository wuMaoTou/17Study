package com.maotou.bspatch_kotlin.httpservice.serviceapi

import com.maotou.bspatch_kotlin.httpservice.beans.UpdateBean
import com.maotou.bspatch_kotlin.httpservice.beans.UserBean
import com.maotou.bspatch_kotlin.httpservice.httpentity.HttpResult
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by wuchundu on 18-4-13.
 */
interface UserService {

    /**
     * 注册
     */
    @POST("user/register.do")
    fun register(@Query("phone") phone: String, @Query("password") password: String,
                 @Query("username") username: String, @Query("sex") sex: Int,
                 @Query("age") age:Int): Observable<HttpResult<UserBean>>;

    /**
     * 登录
     */
    @POST("user/loginbypwd.do")
    fun login(@Query("phone") phone: String, @Query("password") password: String): Observable<HttpResult<UserBean>>

    /**
     * 获取用户信息
     */
    @GET("user/userinfo.do")
    fun getInfo(@Query("phone") phone: String): Observable<HttpResult<UserBean>>

    /**
     * 检查更新
     */
    @GET("update/chackupdate.do")
    fun checkUpdate(@Query("md5value") md5value: String, @Query("versionCode") versionCode: Int, @Query("channelId") channelId: String): Observable<HttpResult<UpdateBean>>
}
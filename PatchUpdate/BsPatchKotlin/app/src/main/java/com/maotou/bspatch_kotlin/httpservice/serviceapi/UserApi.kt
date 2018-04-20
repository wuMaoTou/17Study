package com.maotou.bspatch_kotlin.httpservice.serviceapi

import com.maotou.bspatch_kotlin.httpservice.beans.UpdateBean
import com.maotou.bspatch_kotlin.httpservice.beans.UserBean
import com.maotou.bspatch_kotlin.httpservice.service.BaseApi
import com.maotou.bspatch_kotlin.httpservice.service.HttpMethod
import io.reactivex.Observer

/**
 * Created by wuchundu on 18-4-13.
 */
class UserApi private constructor() : BaseApi(){

    companion object {
        val instance by lazy { UserApi() }
    }

    private var userService: UserService? = null

    init {
        userService = HttpMethod.instance.createApi(UserService::class.java)
    }

    fun register(phone: String, password: String, username: String, sex: Int, age: Int, subscriber: Observer<UserBean>){
//        val userBean = UserBean(phone, password, username, sex, age)
        val observable = userService?.register(phone,password,username,sex,age)?.map(HttpResultFunc<UserBean>())
        toSubscribe(observable!!,subscriber)
    }

    fun login(phone: String, password: String, subscriber: Observer<UserBean>){
        val observable = userService?.login(phone, password)?.map(HttpResultFunc<UserBean>())
        toSubscribe(observable!!,subscriber)
    }

    fun userinfo(phone: String, subscriber: Observer<UserBean>){
        val observable = userService?.getInfo(phone)?.map(HttpResultFunc<UserBean>())
        toSubscribe(observable!!,subscriber)
    }

    fun checkUpdate(md5value: String, versionCode: Int, channelId: String, subscriber: Observer<UpdateBean>){
        val observable = userService?.checkUpdate(md5value, versionCode, channelId)?.map(HttpResultFunc<UpdateBean>())
        toSubscribe(observable!!,subscriber)
    }

}
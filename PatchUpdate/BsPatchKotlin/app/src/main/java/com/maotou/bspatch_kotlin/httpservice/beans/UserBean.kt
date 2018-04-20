package com.maotou.bspatch_kotlin.httpservice.beans

/**
 * Created by wuchundu on 18-4-12.
 */
data class UserBean(var phone: String, var password: String, var username: String,
                    var sex: Int, var age: Int, var token: String = "", var id: Int = 0)
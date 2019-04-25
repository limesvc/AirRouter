package com.example.login.impl.design.ui.activity

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.example.login.LoginServiceApi
import router.air.annotation.Extra
import router.air.annotation.Route

/**
 * @author wuxi
 * @since 2019/3/5
 */
@Route(LoginServiceApi.LOGIN_ACTIVITY, 1)
class LoginActivity : Activity() {
    @Extra
    var account: String = ""
    @Extra(true)
    var password: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Toast.makeText(this, "LoginActivity", Toast.LENGTH_LONG).show()
    }
}
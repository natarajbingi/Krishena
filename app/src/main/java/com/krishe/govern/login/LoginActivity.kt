package com.krishe.govern.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.krishe.govern.MainActivity
import com.krishe.govern.R
import com.krishe.govern.databinding.ActivityLoginBinding
import com.krishe.govern.utils.BaseActivity
import com.krishe.govern.utils.KrisheUtils
import com.krishe.govern.utils.Sessions

class LoginActivity : BaseActivity(), LoginHandler {
    lateinit var binding: ActivityLoginBinding
    lateinit var viewModel: LoginViewModel
    var keepMeSigned = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@LoginActivity, R.layout.activity_login)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.viewModel = viewModel
        viewModel.setLoginVar(this)

        sessions = Sessions(this)
        keepMeSigned = sessions.getUserString(KrisheUtils.KeepMeSigned) == "YES"

        if (keepMeSigned) {
            onClickLoginBtn()
        }
    }

    override fun onClickLoginBtn() {
        if (binding.keepMeSigned.isChecked) {
            sessions.setUserString("YES", KrisheUtils.KeepMeSigned)
        }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onClickLoginFailed() {
        KrisheUtils.toastAction(this, "Login Failed, Please try again.")
    }

    override fun onSetEmailError(bool: Boolean) {
        if (bool) binding.edEmail.error = "Enter a valid User ID" else binding.edEmail.error =
            null
    }

    override fun onSetPwdError(bool: Boolean) {
        if (bool) binding.edPassword.error = "Invalid password" else binding.edPassword.error = null
    }

    override fun onLoginCallSuccess(loginRes: String) {
        Log.e("TAG", "onLoginCallSuccess: $loginRes" )
        sessions.setUserString(loginRes, KrisheUtils.userID)
        onClickLoginBtn()
    }

    override fun onLoginError(msg: String) {
        onClickLoginFailed()
    }

    override fun onPrHide() {
        hidePbar()
    }

    override fun onPrShow() {
        showPbar(this)
    }

    override fun onClickTextView() {

    }

}
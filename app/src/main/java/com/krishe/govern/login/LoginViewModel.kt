package com.krishe.govern.login

import android.app.Application
import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.krishe.govern.networks.NetWorkCall
import com.krishe.govern.utils.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
/**
 * Created by Nataraj Bingi on Oct 24, 2021
 */
class LoginViewModel(application: Application) : BaseViewModel(application),LoginHandler {

    var email: ObservableField<String>? = null
    var pwd: ObservableField<String>? = null
    lateinit var view: LoginHandler
    var paramJson = JSONObject()

    fun setLoginVar(loginHandler: LoginHandler) {
        email = ObservableField()
        pwd = ObservableField()
        this.view = loginHandler
    }

    fun loginBtn() {
        var valid = true
        if (email!!.get() == null || email!!.get()!!.isEmpty()) {
            view.onSetEmailError(true)
            valid = false
        } else {
            view.onSetEmailError(false)
            // _emailText.setError(null);
        }

        /*if (pwd!!.get() == null || pwd!!.get()!!.isEmpty() || pwd!!.get()!!.length < 4) {
            view.onSetPwdError(true)
            valid = false
        } else {
            view.onSetPwdError(false)
        }*/

        if (valid) {
            loginRestFulApi()
        } else {
            view.onClickLoginFailed()
        }
    }

    private fun loginRestFulApi() {
        paramJson.put("userID", email!!.get())
        view.onPrShow()
        viewModelScope.launch(Dispatchers.IO) {
            NetWorkCall.loginImplement(this@LoginViewModel, paramJson)
        }

    }

    fun emailWatcher(): TextWatcher? {
        return object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                email?.set(charSequence.toString())
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                email?.set(editable.toString())
            }
        }
    }

    fun pwdWatcher(): TextWatcher? {
        return object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                pwd?.set(charSequence.toString())
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
//                Log.d("logId", view.getId() + "");
                pwd?.set(editable.toString())
            }
        }
    }

    override fun onClickTextView() {

    }

    override fun onClickLoginBtn() {

    }

    override fun onClickLoginFailed() {

    }

    override fun onSetEmailError(bool: Boolean) {

    }

    override fun onSetPwdError(bool: Boolean) {

    }

    override fun onLoginCallSuccess(loginRes: String) {
        view.onPrHide()
        view.onLoginCallSuccess(loginRes.split("_").get(1))
    }

    override fun onLoginError(msg: String) {
        view.onPrHide()
        view.onLoginError("Login failed, Please try again.")
    }

    override fun onPrHide() {

    }

    override fun onPrShow() {

    }
}
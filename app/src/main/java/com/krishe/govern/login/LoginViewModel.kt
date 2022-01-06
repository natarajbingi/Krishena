package com.krishe.govern.login

import android.app.Application
import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.ObservableField
import com.krishe.govern.utils.BaseViewModel

class LoginViewModel(application: Application) : BaseViewModel(application) {

    var email: ObservableField<String>? = null
    var pwd: ObservableField<String>? = null
    lateinit var view: LoginHandler

    fun setLoginVar(loginHandler: LoginHandler) {
        email = ObservableField()
        pwd = ObservableField()
        this.view = loginHandler
    }

    fun loginMe() {
        view.onClickLoginBtn()
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

        if (pwd!!.get() == null || pwd!!.get()!!.isEmpty() || pwd!!.get()!!.length < 4) {
            view.onSetPwdError(true)
            valid = false
        } else {
            view.onSetPwdError(false)
        }

        if (valid) {
            view.onClickLoginBtn()
        } else {
            view.onClickLoginFailed()
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
}
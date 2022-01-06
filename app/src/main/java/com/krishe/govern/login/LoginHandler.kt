package com.krishe.govern.login

interface LoginHandler {
    fun onClickTextView()

    fun onClickLoginBtn()

    fun onClickLoginFailed()

    fun onSetEmailError(bool: Boolean)

    fun onSetPwdError(bool: Boolean)

    fun onLoginCallSuccess(loginRes: String)

    fun onLoginError(msg: String)

    fun onPrHide()

    fun onPrShow()
}
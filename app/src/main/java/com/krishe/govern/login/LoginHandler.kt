package com.krishe.govern.login
/**
 * Created by Nataraj Bingi on Oct 24, 2021
 */
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
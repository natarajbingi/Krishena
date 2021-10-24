package com.krishe.govern.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class Sessions(context: Context?) {

    private var pref: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var _context: Context? = context
    private val private_mode = 0
    private val PREF_NAME = "KrisheApp"

    init {
        pref = _context!!.getSharedPreferences(PREF_NAME, private_mode)
        editor = pref?.edit()
    }

    fun setUserString(userObject: String, key: String) {
//        val pref = PreferenceManager.getDefaultSharedPreferences(c)
//        val editor = pref.edit()
        editor?.putString(key, userObject)
        editor?.commit()
    }

    fun getUserString(key: String): String? {
        //val pref = PreferenceManager.getDefaultSharedPreferences(ctx)
        return pref?.getString(key, null)
    }

    fun removeUserKey(ctx: Context?, key: String?) {
       // val settings = PreferenceManager.getDefaultSharedPreferences(ctx)
        pref?.edit()?.remove(key)?.apply()
    }
    // --------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------
    fun setUserObj(userObject: Any?, key: String?) {
       // val mPrefs = PreferenceManager.getDefaultSharedPreferences(c)
       // val editor = mPrefs.edit()
       // val prefsEditor = mPrefs.edit()
        val gson = Gson()
        val json = gson.toJson(userObject)
        editor?.putString(key, json)
        editor?.commit()
    }

    fun getUserObj(key: String?, type: Class<*>?): Any? {
       // val mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx)
        val gson = Gson()
        val json = pref?.getString(key, null)
        return gson.fromJson<Any>(json, type)
    }


}
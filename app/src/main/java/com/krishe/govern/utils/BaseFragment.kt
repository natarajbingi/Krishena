package com.krishe.govern.utils

import android.content.Context
import androidx.fragment.app.Fragment

open class BaseFragment  : Fragment() {
    open lateinit var progressBar: ProgressBarHandler
    open lateinit var sessions: Sessions

    override fun onAttach(context: Context) {
        super.onAttach(context)
        progressBar = ProgressBarHandler(context)
        progressBar.hide()

        sessions = Sessions(context)

    }

}
package com.krishe.govern.utils

class KrishEvent<out T>(private val content: T) {
    var hasBeenConsumed = false
        private set


    fun peekContent(): T = content

    sealed class KrishEventState<out R> {
        data class NotYetConsumed<out T>(val data: T) : KrishEventState<T>()
        object AlreadyConsumed : KrishEventState<Nothing>()
    }

    fun getState(): KrishEventState<T>? {
        return if (hasBeenConsumed) {
            KrishEventState.AlreadyConsumed
        } else {
            hasBeenConsumed = true
            KrishEventState.NotYetConsumed(content)
        }
    }
}
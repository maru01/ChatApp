package com.andfactory.realtimedatabasesample

data class ChatData(
        val chatId: Long = 0,
        val userName: String = "",
        val text: String = "") {
    fun isMyChat(userName: String): Boolean {
        return userName == this.userName
    }
}
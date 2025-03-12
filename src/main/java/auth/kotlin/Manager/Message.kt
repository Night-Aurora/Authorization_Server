package auth.kotlin.Manager

import auth.kotlin.Server
import auth.kotlin.Server.Companion.plugin

class Message {
    init {
        InfoMessage.let {
            if(it.isEmpty()){
                it.add(successfulAdd)
                it.add(successfulDel)
                it.add(successfulCreateAccount)
                it.add(successfulRemoveAccount)
            }
        }
        ErrorMessage.let {
            if(it.isEmpty()){
                it.add(unknownHWID)
                it.add(unknownUser)
                it.add(brokenHwid)
            }
        }
        WarnMessage.let {
            if(it.isEmpty()){
                it.add(sameHWID)
                it.add(sameUser)
                it.add(emptyAccountName)
                it.add(emptyPassWord)
            }
        }
    }
    companion object{
        val InfoMessage:ArrayList<String> = ArrayList()
        val ErrorMessage:ArrayList<String> = ArrayList()
        val WarnMessage:ArrayList<String> = ArrayList()
        //hwids
        const val successfulAdd = "成功添加HWID"
        const val successfulDel = "成功删除HWID"
        const val sameHWID = "HWID已存在"
        const val unknownHWID = "HWID不存在"
        const val brokenHwid = "错误的HWID"
        //users
        const val emptyAccountName = "请输入用户名"
        const val emptyPassWord = "请输入密码"
        const val successfulCreateAccount = "成功创建账户"
        const val successfulRemoveAccount = "成功删除账户"
        const val sameUser = "该用户已存在"
        const val unknownUser = "该账户不存在"

        fun ab(message: String) =
            when {
                InfoMessage.contains(message) -> plugin!!.SystemOut(message, Server.OutMode.INFO)
                WarnMessage.contains(message) -> plugin!!.SystemOut(message, Server.OutMode.WARN)
                else -> plugin!!.SystemOut(message, Server.OutMode.ERROR)
            }
    }
}
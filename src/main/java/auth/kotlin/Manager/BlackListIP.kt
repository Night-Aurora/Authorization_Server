package auth.kotlin.Manager

import auth.kotlin.Server

class BlackListIP {
    var blackListIP:MutableList<String> = ArrayList()
    val config = Server.ConfigManager
    fun add(ip:String){
        if (!blackListIP.contains(ip)) {
            Server.plugin!!.SystemOut("$ip Have been moved to blacklist",Server.OutMode.EXECUTE)
            blackListIP.add(ip)
            config.saveBlackListIP()
        }
    }

    fun remove(ip:String){
        blackListIP.removeIf{ it == ip }
    }

    fun inBlackList(ip:String):Boolean{
        return blackListIP.contains(ip)
    }
}
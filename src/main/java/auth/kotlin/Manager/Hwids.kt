package auth.kotlin.Manager

import auth.kotlin.Server
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class Hwids {
    val message = Message
    val HwidList:ArrayList<HWID> = ArrayList()
    val config = Server.ConfigManager
    val number = Pattern.compile("[a-zA-Z]+")
    val letters = Pattern.compile("^[\\da-zA-Z]{64}\$")
    fun addHwid(hwid: String,user: String):String{
        if (hwid.count() != 64)
            return message.brokenHwid
        if(HwidList.contains(HWID(user, hwid)))
            return message.sameHWID
        else for(list in HwidList)
            if(list.isUser(hwid))
                return message.sameHWID
        HwidList.add(HWID(user, hwid))
        config.saveHWIDs()
        return message.successfulAdd
    }
    fun removeHwid(hwid: String):String{
        if(number.matcher(hwid).matches()){
            if (HwidList.removeIf{ it.isUser(hwid)}){
                config.saveHWIDs()
                return message.successfulDel
            }
        } else if (letters.matcher(hwid).matches()){
            if(HwidList.removeIf { hwid1: HWID -> hwid1.isUser(hwid) }){
                config.saveHWIDs()
                return message.successfulDel
            }
        }
        else return message.brokenHwid
        return message.unknownHWID
    }
//    fun getHwid():String{
//        val process = Runtime.getRuntime().exec(arrayOf("wmic", "csproduct", "get", "uuid"))
//        process.outputStream.close()
//        val sc = Scanner(process.inputStream)
//        sc.next()
//        return Server.aes.Encryption(sc.next(),"qjJYRgwNOcdFjNjYA2TsBiRXCwku21lVyUcxP3kk4kA2i54me4IIQGu6YV3dUWTRRI5G3C4zffJKWIY7iO")
//    }
    class HWID(var user: String, var hwid: String){
        fun isUser(rhwid: String):Boolean{
            return rhwid == hwid || rhwid == user
        }
    }
}
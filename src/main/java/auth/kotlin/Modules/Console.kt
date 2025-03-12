package auth.kotlin.Modules

import auth.kotlin.Manager.Message
import auth.kotlin.Server
import java.util.*

class Console:Thread(){
    val plugin = Server.plugin!!
    val hwid = Server.HwidsManager
    val message = Message
    val user = Server.UsersManager
    override fun run() {
        plugin.SystemOut("Console connected",Server.OutMode.INFO)
        plugin.SystemOut("Type /help for help",Server.OutMode.INFO)
        val scan = Scanner(System.`in`)
        while (scan.hasNext()){
            val next = scan.nextLine()
            val cmd = next.split(" ")
            if(cmd.isNotEmpty()){
                when(cmd[0].lowercase()){
                    "help"-> {
                        plugin.SystemOut("\n " +
                                "add [add HWID] \n " +
                                "del [del HWID] \n " +
                                "list [show all recorded HWIDs] \n " +
                                "create [create an account for admin] \n " +
                                "remove [remove exit admin account] \n " +
                                "runtime [how long have the Server been working]"
                            , Server.OutMode.INFO)
                    }
                    "add" -> {
                        if (cmd.size > 2)
                            message.ab(hwid.addHwid(cmd[1], cmd[2]))
                        else
                            plugin.SystemOut("Please enter HWID + UserName", Server.OutMode.WARN)
                    }
                    "del" -> {
                        if (cmd.size > 1)
                            message.ab(hwid.removeHwid(cmd[1]))
                        else
                            plugin.SystemOut("Please enter HWID", Server.OutMode.WARN)
                    }
                    "list" -> {
                        if (hwid.HwidList.isNotEmpty())
                            hwid.HwidList.forEach{ hwid -> plugin.SystemOut(hwid.hwid + " : " + hwid.user, Server.OutMode.INFO) }
                        else
                        plugin.SystemOut("List is empty", Server.OutMode.WARN)
                    }
                    "create" -> {
                        if (cmd.size > 2)
                            message.ab(user.CreateAccount(cmd[1], cmd[2]))
                        else
                            plugin.SystemOut("Please enter account name + password", Server.OutMode.WARN)
                    }
                    "remove" -> {
                        if (cmd.size > 1)
                            message.ab(user.RemoveAccount(cmd[1]))
                        else
                            plugin.SystemOut("Please enter account name", Server.OutMode.WARN)
                    }
                    "runtime" -> {
                        val runtime = System.currentTimeMillis() / 1000 - Server.starttime / 1000
                        val H: Long = runtime / 3600
                        val M: Long = runtime / 60 - H * 60
                        val S: Long = runtime - M * 60 - H * 3600
                        plugin.SystemOut("Up time $H H $M M $S S",Server.OutMode.INFO)
                    }
                }
            }
        }
    }
}
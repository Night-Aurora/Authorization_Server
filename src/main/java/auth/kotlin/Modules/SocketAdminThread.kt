package auth.kotlin.Modules

import auth.kotlin.AES
import auth.kotlin.Manager.Message
import auth.kotlin.Server
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.net.SocketException

/**
 * 87行报错需要修
 */
class SocketAdminThread(var socket: Socket, val keyAES: ByteArray) : Thread() {
    private val plugin = Server.plugin!!
    private val user = Server.UsersManager
    private val Hwids = Server.HwidsManager
    private val message = Message
    val sender = DataOutputStream(socket.getOutputStream())
    val receiver = DataInputStream(socket.getInputStream())
    override fun run() {
        var hasLogin = false
        try {
            Send(sender, "Please Login : login [Account] [Password]",Client.WARN)
            Server.keepAliveList.add(this)
            while (!this.isInterrupted) {
                try {
                    val encryptedMessage = ByteArray(receiver.readInt()).also { receiver.readFully(it) }
                    val message = String(AES.decrypt(encryptedMessage, keyAES))
                    if (message.isNotEmpty()) {
                        val about = message.split(" ").toTypedArray()
                        if (!hasLogin) {
                            if (about.size > 2) {
                                if (!about[0].equals("login", ignoreCase = true)) continue
                                when(user.Login(about[1], about[2], socket)){
                                    -1 -> Send(sender, "Please Enter Account Name", Client.WARN)
                                    -2 -> Send(sender, "Please Enter PassWord", Client.WARN)
                                    -3 -> Send(sender, "Wrong Account or Password", Client.ERROR)
                                    -4 -> Send(sender, "Please wait 10 seconds", Client.ERROR)
                                    0 -> {
                                        Send(sender, "Welcome: " + about[1], Client.INFO)
                                        hasLogin = true
                                    }
                                }
                                continue
                            }
                            Send(sender, "Please Login : login [Account] [Password]", Client.WARN)
                        } else if (about.isNotEmpty()) {
                            when (about[0].lowercase()) {
                                "add" ->
                                    if (about.size > 2)
                                        Send(sender,Hwids.addHwid(about[1], about[2]), Client.WARN)
                                    else
                                        Send(sender, "Please Enter The HWID + UserName", Client.WARN)
                                "del" ->
                                    if (about.size > 1)
                                        Send(sender,Hwids.removeHwid(about[1]), Client.WARN)
                                    else
                                        Send(sender, "Please Enter The HWID", Client.WARN)
                                "list" ->
                                    if (Hwids.HwidList.isNotEmpty()){
                                        var wait = ""
                                        try {
                                            Hwids.HwidList.forEach{
                                                wait += ",[${it.hwid} : ${it.user}]"
                                            }
                                            wait = "${wait.substring(1)},List"
                                            Send(sender,wait,Client.INFO)
                                        } catch (ignore: IOException) {

                                        }
                                    }
                                    else Send(sender, "List is empty", Client.ERROR)
                            }
                        }
                    }
                } catch (e: Exception) {
                    when (e) {
                        is SocketException -> {
                            if(e.message.contentEquals("Connection reset")){
                                plugin.SystemOut(socket.inetAddress.hostAddress + " " + "Lost admin connection",Server.OutMode.ERROR)
                            }
                        }
                        else ->{
                            e.printStackTrace()
                        }
                    }
                    this.interrupt()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    fun Send(sender: DataOutputStream, message: String, mode: Client?) {
        val encryptedData =  when (mode) {
            Client.INFO -> AES.encrypt("[INFO] $message".toByteArray(),keyAES)
            Client.WARN -> AES.encrypt("[WARN] $message".toByteArray(),keyAES)
            Client.ERROR -> AES.encrypt("[ERROR] $message".toByteArray(),keyAES)
            null -> ByteArray(0)
        }
        sender.writeInt(encryptedData.size)
        sender.write(encryptedData)
        sender.flush()
    }


    enum class Client {
        INFO, WARN, ERROR
    }
}
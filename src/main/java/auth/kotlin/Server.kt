package auth.kotlin

import auth.kotlin.ECDSA.generatePrivateKey
import auth.kotlin.ECDSA.generatePublicKey
import auth.kotlin.EcTransfer.hexToByteArray
import auth.kotlin.Manager.*
import auth.kotlin.Modules.Console
import auth.kotlin.Modules.KeepAlive
import auth.kotlin.Modules.SocketAdminThread
import auth.kotlin.Modules.SocketThread
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole
import java.lang.System.currentTimeMillis
import java.net.ServerSocket
import java.security.PrivateKey
import java.security.PublicKey
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


class Server {
    companion object {
        var plugin: Server? = null
        var starttime = 0L
        lateinit var HwidsManager: Hwids
        lateinit var ConfigManager:Config
        lateinit var connects:Connects
        lateinit var UsersManager:Users
        lateinit var blackListIP: BlackListIP
        lateinit var aes: AES
        lateinit var message:Message
        var keepAliveList:ArrayList<SocketAdminThread> = ArrayList()

        /**
         * use ECDSA generate your key
         * protect your private key and paste public to client side
         */
        val public:String = ""
        val private:String = ""
        lateinit var publicKey: PublicKey
        lateinit var privateKey: PrivateKey
        @JvmStatic
        fun main(args: Array<String>) {
            publicKey = generatePublicKey(hexToByteArray(public))
            privateKey = generatePrivateKey(hexToByteArray(private))
            starttime = currentTimeMillis()
            plugin = Server()
            Console().start()
            KeepAlive().start()
            val ServerSocket = ServerSocket(ConfigManager.initS())
            while (true) {
                val socket = ServerSocket.accept()
                val ip = socket.inetAddress.hostAddress
                if (blackListIP.inBlackList(ip)) {
                    plugin!!.SystemOut("Connect Refuse: $ip", OutMode.EXECUTE)
                    socket.close()
                    continue
                }
                SocketThread(socket).start()
            }
        }
    }


    init {
        ConfigManager = Config()
        HwidsManager = Hwids()
        connects = Connects()
        UsersManager = Users()
        blackListIP = BlackListIP()
        aes = AES
        message = Message()
        ConfigManager.Hwids = HwidsManager
        ConfigManager.user = UsersManager
        ConfigManager.blackListIP = blackListIP
        ConfigManager.getConfig()
        ConfigManager.getHWIDs()
        ConfigManager.getBlackListIP()
        SystemOut("Authorization Server is running on port:${ConfigManager.initS()}",OutMode.INFO)
    }
    
    fun spam(it:String):Boolean{
        if(it.isNotEmpty()) {
            if (   it.lowercase().contains("cookie")
                || it.lowercase().contains("administr")
                || it.lowercase().contains("http")
                || it.lowercase().contains("get")
                || it.lowercase().contains("mstshash")
                || it.lowercase().contains("accept")
                || it.lowercase().contains("host")
                || it.lowercase().contains("pragma")
                || it.lowercase().contains("user-agent")
                || it.lowercase().contains("if-modified-since")) return true
        }
        return false
    }

    fun isLetters(received: String):Boolean{
        val pattern = Pattern.compile("^[\\da-zA-Z]*\$")
        return pattern.matcher(received).matches()
    }

    fun SystemOut(out: String, outMode: OutMode?) {
        val formatter = SimpleDateFormat("HH:mm:ss")
        val date = Date(currentTimeMillis())
        when (outMode) {
            OutMode.INFO -> println("[" + formatter.format(date) + " INFO" + "] " + out)
            OutMode.WARN -> AnsiConsole.out.println(Ansi.ansi().fg(Ansi.Color.YELLOW).a("[" + formatter.format(date) + " WARN" + "] " + out).reset())
            OutMode.ERROR -> AnsiConsole.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("[" + formatter.format(date) + " ERROR" + "] " + out).reset())
            OutMode.EXECUTE -> AnsiConsole.out.println(Ansi.ansi().fg(Ansi.Color.CYAN).a("[" + formatter.format(date) + " EXECUTE" + "] " + out).reset())
            null -> TODO()
        }
    }

    enum class OutMode {
        INFO, WARN, ERROR, EXECUTE
    }
}
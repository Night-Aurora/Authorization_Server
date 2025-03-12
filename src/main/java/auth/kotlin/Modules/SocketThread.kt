package auth.kotlin.Modules

import auth.kotlin.AES.generateRandomKey
import auth.kotlin.ECDSA.signData
import auth.kotlin.EcTransfer.byteArrayToHex
import auth.kotlin.RSA.encryptWithPublicKey
import auth.kotlin.AES
import auth.kotlin.Server
import auth.kotlin.Server.Companion.privateKey
import com.sun.nio.sctp.IllegalReceiveException
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer
import java.util.concurrent.*
import kotlin.system.measureTimeMillis

class SocketThread(val socket:Socket) : Thread() {
    private val plugin = Server.plugin!!
    private val hwid = Server.HwidsManager
    private var clientPublicKey: ByteArray? = null
    private var keyAES: ByteArray? = null
    private var ADMIN = false
    private lateinit var connection:Future<ByteArray>
    private val pattern = Regex("[^\\da-zA-Z]+/g")
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    override fun run() {
        val time = measureTimeMillis {
            try {
                plugin.SystemOut(socket.inetAddress.hostAddress + " Connected!", Server.OutMode.INFO)
                val receiver = DataInputStream(socket.getInputStream())

                clientPublicKey = connection(receiver).get(5,TimeUnit.SECONDS)
                if(clientPublicKey!!.size != 162)
                    throw IllegalReceiveException()
                isValid(clientPublicKey!!)
                //generate AES Key
                keyAES = generateRandomKey()
                plugin.SystemOut("AESKey: ${byteArrayToHex(keyAES!!)}", Server.OutMode.INFO)

                val encryptedAESKey = encryptWithPublicKey(keyAES!!, clientPublicKey!!)
                val signature = signData(encryptedAESKey, privateKey)
                val lenArray = ByteBuffer.allocate(4).putInt(encryptedAESKey.size).array()
                val combine = lenArray + encryptedAESKey + signature // all data
                val sender = DataOutputStream(socket.getOutputStream())
                Send(sender,combine)

                //println(encryptedAESKey.toList().toString())
                //println(signature.toList().toString())

                val receive = connection(receiver).get(5,TimeUnit.SECONDS)
                val decryptedMessage = String(AES.decrypt(receive, keyAES!!))
                val split = decryptedMessage.split(" ").toTypedArray()
                if(decryptedMessage.equals("[ADMIN-LOGIN]",ignoreCase = true)){
                    ADMIN = true
                    SocketAdminThread(socket, keyAES!!).start() //ADMIN LOGIN
                    plugin.SystemOut("Transfer ${socket.inetAddress.hostAddress} to admin connection",Server.OutMode.INFO)
                    return
                }
                if(split.isEmpty() || split.size < 2) return

                val head = split[0]
                val body = split[1]
                plugin.SystemOut(decryptedMessage, Server.OutMode.INFO)
                when (head) {
                    "HWID" -> {
                        for (list in hwid.HwidList) {
                            if (list.isUser(body)) {
                                Send(sender, AES.encrypt("[HWID] pass".toByteArray(), keyAES!!))
                                plugin.SystemOut(socket.localAddress.hostAddress + " " + body + " " + "pass",
                                    Server.OutMode.INFO
                                )
                                return
                            }
                        }
                        Send(sender, AES.encrypt("[HWID] Faild".toByteArray(), keyAES!!))
                    }
                }


            } catch (e: Exception) {
                when (e){
                    is ExecutionException -> {
                        //
                    }
                    is SocketException -> {}
                    is IllegalReceiveException ->{
                        plugin.SystemOut(socket.inetAddress.hostAddress + " " + "Connection Error", Server.OutMode.ERROR)
                    }
                    is TimeoutException ->{
                        plugin.SystemOut("Socket Read Timeout", Server.OutMode.ERROR)
                        connection.cancel(true)
                    }
                    else -> e.printStackTrace()
                }
            } finally {
                executorService.shutdownNow()
                if(!ADMIN) {
                    socket.close()//ADMIN Login
                }
            }
        }
        plugin.SystemOut("Task Execute in ${time}ms", Server.OutMode.INFO)
    }

    private fun connection(receiver:DataInputStream): Future<ByteArray> {
        connection = executorService.submit<ByteArray>{
            ByteArray(receiver.readInt()).also { receiver.read(it) }
        }
        return connection
    }

    private fun isValid(message:ByteArray){
        if(plugin.spam(String(message))) {
            Server.blackListIP.add(socket.inetAddress.hostAddress)
            throw IllegalReceiveException()
        }
    }


    @Throws(IOException::class)
    private fun Send(sender:DataOutputStream, message:ByteArray) {
        sender.writeInt(message.size)
        sender.write(message)
        sender.flush()
    }
}
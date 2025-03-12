package auth.kotlin.Manager

import auth.kotlin.Server
import java.math.BigInteger
import java.net.Socket
import java.security.MessageDigest
import java.util.*

class Users {
    var users: MutableList<User> = ArrayList()
    private val config = Server.ConfigManager
    private val loginlong = HashMap<Socket, Long>()
    private val wrongtimes = HashMap<Socket, Int>()
    private val message = Message
    fun CreateAccount(user: String?, password: String?): String {
        if (user == null) return message.emptyAccountName
        if (password == null) return message.emptyPassWord
        for (user1 in users) {
            if (user1.isUser(user)) return message.sameUser
        }
        users.add(User(user, Encryption(password)))
        config.saveConfig()
        return message.successfulCreateAccount
    }

    fun RemoveAccount(user: String?): String {
        if (user == null) return message.emptyAccountName
        return if (users.removeIf { user1: User -> user1.isUser(user) }) {
            config.saveConfig()
            message.successfulRemoveAccount
        } else message.unknownUser
    }

    fun Login(user: String?, password: String?, socket: Socket): Int {
        if (user == null) return -1
        if (password == null) return -2
        if (wrongtimes.getOrDefault(socket, 0) > 2) {
            val system = System.currentTimeMillis()
            loginlong.putIfAbsent(socket, system)
            return if (system - loginlong.getOrDefault(socket, 0L) > 1000 * 10) {
                if (LoginEx(user, password)) {
                    wrongtimes.remove(socket)
                    loginlong.remove(socket)
                    return 0
                }
                loginlong[socket] = system
                -3
            } else -4
        }
        if (LoginEx(user, password)) return 0
        wrongtimes[socket] = wrongtimes.getOrDefault(socket, 0) + 1
        return -3
    }

    private fun LoginEx(user: String, password: String): Boolean {
        for (user1 in users) {
            if (user1.isUser(user)) {
                if (user1.rightPassword(Encryption(password))) {
                    return true
                }
            }
        }
        return false
    }

    fun Encryption(password: String): String {
        var sha: BigInteger? = null
        val inputData = password.replace(" ".toRegex(), "").toByteArray()
        try {
            val messageDigest = MessageDigest.getInstance("SHA")
            messageDigest.update(inputData)
            sha = BigInteger(messageDigest.digest())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        assert(sha != null)
        return sha!!.toString(32)
    }

    class User(val user: String, val password: String) {
        fun isUser(user: String): Boolean {
            return this.user == user
        }

        fun rightPassword(password: String): Boolean {
            return this.password == password
        }
    }
}
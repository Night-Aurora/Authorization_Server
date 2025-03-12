package auth.kotlin

import auth.kotlin.Modules.SocketThread
import java.net.SocketException

class Connects {
    var connects:ArrayList<SocketThread> = ArrayList()
    fun add(thread: SocketThread){
        connects.add(thread)
    }
    @Throws(SocketException::class)
    fun remove(thread: SocketThread){
        thread.socket.close()
        connects.remove(thread)
        thread.stop()
    }
}
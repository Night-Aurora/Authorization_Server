package auth.kotlin.Modules

import auth.kotlin.Server


class KeepAlive:Thread(){
    override fun run() {
        while(true){
            if(Server.keepAliveList.isNotEmpty()){
                val list = Server.keepAliveList.iterator()
                while (list.hasNext()){
                    val it = list.next()
                    try {
                        it.sender.write("[ALIVE]".toByteArray())
                    } catch (e:Exception) {
                        runCatching {
                            //it.interrupt()
                            it.stop()
                        }
                    }
                }
            }
            sleep(1000*30)
        }
    }
}
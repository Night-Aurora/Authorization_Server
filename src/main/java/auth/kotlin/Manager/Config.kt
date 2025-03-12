package auth.kotlin.Manager


import java.io.*

class Config {
    private val SINFO_FILE = "SInfo.txt"
    private val CONFIG_PATH = "Config/"
    private val HWID_LIST = CONFIG_PATH + "HWIDs.txt"
    private val CONFIG = CONFIG_PATH + "Config.txt"
    private val BLACKLISTIP = CONFIG_PATH + "BlackListIP.txt"
    private var HWID_FILE:File? = null
    private var CONFIG_FILE:File? = null
    private var BLACKLISTIP_File:File? = null
    lateinit var Hwids:Hwids
    lateinit var user:Users
    lateinit var blackListIP: BlackListIP
    init {
        HWID_FILE = File(HWID_LIST)
        CONFIG_FILE = File(CONFIG)
        BLACKLISTIP_File = File(BLACKLISTIP)
        isExists(HWID_FILE!!)
        isExists(CONFIG_FILE!!)
        isExists(BLACKLISTIP_File!!)
    }

    fun initS(): Int {
        val file = File(SINFO_FILE)
        if(!file.exists()) file.createNewFile()
        val r = BufferedReader(FileReader(file))
        return r.readLine().toInt()
    }

    fun getBlackListIP(){
        val reader = BufferedReader(FileReader(BLACKLISTIP_File!!))
        blackListIP.blackListIP.clear()
        reader.readLines().filter(String::isNotEmpty).forEach {
            blackListIP.blackListIP.add(it)
        }
    }

    fun saveBlackListIP(){
        isExists(BLACKLISTIP_File!!)
        val write = BufferedWriter(FileWriter(BLACKLISTIP_File!!))
        blackListIP.blackListIP.forEach{
            write.write(it + "\n")
        }
        write.close()
    }

    fun getHWIDs(){
        val reader = BufferedReader(FileReader(HWID_FILE!!))
        var byte:String?
        Hwids.HwidList.clear()
        while (reader.readLine().also { byte = it } != null){
            val info = byte!!.split(":")
            if(info.count()>1) {
                val user = info[0]
                val hwid = info[1]
                Hwids.addHwid(hwid, user)
            }
        }
        reader.close()
    }

    fun saveHWIDs(){
        isExists(HWID_FILE!!)
        val writer = BufferedWriter(FileWriter(HWID_FILE!!))
        Hwids.HwidList.forEach{
            writer.write("${it.user}:${it.hwid}" + "\n")
        }
        writer.close()
    }

    fun saveConfig() {
        isExists(CONFIG_FILE!!)
        val writer = BufferedWriter(FileWriter(CONFIG_FILE!!))
        user.users.forEach{
            writer.write("${it.user}:${it.password}" + "\n")
        }
        writer.close()
    }

    fun getConfig(){
        isExists(CONFIG_FILE!!)
        val reader = BufferedReader(FileReader(CONFIG_FILE!!))
        var byte:String?
//        user.users.clear()
        while (reader.readLine().also { byte = it } != null){
            val info = byte!!.split(":")
            if(info.count()>1) {
                val user = info[0]
                val password = info[1]
                this.user.users.add(Users.User(user,password))
            }
        }
        reader.close()
    }

    fun isExists(file: File){
        if(!file.exists()){
            file.parentFile.mkdirs()
            try {
                file.createNewFile()
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }
}
package auth.kotlin

object EcTransfer {

    //byteArrayToHex 转 16进
    fun byteArrayToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { String.format("%02x", it) }.uppercase()
    }
    //16进制转 ByteArray
    fun hexToByteArray(hex: String): ByteArray {
        if(hex.isEmpty()) return byteArrayOf()
        val len = hex.length
        val data = ByteArray(len / 2)
        for (i in 0 until len step 2) {
            data[i / 2] = ((Character.digit(hex[i], 16) shl 4) + Character.digit(hex[i + 1], 16)).toByte()
        }
        return data
    }
}
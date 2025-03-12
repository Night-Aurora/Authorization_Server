import auth.kotlin.ECDSA.generatePublicKey
import auth.kotlin.ECDSA.verifySignature
import auth.kotlin.EcTransfer
import auth.kotlin.EcTransfer.hexToByteArray
import auth.kotlin.RSA.decryptWithPrivateKey
import auth.kotlin.RSA.generateKeyPair
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.nio.ByteBuffer
import java.security.PublicKey

object Client {
    val public:String = ""
    lateinit var publicKey:ByteArray
    lateinit var privateKey:ByteArray
    lateinit var AESKey:ByteArray
    lateinit var ServerPublicKey: PublicKey
    @JvmStatic
    fun main(args: Array<String>) {
        val map = generateKeyPair()
        publicKey = map["public"]!!
        privateKey = map["private"]!!
        println(publicKey.size)

        val socket = Socket("127.0.0.1", 25513)
        while (!socket.isConnected) continue


        ServerPublicKey = generatePublicKey(hexToByteArray(public))

        val output = DataOutputStream(socket.getOutputStream())
        output.writeInt(publicKey.size) // public key length
        output.flush()
        output.write(publicKey)
        output.flush()

        val input = DataInputStream(socket.getInputStream())

        val encryptedMessage = ByteArray(input.readInt()).also { input.read(it) }
        //    len    ||   AES    || sign
        //[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
        // 0  1  2  3  4  5  6  7  8  9
        val length = ByteBuffer.wrap(encryptedMessage.take(4).toByteArray()).int//lengthOfEncryptedAESKey
        val encryptedAESKey = encryptedMessage.copyOfRange(4,4 + length)//AESKey
        val signature = encryptedMessage.copyOfRange(4 + encryptedAESKey.size, encryptedMessage.size)//Signature

        
        if(encryptedAESKey.isNotEmpty() && signature.isNotEmpty() && verifySignature(encryptedAESKey, signature, ServerPublicKey)){
            AESKey = decryptWithPrivateKey(encryptedAESKey, privateKey)
            println("AESKey: ${EcTransfer.byteArrayToHex(AESKey)}")
            socket.close()
        }
    }
}
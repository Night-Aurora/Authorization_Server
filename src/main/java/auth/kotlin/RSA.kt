package auth.kotlin

import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

object RSA {
    val RSA_Algorithm = "RSA"

    @JvmStatic
    fun main(args: Array<String>) {
        val keyPair = generateKeyPair()
        val public = keyPair["public"] as ByteArray
        val private = keyPair["private"] as ByteArray

        val publicHex = EcTransfer.byteArrayToHex(public)
        val privateHex = EcTransfer.byteArrayToHex(private)

        println("Size of Byte: PublicKey:${public.size} PrivateKey:${private.size}")
        println("Size of Hex: PublicKey:${publicHex.toByteArray().size} PrivateKey:${privateHex.toByteArray().size}")
    }

    fun generateKeyPair(): Map<String,ByteArray> {
        val keyP = KeyPairGenerator.getInstance(RSA_Algorithm)
        keyP.initialize(1024)
        val keyPair = keyP.genKeyPair()
        val publicKey = keyPair.public.encoded
        val privateKey = keyPair.private.encoded
        val map = hashMapOf<String,ByteArray>()
        map["public"] = publicKey
        map["private"] = privateKey
        return map
    }

    fun encryptWithPublicKey(content: ByteArray, key:ByteArray): ByteArray {
        val x509 = X509EncodedKeySpec(key)
        val kf = KeyFactory.getInstance(RSA_Algorithm)
        val publicKey = kf.generatePublic(x509)

        val cipher = Cipher.getInstance(RSA_Algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(content)
    }

    fun decryptWithPrivateKey(content: ByteArray, key:ByteArray): ByteArray {
        val pkcS8EncodedKeySpec = PKCS8EncodedKeySpec(key)
        val kf = KeyFactory.getInstance(RSA_Algorithm)
        val privateKey = kf.generatePrivate(pkcS8EncodedKeySpec)

        val cipher = Cipher.getInstance(RSA_Algorithm)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return cipher.doFinal(content)
    }

    fun encryptWithPrivateKey(content: ByteArray, key:ByteArray): ByteArray {
        val pkcS8EncodedKeySpec = PKCS8EncodedKeySpec(key)
        val kf = KeyFactory.getInstance(RSA_Algorithm)
        val privateKey = kf.generatePrivate(pkcS8EncodedKeySpec)

        val cipher = Cipher.getInstance(RSA_Algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, privateKey)
        return cipher.doFinal(content)
    }

    fun decryptWithPublicKey(content: ByteArray, key:ByteArray): ByteArray {
        val x509 = X509EncodedKeySpec(key)
        val kf = KeyFactory.getInstance(RSA_Algorithm)
        val publicKey = kf.generatePublic(x509)

        val cipher = Cipher.getInstance(RSA_Algorithm)
        cipher.init(Cipher.DECRYPT_MODE, publicKey)
        return cipher.doFinal(content)
    }

}
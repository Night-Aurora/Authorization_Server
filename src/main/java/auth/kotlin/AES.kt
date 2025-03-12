package auth.kotlin

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

object AES {
    private const val AES_ALGORITHM = "AES/CBC/PKCS5Padding"
    fun generateKey(key:ByteArray): SecretKey {
        val keygen = KeyGenerator.getInstance("AES")
        keygen.init(256, SecureRandom(key))
        return keygen.generateKey()
    }
    fun generateRandomKey(): ByteArray {
        val key = KeyGenerator.getInstance("AES")
        key.init(256, SecureRandom())
        return key.generateKey().encoded
    }

    fun encrypt(contain: ByteArray, key: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(AES_ALGORITHM)

        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)
        val ivParameterSpec = IvParameterSpec(iv)

        cipher.init(Cipher.ENCRYPT_MODE, generateKey(key), ivParameterSpec)
        return iv + cipher.doFinal(contain)
    }

    fun decrypt(contain: ByteArray, key: ByteArray): ByteArray {
        val iv = contain.copyOfRange(0, 16)
        val encryptedBytes = contain.copyOfRange(16, contain.size)

        val cipher = Cipher.getInstance(AES_ALGORITHM)
        val ivParameterSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, generateKey(key), ivParameterSpec)
        return cipher.doFinal(encryptedBytes)
    }
}
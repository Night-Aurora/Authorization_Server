package auth.kotlin

import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

object ECDSA {
    fun generateECDSAKeyPair(): KeyPair {
        val ecdsaKeyPair = KeyPairGenerator.getInstance("EC")
        ecdsaKeyPair.initialize(256, SecureRandom())
        return ecdsaKeyPair.generateKeyPair()
    }

    fun generatePublicKey(publicKey: ByteArray): PublicKey {
        val x509EncodedKeySpec = X509EncodedKeySpec(publicKey)
        val keyFactory = KeyFactory.getInstance("EC")
        return keyFactory.generatePublic(x509EncodedKeySpec)
    }

    fun generatePrivateKey(privateKey: ByteArray): PrivateKey {
        val pkcS8EncodedKeySpec = PKCS8EncodedKeySpec(privateKey)
        val KeyFactory = KeyFactory.getInstance("EC")
        return KeyFactory.generatePrivate(pkcS8EncodedKeySpec)
    }

    fun signData(data:ByteArray,privateKey: PrivateKey):ByteArray{
        val signature = Signature.getInstance("SHA256withECDSA")
        signature.initSign(privateKey)
        signature.update(data)
        return signature.sign()
    }

    fun verifySignature(data:ByteArray,signatureStr:ByteArray, publicKey: PublicKey):Boolean{
        val signature = Signature.getInstance("SHA256withECDSA")
        signature.initVerify(publicKey)
        signature.update(data)
        return signature.verify(signatureStr)
    }
}
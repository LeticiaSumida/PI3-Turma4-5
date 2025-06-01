package br.edu.puc.superid
import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object CriptoAES {

    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"
    private const val SECRET_KEY = "1234567890123456" // Exatamente 16 caracteres

    private fun getKey(): SecretKeySpec {
        return SecretKeySpec(SECRET_KEY.toByteArray(Charsets.UTF_8), ALGORITHM)
    }

    fun criptografar(texto: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        val bytesCriptografados = cipher.doFinal(texto.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(bytesCriptografados, Base64.DEFAULT).trim()
    }

    fun descriptografar(textoCriptografado: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, getKey())
        val bytesDecodificados = Base64.decode(textoCriptografado, Base64.DEFAULT)
        val bytesDescriptografados = cipher.doFinal(bytesDecodificados)
        return String(bytesDescriptografados, Charsets.UTF_8)
    }
}

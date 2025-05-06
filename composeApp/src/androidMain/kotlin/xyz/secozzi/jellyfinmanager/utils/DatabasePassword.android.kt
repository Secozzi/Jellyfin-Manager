package xyz.secozzi.jellyfinmanager.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import org.koin.java.KoinJavaComponent.inject
import xyz.secozzi.jellyfinmanager.preferences.BasePreferences
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.CharBuffer
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

// From https://github.com/jobobby04/TachiyomiSY
@OptIn(ExperimentalEncodingApi::class)
actual object DatabasePassword {
    private val generalPreferences by inject<BasePreferences>(BasePreferences::class.java)
    private val keyStore = KeyStore.getInstance(Keystore).apply {
        load(null)
    }

    actual fun getDatabasePassword(): ByteArray {
        val encrypted = generalPreferences.sqlPassword.get().ifBlank {
            generateAndEncryptSqlPw()
        }
        return decrypt(encrypted, AliasSql)
    }

    private val encryptionCipherSql
        get() = Cipher.getInstance(CryptoSettings).apply {
            init(
                Cipher.ENCRYPT_MODE,
                getKey(AliasSql),
            )
        }

    private fun getDecryptCipher(iv: ByteArray, alias: String): Cipher {
        return Cipher.getInstance(CryptoSettings).apply {
            init(
                Cipher.DECRYPT_MODE,
                getKey(alias),
                IvParameterSpec(iv),
            )
        }
    }

    private fun getKey(alias: String): SecretKey {
        val loadedKey = keyStore.getEntry(alias, null) as? KeyStore.SecretKeyEntry
        return loadedKey?.secretKey ?: generateKey(alias)
    }

    private fun generateKey(alias: String): SecretKey {
        return KeyGenerator.getInstance(Algorithm).apply {
            init(
                KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setKeySize(KeySize)
                    .setBlockModes(BlockMode)
                    .setEncryptionPaddings(Padding)
                    .setRandomizedEncryptionRequired(true)
                    .setUserAuthenticationRequired(false)
                    .build(),
            )
        }.generateKey()
    }

    private fun encrypt(password: ByteArray, cipher: Cipher): String {
        val outputStream = ByteArrayOutputStream()
        outputStream.use { output ->
            output.write(cipher.iv)
            ByteArrayInputStream(password).use { input ->
                val buffer = ByteArray(BufferSize)
                while (input.available() > BufferSize) {
                    input.read(buffer)
                    output.write(cipher.update(buffer))
                }
                output.write(cipher.doFinal(input.readBytes()))
            }
        }

        return Base64.Default.encode(outputStream.toByteArray())
    }

    private fun decrypt(encryptedPassword: String, alias: String): ByteArray {
        val inputStream = Base64.Default.decode(encryptedPassword).inputStream()
        return inputStream.use { input ->
            val iv = ByteArray(IvSize)
            input.read(iv)
            val cipher = getDecryptCipher(iv, alias)

            ByteArrayOutputStreamPassword().use { output ->
                val buffer = ByteArray(BufferSize)
                while (inputStream.available() > BufferSize) {
                    inputStream.read(buffer)
                    output.write(cipher.update(buffer))
                }
                output.write(cipher.doFinal(inputStream.readBytes()))
                output.toByteArray().also {
                    output.clear()
                }
            }
        }
    }

    private fun generateAndEncryptSqlPw(): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val passwordArray = CharArray(SqlPasswordLength)
        for (i in 0..<SqlPasswordLength) {
            passwordArray[i] = charPool[SecureRandom().nextInt(charPool.size)]
        }
        val passwordBuffer = Charsets.UTF_8.encode(CharBuffer.wrap(passwordArray))
        val passwordBytes = ByteArray(passwordBuffer.limit())
        passwordBuffer.get(passwordBytes)
        val encrypted = encrypt(passwordBytes, encryptionCipherSql)
        generalPreferences.sqlPassword.set(encrypted)
            .also {
                passwordArray.fill('#')
                passwordBuffer.array().fill('#'.code.toByte())
                passwordBytes.fill('#'.code.toByte())
            }
        return encrypted
    }
}

private const val BufferSize = 2048
private const val KeySize = 256
private const val IvSize = 16

private const val Algorithm = KeyProperties.KEY_ALGORITHM_AES
private const val BlockMode = KeyProperties.BLOCK_MODE_CBC
private const val Padding = KeyProperties.ENCRYPTION_PADDING_PKCS7
private const val CryptoSettings = "$Algorithm/$BlockMode/$Padding"

private const val Keystore = "AndroidKeyStore"
private const val AliasSql = "sqlPw"

private const val SqlPasswordLength = 32

private class ByteArrayOutputStreamPassword : ByteArrayOutputStream() {
    fun clear() {
        this.buf.fill('#'.code.toByte())
    }
}

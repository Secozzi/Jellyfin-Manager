package xyz.secozzi.jellyfinmanager.utils

import org.koin.java.KoinJavaComponent.inject
import xyz.secozzi.jellyfinmanager.preferences.BasePreferences
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.random.Random

@OptIn(ExperimentalEncodingApi::class)
actual object DatabasePassword {
    private val generalPreferences by inject<BasePreferences>(BasePreferences::class.java)

    actual fun getDatabasePassword(): ByteArray {
        var encryptedPassword = generalPreferences.sqlPassword.get()
        if (encryptedPassword.isBlank()) {
            val password = getRandomString().encrypt(KEY_PASSWORD)
            generalPreferences.sqlPassword.set(password)
            encryptedPassword = password
        }

        return encryptedPassword.decrypt(KEY_PASSWORD).toByteArray()
    }

    private fun getRandomString(): String {
        val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..32)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    private fun String.encrypt(password: String): String {
        val salt = Random.nextBytes(SALT_LENGTH)
        val iv = Random.nextBytes(IV_LENGTH)
        val secretKey = generateSecretKey(password, salt)

        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))

        val encryptedValue = cipher.doFinal(this.toByteArray())
        return Base64.Default.encode(salt + iv + encryptedValue)
    }

    private fun String.decrypt(password: String): String {
        val decodedValue = Base64.Default.decode(this)

        val salt = decodedValue.slice(0 until SALT_LENGTH).toByteArray()
        val iv = decodedValue.slice(SALT_LENGTH until SALT_LENGTH + IV_LENGTH).toByteArray()
        val encrypted = decodedValue.slice(SALT_LENGTH + IV_LENGTH until decodedValue.size).toByteArray()
        val secretKey = generateSecretKey(password, salt)

        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))

        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted)
    }

    private fun generateSecretKey(password: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance(KEY_ALGORITHM)
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val key = factory.generateSecret(spec)
        return SecretKeySpec(key.encoded, "AES")
    }

    // Obviously not secure but whatever, this isn't a bank app
    private const val KEY_PASSWORD = "jellyfin-manager-key-password"

    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val KEY_ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val ITERATIONS = 4096
    private const val KEY_LENGTH = 256
    private const val SALT_LENGTH = 16
    private const val IV_LENGTH = 16
}

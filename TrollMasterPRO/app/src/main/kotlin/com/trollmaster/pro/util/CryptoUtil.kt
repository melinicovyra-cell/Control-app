package com.trollmaster.pro.util

import java.security.MessageDigest

object CryptoUtil {

    fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    // VIP key stored obfuscated (XOR 0x42)
    private val vipKeyObf = byteArrayOf(11, 96, 56, 106, 112, 7, 30, 35, 57, 99, 31, 63, 114, 9, 107, 52)
    private val vipHashExpected = "10e1fdd40d4552dde84744ddd29ae02707097aa585844ce9722c7598b9f1496c"

    // ADMIN key stored obfuscated (XOR 0x42)
    private val admKeyObf = byteArrayOf(7, 38, 47, 115, 44, 29, 26, 123, 80, 22, 99, 18, 80, 45)
    private val admHashExpected = "2c4e1108c1bc299466d48b0ada9a3f7f12d9ef6f60f357b0eb8e94f4e0a4c517"

    private fun deobfuscate(obf: ByteArray): String {
        return obf.map { (it.toInt() xor 0x42).toChar() }.joinToString("")
    }

    fun verifyVipKey(input: String): Boolean {
        val hash = sha256(input)
        return hash == vipHashExpected
    }

    fun verifyAdminKey(input: String): Boolean {
        val hash = sha256(input)
        return hash == admHashExpected
    }

    fun getVipHash(userInput: String): String {
        return sha256(userInput)
    }

    fun getAdmHash(userInput: String): String {
        return sha256(userInput)
    }
}

package com.razorpay.library

import android.content.Context
import com.razorpay.library.utils.PrefManager
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object VerifySignatureUtils {

    fun isSignatureVerify(
        razorpayOrderId: String?,
        razorpayPaymentId: String?,
        razorpaySignature: String?,
        context: Context?
    ): Boolean {
        val data = "$razorpayOrderId|$razorpayPaymentId"
            val generatedSignature = hmacWithJava("HmacSHA256", data, PrefManager.getInstance(context).getStringValue("key_secret"))
        return generatedSignature.contentEquals(razorpaySignature)
    }

    private fun hmacWithJava(algorithm: String?, data: String, razorpayTestSecret: String): String {
        val secretKeySpec = SecretKeySpec(razorpayTestSecret.toByteArray(), algorithm)
        val mac = Mac.getInstance(algorithm)
        mac.init(secretKeySpec)
        return bytesToHex(mac.doFinal(data.toByteArray()))
    }

    private fun bytesToHex(hash: ByteArray): String {
        val hexString = StringBuilder(2 * hash.size)
        for (h in hash) {
            val hex = Integer.toHexString(0xff and h.toInt())
            if (hex.length == 1) hexString.append('0')
            hexString.append(hex)
        }
        return hexString.toString()
    }

}
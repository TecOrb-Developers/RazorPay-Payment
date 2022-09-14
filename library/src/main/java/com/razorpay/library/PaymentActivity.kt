package com.razorpay.library

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.razorpay.library.utils.PrefManager
import org.json.JSONObject

class PaymentActivity : AppCompatActivity(), PaymentResultWithDataListener {
    private var name: String? = null
    private var description: String? = null
    private var amount: Long? = null
    private var email: String? = null
    private var mobile: String? = null
    private var address: String? = null
    private var currency: String? = null
    private var orderId: String? = null
    private var receipt: String? = null
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        context = this

        intent?.let {

            orderId = it.getStringExtra("order_id")
            receipt = it.getStringExtra("receipt")
            currency = it.getStringExtra("currency")
            name = it.getStringExtra("name")
            description = it.getStringExtra("description")
            amount = it.getLongExtra("amount", 0L)
            email = it.getStringExtra("email")
            mobile = it.getStringExtra("mobile")
            address = it.getStringExtra("address")
            PrefManager.getInstance(context).setStringValue("api_key", it.getStringExtra("api_key"))
            PrefManager.getInstance(context)
                .setStringValue("key_secret", it.getStringExtra("key_secret"))

            initRazorpay(
                name,
                description,
                orderId,
                receipt,
                amount,
                email,
                mobile,
                address,
                currency
            )
        }
    }

    private fun initRazorpay(
        fullName: String?,
        description: String?,
        razorPayOrderId: String?,
        receipt: String?,
        amount: Long?,
        email: String?,
        mobile: String?,
        fullAddress: String?,
        currency: String?
    ) {
        val activity: PaymentActivity? = this
        val co = Checkout()
        co.setKeyID(PrefManager.getInstance(context).getStringValue("api_key"))
        try {

            val options = JSONObject()
            options.put("name", fullName)
            options.put("description", description)
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("currency", currency)
            options.put(
                "order_id",
                razorPayOrderId
            ) //First you have to get razorpayOrderId using razorpay API
            options.put("amount", amount)

            val preFill = JSONObject()
            preFill.put("email", email)
            preFill.put("contact", mobile)

            val noTes = JSONObject()
            noTes.put("address", fullAddress)
            noTes.put("merchant_order_id", receipt)
            options.put("prefill", preFill)
            options.put("notes", noTes)
            co.open(activity, options)
        } catch (e: Exception) {
            Log.i("tag", "msg=====" + e.message)
            e.printStackTrace()
        }

    }

    override fun onPaymentSuccess(s: String?, p1: PaymentData?) {

        try {

            Log.d("Payment", "call onPaymentSuccess() on PaymentActivity")
            var razorpaySignature: String?
            var razorpayPaymentId: String?
            var razorpayOrderId: String?

            p1?.let {
                razorpaySignature = it.signature
                razorpayPaymentId = it.paymentId
                razorpayOrderId = it.orderId
                Log.d(
                    "Razorpay",
                    "Success:$s\nPayId:$razorpayPaymentId  OrderId:$razorpayOrderId   Signature:$razorpaySignature"
                )

                if (VerifySignatureUtils.isSignatureVerify(
                        razorpayOrderId, razorpayPaymentId, razorpaySignature, context
                    )
                ) {
                    OpenPaymentController.setSuccess(s, p1, true)
                    Log.d("Razorpay", "Success:$s")
                    finish()
                } else {
                    OpenPaymentController.setSuccess(s, p1, false)
                }
            }


        } catch (e: Exception) {
            e.printStackTrace();
            Log.d("Razorpay", "Exception:" + e.message);
        }
    }

    override fun onPaymentError(p0: Int, s: String?, p2: PaymentData?) {

        Log.d("Payment", "call onPaymentError() on PaymentActivity")

        try {
            OpenPaymentController.setFailed(p0, s, p2)
            finish()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        Checkout.clearUserData(this)
    }

}
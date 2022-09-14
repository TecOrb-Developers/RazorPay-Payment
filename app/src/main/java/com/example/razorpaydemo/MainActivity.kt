package com.example.razorpaydemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.razorpaydemo.models.ModelCreateOrder
import com.example.razorpaydemo.models.ModelPaymentError
import com.example.razorpaydemo.retrofit.RetrofitClient
import com.example.razorpaydemo.utils.AppConstants
import com.example.razorpaydemo.utils.NetworkUtils
import com.google.gson.JsonObject
import com.razorpay.PaymentData
import com.razorpay.library.OpenPaymentController
import id.ionbit.ionalert.IonAlert
import okhttp3.Credentials
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), OpenPaymentController.PaymentCallBack {
    private lateinit var context: Context
    private var invoiceID: String? = null
    private var amount: String? = null
    private var currency: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        context = this

        findViewById<Button>(R.id.btnPay).setOnClickListener {
            if (isValidate()) {
                if (NetworkUtils.isNetworkAvailable()) {
                    callApiRazorpayOrderId(
                        AppConstants.BASE_URL,
                        findViewById<EditText>(R.id.editTextInvoiceId).text.toString(),
                        findViewById<EditText>(R.id.editTextAmount).text.toString(),
                        "INR"
                    )
                } else {
                    IonAlert(context, IonAlert.WARNING_TYPE)
                        .setContentText("Oops! No internet connection found. Please try again!")
                        .setConfirmText("OK")
                        .setConfirmClickListener { sDialog -> sDialog.dismissWithAnimation() }
                        .show()
                }
            }
        }
    }

    private fun isValidate(): Boolean {
        invoiceID = findViewById<EditText>(R.id.editTextInvoiceId).text.toString()
        amount = findViewById<EditText>(R.id.editTextAmount).text.toString()
        currency = "INR"
        if (TextUtils.isEmpty(amount)) {
            IonAlert(this, IonAlert.WARNING_TYPE)
                .setContentText("Amount can't be blank!")
                .setConfirmText("OK")
                .setConfirmClickListener { sDialog -> sDialog.dismissWithAnimation() }
                .show()
            return false
        } else if (TextUtils.isEmpty(invoiceID)) {
            IonAlert(this, IonAlert.WARNING_TYPE)
                .setContentText("Invoice ID can't be blank!")
                .setConfirmText("OK")
                .setConfirmClickListener { sDialog -> sDialog.dismissWithAnimation() }
                .show()
            return false
        } else if (TextUtils.isEmpty(currency)) {
            IonAlert(this, IonAlert.WARNING_TYPE)
                .setContentText("Currency can't be blank!")
                .setConfirmText("OK")
                .setConfirmClickListener { sDialog -> sDialog.dismissWithAnimation() }
                .show()
            return false
        }
        return true
    }

    private fun callApiRazorpayOrderId(
        url: String?,
        invoiceId: String?,
        amount: String?,
        currency: String?
    ) {
        val credentials =
            Credentials.basic(AppConstants.RAZORPAY_TEST_KEY, AppConstants.RAZORPAY_TEST_SECRET)
        val amountDouble = amount!!.toDouble()
        val amountINR = amountDouble * 100
        val finalAmount = amountINR.toLong()
        val obj = JsonObject()
        obj.addProperty("amount", finalAmount)
        obj.addProperty("currency", currency)
        obj.addProperty("receipt", invoiceId)
        RetrofitClient.create()
            .getRazorPayOrderId(credentials!!, url!!, obj)
            .enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    try {
                        if (response.isSuccessful) {
                            if (response.body() != null) {

                                val model = response.body()
                                val finalResponse: String =
                                    MyApplication.getInstance().gson.toJson(model)
                                val razorpayOrderResponse =
                                    MyApplication.getInstance().gson.fromJson(
                                        finalResponse,
                                        ModelCreateOrder::class.java
                                    )
                                razorpayOrderResponse?.let {
                                    val razorpayOrderId = it.id
                                    val amountPaid = it.amount
                                    val currencyINR = it.currency
                                    val receipt = it.receipt
                                    callPaymentController(
                                        razorpayOrderId,
                                        receipt,
                                        amountPaid,
                                        currencyINR
                                    )

                                }
                            }
                        } else if (response.code() == 500) {
                            IonAlert(context, IonAlert.ERROR_TYPE)
                                .setContentText("Oops! Server Error found please wait and try again")
                                .setConfirmText("OK")
                                .setConfirmClickListener { sDialog -> sDialog.dismissWithAnimation() }
                                .show()
                        } else if (response.errorBody() != null) {
                            try {
                                val jsonObject = JSONObject(response.errorBody()!!.string())
                                val jsonObjPrice = jsonObject.getJSONObject("error")
                                val desc = jsonObjPrice.getString("description")
                                val code = jsonObjPrice.getString("code")

                                IonAlert(context, IonAlert.ERROR_TYPE)
                                    .setContentText("$code:$desc")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener { sDialog -> sDialog.dismissWithAnimation() }
                                    .show()

                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun callPaymentController(
        razorpayOrderId: String?,
        receipt: String?,
        amountPaid: Long?,
        currencyINR: String?
    ) {
        val intent = Intent()
        intent.putExtra("order_id", razorpayOrderId)
        intent.putExtra("receipt", receipt)
        intent.putExtra("currency", currencyINR)
        intent.putExtra("name", "Merchant Name")
        intent.putExtra("description", "Product short description")
        intent.putExtra("amount", amountPaid)
        intent.putExtra("email", "")
        intent.putExtra("mobile", "")
        intent.putExtra("address", "")
        intent.putExtra("api_key", AppConstants.RAZORPAY_TEST_KEY)
        intent.putExtra("key_secret", AppConstants.RAZORPAY_TEST_SECRET)

        OpenPaymentController(this, intent, context)
    }

    override fun success(s: String?, paymentData: PaymentData?, isSignatureVerified: Boolean) {
        if (isSignatureVerified) {
            IonAlert(context, IonAlert.SUCCESS_TYPE)
                .setContentText("Signature verified and payment has been done.\nPayment ID => ${paymentData?.paymentId}\nOrder ID => ${paymentData?.orderId}")
                .setConfirmText("OK")
                .setConfirmClickListener { sDialog ->
                    sDialog.dismissWithAnimation()
                }
                .show()
        } else
            Toast.makeText(
                this,
                "Signature not verified and payment is under process.",
                Toast.LENGTH_LONG
            ).show()
        Log.d("=>$s", "=>$paymentData")
    }

    override fun failed(p0: Int, s: String?, p2: PaymentData?) {
        val paymentError = MyApplication.getInstance().gson?.fromJson(s, ModelPaymentError::class.java)

        IonAlert(context, IonAlert.ERROR_TYPE)
            .setContentText("${paymentError?.let { it.error.description }}")
            .setConfirmText("OK")
            .setConfirmClickListener { sDialog -> sDialog.dismissWithAnimation() }
            .show()
        Log.d("=>$s", "=>$p2")
    }
}
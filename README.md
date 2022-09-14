# RazorPay-Payment

![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)
[![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=23)


This is a simple library that helps you integrate the Razorpay Payment Gateway into your apps.

## Features

- You can payment through this library using card, online banking, UPI and wallets.
- You can get onSuccessPayment callback on your activity of main project.
- You can get onFailedPayment callback on your activity of main project.

# How it works:

1. Gradle Dependency

- Add the JitPack repository to your project's build.gradle file

```groovy
    allprojects {
        repositories {
            ...
    	    maven { url 'https://jitpack.io' }
        }
    }
```
- Add the dependency in your app's build.gradle file

```groovy
    dependencies {
        implementation 'com.github.TecOrb-Developers:RazorPay-Payment:1.0'
    }
```

2. Add permission 
```
 Manifest.permission.INTERNET
```

3. Firstly, you have to add razorpay payment gateway dependency in your main project.
```
 implementation 'com.razorpay:checkout:1.6.20'
```
4. How to get razorpayOrder ID?

 There are two type to get the razorpayOrderId which are as follows:

- To use razorpay create order api in your project using razor pay server url
- To use razorpay create order api in your own server as per your project requirement

## To use razorpay create order api in your project using razor pay server url

We will use the below params to create the order in razorpay server.

    • API Url ==> https://api.razorpay.com/v1/orders
    • Method ==> POST
    • Invoice Id ==> This id can be your card id or your invoic id etc.
    • Amount ==> Amount which you want to pay
    • Curency ==> Your currency like “INR” (Indian Currency)
   
```
callApiRazorpayOrderId(
    AppConstants.BASE_URL,
    invoiceID,
    amount,
    "INR"
)
```
5. You have to mention api url, razorpay_key and razorpay_key_secret into your project in AppConstants util class like below
```
object AppConstants {
    const val BASE_URL = "https://api.razorpay.com/v1/orders/"

    // razorpay test key (project)
    const val RAZORPAY_TEST_KEY = "RAZORPAY_TEST_KEY" //getting from razorpay account
    const val RAZORPAY_TEST_SECRET = "RAZORPAY_TEST_SECRET" // getting from razorpay account
}
```
6. If you have to pay Rs. 50 then you have to send Rs. 50*100=5000 Paisa in amount field. This api response is below in json format 
```
      {
        "id": "order_DaZlswtdcn9UNV",
        "entity": "order",
        "amount": 50,
        "amount_paid": 0,
        "amount_due": 50,
        "currency": "INR",
        "receipt": "Receipt #20",
        "status": "created",
        "attempts": 0,
        "notes": {
          "key1": "value1",
          "key2": "value2",
          "created_at": 1572502745
        }
      }
```

7. After getting the value of id, amount, currency and receipt from this response and include the other necessary params like name, description, email, mobile, address, razorpay key and razorpay ket secret using intent, you have to open below class.
```
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
```

8. After that you have to implement PaymentCallback from OpenPaymentController class on your project activity. After implement the PaymentCallback you can handle the success and failed event like below
```
              override fun success(s: String?, paymentData: PaymentData?, isSignatureVerified: Boolean) {
                  if (isSignatureVerified) {
                      IonAlert(context, IonAlert.SUCCESS_TYPE)
                          .setContentText("Signature verified and payment has been done.\nPayment ID => ${paymentData?.paymentId}\n
                          Order ID => ${paymentData?.orderId}")
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
              }
```

If isSignatureVerified is true in success() method then your payment has been successfully verified and completed.
 
# Developers

MIT License

Copyright (c) 2019 TecOrb Technologies

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

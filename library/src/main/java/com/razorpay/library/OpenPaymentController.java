package com.razorpay.library;

import android.content.Context;
import android.content.Intent;

import com.razorpay.PaymentData;

public class OpenPaymentController {
    private static PaymentCallBack callBack;

    public OpenPaymentController(PaymentCallBack callBack, Intent intent, Context context) {
        OpenPaymentController.callBack = callBack;
        Intent intent1 = new Intent(context, PaymentActivity.class);

        intent1.putExtra("order_id", intent.getStringExtra("order_id"));
        intent1.putExtra("receipt", intent.getStringExtra("receipt"));
        intent1.putExtra("currency", intent.getStringExtra("currency"));
        intent1.putExtra("name", intent.getStringExtra("name"));
        intent1.putExtra("description", intent.getStringExtra("description"));
        intent1.putExtra("amount", intent.getLongExtra("amount",0L));
        intent1.putExtra("email", intent.getStringExtra("email"));
        intent1.putExtra("mobile", intent.getStringExtra("mobile"));
        intent1.putExtra("address", intent.getStringExtra("address"));
        intent1.putExtra("api_key", intent.getStringExtra("api_key"));
        intent1.putExtra("key_secret", intent.getStringExtra("key_secret"));

        context.startActivity(intent1);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

    }

    public static void setSuccess(String s, PaymentData paymentData, boolean isSignatureVerified) {
        if (callBack != null) callBack.success(s, paymentData, isSignatureVerified);
    }

    public static void setFailed(int p0, String s, PaymentData p2) {
        if (callBack != null) callBack.failed(p0, s, p2);
    }

    public interface PaymentCallBack {
        void success(String s, PaymentData paymentData, boolean isSignatureVerified);

        void failed(int p0, String s, PaymentData p2);
    }
}

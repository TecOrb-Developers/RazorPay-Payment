package com.example.razorpaydemo.models

data class ModelPaymentError(
    val error: Error
) {
    data class Error(
        val code: String, // BAD_REQUEST_ERROR
        val description: String, // Payment processing cancelled by user
        val reason: String, // payment_cancelled
        val source: String, // customer
        val step: String // payment_authentication
    )
}
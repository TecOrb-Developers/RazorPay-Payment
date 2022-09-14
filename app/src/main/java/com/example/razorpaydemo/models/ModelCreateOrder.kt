package com.example.razorpaydemo.models

data class ModelCreateOrder(
    val amount: Long,
    val amount_due: Long,
    val amount_paid: Long,
    val attempts: Int,
    val created_at: Int,
    val currency: String,
    val entity: String,
    val id: String,
    //val notes: List<Any>,
    val offer_id: Any,
    val receipt: String,
    val status: String
)
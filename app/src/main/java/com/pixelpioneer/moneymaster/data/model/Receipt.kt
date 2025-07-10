// app/src/main/java/com/pixelpioneer/moneymaster/data/model/Receipt.kt
package com.pixelpioneer.moneymaster.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Receipt(
    val storeName: String?,
    val date: String?,
    val items: List<ReceiptItem>
) : Parcelable {
    val totalAmount: Double
        get() = items.sumOf { it.price }
}

@Parcelize
data class ReceiptItem(
    val name: String,
    val price: Double
) : Parcelable
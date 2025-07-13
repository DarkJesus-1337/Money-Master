package com.pixelpioneer.moneymaster.data.sample

import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.data.enums.BudgetPeriod
import com.pixelpioneer.moneymaster.data.model.*
import android.graphics.Color
import androidx.core.graphics.toColorInt

object SampleData {
    
    // Sample Transaction Categories
    val sampleCategories = listOf(
        TransactionCategory(
            id = 1,
            name = "Lebensmittel",
            color = "#4CAF50".toColorInt(),
            icon = R.drawable.ic_food
        ),
        TransactionCategory(
            id = 2,
            name = "Transport",
            color = "#2196F3".toColorInt(),
            icon = R.drawable.ic_transport
        ),
        TransactionCategory(
            id = 3,
            name = "Unterhaltung",
            color = Color.parseColor("#FF9800"),
            icon = R.drawable.ic_entertainment
        ),
        TransactionCategory(
            id = 4,
            name = "Gesundheit",
            color = Color.parseColor("#E91E63"),
            icon = R.drawable.ic_heart
        ),
        TransactionCategory(
            id = 5,
            name = "Bildung",
            color = Color.parseColor("#9C27B0"),
            icon = R.drawable.ic_school
        )
    )
    
    // Sample Transactions
    val sampleTransactions = listOf(
        Transaction(
            id = 1,
            amount = 45.67,
            title = "Supermarkt Einkauf",
            description = "Wocheneinkauf bei Rewe",
            category = sampleCategories[0],
            date = System.currentTimeMillis() - 86400000, // Yesterday
            isExpense = true
        ),
        Transaction(
            id = 2,
            amount = 12.50,
            title = "Bus Ticket",
            description = "Monatskarte",
            category = sampleCategories[1],
            date = System.currentTimeMillis() - 172800000, // 2 days ago
            isExpense = true
        ),
        Transaction(
            id = 3,
            amount = 25.00,
            title = "Kino",
            description = "Avengers Endgame",
            category = sampleCategories[2],
            date = System.currentTimeMillis() - 259200000, // 3 days ago
            isExpense = true
        ),
        Transaction(
            id = 4,
            amount = 2500.00,
            title = "Gehalt",
            description = "Monatliches Gehalt",
            category = TransactionCategory(6, "Einkommen", Color.parseColor("#4CAF50"), R.drawable.ic_finance_chip),
            date = System.currentTimeMillis() - 604800000, // 1 week ago
            isExpense = false
        ),
        Transaction(
            id = 5,
            amount = 15.99,
            title = "Netflix",
            description = "Monatliches Abo",
            category = sampleCategories[2],
            date = System.currentTimeMillis() - 86400000,
            isExpense = true
        )
    )
    
    // Sample Budgets
    val sampleBudgets = listOf(
        Budget(
            id = 1,
            category = sampleCategories[0],
            amount = 300.0,
            period = BudgetPeriod.MONTHLY,
            spent = 125.67
        ),
        Budget(
            id = 2,
            category = sampleCategories[1],
            amount = 100.0,
            period = BudgetPeriod.MONTHLY,
            spent = 45.50
        ),
        Budget(
            id = 3,
            category = sampleCategories[2],
            amount = 150.0,
            period = BudgetPeriod.MONTHLY,
            spent = 89.99
        )
    )
    
    // Sample Assets (Crypto)
    val sampleAssets = listOf(
        Asset(
            id = "bitcoin",
            rank = "1",
            symbol = "BTC",
            name = "Bitcoin",
            supply = "19757131.0000000000000000",
            maxSupply = "21000000.0000000000000000",
            marketCapUsd = "846479297648.9018896394645431",
            volumeUsd24Hr = "14213755025.8648956275756091",
            priceUsd = "42853.7890123456789012345678",
            changePercent24Hr = "2.4567890123456789",
            vwap24Hr = "42500.1234567890123456789012",
            explorer = "https://blockchain.info/"
        ),
        Asset(
            id = "ethereum",
            rank = "2",
            symbol = "ETH",
            name = "Ethereum",
            supply = "120426315.8734050000000000",
            maxSupply = null,
            marketCapUsd = "301479297648.9018896394645431",
            volumeUsd24Hr = "8213755025.8648956275756091",
            priceUsd = "2502.3456789012345678901234",
            changePercent24Hr = "-1.2345678901234567",
            vwap24Hr = "2485.6789012345678901234567",
            explorer = "https://etherscan.io/"
        ),
        Asset(
            id = "tether",
            rank = "3",
            symbol = "USDT",
            name = "Tether",
            supply = "91426315.8734050000000000",
            maxSupply = null,
            marketCapUsd = "91426315.8734050000000000",
            volumeUsd24Hr = "24213755025.8648956275756091",
            priceUsd = "1.0001234567890123456789",
            changePercent24Hr = "0.0123456789012345",
            vwap24Hr = "1.0000000000000000000000",
            explorer = "https://www.omniexplorer.info/"
        )
    )
    
    // Sample History Data Points
    val sampleHistoryDataPoints = listOf(
        HistoryDataPoint(
            priceUsd = "42000.1234567890123456789",
            time = System.currentTimeMillis() - 86400000,
            date = "2024-01-15"
        ),
        HistoryDataPoint(
            priceUsd = "41500.9876543210987654321",
            time = System.currentTimeMillis() - 172800000,
            date = "2024-01-14"
        ),
        HistoryDataPoint(
            priceUsd = "43200.5555555555555555555",
            time = System.currentTimeMillis() - 259200000,
            date = "2024-01-13"
        ),
        HistoryDataPoint(
            priceUsd = "42800.7777777777777777777",
            time = System.currentTimeMillis() - 345600000,
            date = "2024-01-12"
        )
    )
    
    // Sample Receipt Items
    val sampleReceiptItems = listOf(
        ReceiptItem(name = "Milch 1L", price = 1.29),
        ReceiptItem(name = "Brot Vollkorn", price = 2.49),
        ReceiptItem(name = "Bananen 1kg", price = 1.99),
        ReceiptItem(name = "Joghurt Natur", price = 0.89),
        ReceiptItem(name = "Käse Gouda", price = 3.99)
    )
    
    // Sample Receipt
    val sampleReceipt = Receipt(
        storeName = "REWE Supermarkt",
        date = "2024-01-15",
        items = sampleReceiptItems
    )
    
    // Sample Assets Response
    val sampleAssetsResponse = AssetsResponse(
        data = sampleAssets,
        timestamp = System.currentTimeMillis()
    )
    
    // Sample History Response
    val sampleHistoryResponse = HistoryResponse(
        data = sampleHistoryDataPoints,
        timestamp = System.currentTimeMillis()
    )
}
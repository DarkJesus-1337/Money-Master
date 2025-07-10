package com.pixelpioneer.moneymaster.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.data.model.Receipt

class ReceiptResultActivity : ComponentActivity() {

    private lateinit var receipt: Receipt
    private lateinit var adapter: ReceiptItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_result)

        receipt = intent.getParcelableExtra("receipt") ?: return

        setupViews()
        setupRecyclerView()
        setupButtons()
    }

    private fun setupViews() {
        findViewById<TextView>(R.id.store_name_text).text = receipt.storeName ?: "Unbekannt"
        findViewById<TextView>(R.id.date_text).text = receipt.date ?: "Heute"
        findViewById<TextView>(R.id.total_amount_text).text =
            "Gesamt: ${String.format("%.2f", receipt.totalAmount)} €"
    }

    private fun setupRecyclerView() {
        adapter = ReceiptItemAdapter(receipt.items.toMutableList())
        findViewById<RecyclerView>(R.id.items_recycler_view).apply {
            layoutManager = LinearLayoutManager(this@ReceiptResultActivity)
            adapter = this@ReceiptResultActivity.adapter
        }
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.retry_button).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.save_button).setOnClickListener {
            // Hier Transaktionen speichern
            saveTransactions()
            finish()
        }
    }

    private fun saveTransactions() {
        // TODO: Implementiere das Speichern der Transaktionen
    }
}
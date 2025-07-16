package com.pixelpioneer.moneymaster.ui.screens.statistics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.pixelpioneer.moneymaster.data.model.Asset
import com.pixelpioneer.moneymaster.data.model.HistoryDataPoint
import com.pixelpioneer.moneymaster.ui.components.coincap.CryptoAssetsScreenContent
import com.pixelpioneer.moneymaster.ui.theme.MoneyMasterTheme
import com.pixelpioneer.moneymaster.ui.viewmodel.CryptoViewModel
import com.pixelpioneer.moneymaster.util.UiState

@Composable
fun CryptoAssetsScreen(viewModel: CryptoViewModel) {
    LaunchedEffect(Unit) {
        viewModel.loadCryptoAssets()
    }

    val cryptoAssetsState by viewModel.cryptoAssetsState.collectAsState()
    val cryptoHistoryState by viewModel.cryptoHistoryState.collectAsState()
    val selectedAsset by viewModel.selectedAsset.collectAsState()

    CryptoAssetsScreenContent(
        cryptoAssetsState = cryptoAssetsState,
        cryptoHistoryState = cryptoHistoryState,
        selectedAsset = selectedAsset,
        onAssetSelected = { viewModel.selectAsset(it) }
    )
}

@Preview(showBackground = true)
@Composable
private fun CryptoAssetsScreenPreview() {
    MoneyMasterTheme(darkTheme = true) {
        val mockAssets = listOf(
            Asset(
                id = "bitcoin",
                rank = "1",
                symbol = "BTC",
                name = "Bitcoin",
                supply = "19500000.0000000000000000",
                maxSupply = "21000000.0000000000000000",
                marketCapUsd = "1000000000000.0000000000000000",
                volumeUsd24Hr = "20000000000.0000000000000000",
                priceUsd = "50000.0000000000000000",
                changePercent24Hr = "2.5000000000000000",
                vwap24Hr = "49800.0000000000000000",
                explorer = "https://blockchain.info/"
            ),
            Asset(
                id = "ethereum",
                rank = "2",
                symbol = "ETH",
                name = "Ethereum",
                supply = "120000000.0000000000000000",
                maxSupply = null,
                marketCapUsd = "400000000000.0000000000000000",
                volumeUsd24Hr = "15000000000.0000000000000000",
                priceUsd = "3000.0000000000000000",
                changePercent24Hr = "-1.2000000000000000",
                vwap24Hr = "3050.0000000000000000",
                explorer = "https://etherscan.io/"
            )
        )

        val sampleHistoryData = listOf(
            HistoryDataPoint(
                priceUsd = "48000.0",
                time = System.currentTimeMillis() - 24 * 60 * 60 * 1000,
                date = ""
            ),
            HistoryDataPoint(
                priceUsd = "49200.0",
                time = System.currentTimeMillis() - 18 * 60 * 60 * 1000,
                date = ""
            ),
            HistoryDataPoint(
                priceUsd = "50500.0",
                time = System.currentTimeMillis() - 12 * 60 * 60 * 1000,
                date = ""
            ),
            HistoryDataPoint(
                priceUsd = "50000.0",
                time = System.currentTimeMillis(),
                date = ""
            )
        )

        CryptoAssetsScreenContent(
            cryptoAssetsState = UiState.Success(mockAssets),
            cryptoHistoryState = UiState.Success(sampleHistoryData),
            selectedAsset = mockAssets.first(),
            onAssetSelected = { }
        )
    }
}
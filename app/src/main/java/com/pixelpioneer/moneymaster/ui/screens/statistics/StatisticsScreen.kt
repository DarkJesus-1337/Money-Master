package com.pixelpioneer.moneymaster.ui.screens.statistics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pixelpioneer.moneymaster.ui.components.statistics.CategoriesTab
import com.pixelpioneer.moneymaster.ui.components.statistics.OverviewTab
import com.pixelpioneer.moneymaster.ui.components.statistics.TrendsTab
import com.pixelpioneer.moneymaster.ui.navigation.MoneyMasterBottomNavigation
import com.pixelpioneer.moneymaster.ui.viewmodel.CryptoViewModel
import com.pixelpioneer.moneymaster.ui.viewmodel.StatisticsViewModel

@Composable
fun StatisticsScreen(
    navController: NavController,
    statisticsViewModel: StatisticsViewModel,
    cryptoViewModel: CryptoViewModel
) {
    val statisticsState = statisticsViewModel.statisticsState.collectAsState().value
    val categoryStatsState = statisticsViewModel.categoryStatsState.collectAsState().value
    val monthlyTrendsState = statisticsViewModel.monthlyTrendsState.collectAsState().value

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Übersicht", "Kategorien", "Trends", "Crypto")

    Scaffold(
        bottomBar = { MoneyMasterBottomNavigation(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "Statistiken",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> OverviewTab(statisticsState)
                1 -> CategoriesTab(categoryStatsState)
                2 -> TrendsTab(monthlyTrendsState)
                3 -> CryptoAssetsScreen(cryptoViewModel)
            }
        }
    }
}

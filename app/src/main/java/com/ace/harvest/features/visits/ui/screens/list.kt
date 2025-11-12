package com.ace.harvest.features.visits.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ListScreen() {
    LazyColumn {
        items(100) { index ->
            Text("Item $index")
        }
    }
}
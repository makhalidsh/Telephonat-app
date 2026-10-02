package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.RepairRecord
import com.example.data.model.RepairStatus
import com.example.ui.components.RepairCard
import com.example.ui.theme.AmberSoft
import com.example.ui.theme.AmberSoftBg
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlueBorder
import com.example.ui.theme.BrandBlueLight
import com.example.ui.theme.CanvasBg
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.EmeraldSoftBg
import com.example.ui.theme.MonospaceCodeMedium
import com.example.ui.theme.RoseSoft
import com.example.ui.theme.SkySoft
import com.example.ui.theme.SkySoftBg
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceSubtle
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPlaceholder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.RepairViewModel
import com.example.util.AppLanguage
import com.example.util.Strings

@Composable
fun RepairLedgerScreen(
    viewModel: RepairViewModel,
    isArchiveMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val allRepairs by viewModel.allRepairs.collectAsStateWithLifecycle()
    val filteredRepairs by viewModel.filteredRepairs.collectAsStateWithLifecycle()
    val subFilter by viewModel.statusSubFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val highlightedCode by viewModel.highlightedCode.collectAsStateWithLifecycle()

    var recordToDelete by remember { mutableStateOf<RepairRecord?>(null) }

    val inShopRepairs = allRepairs.filter { it.status != RepairStatus.DELIVERED.id }
    val waitingCount = inShopRepairs.count { it.status == RepairStatus.WAITING.id }
    val repairingCount = inShopRepairs.count { it.status == RepairStatus.REPAIRING.id }
    val readyCount = inShopRepairs.count { it.status == RepairStatus.READY.id }
    val deliveredCount = allRepairs.count { it.status == RepairStatus.DELIVERED.id }

    Box(modifier = modifier.fillMaxSize().background(CanvasBg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Calm, Clean White Header (Apple / Google Style)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isArchiveMode) Strings.deliveredTab(language) else Strings.tabAtelier(language),
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isArchiveMode) {
                            if (language == AppLanguage.AR) "$deliveredCount أجهزة مسلّمة للزبائن" else "$deliveredCount appareils livrés aux clients"
                        } else {
                            if (language == AppLanguage.AR) "$waitingCount انتظار • $repairingCount قيد الإصلاح • $readyCount جاهز"
                            else "$waitingCount en attente • $repairingCount en cours • $readyCount prêt"
                        },
                        color = if (readyCount > 0 && !isArchiveMode) EmeraldSoft else TextSecondary,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Quick Barcode / Sticker scanner icon button
                IconButton(
                    onClick = { viewModel.openScanner() },
                    modifier = Modifier
                        .size(42.dp)
                        .shadow(elevation = 1.dp, shape = CircleShape, spotColor = Color(0x10000000))
                        .clip(CircleShape)
                        .background(SurfaceWhite)
                        .border(1.dp, BorderSoft, CircleShape)
                        .testTag("open_scanner_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = Strings.scanSticker(language),
                        tint = BrandBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar (Clean White Pill)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = {
                    Text(
                        text = Strings.searchPlaceholder(language),
                        color = TextPlaceholder,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_field"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandBlue,
                    unfocusedBorderColor = BorderSoft,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = SurfaceWhite,
                    unfocusedContainerColor = SurfaceWhite
                )
            )

            // Sub-status filter pills (In-Shop mode only)
            if (!isArchiveMode) {
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterPill(
                            title = Strings.all(language),
                            isSelected = subFilter == null,
                            count = inShopRepairs.size,
                            accentColor = BrandBlue,
                            activeBg = BrandBlueLight,
                            activeBorder = BrandBlueBorder,
                            onClick = { viewModel.setStatusSubFilter(null) }
                        )
                    }
                    item {
                        FilterPill(
                            title = Strings.statusWaiting(language),
                            isSelected = subFilter == RepairStatus.WAITING.id,
                            accentColor = AmberSoft,
                            activeBg = AmberSoftBg,
                            activeBorder = BorderSoft,
                            count = waitingCount,
                            onClick = { viewModel.setStatusSubFilter(RepairStatus.WAITING.id) }
                        )
                    }
                    item {
                        FilterPill(
                            title = Strings.statusRepairing(language),
                            isSelected = subFilter == RepairStatus.REPAIRING.id,
                            accentColor = SkySoft,
                            activeBg = SkySoftBg,
                            activeBorder = BorderSoft,
                            count = repairingCount,
                            onClick = { viewModel.setStatusSubFilter(RepairStatus.REPAIRING.id) }
                        )
                    }
                    item {
                        FilterPill(
                            title = Strings.statusReady(language),
                            isSelected = subFilter == RepairStatus.READY.id,
                            accentColor = EmeraldSoft,
                            activeBg = EmeraldSoftBg,
                            activeBorder = BorderSoft,
                            count = readyCount,
                            onClick = { viewModel.setStatusSubFilter(RepairStatus.READY.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main List with generous breathing room
            if (filteredRepairs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(SurfaceSubtle),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📱", fontSize = 28.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isArchiveMode) Strings.emptyDelivered(language) else Strings.emptyInShop(language),
                            color = TextSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("repair_list"),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredRepairs, key = { it.id }) { record ->
                        val isHighlighted = record.code == highlightedCode
                        RepairCard(
                            record = record,
                            language = language,
                            isHighlighted = isHighlighted,
                            onClick = {
                                viewModel.openDetail(record)
                            },
                            onPrintSticker = {
                                viewModel.openSticker(record)
                            }
                        )
                    }
                }
            }
        }

        // Floating Action Button (+ Nouveau Téléphone)
        if (!isArchiveMode) {
            FloatingActionButton(
                onClick = { viewModel.openIntake() },
                containerColor = BrandBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 90.dp)
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(18.dp), spotColor = BrandBlue.copy(alpha = 0.4f))
                    .testTag("add_repair_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = Strings.newIntake(language), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = Strings.newIntake(language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    )
                }
            }
        }

        // Delete confirmation
        recordToDelete?.let { rec ->
            AlertDialog(
                onDismissRequest = { recordToDelete = null },
                title = { Text(text = Strings.deleteConfirm(language), color = TextPrimary) },
                text = {
                    Text(
                        text = "#${rec.code} - ${rec.brand} ${rec.model} (${rec.clientName})",
                        color = TextSecondary
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteRecord(rec)
                        recordToDelete = null
                    }) {
                        Text(if (language == AppLanguage.AR) "حذف" else "Supprimer", color = RoseSoft, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { recordToDelete = null }) {
                        Text(if (language == AppLanguage.AR) "إلغاء" else "Annuler", color = TextSecondary)
                    }
                },
                containerColor = SurfaceWhite
            )
        }
    }
}

@Composable
private fun FilterPill(
    title: String,
    isSelected: Boolean,
    count: Int,
    accentColor: Color = BrandBlue,
    activeBg: Color = BrandBlueLight,
    activeBorder: Color = BrandBlueBorder,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) activeBg else SurfaceWhite)
            .border(1.dp, if (isSelected) activeBorder else BorderSoft, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                color = if (isSelected) accentColor else TextSecondary,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "$count",
                color = if (isSelected) accentColor else TextMuted,
                fontSize = 11.sp,
                style = MonospaceCodeMedium
            )
        }
    }
}

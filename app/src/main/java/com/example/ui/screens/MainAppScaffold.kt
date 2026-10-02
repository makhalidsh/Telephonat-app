package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BackupExportDialog
import com.example.ui.components.BarcodeScannerModal
import com.example.ui.components.RepairDetailSheet
import com.example.ui.components.ThermalLabelPreviewDialog
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlueLight
import com.example.ui.theme.CanvasBg
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.RepairViewModel
import com.example.util.AppLanguage
import com.example.util.Strings

@Composable
fun MainAppScaffold(
    viewModel: RepairViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.language.collectAsStateWithLifecycle()
    val settings by viewModel.workshopSettings.collectAsStateWithLifecycle()
    val selectedNavIndex by viewModel.selectedNavIndex.collectAsStateWithLifecycle()
    val nextCodePair by viewModel.nextCodePair.collectAsStateWithLifecycle()

    val isIntakeOpen by viewModel.isIntakeOpen.collectAsStateWithLifecycle()
    val editingRecord by viewModel.editingRecord.collectAsStateWithLifecycle()
    val detailRecord by viewModel.detailRecord.collectAsStateWithLifecycle()
    val stickerRecord by viewModel.selectedRecordForSticker.collectAsStateWithLifecycle()
    val isScannerOpen by viewModel.isScannerOpen.collectAsStateWithLifecycle()
    val isBackupOpen by viewModel.isBackupDialogOpen.collectAsStateWithLifecycle()
    val backupJson by viewModel.backupJsonString.collectAsStateWithLifecycle()

    // Back handling: If on secondary tabs (Archive or Settings), return to Atelier (0)
    BackHandler(enabled = selectedNavIndex != 0) {
        viewModel.setSelectedNavIndex(0)
    }

    val layoutDirection = if (language == AppLanguage.AR) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = CanvasBg,
            bottomBar = {
                NavigationBar(
                    containerColor = SurfaceWhite,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp), spotColor = Color(0x12000000))
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .border(1.dp, BorderSoft, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_nav_bar")
                ) {
                    // Item 0: Atelier (In Shop)
                    val isAtelierSelected = selectedNavIndex == 0
                    NavigationBarItem(
                        selected = isAtelierSelected,
                        onClick = { viewModel.setSelectedNavIndex(0) },
                        icon = {
                            Icon(
                                imageVector = if (isAtelierSelected) Icons.Filled.PhoneAndroid else Icons.Outlined.PhoneAndroid,
                                contentDescription = Strings.tabAtelier(language),
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = Strings.tabAtelier(language),
                                fontSize = 11.5.sp,
                                fontWeight = if (isAtelierSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandBlue,
                            selectedTextColor = BrandBlue,
                            indicatorColor = BrandBlueLight,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_atelier")
                    )

                    // Item 1: Livrés / Archive
                    val isArchiveSelected = selectedNavIndex == 1
                    NavigationBarItem(
                        selected = isArchiveSelected,
                        onClick = { viewModel.setSelectedNavIndex(1) },
                        icon = {
                            Icon(
                                imageVector = if (isArchiveSelected) Icons.Filled.Inventory2 else Icons.Outlined.Inventory2,
                                contentDescription = Strings.tabArchive(language),
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = Strings.tabArchive(language),
                                fontSize = 11.5.sp,
                                fontWeight = if (isArchiveSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandBlue,
                            selectedTextColor = BrandBlue,
                            indicatorColor = BrandBlueLight,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_archive")
                    )

                    // Item 2: Paramètres / Settings
                    val isSettingsSelected = selectedNavIndex == 2
                    NavigationBarItem(
                        selected = isSettingsSelected,
                        onClick = { viewModel.setSelectedNavIndex(2) },
                        icon = {
                            Icon(
                                imageVector = if (isSettingsSelected) Icons.Filled.Settings else Icons.Outlined.Settings,
                                contentDescription = Strings.tabSettings(language),
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = Strings.tabSettings(language),
                                fontSize = 11.5.sp,
                                fontWeight = if (isSettingsSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandBlue,
                            selectedTextColor = BrandBlue,
                            indicatorColor = BrandBlueLight,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_settings")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedNavIndex) {
                    0 -> RepairLedgerScreen(viewModel = viewModel, isArchiveMode = false)
                    1 -> RepairLedgerScreen(viewModel = viewModel, isArchiveMode = true)
                    2 -> SettingsScreen(
                        currentSettings = settings,
                        language = language,
                        onSaveSettings = { updated ->
                            viewModel.updateWorkshopSettings(updated)
                        },
                        onOpenBackupExport = {
                            viewModel.openBackupDialog()
                        },
                        onTestSupabase = { url, key, callback ->
                            viewModel.testSupabaseConnection(url, key, callback)
                        },
                        onSyncSupabase = { callback ->
                            viewModel.syncWithSupabase(callback)
                        }
                    )
                }
            }
        }

        // Intake Form Dialog
        if (isIntakeOpen) {
            IntakeModal(
                language = language,
                nextCodePair = nextCodePair,
                editingRecord = editingRecord,
                onSave = { code, num, brand, model, color, prob, price, client, phone, notes, andPrint ->
                    viewModel.saveRepair(code, num, brand, model, color, prob, price, client, phone, notes, andPrint)
                },
                onDismiss = { viewModel.closeIntake() }
            )
        }

        // Spacious Repair Detail Bottom Sheet
        detailRecord?.let { rec ->
            RepairDetailSheet(
                record = rec,
                language = language,
                onStatusChange = { newStatus ->
                    viewModel.updateStatus(rec, newStatus)
                },
                onPrintSticker = {
                    viewModel.openSticker(rec)
                },
                onEdit = {
                    viewModel.closeDetail()
                    viewModel.openIntake(rec)
                },
                onDelete = {
                    viewModel.deleteRecord(rec)
                    viewModel.closeDetail()
                },
                onDismiss = {
                    viewModel.closeDetail()
                }
            )
        }

        // Thermal Sticker 50x30mm Preview Dialog (Customized with Workshop Settings!)
        stickerRecord?.let { rec ->
            ThermalLabelPreviewDialog(
                record = rec,
                language = language,
                workshopName = settings.workshopName,
                workshopPhone = settings.workshopPhone,
                onDismiss = { viewModel.closeSticker() }
            )
        }

        // CameraX Barcode / QR Scanner Modal
        if (isScannerOpen) {
            BarcodeScannerModal(
                language = language,
                onBarcodeDetected = { scanned ->
                    viewModel.onBarcodeScanned(scanned)
                },
                onDismiss = { viewModel.closeScanner() }
            )
        }

        // JSON Backup / Export / Import Dialog
        if (isBackupOpen) {
            BackupExportDialog(
                language = language,
                jsonString = backupJson,
                onImport = { json ->
                    viewModel.importBackup(json) { count ->
                        Toast.makeText(context, "$count appareils importés !", Toast.LENGTH_SHORT).show()
                        viewModel.closeBackupDialog()
                    }
                },
                onDismiss = { viewModel.closeBackupDialog() }
            )
        }
    }
}

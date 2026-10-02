package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlueLight
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.MonospaceCodeMedium
import com.example.ui.theme.SurfaceSubtle
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPlaceholder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.AppLanguage
import com.example.util.Strings

@Composable
fun BackupExportDialog(
    language: AppLanguage,
    jsonString: String,
    onImport: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var inputImportJson by remember { mutableStateOf("") }
    var activeTab by remember { mutableStateOf(0) } // 0 = Export, 1 = Import

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .shadow(elevation = 10.dp, shape = RoundedCornerShape(24.dp), spotColor = Color(0x18000000))
                .clip(RoundedCornerShape(24.dp))
                .background(SurfaceWhite)
                .border(1.dp, BorderSoft, RoundedCornerShape(24.dp))
                .testTag("backup_export_dialog"),
            color = SurfaceWhite
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = Strings.backupExport(language),
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (language == AppLanguage.AR) "تصدير السجل لمزامنة الويب أو حفظ نسخة" else "Export JSON pour Web Sync & Sauvegarde",
                            color = BrandBlue,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Segmented tab [Export / Import]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceSubtle)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeTab == 0) BrandBlue else Color.Transparent)
                            .clickable { activeTab = 0 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (language == AppLanguage.AR) "تصدير السجل (JSON)" else "Exporter (JSON)",
                            color = if (activeTab == 0) Color.White else TextSecondary,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeTab == 1) BrandBlue else Color.Transparent)
                            .clickable { activeTab = 1 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (language == AppLanguage.AR) "استيراد بيانات" else "Importer (JSON)",
                            color = if (activeTab == 1) Color.White else TextSecondary,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (activeTab == 0) {
                    // Export View
                    OutlinedTextField(
                        value = jsonString,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MonospaceCodeMedium.copy(fontSize = 11.5.sp, color = TextPrimary),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlue,
                            unfocusedBorderColor = BorderSoft,
                            focusedContainerColor = SurfaceSubtle,
                            unfocusedContainerColor = SurfaceSubtle
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("RepairBackup", jsonString)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copié dans le presse-papier !", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f).height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = if (language == AppLanguage.AR) "نسخ JSON" else "Copier JSON", fontSize = 12.5.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, jsonString)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share JSON Backup"))
                            },
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = if (language == AppLanguage.AR) "مشاركة" else "Partager", fontSize = 12.5.sp)
                        }
                    }
                } else {
                    // Import View
                    OutlinedTextField(
                        value = inputImportJson,
                        onValueChange = { inputImportJson = it },
                        placeholder = { Text("Coller le JSON ici...", color = TextPlaceholder) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MonospaceCodeMedium.copy(fontSize = 11.5.sp, color = TextPrimary),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlue,
                            unfocusedBorderColor = BorderSoft,
                            focusedContainerColor = SurfaceSubtle,
                            unfocusedContainerColor = SurfaceSubtle
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (inputImportJson.isNotBlank()) {
                                onImport(inputImportJson)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSoft, contentColor = Color.White),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == AppLanguage.AR) "تأكيد واستيراد إلى السجل" else "Valider & Importer",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

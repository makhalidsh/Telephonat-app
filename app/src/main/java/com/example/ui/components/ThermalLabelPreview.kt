package com.example.ui.components

import android.bluetooth.BluetoothDevice
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.RepairRecord
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlueLight
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.EmeraldSoftBg
import com.example.ui.theme.MonospaceCodeMedium
import com.example.ui.theme.SurfaceSubtle
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.AppLanguage
import com.example.util.Strings
import com.example.util.ThermalLabelPrinter
import kotlinx.coroutines.launch

@Composable
fun ThermalLabelPreviewDialog(
    record: RepairRecord,
    language: AppLanguage,
    workshopName: String = "Phone Repair Register",
    workshopPhone: String = "",
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val labelBitmap = remember(record, workshopName, workshopPhone) {
        ThermalLabelPrinter.createLabelBitmap(record, workshopName, workshopPhone)
    }

    var isBtListVisible by remember { mutableStateOf(false) }
    var pairedBtDevices by remember { mutableStateOf<List<BluetoothDevice>>(emptyList()) }
    var isBtPrinting by remember { mutableStateOf(false) }

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
                .testTag("thermal_label_dialog"),
            color = SurfaceWhite
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = Strings.thermalPreviewTitle(language),
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "50x30mm Back-of-Phone Sticker • 203 DPI",
                            color = BrandBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // The 50x30mm thermal label rendered preview (Aspect ratio 5:3 = 480:288)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(480f / 288f)
                        .shadow(elevation = 3.dp, shape = RoundedCornerShape(10.dp), spotColor = Color(0x15000000))
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .border(1.5.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                        .padding(2.dp)
                ) {
                    Image(
                        bitmap = labelBitmap.asImageBitmap(),
                        contentDescription = "Thermal label #${record.code}",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // QR Code Content Info Pill (Soft light background)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceSubtle)
                        .border(1.dp, BorderSoft, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = if (language == AppLanguage.AR) "محتوى رمز الاستجابة السريعة (QR):" else "Contenu QR Code:",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = record.qrPayload(),
                            style = MonospaceCodeMedium.copy(color = TextSecondary, fontSize = 11.sp),
                            maxLines = 2
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isBtListVisible) {
                    // Paired Bluetooth printers selection list
                    Text(
                        text = if (language == AppLanguage.AR) "اختر طابعة حرارية مقترنة:" else "Choisir imprimante Bluetooth appairée:",
                        color = BrandBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (isBtPrinting) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = BrandBlue)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (language == AppLanguage.AR) "جاري إرسال أمر الطباعة..." else "Envoi à l'imprimante...",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    } else if (pairedBtDevices.isEmpty()) {
                        Text(
                            text = if (language == AppLanguage.AR) "لا توجد أجهزة بلوتوث مقترنة. قم بربط الطابعة من إعدادات الهاتف أولاً." else "Aucune imprimante Bluetooth trouvée dans les appareils associés.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        LazyColumn(modifier = Modifier.height(110.dp)) {
                            items(pairedBtDevices) { device ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SurfaceSubtle)
                                        .border(1.dp, BorderSoft, RoundedCornerShape(10.dp))
                                        .clickable {
                                            isBtPrinting = true
                                            scope.launch {
                                                val result = ThermalLabelPrinter.printToBluetoothDevice(device, labelBitmap)
                                                isBtPrinting = false
                                                if (result.isSuccess) {
                                                    Toast.makeText(context, "Imprimé avec succès !", Toast.LENGTH_SHORT).show()
                                                    onDismiss()
                                                } else {
                                                    Toast.makeText(context, "Erreur: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Bluetooth, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = device.name ?: device.address, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // System Print Button
                    Button(
                        onClick = {
                            ThermalLabelPrinter.printViaPrintManager(context, record, workshopName, workshopPhone)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp), spotColor = BrandBlue.copy(alpha = 0.3f))
                            .testTag("system_print_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = Strings.printViaSystem(language),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Bluetooth ESC/POS Print Button
                    Button(
                        onClick = {
                            pairedBtDevices = ThermalLabelPrinter.getPairedBluetoothPrinters()
                            isBtListVisible = !isBtListVisible
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("bluetooth_print_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldSoftBg,
                            contentColor = EmeraldSoft
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Bluetooth, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = Strings.printBluetooth(language),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Share Button
                OutlinedButton(
                    onClick = {
                        ThermalLabelPrinter.shareLabelImage(context, record, workshopName, workshopPhone)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("share_sticker_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = Strings.shareSticker(language), fontSize = 12.5.sp)
                }
            }
        }
    }
}

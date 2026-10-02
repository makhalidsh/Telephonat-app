package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RepairRecord
import com.example.data.model.RepairStatus
import com.example.ui.theme.AmberSoft
import com.example.ui.theme.AmberSoftBg
import com.example.ui.theme.AmberSoftBorder
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlueBorder
import com.example.ui.theme.BrandBlueLight
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.EmeraldSoftBg
import com.example.ui.theme.EmeraldSoftBorder
import com.example.ui.theme.MonospaceCodeLarge
import com.example.ui.theme.MonospacePhone
import com.example.ui.theme.MonospacePrice
import com.example.ui.theme.RoseSoft
import com.example.ui.theme.RoseSoftBg
import com.example.ui.theme.SkySoft
import com.example.ui.theme.SkySoftBg
import com.example.ui.theme.SkySoftBorder
import com.example.ui.theme.SurfaceSubtle
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.AppLanguage
import com.example.util.MoroccanPhoneUtils
import com.example.util.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairDetailSheet(
    record: RepairRecord,
    language: AppLanguage,
    onStatusChange: (RepairStatus) -> Unit,
    onPrintSticker: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceWhite,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(BorderSoft)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .verticalScroll(rememberScrollState())
                .testTag("repair_detail_sheet")
        ) {
            // Header: Code Badge + Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(BrandBlueLight)
                            .border(1.dp, BrandBlueBorder, RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "#${record.code}",
                            style = MonospaceCodeLarge.copy(color = BrandBlue, fontSize = 22.sp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "${record.brand} ${record.model}",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = Strings.formatRelativeTime(record.receivedAt, language),
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Status Stepper Selector (Clean Soft Pills)
            Text(
                text = if (language == AppLanguage.AR) "حالة الجهاز:" else "État de l'appareil :",
                color = TextSecondary,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceSubtle)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                RepairStatus.entries.forEach { status ->
                    val isSelected = record.statusEnum == status
                    val (label, color, bg) = when (status) {
                        RepairStatus.WAITING -> Triple(Strings.statusWaiting(language), AmberSoft, AmberSoftBg)
                        RepairStatus.REPAIRING -> Triple(Strings.statusRepairing(language), SkySoft, SkySoftBg)
                        RepairStatus.READY -> Triple(Strings.statusReady(language), EmeraldSoft, EmeraldSoftBg)
                        RepairStatus.DELIVERED -> Triple(Strings.statusDelivered(language), TextSecondary, SurfaceWhite)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) bg else Color.Transparent)
                            .border(1.dp, if (isSelected) color.copy(alpha = 0.4f) else Color.Transparent, RoundedCornerShape(10.dp))
                            .clickable { onStatusChange(status) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) color else TextMuted,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Client Info Card (Soft white surface with subtle border)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceSubtle)
                    .border(1.dp, BorderSoft, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = Strings.clientName(language),
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                            Text(
                                text = record.clientName,
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Price
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceWhite)
                                .border(1.dp, BorderSoft, RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${record.price.toInt()} ${Strings.currency(language)}",
                                style = MonospacePrice.copy(color = TextPrimary, fontSize = 17.sp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = MoroccanPhoneUtils.formatMoroccanPhone(record.clientPhone),
                            style = MonospacePhone.copy(color = TextSecondary, fontSize = 14.5.sp)
                        )

                        // Call & WhatsApp quick actions
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(EmeraldSoftBg)
                                    .border(1.dp, EmeraldSoftBorder, RoundedCornerShape(10.dp))
                                    .clickable { MoroccanPhoneUtils.dialCustomer(context, record.clientPhone) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, tint = EmeraldSoft, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(Strings.callClient(language), color = EmeraldSoft, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceWhite)
                                    .border(1.dp, BorderSoft, RoundedCornerShape(10.dp))
                                    .clickable {
                                        val msg = if (language == AppLanguage.AR) {
                                            "السلام عليكم ${record.clientName}، بخصوص هاتفكم ${record.brand} ${record.model} (تذكرة #${record.code})."
                                        } else {
                                            "Bonjour ${record.clientName}, concernant votre ${record.brand} ${record.model} (Ticket #${record.code})."
                                        }
                                        MoroccanPhoneUtils.openWhatsApp(context, record.clientPhone, msg)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = EmeraldSoft, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(Strings.whatsAppClient(language), color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Issue & Notes Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceSubtle)
                    .border(1.dp, BorderSoft, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = Strings.problem(language),
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = record.problem,
                        color = TextPrimary,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Medium
                    )

                    if (record.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Key, contentDescription = null, tint = AmberSoft, modifier = Modifier.size(16.dp).padding(top = 2.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = Strings.internalNotes(language),
                                    color = AmberSoft,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = record.notes,
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Bottom Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onPrintSticker,
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue, contentColor = Color.White),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(Strings.printSticker(language), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(0.7f).height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (language == AppLanguage.AR) "تعديل" else "Modifier", fontSize = 12.5.sp)
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(RoseSoftBg)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RoseSoft)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

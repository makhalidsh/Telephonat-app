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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.RepairRecord
import com.example.data.model.RepairStatus
import com.example.ui.theme.AmberSoft
import com.example.ui.theme.AmberSoftBg
import com.example.ui.theme.AmberSoftBorder
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlueLight
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.EmeraldSoftBg
import com.example.ui.theme.EmeraldSoftBorder
import com.example.ui.theme.MonospaceCodeMedium
import com.example.ui.theme.MonospacePhone
import com.example.ui.theme.MonospacePrice
import com.example.ui.theme.SkySoft
import com.example.ui.theme.SkySoftBg
import com.example.ui.theme.SkySoftBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.AppLanguage
import com.example.util.MoroccanPhoneUtils
import com.example.util.Strings

@Composable
fun RepairCard(
    record: RepairRecord,
    language: AppLanguage,
    isHighlighted: Boolean,
    onClick: () -> Unit,
    onPrintSticker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val (statusLabel, statusColor, statusBg, statusBorder) = when (record.statusEnum) {
        RepairStatus.WAITING -> Quadruple(Strings.statusWaiting(language), AmberSoft, AmberSoftBg, AmberSoftBorder)
        RepairStatus.REPAIRING -> Quadruple(Strings.statusRepairing(language), SkySoft, SkySoftBg, SkySoftBorder)
        RepairStatus.READY -> Quadruple(Strings.statusReady(language), EmeraldSoft, EmeraldSoftBg, EmeraldSoftBorder)
        RepairStatus.DELIVERED -> Quadruple(Strings.statusDelivered(language), TextSecondary, SurfaceSubtle, BorderSoft)
    }

    val cardBorderColor = if (isHighlighted) BrandBlue else BorderSoft

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0x0F000000))
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceCard)
            .border(if (isHighlighted) 1.5.dp else 1.dp, cardBorderColor, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(16.dp)
            .testTag("repair_card_${record.code}")
    ) {
        Column {
            // Top Row: Ticket Code Badge + Intake Time + Soft Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(BrandBlueLight)
                            .border(1.dp, BrandBlue.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "#${record.code}",
                            style = MonospaceCodeMedium.copy(color = BrandBlue, fontSize = 14.5.sp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = Strings.formatRelativeTime(record.receivedAt, language),
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                // Soft Pastel Status Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(statusBg)
                        .border(1.dp, statusBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = statusLabel,
                            color = statusColor,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Device Model & Color
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${record.brand} ${record.model}",
                    color = TextPrimary,
                    fontSize = 16.5.sp,
                    fontWeight = FontWeight.SemiBold
                )

                if (record.color.isNotBlank()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SurfaceSubtle)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = record.color,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            // Issue description
            Text(
                text = record.problem,
                color = TextSecondary,
                fontSize = 13.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Customer info & bottom strip (Soft light container)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceSubtle)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = record.clientName,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = MoroccanPhoneUtils.formatMoroccanPhone(record.clientPhone),
                        style = MonospacePhone.copy(color = TextSecondary, fontSize = 12.sp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Price tag
                    Text(
                        text = "${record.price.toInt()} ${Strings.currency(language)}",
                        style = MonospacePrice.copy(color = TextPrimary, fontSize = 15.sp)
                    )

                    // Call Button (Soft mint round icon)
                    IconButton(
                        onClick = { MoroccanPhoneUtils.dialCustomer(context, record.clientPhone) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EmeraldSoftBg)
                    ) {
                        Icon(
                            Icons.Default.Call,
                            contentDescription = Strings.callClient(language),
                            tint = EmeraldSoft,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Print Sticker Button (Soft blue round icon)
                    IconButton(
                        onClick = onPrintSticker,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BrandBlueLight)
                            .testTag("print_sticker_btn_${record.code}")
                    ) {
                        Icon(
                            Icons.Default.Print,
                            contentDescription = Strings.printSticker(language),
                            tint = BrandBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
